package com.job.db.dataservice.connection.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.job.db.dataservice.exception.DaoException;

/**
 * 返回数据库(Master)的写连接
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:33:57
 */
public class WriteConnectionGetter extends AbstractConnectionGetter {
    
    private static Logger log = Logger.getLogger(WriteConnectionGetter.class);

    public WriteConnectionGetter() {
    }
    
    public Connection getConnection(String dbName) {
        Connection connection = null;
        try {
            connection = getDefaultConnection(dbName, true);
        } catch (SQLException e) {
            log.error("get connection error: dbName=" + dbName, e);
            throw new DaoException("get connection error ", e);
        }
        return connection;
    }
}
