package com.echoman.robot.weixin;

import java.util.Set;

import com.echoman.robot.weixin.model.WeixinArticle;
import com.echoman.robot.weixin.model.WeixinGZH;
import com.echoman.util.CommonUtil;

public class WeixinRobot {

	private WeixinRobotHelper helper = new WeixinRobotHelper();
	
	public void searchGZH(String keyword){
		
		Set<WeixinGZH> gzhs = helper.getGZHList(keyword);
		
		for(WeixinGZH gzh: gzhs){
			getArticleList(gzh);
//			System.out.println("Request : " + urls);
//			for(String url: urls){
//				getArticle(url, gzh);
//			}
		}
		
	}
	
	public void getArticleList(WeixinGZH gzh){
		
		CommonUtil.wait2(50000, 80000);
		
		Set<String> urls = helper.getArticleList(gzh, 1);
		int totalPage = gzh.getTotalPage();
		getArticle(urls, gzh);
		
		for(int page = 2; page <= totalPage; page++){
			urls = helper.getArticleList(gzh, page);
			getArticle(urls, gzh);
		}
		
	}
	
	public void getArticle(Set<String> urls, WeixinGZH gzh){
		
		for(String url: urls){
			WeixinArticle article = helper.getArticle(url, gzh);
			WeixinScheduler.instance().addArticle(article);
			CommonUtil.wait2(50000, 80000);
		}
	}
}
