package com.job.db.dataservice.datasource;

/**
 * 数据源统一配置信息
 * @author wangxinchun
 */
public class DataSourceConfig {
	/**
	 * db 所在的服务器ip
	 */
	private String ip;

	/**
	 * 端口号
	 */
	private String port;

	/**
	 * 数据库名字
	 */
	private String dbName;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 数据库驱动的名字
	 */
	private String driveClassName;

	/**
	 * 数据源连接池对的类实现
	 */
	private String poolClass;

	/**
	 * 连接池最大连接数
	 */
	private String maxConnection;

	/**
	 * 连接池最小连接数
	 */
	private String minConnection;

	/**
     * 
     */
	private String maxLifetime;

	private String simu;

	/**
	 * 如果发现了空闲的数据库连接.house keeper 将会用这个语句来测试.这个语句最好非常快的被执行.如果没有定义,测试过程将会被忽略。
	 */
	private String houseKeepingSql;

	private String statistics;

	/**
	 * 查询的时候 缓存的记录条数
	 */
	private String useCursorFetch;

	public String getMinConnection() {
		return minConnection;
	}

	public void setMinConnection(String minConnection) {
		this.minConnection = minConnection;
	}

	public String getMaxLifetime() {
		return maxLifetime;
	}

	public void setMaxLifetime(String maxLifetime) {
		this.maxLifetime = maxLifetime;
	}

	public String getSimu() {
		return simu;
	}

	public void setSimu(String simu) {
		this.simu = simu;
	}

	public String getHouseKeepingSql() {
		return houseKeepingSql;
	}

	public void setHouseKeepingSql(String houseKeepingSql) {
		this.houseKeepingSql = houseKeepingSql;
	}

	public String getStatistics() {
		return statistics;
	}

	public void setStatistics(String statistics) {
		this.statistics = statistics;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriveClassName() {
		return driveClassName;
	}

	public void setDriveClassName(String driveClassName) {
		this.driveClassName = driveClassName;
	}

	public String getPoolClass() {
		return poolClass;
	}

	public void setPoolClass(String poolClass) {
		this.poolClass = poolClass;
	}

	public String getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(String maxConnection) {
		this.maxConnection = maxConnection;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DataSourceConfig dbConfig = (DataSourceConfig) o;
		if (ip != null ? !ip.equals(dbConfig.ip) : dbConfig.ip != null)
			return false;
		if (port != null ? !port.equals(dbConfig.port) : dbConfig.port != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (ip != null ? ip.hashCode() : 0);
		result = 31 * result + (port != null ? port.hashCode() : 0);
		return result;
	}

	public String getUseCursorFetch() {
		return useCursorFetch;
	}

	public void setUseCursorFetch(String useCursorFetch) {
		this.useCursorFetch = useCursorFetch;
	}

	@Override
	public String toString() {
		return "DataSourceConfig [ip=" + ip + ", port=" + port + ", dbName="
				+ dbName + ",driveClassName=" + driveClassName + ", poolClass="
				+ poolClass + ", maxConnection=" + maxConnection
				+ ", minConnection=" + minConnection + ", maxLifetime="
				+ maxLifetime + ", simu=" + simu + ", houseKeepingSql="
				+ houseKeepingSql + ", statistics=" + statistics
				+ ", useCursorFetch=" + useCursorFetch + "]";
	}

}
