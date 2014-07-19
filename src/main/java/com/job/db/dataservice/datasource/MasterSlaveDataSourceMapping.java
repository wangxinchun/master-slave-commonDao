package com.job.db.dataservice.datasource;

import java.util.List;

import javax.sql.DataSource;

/**
 * 一主多从的配置包装类
 * @author wangxinchun1988@163.com
 */
public class MasterSlaveDataSourceMapping {
	
	private List<MasterSlaveDataSourceMappingItem> list;
	
	public List<MasterSlaveDataSourceMappingItem> getList() {
		return list;
	}

	public void setList(List<MasterSlaveDataSourceMappingItem> list) {
		this.list = list;
	}

	/** 主从库配置项
	 */
	public static class MasterSlaveDataSourceMappingItem{
		/** 主库数据源*/
		private DataSource master;
		/** 从库数据源列表*/
		private List<DataSource> slaveList;
		public DataSource getMaster() {
			return master;
		}
		public void setMaster(DataSource master) {
			this.master = master;
		}
		public List<DataSource> getSlaveList() {
			return slaveList;
		}
		public void setSlaveList(List<DataSource> slaveList) {
			this.slaveList = slaveList;
		}
		
		
		
	}
}
