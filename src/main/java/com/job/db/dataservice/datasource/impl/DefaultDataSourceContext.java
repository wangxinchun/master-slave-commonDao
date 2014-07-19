package com.job.db.dataservice.datasource.impl;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;

import com.job.db.dataservice.datasource.DataSourceContext;

public class DefaultDataSourceContext extends DataSourceContext {

	protected void doMasterInit(DataSource dataSource) {
		URLInfo urlInfo = getDataSourceURLInfo(dataSource);
		String key = urlInfo.getDataSourceKey();
		if(StringUtils.isNotEmpty(urlInfo.getDb())){
			masterDb2IpPortMapping.put(urlInfo.getDb(), key);
		}
		if(masterMap.get(key) != null){
			return;
		}
		masterMap.put(key, new DefaultDatasourceProvider(dataSource));
		
	}

	protected void doSlaveInit(DataSource dataSource) {
		URLInfo urlInfo = getDataSourceURLInfo(dataSource);
		String key = urlInfo.getDataSourceKey();
		if(StringUtils.isNotEmpty(urlInfo.getDb())) {
			if(slaveDb2IpPortListMapping.get(urlInfo.getDb()) == null){
				slaveDb2IpPortListMapping.put(urlInfo.getDb(), new ArrayList<String>());
			}
			slaveDb2IpPortListMapping.get(urlInfo.getDb()).add(key);
		}
		if(slaveMap.get(key) != null){
			return;
		}
		slaveMap.put(key, new DefaultDatasourceProvider(dataSource));
		
	}

	private URLInfo getDataSourceURLInfo(DataSource dataSource) {
		URLInfo urlInfo = new URLInfo();
		if (dataSource instanceof BasicDataSource) {
			// jdbc:mysql://192.168.229.37:3309/tts?useUnicode=true&characterEncoding=utf8
			BasicDataSource basicDataSource = (BasicDataSource) dataSource;
			String url = basicDataSource.getUrl();
			String temp = url.substring(url.indexOf("//")+2);
			if(temp.indexOf("?") >0 ){
				temp = temp.substring(0,temp.indexOf("?"));
			}
			String[] tempArr = temp.split(":|/");
			urlInfo.setIp(tempArr[0]);
			urlInfo.setPort(tempArr[1]);
			urlInfo.setDb(tempArr[2]);
		}
		return urlInfo;
	}
	 
	 
	 private class URLInfo{
		 private String ip;
		 private String port;
		 private String db;
		 
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getDb() {
			return db;
		}
		public void setDb(String db) {
			this.db = db;
		}
		 
		public String getDataSourceKey(){
			return ip+":"+port;
		}
	 }
}
