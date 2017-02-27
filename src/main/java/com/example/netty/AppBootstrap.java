package com.example.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppBootstrap {
	
	final static Logger logger = LoggerFactory.getLogger(AppBootstrap.class);

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		logger.debug("App context started...");
	}
}