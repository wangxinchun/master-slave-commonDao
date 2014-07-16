package com.job.db.dataservice.connection.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.job.db.dao.AppDBContext;
import com.job.db.dataservice.connection.ConnectionGetter;
import com.job.db.dataservice.datasource.DataSourceContext;
import com.job.db.dataservice.datasource.IDataSourceProvider;
import com.job.db.dataservice.exception.DaoException;

/**
 * 提供根据dbName 获取读或写连接
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:26:27
 */
public abstract class AbstractConnectionGetter implements ConnectionGetter {
	
	private static Integer concount =Integer.MIN_VALUE;
	
    public Connection getDefaultConnection(String dbName, boolean isWrite) throws SQLException {
        if (dbName == null) {
            throw new DaoException("get connection error cause dbName id null");
        }
		Integer id = AppDBContext.getLastReadWriteFlag();
		// 写操作直接获得connection ,如果上一次操作是写连接，那么优先返回写连接
		if ( isWrite || (id != null && id < 0)) {
			AppDBContext.setLastReadWriteFlag(-1);
            IDataSourceProvider provider = DataSourceContext.getMasterDataSourceProvider(dbName);
            if (provider == null) {
                throw new DaoException("no write database dbName is " + dbName);
            }
            return provider.getDataSource().getConnection();
        } else {
			// 读操作取模做balance 负载均衡
            List<IDataSourceProvider> providers = DataSourceContext.getSlaveDataSourceProvider(dbName);
            if (providers == null || providers.size() == 0) {
                throw new DaoException("no slave database for clientId " + dbName);
            }
            int psize = providers.size();
			if (id == null) {
				id = Math.abs(concount++ % psize);
				AppDBContext.setLastReadWriteFlag(id);
			}
			IDataSourceProvider sdp = providers.get(id % psize);
            return sdp.getDataSource().getConnection();
        }
    }
}
