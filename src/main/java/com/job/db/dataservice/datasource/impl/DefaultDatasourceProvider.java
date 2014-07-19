package com.job.db.dataservice.datasource.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.job.db.dataservice.datasource.IDataSourceProvider;

/**
 * Proxool 连接池提供工厂类
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:14:08
 */
public class DefaultDatasourceProvider implements IDataSourceProvider {

    private static Logger log = Logger.getLogger(DefaultDatasourceProvider.class);

    private DataSource dataSource;

    
    public DefaultDatasourceProvider( DataSource dataSource){
    	this.dataSource = dataSource;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }

    public void shutdown() {
    	log.info("shutdown");
    }
    
}
