package com.echoman;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware{
	
	private static ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringContext.context = applicationContext;
	}
	
	public static Object getBean(String beanName){
		if (null == beanName){
			return null;
		}
		return context.getBean(beanName);
	}
}
