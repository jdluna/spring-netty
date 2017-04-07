package com.example.netty.core.endpoint;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.core.configuration.ServerConfiguration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;

public class Server {
	
	private final static Logger logger = LoggerFactory.getLogger(Server.class);
	
	private String host;
	
	private int port;
	
	private ServerConfiguration configuration;
	
	private ServerBootstrap bootstrap;
	
	private Channel channel;

	public synchronized void start() {
		try {
			bootstrap = configuration.build();
			
			this.channel = bootstrap.bind(host, port).sync().channel();
			
			logger.debug("Netty server started at {}:{}", host, port);
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public synchronized void stop() {
		try {
			channel.deregister().await();
			channel.close().sync().await(10, TimeUnit.SECONDS);
			
			bootstrap.group().shutdownGracefully();
			bootstrap.childGroup().shutdownGracefully();
			
			logger.debug("Netty server stopped at {}:{}", host, port);
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServerConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ServerConfiguration configuration) {
		this.configuration = configuration;
	}
}
