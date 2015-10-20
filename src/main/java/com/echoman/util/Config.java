package com.echoman.util;

import java.io.IOException;
import java.io.InputStream;
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

public class Config {

	private final static Logger LOG = LoggerFactory.getLogger(Config.class);
	private static final String CONFIG_FILENAME = "config.xml";
	
	private static Document doc;
	
	private volatile long lastModifiedTime = 0;
	
	static{
		getConfigFile();
	}
	
	private static void getConfigFile(){

		Resource res = new ClassPathResource("/" + CONFIG_FILENAME);
		try {
			InputStream ins = res.getInputStream();
			doc = Jsoup.parse(ins, "UTF-8", "");
		} catch (IOException e) {
			LOG.error("Fail to read config, {}", e);
			System.exit(0);
		}
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
