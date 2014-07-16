package com.job.db.dataservice.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SmartDataSource;

import com.job.db.dao.AppDBContext;

/**
 *  基于spring管理的事务的数据库连接获取
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午5:16:22
 */
public class AppContextDatasource implements SmartDataSource {
	private static final Logger logger = LoggerFactory.getLogger(AppContextDatasource.class);
	
	public AppContextDatasource() {
		logger.warn("create AppContextDatasource");
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		Connection con;
		//直接从当前线程的写连接获取
		con =  AppDBContext.getWCon(null);
		logger.info("get Connection: con=" + con);
		return con;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		logger.warn("not support");
		throw new SQLException("not support");
	}

	@Override
	public boolean shouldClose(Connection con) {
		logger.warn("should close Connection: con=" + con);
		return false;
	}


	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
}

}
