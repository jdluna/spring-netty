package com.example.netty.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class NettyClient {

	private String host;
	
	private int port;

	private Bootstrap bootstrap;
	
	private Channel channel;

	public synchronized void start() {
		channel = bootstrap.connect(host, port).syncUninterruptibly().channel();
	}
	
	public synchronized void stop() {
		bootstrap.group().shutdownGracefully().syncUninterruptibly();
	}
	
	public ChannelFuture write(Object object) {
		return channel.write(object);
	}
	
	public ChannelFuture writeAndFlush(Object object) {
		return channel.writeAndFlush(object);
	}
	
	public void flush() {
		channel.flush();
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

	public Channel getChannel() {
		return channel;
	}
}
