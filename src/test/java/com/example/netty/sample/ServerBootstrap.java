package com.example.netty.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.netty.sample.conf.AppConfig;
import com.example.netty.sample.util.NettyServer;


public class ServerBootstrap {
	
	final static Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

	@SuppressWarnings({ "resource" })
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		NettyServer nettyServer = appContext.getBean(NettyServer.class);
		nettyServer.start();
	}
}