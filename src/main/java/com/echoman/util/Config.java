package com.echoman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jodd.bean.BeanUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.echoman.model.RobotBean;
import com.echoman.robot.RobotType;
import com.google.common.collect.Sets;

public class Config {

	private final static Logger LOG = LoggerFactory.getLogger(Config.class);
	private static final String CONFIG_FILENAME = "config.xml";
	
	private static Document doc;
	
	private volatile long lastModifiedTime = 0;
	
	static{
		getConfigFile();
	}
	
	private static void getConfigFile(){
		
			String path;
			String osName = System.getProperty("os.name");
			if(osName != null && osName.toLowerCase().contains("windows")) {//for local debug
//				path = "../EchoMan/conf/";
				path = "../EchoMan/conf/";
			} else {
				path = "../conf/";
			}
		
		File f = new File(path + CONFIG_FILENAME);
		System.out.println(f);
//		Resource res = new ClassPathResource("/" + CONFIG_FILENAME);
		try {
//			InputStream ins = res.getInputStream();
			InputStream ins = new FileInputStream(f);
			doc = Jsoup.parse(ins, "UTF-8", "");
		} catch (IOException e) {
			LOG.error("Fail to read config, {}", e);
			System.exit(0);
		}
	}
	
	public static Set<RobotBean> getRobotBeans(RobotType type){
		
		Set<RobotBean> result = Sets.newHashSet();
		
		try {
			Set<RobotBean> all = getObjects(RobotBean.class);
			for(RobotBean bean: all){
				if(type.name().equalsIgnoreCase(bean.getType())){
					result.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static<T> Set<T> getObjects(Class<T> temp) throws Exception{
		
		String className = temp.getSimpleName();
		// template class should end with Bean
		int end = className.length() - 4;
		String tagName = className.substring(0, end).toLowerCase();
		
		Elements elems = doc.getElementsByTag(tagName);
		Set<T> results = new HashSet<>();
		
		for(Element elem: elems){
			T bean = temp.newInstance();
			Elements nodes = elem.children();
			for(Element node: nodes){
				String name = node.tagName();
				BeanUtil.setProperty(bean, name, node.text());
			}
			results.add(bean);
		}
		
		return results;
	}
	
	public static void main(String...strings) throws Exception{
		Set<RobotBean> robots=  getObjects(RobotBean.class);
		System.out.println(robots);
	}
}
