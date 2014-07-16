package com.job.db.dataservice.datasource.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

import com.job.db.dataservice.datasource.DataSourceConfig;
import com.job.db.dataservice.datasource.IDataSourceProvider;
import com.job.db.utils.DBUtil;

/**
 * Proxool 连接池提供工厂类
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:14:08
 */
public class ProxoolDatasourceProvider implements IDataSourceProvider {

    private static Logger log = Logger.getLogger(ProxoolDatasourceProvider.class);

    private ProxoolDataSource proxoolDataSource;

    public DataSource getDataSource() {
        return proxoolDataSource;
    }

    public void shutdown() {
        ProxoolFacade.shutdown();
    }

    /**
     * 连接池统一配置入口
     */
    public void config(DataSourceConfig dbConfig) {
        if (proxoolDataSource == null) {
            proxoolDataSource = new ProxoolDataSource();

        }
        proxoolDataSource.setDriverUrl(getDBUrl(dbConfig.getIp(), dbConfig.getPort(), dbConfig.getUseCursorFetch(),dbConfig.getDbName()));
        proxoolDataSource.setDriver(dbConfig.getDriveClassName());
        proxoolDataSource.setUser(dbConfig.getUsername());
        proxoolDataSource.setPassword(dbConfig.getPassword());
        proxoolDataSource.setMaximumConnectionCount(Integer.valueOf(dbConfig.getMaxConnection()));
        proxoolDataSource.setMinimumConnectionCount(Integer.valueOf(dbConfig.getMinConnection()));
        proxoolDataSource.setMaximumConnectionLifetime(Integer.valueOf(dbConfig.getMaxLifetime()));
        proxoolDataSource.setTestBeforeUse(true);
        proxoolDataSource.setSimultaneousBuildThrottle(Integer.valueOf(dbConfig.getSimu()));
        //如果一条sql的执行时间 超过这个值 则会kill掉
        proxoolDataSource.setMaximumActiveTime(60*1000*30);
        //连接的存活时间
        //proxoolDataSource.setMaximumConnectionLifetime(Integer.parseInt(dbConfig.getMaxLifetime()));
        proxoolDataSource.setHouseKeepingTestSql(dbConfig.getHouseKeepingSql());
        proxoolDataSource.setStatistics(dbConfig.getStatistics());
        proxoolDataSource.setAlias(dbConfig.getDbName());
        Connection connection = null;
        try {
            connection = proxoolDataSource.getConnection();
        } catch (SQLException e) {
            log.error("error" + e.getMessage(), e);
        } finally {
            DBUtil.closeConnection(connection);
        }

    }

    /**
     * 当前数据源已经激活的连接数量
     */
    public int getActiveConnection() {
        int count = 0;
        try {
            count = ProxoolFacade.getSnapshot(proxoolDataSource.getAlias()).getActiveConnectionCount();
        } catch (ProxoolException e) {
            log.error("error" + e.getMessage(), e);
        }
        return count;
    }

    /**
     * 当前数据源最大的连接数量
     */
    public int getMaxConnection() {
        int count = 0;

        try {
            count = ProxoolFacade.getSnapshot(proxoolDataSource.getAlias()).getMaximumConnectionCount();
        } catch (ProxoolException e) {
            log.error("error" + e.getMessage(), e);
        }
        return count;
    }

    public int compare(IDataSourceProvider o1, IDataSourceProvider o2) {
        log.info("connection1 count = +++++++++++++++++++++++++++" + o1.getActiveConnection());
        log.info("connection2 count = ++++++++++++++++++++++++++" + o2.getActiveConnection());
        return Integer.valueOf(o1.getActiveConnection()).compareTo(Integer.valueOf(o2.getActiveConnection()));
    }
    
    /**
     * jdbc 以不指定库的形式拼接连接
     */
    private static String getDBUrl(String ip, String port, String useCursorFetch,String dbName) {
        StringBuilder builder = new StringBuilder("jdbc:mysql://");
        builder.append(ip).append(":").append(port).append("/").append(dbName).append("?characterEncoding=UTF8&useCursorFetch=" + useCursorFetch);
        log.warn("DBUrl:" + builder.toString());
        return builder.toString();
    }
}
