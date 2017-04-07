package com.example.netty.core.endpoint;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.core.configuration.ClientConfiguration;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Client {
	
	private final static Logger logger = LoggerFactory.getLogger(Client.class);
	
	private String host;
	
	private int port;
	
	private ClientConfiguration configuration;

	private Bootstrap bootstrap;
	
	private Channel channel;
	
	public synchronized void start() {
		try {
			bootstrap = configuration.build();
			this.channel = bootstrap.connect(host, port).sync().channel();
		
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public synchronized void stop() {
		try {
			channel.deregister().await();
			channel.close().sync().await(10, TimeUnit.SECONDS);
			
			bootstrap.group().shutdownGracefully();
			
			logger.debug("Netty client stopped");
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public ChannelFuture send(Object object) {
		return channel.writeAndFlush(object);
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

	public ClientConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ClientConfiguration configuration) {
		this.configuration = configuration;
	}
}
