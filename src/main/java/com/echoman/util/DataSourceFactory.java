package com.echoman.util;

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
			String url = "jdbc:mysql://rdsy32ua2y32ua2.mysql.rds.aliyuncs.com:3306/rcst7f6k57654t45?useUnicode=false&characterEncoding=UTF-8";
			String username = "rcst7f6k57654t45";
			String password = "tb7778479";
			
			dataSource = new ComboPooledDataSource();
			dataSource.setDriverClass(driver);
			dataSource.setJdbcUrl(url);
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setAcquireIncrement(5);
			dataSource.setMinPoolSize(5);
			dataSource.setMaxPoolSize(10);
			dataSource.setMaxIdleTime(120);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataSource getDataSource(){
		return dataSource;
	}
}
