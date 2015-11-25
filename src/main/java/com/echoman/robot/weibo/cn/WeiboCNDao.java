package com.echoman.robot.weibo.cn;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.model.SendTasks;
import com.echoman.robot.weibo.model.FansKeywords;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.storage.AsyncSuperDao;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class WeiboCNDao extends AsyncSuperDao {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboCNDao.class);
	
	private Set<FansKeywords> searchedKw = Sets.newHashSet();

	public WeiboCNDao(String tablePrefix, int batchSize) {
		super(tablePrefix, batchSize);
	}

	public FansKeywords getAndUpdateKeyword(){
		String sql = "select * from jtyd_fans_keywords where del_flag = 0";
		String notInWilds = getNotInWilds(searchedKw.size());
		sql += notInWilds +" limit 1";
		FansKeywords kw = null;
		try {
			kw = dao.getBean(sql, getNotInWilsParam(), FansKeywords.class);
			searchedKw.add(kw);
			if(kw != null){
				String updateSql = "update jtyd_fans_keywords set del_flag = 1 where id = ?";
				dao.update(updateSql, new Object[]{kw.getId()});
			}
			
		} catch (Exception e) {
			LOG.error("Error when search user ", e);
		}
		
		return kw;
	}
	
	private String getNotInWilds(int num){
		if(num == 0) return "";
		String ret = "";
		ret = getWilds(num);
		return " not in " + ret;
	}
	
	private Object[] getNotInWilsParam(){
		return Collections2.transform(searchedKw, new Function<FansKeywords, Object>() {
			@Override
			public Object apply(FansKeywords input) {
				return input.getId();
			}
		}).toArray();
	}
	
	public SendTasks getSendTasks(){
		
		SendTasks task = null;
		String getSql0 = "select * from jtyd_send_tasks where del_flag = 0 limit 1";
		
		try {
			task = dao.getBean(getSql0, SendTasks.class);
		} catch (SQLException e) {
			LOG.error("Error getSendTasks, ", e);
		}
		return task;
	}
	
	public List<WeiboUser> getWeiboUserByGrabtag(String grabTag){
		String getSql1 = "select * from jtyd_weibo_user where grab_tag = '"+ grabTag +"' limit 1";
		List<WeiboUser> users = Lists.newArrayList();
		try {
			users = dao.getBeans(getSql1, WeiboUser.class);
		} catch (SQLException e) {
			LOG.error("Error getWeiboUserByGrabtag, ", e);
		}
		return users;
	}
	
	public static void main(String...strings){
		WeiboCNDao dao = new WeiboCNDao("", 1);
//		System.out.println(dao.getWildcard(3));
		System.out.println(dao.getAndUpdateKeyword());
	}
}
