package com.example.netty.iso8583;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppBootstrap {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		NettyServer nettyServer = appContext.getBean(NettyServer.class);
		nettyServer.start();
	}
}