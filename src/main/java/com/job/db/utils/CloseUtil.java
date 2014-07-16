package com.job.db.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关闭资源
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午3:57:22
 */
public final class CloseUtil {
	private static final Logger logger = LoggerFactory.getLogger(CloseUtil.class);
    
	private CloseUtil() {

    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            	logger.error("error message {}" , e.getMessage(), e);
            }
        }
    }

    public static void closePs(Statement statement) {
        try { 
        	if (statement != null && !statement.isClosed()) {
            	statement.close();
            }
        } catch (SQLException e) {
        	logger.error("error message {}" , e.getMessage(), e);
        }
    }

    public static void closeSt(Statement statement) {
        try {
        	if (statement != null&& !statement.isClosed()) {
                statement.close();
        	}
         } catch (SQLException e) {
        	 logger.error("error message {}" , e.getMessage(), e);
        }
    }

	public static void closeRs(ResultSet resultSet) {
	
		try {
			if(null!=resultSet&&!resultSet.isClosed()){
				resultSet.close();
			}
		} catch (SQLException e) {
			logger.error("error message {}" , e.getMessage(), e);
		}
		
	}
}
