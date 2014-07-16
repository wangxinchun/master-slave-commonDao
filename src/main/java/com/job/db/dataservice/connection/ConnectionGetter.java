package com.job.db.dataservice.connection;

import java.sql.Connection;

/**
 * 提供Connection连接
 * @author wangxinchun
 */
public interface ConnectionGetter {

	/**
	 * 根据dbName获取数据库连接
	 * @param dbName 数据库的名字
	 */
    public Connection getConnection(String dbName);

}
