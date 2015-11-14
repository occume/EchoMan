package com.echoman.storage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.echoman.robot.weibo.model.FansKeywords;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.CommonUtil;
import com.echoman.util.DataSourceFactory;
import com.google.common.collect.Lists;

public class SuperDao implements Dao<Storable> {
	
	public String tablePrefix = "robot_";
	
	public final String INSERT_PREFIX = "insert into " + tablePrefix;
	public final String SELECT_PREFIX = "select * from " + tablePrefix;
	
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
		
		return getQueryRunner().batch(sql, params);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean exist(Storable bean) {
		
		if(bean == null){
			throw new IllegalArgumentException("bean must not be null");
		}
		
		String sql = assembleExist(bean);
		
		try {
			return getQueryRunner().query(sql, bean.equalValues(), EXIST_HANDLER);
		} catch (SQLException e) { return false; }
	}
	
	public<T> List<T> getBeans(final String sql, final Class<T> beanClass) throws SQLException{
		final Field[] fields = beanClass.getDeclaredFields();
		final List<T> result = Lists.newArrayList();
		getQueryRunner().query(sql, new ResultSetHandler<T>(){
			@Override
			public T handle(ResultSet rs) throws SQLException {
				while(rs.next()){
					T bean;
					try {
						bean = beanClass.newInstance();
					} catch (Exception e1) {
						e1.printStackTrace();
						continue;
					}
					
					for(Field f: fields){
						if(f.isAnnotationPresent(NonColumn.class)) continue;
						String fName = f.getName();
						String column = CommonUtil.underscoreName(fName);
						if(!(sql.contains("*") || sql.contains(column))) continue;
						Object val = rs.getObject(column);
						if(val == null) continue;
						try {
							Method setter = beanClass.getMethod("set" + CommonUtil.camelName(fName), f.getType());
							setter.invoke(bean, val);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					result.add(bean);
				}
				return null;
			}});
		return result;
	}
	
	public<T> T getBean(final String sql, final Class<T> beanClass) throws SQLException{
		
		return getQueryRunner().query(sql, new ResultSetHandler<T>(){
			@Override
			public T handle(ResultSet rs) throws SQLException {
				T result = null;
				if(rs.next()) result = fillBean(rs, sql, beanClass);
				return result;
			}
		});
	}
	
	private <T> T fillBean(ResultSet rs, String sql, final Class<T> beanClass) throws SQLException{
		final Field[] fields = beanClass.getDeclaredFields();
		
		T bean = null;
		try {
			bean = beanClass.newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
			return bean;
		}
		
		for(Field f: fields){
			if(f.isAnnotationPresent(NonColumn.class)) continue;
			String fName = f.getName();
			String column = CommonUtil.underscoreName(fName);
			if(!(sql.contains("*") || sql.contains(column))) continue;
			Object val = rs.getObject(column);
			if(val == null) continue;
			try {
				Method setter = beanClass.getMethod("set" + CommonUtil.camelName(fName), f.getType());
				setter.invoke(bean, val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return bean;
	}
	
	public<T> void update(final String updateSql, Object[] params){
		Connection conn = null;
		
		try {
			conn = DataSourceFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			
			getQueryRunner().update(conn, updateSql, params);
			
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
				}
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	private String assembleExist(Object bean){
		
		Class klass = bean.getClass();
		Field[] fields = klass.getDeclaredFields();
		String table = CommonUtil.underscoreName(klass.getSimpleName());
		String sql = SELECT_PREFIX + table + " where 1 = 1 ";

		for(Field f: fields){
			if(!f.isAnnotationPresent(EqualColumn.class)) continue;
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
			if(f.isAnnotationPresent(NonColumn.class)) continue;
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
	public int createTable(Class klass) throws SQLException{

		Field[] fields = klass.getDeclaredFields();
		String table = CommonUtil.underscoreName(klass.getSimpleName());
		String sql = "CREATE TABLE IF NOT EXISTS `" + tablePrefix + table + "` (" +
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
		
		
		return getQueryRunner().update(sql);
	}
	
	private QueryRunner getQueryRunner(){
		return new QueryRunner(DataSourceFactory.getDataSource());
	}
	
	public static void main(String...strings) throws SQLException{
//		WeiboUser bean = new WeiboUser();
//		bean.setName("jd");
//		bean.setUid("001");;
		SuperDao dao = new SuperDao();
//		dao.createTable(new WeiboUser());
//		dao.save(bean);
		
//		List<FansKeywords> users = dao.getBeans("select * from jtyd_fans_keywords limit 2", FansKeywords.class);
//		System.out.println(users);
		String getSql = "select * from jtyd_fans_keywords limit 2";
		FansKeywords kw = dao.getBean(getSql, FansKeywords.class);
		
		String updateSql = "update jtyd_fans_keywords set del_flag = 1 where id = ?";
		dao.update(updateSql, new Object[]{kw.getId()});
//		System.out.println(kw);
	}
}