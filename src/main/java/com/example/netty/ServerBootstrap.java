package com.example.netty;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.netty.config.AppConfig;
import com.example.netty.core.endpoint.TcpServer;

public class ServerBootstrap {
	
	@SuppressWarnings({ "resource" })
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		TcpServer tcpServer = appContext.getBean(TcpServer.class);
		tcpServer.start();
	}
}