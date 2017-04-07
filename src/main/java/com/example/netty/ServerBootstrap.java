package com.example.netty;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.netty.config.AppConfig;
import com.example.netty.core.endpoint.NettyServer;


public class ServerBootstrap {
	
	@SuppressWarnings({ "resource" })
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		NettyServer nettyServer = appContext.getBean(NettyServer.class);
		nettyServer.start();
	}
}