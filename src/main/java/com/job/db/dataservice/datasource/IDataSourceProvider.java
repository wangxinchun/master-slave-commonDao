package com.job.db.dataservice.datasource;

import java.util.Comparator;

import javax.sql.DataSource;


/**
 * 该接口提供datasource的连接池
 * @author wangxinchun1988@163.com
 * @date 2014-7-15下午2:15:50
 */
public interface IDataSourceProvider extends Comparator<IDataSourceProvider>{

    /**
     * 获得datasource
     * @return
     */
    public DataSource getDataSource();

    /**
     * 关闭连接池
     */
    public void shutdown();

    /**
     * 初始化连接池
     * @param dbConfig
     */
    public void config(DataSourceConfig dbConfig);

    /**
     * 活动的连接
     * @return
     */
    public int getActiveConnection();

    /**
     * 最大连接数
     * @return
     */
    public int getMaxConnection();
    
}
