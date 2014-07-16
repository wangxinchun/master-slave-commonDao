package com.job.db.dataservice.connection.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.job.db.dataservice.exception.DaoException;

/**
 * 返回read连接（返回从库的数据库连接）
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:33:11
 */
public class ReadConnectionGetter extends AbstractConnectionGetter {

    private static Logger log = Logger.getLogger(ReadConnectionGetter.class);


    public Connection getConnection(String dbName) {
        Connection connection = null;
        try {
            connection = getDefaultConnection(dbName, false);
        } catch (SQLException e) {
            log.error("get connection error" + e.getMessage(),e);
            throw new DaoException("get connection error ", e);
        }
        
        return connection;

    }
}
