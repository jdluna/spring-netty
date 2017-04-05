package com.example.netty.sample.util;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;

public class NettyServer {
	
	final static Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	private String host;
	
	private int port;

	private ServerBootstrap bootstrap;
	
	private Channel channel;

	public synchronized void start() {
        channel = bootstrap.bind(host, port).syncUninterruptibly().channel();
	}
	
	public synchronized void stop() {
		try {
			channel.deregister().await();
			
			channel.close().await(10, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			 logger.error("Error while stopping the server", e);
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

	public ServerBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(ServerBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}
}
