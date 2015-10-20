package com.echoman.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Bootstrap {

	public static void main(String[] args) {
		@SuppressWarnings({ "unused", "resource" })
		ApplicationContext context = new AnnotationConfigApplicationContext(EchoManConfig.class);
	}

}
