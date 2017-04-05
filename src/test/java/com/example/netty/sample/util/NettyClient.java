package com.example.netty.sample.util;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class NettyClient {
	
	final static Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	private String host;
	
	private int port;

	private Bootstrap bootstrap;
	
	private Channel channel;

	public synchronized void start() {
		channel = bootstrap.connect(host, port).syncUninterruptibly().channel();
	}
	
	public synchronized void stop() {
		try {
			channel.deregister().await();
			
			channel.close().await(10, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			 logger.error("Error while stopping the client", e);
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

	public Bootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}
}
