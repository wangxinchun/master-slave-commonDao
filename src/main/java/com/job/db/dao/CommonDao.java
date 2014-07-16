package com.job.db.dao;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.job.db.dataservice.exception.DaoException;
import com.job.db.utils.BeanMaker;
import com.job.db.utils.BeanUtil;
import com.job.db.utils.CloseUtil;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

/**
 *  应用程序调用入口
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午5:14:25
 */
public class CommonDao {
	private static Logger log = Logger.getLogger(CommonDao.class);

	private static Field rowDataField = null;
	static {
		try {
			rowDataField = ResultSetImpl.class.getDeclaredField("rowData");
			rowDataField.setAccessible(true);
		} catch (Exception e) {
			log.error("get rowData field fail", e);
		}
	}
	
	private CommonDao(){
		
	}
	
    private boolean updateForSql(String sql, String tableId, Object[] params) {
        return executeSql(sql, params,tableId);
    }

	private boolean updateForSql(String sql, String taleId) {
        return executeSql(sql, null,taleId);
    }
	
	private int updateWithNumberForSql(String sql,Object[] params, String taleId) {
        return executeWithNumberSql(sql, params,taleId);
    }


    private long insertForSql(String sql, String tableId, Object[] params) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            return -1;
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (params != null && params.length != 0) {
                int index = 1;
                for (Object param : params) {
                    preparedStatement.setObject(index++, param);
                }
            }
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int id = -1;
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            return id;
        } catch (SQLException e) {
        	
        	String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("update error", e);
        } finally {
            CloseUtil.closePs(preparedStatement);
        }
    }
    
    public boolean insertForTransfer(String sql, String tableId) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            return false;
        }
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
        	String msg = "sql=" +sql;
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("insert for transfer error", e);
        } finally {
            CloseUtil.closePs(preparedStatement);
        }
        return false;
    }
    
    private long insertForSql(String sql, String tableId) {
        Integer[] ids = insertBatch(new String[]{sql}, tableId);
        if(ids==null||ids.length==0) return 0;
        else  return ids[0];
    }
    
	@Transactional( value = "txManager",rollbackFor = Exception.class)
    public void updateBatch(String[] sql, String tableId) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        Statement statement = null;
        try {
             statement = connection.createStatement();

            for (String s : sql) {
                statement.addBatch(s);

            }
            statement.executeBatch();
        } catch (SQLException e) {
        	
        	String msg = "sql=" +Arrays.toString(sql);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
        	
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId +  ", " + msg, e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                log.error("rollback error" + e1.getMessage(), e);
            }
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }

    }

    public void updateInsuranceBatch(String[] sql, String tableId) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        Statement statement = null;
        try {
             statement = connection.createStatement();

            for (String s : sql) {
                statement.addBatch(s);

            }
            statement.executeBatch();
        } catch (Exception e) {
        	
        	String msg = "sql=" +Arrays.toString(sql);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
        	
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId +  ", " + msg, e);
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }

    }
    @Transactional( value = "txManager",rollbackFor = Exception.class)
    public Integer[] insertBatch(String[] sql, String tableId) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        Statement statement = null;
        try {
             statement = connection.createStatement();

            for (String s : sql) {
                statement.addBatch(s);
            }
            statement.executeBatch();
            ResultSet resultSet = statement.getGeneratedKeys();
            List<Integer> list = new ArrayList<Integer>();
            while (resultSet.next()) {
                list.add(resultSet.getInt(1));
            }
            Integer[] ids = list.toArray(new Integer[0]);
            return ids;
        } catch (SQLException e) {
        	String msg = "sql=" + Arrays.toString(sql);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }

    }
    
    @Transactional( value = "txManager",rollbackFor = Exception.class)
    public Integer[] insertBatchForSplit(String[] sql, String tableId) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        Statement statement = null;
        try {
             statement = connection.createStatement();

            for (String s : sql) {
                statement.addBatch(s);

            }
            statement.executeBatch();
            ResultSet resultSet = statement.getGeneratedKeys();
            List<Integer> list = new ArrayList<Integer>();
            while (resultSet.next()) {
                list.add(resultSet.getInt(1));
            }
            Integer[] ids = list.toArray(new Integer[0]);
            return ids;
        } catch (SQLException e) {
        	String msg = "sql=" + Arrays.toString(sql);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            try { 
                connection.rollback();
            } catch (SQLException e1) {
                log.error("rollback error" + e1.getMessage(), e1);
            }
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }

    }
    
	public int delete(String sql, String tableId) {
		String[] sqls = new String[]{sql};
		int result[] = batchDelete(sqls, tableId);
		return result == null || result.length == 0 ? 0 : result[0];
	}
    
	@Transactional( value = "txManager",rollbackFor = Exception.class)
    public int[] batchDelete(String[] sql, String tableId) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        Statement statement = null;
        try {
             statement = connection.createStatement();

            for (String s : sql) {
                statement.addBatch(s);

            }
            int[] result=statement.executeBatch();
           
           return result;
        } catch (SQLException e) {
        	String msg = "sql=" + Arrays.toString(sql);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("batch delete error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
        
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }

    }
	@Transactional( value = "txManager",rollbackFor = Exception.class)
    public int[] insertBatch(String sql, String tableId ,Object[][] datas) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (Object[] data : datas) {
            	for(int i=0;i<data.length;i++){
            		statement.setObject(i+1, data[i]);
            	}
                statement.addBatch();
            }
            int[] result=statement.executeBatch();
            return result;
        } catch (SQLException e) {
        	String msg = "sql=" +sql + ", datas.length==" + datas.length;
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }
    }
	@Transactional( value = "txManager",rollbackFor = Exception.class)
    public int[] insertBatchSupportTransacation(String sql, String tableId ,Object[][] datas) {
        Connection connection = AppDBContext.getWCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (Object[] data : datas) {
            	for(int i=0;i<data.length;i++){
            		statement.setObject(i+1, data[i]);
            	}
                statement.addBatch();
            }
            int[] result=statement.executeBatch();
            return result;
        } catch (SQLException e) {
        	String msg = "sql=" +sql + ", datas.length==" + datas.length;
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }
    }
	
    public int[] insertInsuranceBatch(String sql, String tableId ,Object[][] datas) {
        Connection connection = AppDBContext.getWCon(tableId);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (Object[] data : datas) {
            	for(int i=0;i<data.length;i++){
            		statement.setObject(i+1, data[i]);
            	}
                statement.addBatch();
            }
            int[] result=statement.executeBatch();
            return result;
        } catch (Exception e) {
        	String msg = "sql=" +sql + ", datas.length==" + datas.length;
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("batch insert error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("batch insert error ", e);

        } finally {
            CloseUtil.closeSt(statement);
        }
    }
    private boolean executeSql(String sql, Object[] params,String tableId) {
    	Connection connection = AppDBContext.getWCon(tableId);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null && params.length != 0) {
                int index = 1;
                for (Object param : params) {
                    preparedStatement.setObject(index++, param);
                }
            }
            int i = preparedStatement.executeUpdate();
            if (i == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
        	String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("db error " + ", tableId=" + tableId + ", " + msg , e);
            throw new DaoException("update error", e);
        } finally {
            CloseUtil.closePs(preparedStatement);
        }

    }
    
    private int executeWithNumberSql(String sql, Object[] params,String tableId) {
    	Connection connection = AppDBContext.getWCon(tableId);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null && params.length != 0) {
                int index = 1;
                for (Object param : params) {
                    preparedStatement.setObject(index++, param);
                }
            }
             return preparedStatement.executeUpdate();
        } catch (Exception e) {
        	String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("db error " + ", tableId=" + tableId + ", " + msg , e);
            throw new DaoException("update error", e);
        } finally {
            CloseUtil.closePs(preparedStatement);
        }

    }
    
    public <T> T findObject(String sql, String tableId, Class<T> clz) {
    	return findForObject(sql, tableId, clz);
    }

    public <T> T findObject(String sql, Class<T> clz) {
    	return findForObject(sql, null, clz);
    }
    
    public <T> T findObject(String sql, String tableId, Object[] params, Class<T> clz) {
    	return findForObject(sql, tableId, params, clz);
    }

    public <T> T findObject(String sql, Object[] params, Class<T> clz) {
    	return findForObject(sql, null, params, clz);
    }
    
    public Map<String, Object> findMap(String sql, String tableId) {
    	return findForOneMap(sql, tableId);
    }
    
    public Map<String, String> findStrMap(String sql) {
    	return findForOneStrMap(sql);
    }

    public Map<String, String> findStrMap(String sql, Object[] params) {
    	return findForOneStrMap(sql,null,params);
    }

    public Map<String, Object> findMap(String sql, String tableId, Object[] params) {
    	return findForOneMap(sql, params, tableId);
    }
    
    public Map<String, String> findMap(String sql, Object[] params) {
    	return findForOneStrMap(sql,null, params);
    }

    public Map<String, String> findStrMap(String sql,String tableId, Object[] params) {
    	return findForOneStrMap(sql,tableId, params);
    }
    
    public <T> List<T> findList(String sql, String tableId, Class<T> clz,int transactionType) {  
    		return findForListObject(sql, tableId, clz,transactionType);   	
    }

    public <T> List<T> findList(String sql, String tableId, Class<T> clz) {
    	return findForListObject(sql, tableId, clz,-1);
    }
    
    public <T> List<T> findList(String sql, String tableId, Object[] params, Class<T> clz) {
    	return findForListObject(sql, tableId, params, clz ,-1);
    }
    
    public List<Map<String, Object>> findListMap(String sql, String tableId) {
    	return findForListMap(sql, tableId);
    }
    
    public List<Map<String, String>> findListStrMap(String sql) {
    	return findForListStrMap(sql);
    }

    public List<Map<String, String>> findListStrMap(String sql,String tableId) {
    	return findForListStrMap(sql,tableId,null);
    }
    
    public List<Map<String, String>> findListStrMap(String sql,Object[] params) {
    	return findForListStrMap(sql,null,params);
    }
    public List<Map<String, Object>> findListMap(String sql, String tableId, Object[] params) {
    	return findForListMap(sql, params, tableId);
    }
    
    public List<Map<String, String>> findStrListMap(String sql,String tableId, Object[] params) {
    	return findForListStrMap(sql,tableId, params);
    }
    
    public List<Map<String, String>> findStrListMap(String sql, Object[] params) {
    	return findForListStrMap(sql,null, params);
    }

    public Object findResult(String sql, String tableId, IResultGetter resultGetter) {
    	return findFromIResult(sql, tableId, resultGetter);
    }

    public boolean update(String sql, String tableId) {
    	return updateForSql(sql, tableId);
    }
    /*
     * 更新语句返回影响条数
     */
    public int updateWithNumber(String sql,Object[] params,String tableId)
    {
    	return updateWithNumberForSql(sql,params,tableId);
    }
    
    public int updateWithNumber(String sql,String tableId)
    {
    	return updateWithNumberForSql(sql,null,tableId);
    }

    public long insert(String sql, String tableId) {
    	return insertForSql(sql, tableId);
    }

    public long insert(String sql, String tableId, Object[] params) {
    	return insertForSql(sql, tableId, params);
    }

    public long insert(String sql, Object[] params) {
    	return insertForSql(sql, null, params);
    }

    public boolean update(String sql, String tableId, Object[] params) {
    	return updateForSql(sql, tableId, params);
    }
    
    public boolean update(String sql,Object[] params) {
    	return updateForSql(sql, null, params);
    }

    
    private Map<String, Object> findForOneMap(String sql, Object[] paras, String tableId) {
        List<Map<String, Object>> map = findForListMap(sql, paras, tableId);
        if (map.size() == 0) {
            return new HashMap<String, Object>();
        }
        return map.get(0);
    }
    
    private Map<String, String> findForOneStrMap(String sql,String tableId, Object[] paras) {
        List<Map<String, String>> map = findForListStrMap(sql,tableId, paras);
        if (map.size() == 0) {
            return new HashMap<String, String>();
        }
        return map.get(0);
    }

    private Map<String, Object> findForOneMap(String sql, String tableId) {
        List<Map<String, Object>> forListMap = findForListMap(sql, tableId);
        if (forListMap.size() == 0) {
            return new HashMap<String, Object>();
        }
        return forListMap.get(0);

    }
    
    private Map<String, String> findForOneStrMap(String sql) {
        List<Map<String, String>> forListMap = findForListStrMap(sql);
        if (forListMap.size() == 0) {
            return new HashMap<String, String>();
        }
        return forListMap.get(0);

    }

    private List<Map<String, Object>> findForListMap(String sql, Object[] paras, String tableId) {
        Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        PreparedStatement statement = null;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            statement = connection.prepareStatement(sql);
            if (paras != null && paras.length != 0) {
                int index = 1;
                for (Object para : paras) {
                    statement.setObject(index++, para);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData data = resultSet.getMetaData();
            int count = data.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= count; i++) {                   
                    String key = data.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    map.put(key, value);
                }
                list.add(map);
            }
        } catch (SQLException e) {
        	String msg = "sql=" +sql + ", params=" + Arrays.toString(paras);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("query " + sql + "error", e);
        } finally {
            CloseUtil.closePs(statement);
        }

        return list;
    }
    
    private List<Map<String, String>> findForListStrMap(String sql,String tableId, Object[] paras) {
    	Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        if(connection == null ){
            throw new DaoException("get connection error ");
        }
        PreparedStatement statement = null;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            statement = connection.prepareStatement(sql);
            if (paras != null && paras.length != 0) {
                int index = 1;
                for (Object para : paras) {
                    statement.setObject(index++, para);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData data = resultSet.getMetaData();
            int count = data.getColumnCount();
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 1; i <= count; i++) {
                    map.put(data.getColumnLabel(i), resultSet.getString(i));
                }
                list.add(map);
            }

        } catch (SQLException e) {
        	String msg = "sql=" +sql + ", params=" + Arrays.toString(paras);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
        	
            log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("query " + sql + "error", e);
        } finally {
            CloseUtil.closePs(statement);
        }
        return list;
    }

    private List<Map<String, Object>> findForListMap(String sql, String tableId) {
        return findForListMap(sql, null, tableId);

    } 
    
    private List<Map<String, String>> findForListStrMap(String sql) {
        return findForListStrMap(sql, null,null);
    }
    
    private <T> T findForObject(String sql, String tableId, Class<T> clz) {
        List<T> list = findForListObject(sql, tableId, clz,-1);
		if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    private <T> T findForObject(String sql, String tableId, Object[] params, Class<T> clz) {
        List<T> list = findForListObject(sql, tableId, params, clz,-1);
		if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    private <T> List<T> findForListObject(String sql, String tableId, Class<T> clz,int transactionType) {
        return findForListObject(sql, tableId, null, clz,transactionType);
    }

    /**
     * 注意在超时的时候，会返回null
     * @param sql
     * @param tableId
     * @param params
     * @param clz
     * @param transactionType
     * @return
     */
    private <T> List<T> findForListObject(String sql, String tableId, Object[] params, Class<T> clz ,int transactionType) {
        List<T> list = new LinkedList<T>();
        Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        if(connection == null ){
            return list;
        }
    
        PreparedStatement statement = null;
        try {
        	//添加事务级别
        	if(transactionType != -1){
        		connection.setTransactionIsolation(transactionType);
        	}
            statement = connection.prepareStatement(sql);
            if (params != null && params.length != 0) {
                int index = 1;
                for (Object param : params) {
                    statement.setObject(index++, param);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData data = resultSet.getMetaData();
            int count = data.getColumnCount();
            BeanUtil beanUtil = new BeanUtil(clz);
            while (resultSet.next()) {
                T inetance = BeanMaker.makeBeanByClass(clz);
                for (int i = 1; i <= count; i++) {
                    String s = data.getColumnLabel(i);
                    Object o = resultSet.getObject(i);
                    if(s.equalsIgnoreCase("journey_join")){
                    	s = "journeyJoin";
                    }
                    beanUtil.setValue(inetance, s, o);
                }
                list.add(inetance);
            }
        } catch (SQLException e) {
        	  if(e  instanceof MySQLTimeoutException){
        			if(transactionType != -1){
        				return null;
        			}
              }else{
              }
	          String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
	          if (msg.length() > 2000) {
	        		msg = msg.substring(0, 2000);
	          }
	          log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
        } finally {
            CloseUtil.closePs(statement);  
    		try {
    			if(transactionType != -1){
    				connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    			}
			} catch (SQLException e) {
				log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + e.getMessage(), e);
			}
        	
        }
        return list;
    }
    
    public List<Object> findOneColumn(String sql, String tableId, Object[] params) {
    	List<Object> ret = new ArrayList<Object>();
    	Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        if(connection == null ){
           return ret;
        }
    	PreparedStatement statement = null;
    	try {
    		statement = connection.prepareStatement(sql);
    		if (params != null && params.length != 0) {
    			int index = 1;
    			for (Object param : params) {
    				statement.setObject(index++, param);
    			}
    		}
    		ResultSet resultSet = statement.executeQuery();
    		while (resultSet.next()) {
    			ret.add(resultSet.getObject(1));
    		}
    	} catch (SQLException e) {
    		String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
        	
    		log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
    	} finally {
    		CloseUtil.closePs(statement);
    	}
    	return ret;
    }

    
    public  long findValue(String sql, String tableId, Object[] params) {
    	Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        if(connection == null){
            return -1;
        }
    	PreparedStatement statement = null;

    	try {
    		statement = connection.prepareStatement(sql);
    		if (params != null && params.length != 0) {
    			int index = 1;
    			for (Object param : params) {
    				statement.setObject(index++, param);
    			}
    		}
    		ResultSet resultSet = statement.executeQuery();
            long id = -1;
            if (resultSet.next()) {
                Object idObj = resultSet.getObject(1);
                if(idObj instanceof Integer) {
                	id = (Integer)idObj; 
                }else if (idObj instanceof Long) {
        			id = (Long) idObj;
                } else if (idObj instanceof BigDecimal) {
                    id = ((BigDecimal) idObj).longValueExact();
                }
            }
            return id;
    	} catch (SQLException e) {
    		String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
        	
    		log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
    		return -1;
    	} finally {
    		CloseUtil.closePs(statement);
    	}

    }    
    
    public double findDoublueValue(String sql, String tableId, Object[] params) {
    	Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            if (params != null && params.length != 0) {
                int index = 1;
                for (Object param : params) {
                    statement.setObject(index++, param);
                }
            }
            ResultSet resultSet = statement.executeQuery();
            double id = -1;
            if (resultSet.next()) {
                Object idObj = resultSet.getObject(1);
                if(idObj instanceof Integer) {
                    id = (Integer)idObj; 
                }else if (idObj instanceof Long) {
                    id = (Long) idObj;
                }else if (idObj instanceof BigDecimal) {
                    id = ((BigDecimal) idObj).doubleValue();
                }else if (idObj instanceof Double) {
                    id = (Double)idObj;
                }
            }
            return id;
        } catch (SQLException e) {
            String msg = "sql=" +sql + ", params=" + Arrays.toString(params);
            if (msg.length() > 2000) {
                msg = msg.substring(0, 2000);
            }
            
            log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            return -1;
        } finally {
            CloseUtil.closePs(statement);
        }
    }    
    
    private Object findFromIResult(String sql, String tableId, IResultGetter resultGetter) {
    	Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(tableId):AppDBContext.getRCon(tableId);
        if(connection == null){
            new DaoException("get connection error");
        }
        Statement statement = null;
        Object o = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            o = resultGetter.getResultFormRs(resultSet);

        } catch (SQLException e) {
        	String msg = "sql=" +sql;
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("error" + e.getMessage() + ", tableId=" + tableId + ", " + msg, e);
            throw new DaoException("query " + sql + "error", e);
        } finally {
            CloseUtil.closeSt(statement);
        }

        return o;
    }

	public static String buildWhereFromConditions(List<String> conditions) {
		if (conditions == null || conditions.isEmpty())
			return "";

		StringBuilder where = new StringBuilder(" WHERE ");
		boolean first = true;
		for (String p : conditions) {
			if (first)
				first = false;
			else
				where.append(" AND ");
			where.append(p);
		}
		return where.toString();
	}
	
	public List<String> getTableColumns(String table,String clientId) {
		Connection connection = AppDBContext.IS_MUST_READ_MASTER_MACHINE?AppDBContext.getWCon(clientId):AppDBContext.getRCon(clientId);
        if(connection == null){
            new DaoException("get connection error");
        }
        PreparedStatement statement = null;
        List<String> ret = new LinkedList<String>();
        String sql = "select * from " + table + " where 1=0";
        try {
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData data = resultSet.getMetaData();
            int count = data.getColumnCount();
            for (int i = 1; i <= count; ++i) {
            	String column = data.getColumnLabel(i); 
            	if("join".equals(column)){
            		column = "`" +  column + "`";
            	}
        		ret.add(column);
        	}
        } catch (SQLException e) {
        	String msg = "sql=" +sql;
        	if (msg.length() > 2000) {
        		msg = msg.substring(0, 2000);
        	}
            log.error("error" + e.getMessage() + ", tableId=" + clientId + ", " + msg, e);
            throw new DaoException("query " + sql + "error", e);
        } finally {
            CloseUtil.closePs(statement);
        }
        return ret;
	}
	
	public List<String> getTableColumnsWithoutId(String table,String clientId) {
        List<String> allColumnNames = getTableColumns(table, clientId);
        allColumnNames.remove("id");
        return allColumnNames;
	}
	
	public String getStrOfTableColumnsWithoutId(String clientId,String table) {
		return StringUtils.join(getTableColumnsWithoutId(table,clientId), ",");
	}
    
	
}
