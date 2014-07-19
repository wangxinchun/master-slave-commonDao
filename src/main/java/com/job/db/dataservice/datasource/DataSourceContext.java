package com.job.db.dataservice.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.job.db.dataservice.datasource.MasterSlaveDataSourceMapping.MasterSlaveDataSourceMappingItem;

/**
 * 数据源上下文
 * @author wangxinchun1988@163.com
 * @date 2014-7-14下午5:02:14
 */
public abstract class DataSourceContext {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceContext.class);
    /**
     * 主库数据源     (ip+port->IDataSourceProvider)*/
    protected  static final Map<String, IDataSourceProvider> masterMap = new HashMap<String, IDataSourceProvider>();
    /** 主库数据库名称到ip+port的映射 (dbNmae->ip+port)*/
    protected  static final Map<String,String> masterDb2IpPortMapping = new HashMap<String,String>();
    
    /** 从库数据源  (ip+port->IDataSourceProvider)*/
    protected  static  final Map<String, IDataSourceProvider> slaveMap = new HashMap<String, IDataSourceProvider>();
    /** 从库数据库名称到ip+port的映射*/
    protected  static final Map<String,List<String>> slaveDb2IpPortListMapping = new HashMap<String,List<String>>();
    
    /** 主从数据源列表配置*/
    private MasterSlaveDataSourceMapping masterSlaveDataSourceMapping;
    
    /**
     * 初始化资源
     */
    public void init(){
    	logger.info("DataSourceContext init begin");
    	List<MasterSlaveDataSourceMappingItem> masterSlaveList = masterSlaveDataSourceMapping.getList();
    	for(MasterSlaveDataSourceMappingItem item : masterSlaveList){
    		DataSource masterConfigList = item.getMaster();
    		masterInit(masterConfigList);
    		
    		List<DataSource>  slaveConfigList = item.getSlaveList();
    		slaveInit(slaveConfigList);
    	}
    	logger.info("DataSourceContext init end");
    }
    
    /** 初始化主库资源*/
    private final void masterInit(DataSource dataSource) {
    	doMasterInit(dataSource);
    }
    
    /** 初始化从库资源*/
    private final void slaveInit(List<DataSource> dataSourceList) {
    	for(DataSource dataSource : dataSourceList){
    		doSlaveInit(dataSource);
    	}
    }
    
    protected abstract void doMasterInit(DataSource dataSource);
    
    protected abstract void doSlaveInit(DataSource dataSource);
    
    /**
     * 关闭资源
     */
    public void shutDown() {
    	logger.info("DataSourceContext shutDown begin ");
        for (IDataSourceProvider provider : masterMap.values()) {
            provider.shutdown();
        }
        Collection<IDataSourceProvider> listCollection = slaveMap.values();
        for (IDataSourceProvider provider: listCollection) {
                provider.shutdown(); 
        }
        logger.info("DataSourceContext shutDown end ");
    }

    /** 根据dbname获取主库数据源*/
    public static IDataSourceProvider getMasterDataSourceProvider(String dbName){
    	String ipPortKey = masterDb2IpPortMapping.get(dbName);
    	if(ipPortKey == null){
    		return null;
    	} else {
    		return masterMap.get(ipPortKey);
    	}
    }
    
    /** 根据数据库的名字获取多个从库数据源列表*/
    public static  List<IDataSourceProvider> getSlaveDataSourceProvider(String dbName){
    	List<String> ipPortKeyList = slaveDb2IpPortListMapping.get(dbName);
    	if(ipPortKeyList == null){
    		return null;
    	} else {
    		List<IDataSourceProvider> retList = new ArrayList<IDataSourceProvider>();
    		for(String item : ipPortKeyList){
    			IDataSourceProvider provider = slaveMap.get(item);
    			retList.add(provider);
    		}
    		return retList;
    	}
    }

	public void setMasterSlaveDataSourceMapping(
			MasterSlaveDataSourceMapping masterSlaveDataSourceMapping) {
		this.masterSlaveDataSourceMapping = masterSlaveDataSourceMapping;
	}
    

    
}
