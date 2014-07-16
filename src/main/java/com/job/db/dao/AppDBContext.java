package com.job.db.dao;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.job.db.dataservice.connection.impl.ReadConnectionGetter;
import com.job.db.dataservice.connection.impl.WriteConnectionGetter;

/**
 * 应用上下文
 * 1、保存了当前的读，写连接
 * 2、最近获取的连接是读还是写
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:18:48
 */
public class AppDBContext {
	private static Logger log = Logger.getLogger(AppDBContext.class);
	private static ThreadLocal<Connection> rconl = new ThreadLocal<Connection>();
	private static ThreadLocal<Connection> wconl = new ThreadLocal<Connection>();
	/** 记录一个线程曾经使用过的应用数据库连接池，-1为写连接，0-n为读连接 */
	private static ThreadLocal<Integer> lastReadWriteFlag = new ThreadLocal<Integer>();
	public static boolean IS_MUST_READ_MASTER_MACHINE = false;
	
    private static ThreadLocal<Integer> type = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
	
	private static ThreadLocal<Integer> conType = new ThreadLocal<Integer>();
	
	private static final int SYS_CONNECTION = 1;
	private static final int MASTER_CONNECTION = 2;
	private static final int SLAVE_CONNECTION = 3;
	
    public static int getType() {
        return type.get();
    }

    public static void setType(int type1) {
        type.set(type1);
    }

	/**
	 * 读连接
	 * 
	 * 逻辑：若已有写连接，则返回写连接，否则从多个读连接池中任取出一个读连接（后续调用始终返回此连接）。
	 * 若线程中途调用了 releaseDbCon()，该方法可保证释放前后的连接来自同一连接池。若释放前获得过写连接，再次调用会直接获得写连接。
	 * 
	 * @param dbName
	 * @return
	 */
	public static Connection getRCon(String dbName){
		Connection wcon = wconl.get();
		if (wcon != null) {
			releaseRcon();
			conType.set(MASTER_CONNECTION);
			return wcon;
		}
		
		conType.set(SLAVE_CONNECTION);
		
		Connection rcon =  rconl.get();
		log.info("----------get read conn-------------");
		if (rcon==null){
			try{
				if (log.isDebugEnabled()) {
					log.debug("----------create read conn-------------");
				}
				ReadConnectionGetter cgetter = new ReadConnectionGetter();
				rcon=cgetter.getConnection(dbName);
				Statement st = rcon.createStatement();
				st.execute("use `"+dbName + "`");
				st.close();
				rconl.set(rcon);
			}catch (Exception e){
				closeCon(rcon);
				log.error("========get connection error======="+dbName, e);
			}
		}
		return rcon;
	}
	
	/**
	 * 写连接
	 * @param dbName
	 * @return
	 */
	public static Connection getWCon(String dbName){
		conType.set(MASTER_CONNECTION);
		Connection wcon =  wconl.get();
		log.info("----------get write conn-------------");
		if (wcon==null){
			try{
				if (log.isDebugEnabled()) {
					log.debug("----------create write conn-------------");
				}
				WriteConnectionGetter cgetter = new WriteConnectionGetter();
				wcon = cgetter.getConnection(dbName);
				Statement st = wcon.createStatement();
				st.execute("use `"+dbName + "`");
				st.close();
				wconl.set(wcon);
			}catch (Exception e){
				closeCon(wcon);
				log.error("========get connection error======="+dbName, e);
			}
		}
		return wcon;
	}
	

	public static Integer getLastReadWriteFlag() {
		return lastReadWriteFlag.get();
	}
	
	public static int getConType() {
		Integer n = conType.get();
		if (n == null) {
			return 0;
		}
		return n;
	}
	
	public static String getConTypePrefix() {
		int type = getConType();
		switch(type) {
		case SYS_CONNECTION:
			return "Sys";
		case SLAVE_CONNECTION:
			return "Slave";
		case MASTER_CONNECTION:
			return "Master";
		default:
			return "Unknown";
		}
	}

	public static void setLastReadWriteFlag(Integer flag) {
		AppDBContext.lastReadWriteFlag.set(flag);
	}

	public static void releaseAppResource(){
		try{
			lastReadWriteFlag.remove();
            releaseDbCon();
            type.remove();
            MDC.remove("domain");
		}catch (Exception e)
		{
			
		}
	}
	

	private static void closeCon(Connection con){
		try{
			if (con!=null){
				con.close();
			}
		}catch (Exception e){
			log.error("REASE CONNECTION ERROR!!", e);
		}
	}
	
	public static void releaseRcon() {
		try {
			Connection rcon = rconl.get();
			if(rcon != null){
			}
			if (log.isDebugEnabled() && rcon != null) {
				log.debug("----------close read conn-------------");
			}
			closeCon(rcon);
			rconl.remove();
		} catch (Exception e) {
			log.error("release rcon failed!", e);
		}
	}
	
	public static void releaseWcon() {
		try {
			Connection wcon = wconl.get();
			if(wcon != null){
			}
			if (log.isDebugEnabled() && wcon != null) {
				log.debug("----------close write conn-------------");
			}
			wconl.remove();
			closeCon(wcon);
		} catch (Exception e) {
			log.error("release wcon failed!", e);
		}
	}
	
	public static void releaseDbCon() {
		releaseRcon();
		releaseWcon();
	}

	
}
