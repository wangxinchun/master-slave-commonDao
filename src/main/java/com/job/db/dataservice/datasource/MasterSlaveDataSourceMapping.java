package com.job.db.dataservice.datasource;

import java.util.List;

import javax.sql.DataSource;

public class MasterSlaveDataSourceMapping {
	
	private List<MasterSlaveDataSourceMappingItem> list;
	
	public List<MasterSlaveDataSourceMappingItem> getList() {
		return list;
	}

	public void setList(List<MasterSlaveDataSourceMappingItem> list) {
		this.list = list;
	}

	public static class MasterSlaveDataSourceMappingItem{
		private DataSource master;
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
