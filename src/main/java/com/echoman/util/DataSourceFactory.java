package com.echoman.util;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceFactory {

	private static ComboPooledDataSource dataSource;
	
	static{
        try {
        	
	        String driver = "org.gjt.mm.mysql.Driver";
//	        String url = "jdbc:mysql://localhost:3306/echoman";
//			String username = "root";
//			String password = "5651403";
//			String url = "jdbc:mysql://rdsy32ua2y32ua2.mysql.rds.aliyuncs.com:3306/rcst7f6k57654t45?useUnicode=false&characterEncoding=UTF-8";
//			String username = "rcst7f6k57654t45";
//			String password = "tb7778479";
			String url = "jdbc:mysql://202.197.237.29:3306/jtyd?useUnicode=false&characterEncoding=UTF-8";
			String username = "idtn";
			String password = "idtn123";
			
			dataSource = new ComboPooledDataSource();
			dataSource.setDriverClass(driver);
			dataSource.setJdbcUrl(url);
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setAcquireIncrement(2);
			dataSource.setMinPoolSize(10);
			dataSource.setMaxPoolSize(20);
			dataSource.setMaxIdleTime(3600);
//			dataSource.setIdleConnectionTestPeriod(60);
//			dataSource.setCheckoutTimeout(20);
			
			dataSource.setAutomaticTestTable("_test");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataSource getDataSource(){
		return dataSource;
	}
	
	public static String stats(){
		StringBuffer sb = new StringBuffer();
		try {
			sb
			.append("getAutomaticTestTable: ").append(dataSource.getAutomaticTestTable()).append("\n")
			.append("getConnectionCustomizerClassName: ").append(dataSource.getConnectionCustomizerClassName()).append("\n")
			.append("getConnectionTesterClassName: ").append(dataSource.getConnectionTesterClassName()).append("\n")
			.append("getIdleConnectionTestPeriod: ").append(dataSource.getIdleConnectionTestPeriod()).append("\n")
			.append("getInitialPoolSize: ").append(dataSource.getInitialPoolSize()).append("\n")
			.append("getMaxIdleTime: ").append(dataSource.getMaxIdleTime()).append("\n")
			.append("getMaxIdleTimeExcessConnections: ").append(dataSource.getMaxIdleTimeExcessConnections()).append("\n")
			.append("getMaxStatements: ").append(dataSource.getMaxStatements()).append("\n")
			.append("getMaxStatementsPerConnection: ").append(dataSource.getMaxStatementsPerConnection()).append("\n")
			.append("getNumBusyConnections: ").append(dataSource.getNumBusyConnections()).append("\n")
			.append("getNumIdleConnections: ").append(dataSource.getNumIdleConnections()).append("\n")
			.append("getNumUnclosedOrphanedConnections: ").append(dataSource.getNumUnclosedOrphanedConnections()).append("\n")
			.append("getDescription: ").append(dataSource.getDescription()).append("\n")
			;
		} catch (SQLException e) {
			sb.append(e.getMessage());
		}
		return sb.toString();
	}
}
