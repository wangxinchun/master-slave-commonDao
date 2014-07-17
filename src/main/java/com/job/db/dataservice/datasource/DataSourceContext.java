package com.job.db.dataservice.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.job.db.dataservice.datasource.MasterSlaveConfigMapping.MasterSlaveConfigMappingItem;
import com.job.db.dataservice.datasource.impl.ProxoolDatasourceProvider;

/**
 * 数据源上下文
 * @author wangxinchun1988@163.com
 * @date 2014-7-14下午5:02:14
 */
public  class DataSourceContext {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceContext.class);

    /**
     * 主库数据源     (ip+port->IDataSourceProvider)*/
    public  static final Map<String, IDataSourceProvider> masterMap = new HashMap<String, IDataSourceProvider>();
    /** 主库数据库名称到ip+port的映射 (dbNmae->ip+port)*/
    public  static final Map<String,String> masterDb2IpPortMapping = new HashMap<String,String>();
    
    /** 从库数据源  (ip+port->IDataSourceProvider)*/
    public  static  final Map<String, IDataSourceProvider> slaveMap = new HashMap<String, IDataSourceProvider>();
    /** 从库数据库名称到ip+port的映射*/
    public  static final Map<String,List<String>> slaveDb2IpPortListMapping = new HashMap<String,List<String>>();
    
    /** 主从数据源列表配置*/
    private MasterSlaveConfigMapping masterSlaveConfigMapping;
    
    /**
     * 初始化资源
     */
    public void init(){
    	logger.info("DataSourceContext init begin");
    	List<MasterSlaveConfigMappingItem> mappingList = masterSlaveConfigMapping.getMappingList();
    	for(MasterSlaveConfigMappingItem item : mappingList){
    		DataSourceConfig masterConfigList = item.getMaster();
    		doMasterInit(masterConfigList,masterMap);
    		
    		List<DataSourceConfig> slaveConfigList = item.getSlaveList();
    		doSlaveInit(slaveConfigList,slaveMap);
    	}
    	logger.info("DataSourceContext init end");
    }
    
    /** 初始化主库资源*/
    private final void doMasterInit(DataSourceConfig config, Map<String, IDataSourceProvider> masterMap) {
    	String key = config.getIp() + config.getPort();
    	if(StringUtils.isNotEmpty(config.getDbName())){
    		masterDb2IpPortMapping.put(config.getDbName(), key);
    	}
    	if(masterMap.get(key) != null){
    		return;
    	}
    	IDataSourceProvider dataSourceProvider= new ProxoolDatasourceProvider();
    	logger.info("DataSourceContext doMasterInit {}",config);
    	dataSourceProvider.config(config);
    	masterMap.put(key, dataSourceProvider);
    }
    
    /** 初始化从库资源*/
    private final void doSlaveInit(List<DataSourceConfig> configList, Map<String, IDataSourceProvider> slaveMap) {
    	for(DataSourceConfig config : configList){
    		String key = config.getIp() + config.getPort();
    		if(StringUtils.isNotEmpty(config.getDbName())){
    			if(slaveDb2IpPortListMapping.get(config.getDbName()) == null){
    				slaveDb2IpPortListMapping.put(config.getDbName(), new ArrayList<String>());
    			}
    			slaveDb2IpPortListMapping.get(config.getDbName()).add(key);
    		}
        	if(masterMap.get(key) != null){
        		continue;
        	}
    		IDataSourceProvider dataSourceProvider= new ProxoolDatasourceProvider();
    		logger.info("DataSourceContext doSlaveInit {}",config);
    		dataSourceProvider.config(config);
    		if(slaveMap.get(key) == null){
    			slaveMap.put(key, dataSourceProvider);
    		}
    	}
    }
    
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

    
    public static IDataSourceProvider getMasterDataSourceProvider(String dbName){
    	String ipPortKey = masterDb2IpPortMapping.get(dbName);
    	if(ipPortKey == null){
    		return null;
    	} else {
    		return masterMap.get(ipPortKey);
    	}
    }
    
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
    
	public void setMasterSlaveConfigMapping(
			MasterSlaveConfigMapping masterSlaveConfigMapping) {
		this.masterSlaveConfigMapping = masterSlaveConfigMapping;
	}
    

    
}
