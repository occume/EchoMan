package com.echoman.bootstrap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.echoman.robot.weibo.cn.WeiboCNBiz;

public class Bootstrap {

	public static void main(String[] args) {
//		@SuppressWarnings({ "unused", "resource" })
//		ApplicationContext context = new AnnotationConfigApplicationContext(EchoManConfig.class);
		WeiboCNBiz.instance().start();
	}

}
