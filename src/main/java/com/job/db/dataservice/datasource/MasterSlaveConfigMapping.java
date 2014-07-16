package com.job.db.dataservice.datasource;

import java.util.List;


/**
 * 主从库配置信息
 * 允许配置多个主从库列表信息
 * @author wangxinchun1988@163.com
 * @date 2014-7-14下午4:53:40
 */
public class MasterSlaveConfigMapping {
	
	private List<MasterSlaveConfigMappingItem> mappingList;
	
	
	public List<MasterSlaveConfigMappingItem> getMappingList() {
		return mappingList;
	}


	public void setMappingList(List<MasterSlaveConfigMappingItem> mappingList) {
		this.mappingList = mappingList;
	}


	/**
	 * 主从库配置项（一个主库对应多个从库）
	 * @author wangxinchun1988@163.com
	 * @date 2014-7-14下午4:52:46
	 */
	public static class MasterSlaveConfigMappingItem{
		/** 主库数据源配置信息*/
		private DataSourceConfig master;
		/** 从库数据源配置信息*/
		private List<DataSourceConfig> slaveList;


		public DataSourceConfig getMaster() {
			return master;
		}

		public void setMaster(DataSourceConfig master) {
			this.master = master;
		}

		public List<DataSourceConfig> getSlaveList() {
			return slaveList;
		}

		public void setSlaveList(List<DataSourceConfig> slaveList) {
			this.slaveList = slaveList;
		}
	}

}
