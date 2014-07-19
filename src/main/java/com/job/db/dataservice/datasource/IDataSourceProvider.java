package com.job.db.dataservice.datasource;

import javax.sql.DataSource;


/**
 * 该接口提供datasource的连接池
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:15:50
 */
public interface IDataSourceProvider {

    /**
     * 获得datasource
     * @return
     */
    public DataSource getDataSource();

    /**
     * 关闭连接池
     */
    public void shutdown();

}
