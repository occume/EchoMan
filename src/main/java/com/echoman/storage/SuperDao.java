package com.echoman.storage;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.CommonUtil;
import com.echoman.util.DataSourceFactory;
import com.google.common.collect.Lists;

public class SuperDao implements Storage<Storable> {
	
	public static final String INSERT_PREFIX = "insert into robot_";
	public static final String SELECT_PREFIX = "select * from robot_";
	
	public static ResultSetHandler<Boolean> EXIST_HANDLER =  new ResultSetHandler<Boolean>(){
		@Override
		public Boolean handle(ResultSet rs) throws SQLException {
			return rs.next();
		}
	};

	@Override
	public int save(Storable bean) throws SQLException {
		
		if(bean == null){
			throw new IllegalArgumentException("bean must not be null");
		}
		
		return batchSave(Lists.newArrayList(bean))[0];
	}
	
	@Override
	public int[] batchSave(List<Storable> list) throws SQLException {
		
		if(list == null){
			throw new IllegalArgumentException("list must not be null");
		}
		
		if(list.size() == 0){
			throw new IllegalArgumentException("list must have at least one item");
		}
		
		String sql = assembleInsert(list.get(0));
		int batchSize = list.size();
		
		Object[][] params = new Object[batchSize][];
		for(int i = 0; i < batchSize; i++){
			params[i] = list.get(i).toArray();
		}
		
		QueryRunner qr = new QueryRunner(DataSourceFactory.getDataSource());
		return qr.batch(sql, params);
	}
	
	@Override
	public boolean exist(Storable bean) {
		
		if(bean == null){
			throw new IllegalArgumentException("bean must not be null");
		}
		
		String sql = assembleExist(bean);
		System.out.println(sql);
		QueryRunner qr = new QueryRunner(DataSourceFactory.getDataSource());
		try {
			return qr.query(sql, bean.uniqueValues(), EXIST_HANDLER);
		} catch (SQLException e) { return false; }
	}
	
	

	@SuppressWarnings("rawtypes")
	private String assembleExist(Object bean){
		
		Class klass = bean.getClass();
		Field[] fields = klass.getDeclaredFields();
		String table = CommonUtil.underscoreName(klass.getSimpleName());
		String sql = SELECT_PREFIX + table + " where 1 = 1 ";

		for(Field f: fields){
			if(f.getAnnotation(Unique.class) == null) continue;
			String columnName = CommonUtil.underscoreName(f.getName());
			sql += (" and `" + columnName + "`" + "= ?");
		}
		
		return sql;
	}
	
	@SuppressWarnings("rawtypes")
	private String assembleInsert(Object bean){
		
		Class klass = bean.getClass();
		Field[] fields = klass.getDeclaredFields();
		String table = CommonUtil.underscoreName(klass.getSimpleName());
		String sql = INSERT_PREFIX + table + "(";
		
		int columnNum = 0;

		for(Field f: fields){
			if(f.getAnnotation(NonStore.class) != null) continue;
			String columnName = CommonUtil.underscoreName(f.getName());
			sql += ("`" + columnName + "`" + ",");
			columnNum++;
		}
		
		sql = sql.substring(0, sql.length() - 1);
		
		sql += ") values (";
		
		for(int i = 0; i < columnNum; i++){
			sql += "?,";
		}
		
		sql = sql.substring(0, sql.length() - 1);
		sql += ")";
		
		return sql;
	}
	
	@SuppressWarnings("rawtypes")
	public void createTable(Storable bean){
//		CREATE TABLE `robot_baidu_forum` (
//				  `id` int(11) NOT NULL AUTO_INCREMENT,
//				  `fid` varchar(20) DEFAULT NULL,
//				  `name` varchar(100) DEFAULT NULL,
//				  `member_num` int(11) DEFAULT NULL,
//				  `post_num` int(11) DEFAULT NULL,
//				  `slogan` varchar(300) DEFAULT NULL,
//				  PRIMARY KEY (`id`)
//				) ENGINE=InnoDB AUTO_INCREMENT=1492 DEFAULT CHARSET=utf8
		Class klass = bean.getClass();
		Field[] fields = klass.getDeclaredFields();
		String table = CommonUtil.underscoreName(klass.getSimpleName());
		String sql = "CREATE TABLE `robot_" + table + "` (" +
					"`id` int(11) NOT NULL AUTO_INCREMENT,";

		for(Field f: fields){
			Column column = f.getAnnotation(Column.class);
			if(column == null) continue;
			String type = column.type();
			int length = column.length();
			String columnName = CommonUtil.underscoreName(f.getName());
			sql += "`" + columnName + "` " + type + "(" + length + ") DEFAULT NULL,";
		}
		sql += " PRIMARY KEY (`id`)" +
				") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
		
		System.out.println(sql);
	}
	
	public static void main(String...strings) throws SQLException{
		WeiboUser bean = new WeiboUser();
		bean.setName("jd");
		bean.setUid("001");;
		SuperDao dao = new SuperDao();
//		dao.createTable(new WeiboUser());
		dao.save(bean);
	}
}