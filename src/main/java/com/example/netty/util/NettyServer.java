package com.example.netty.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class NettyServer {
	
	private String host;
	
	private int port;

	private ServerBootstrap bootstrap;
	
	private Channel channel;

	public synchronized void start() {
        channel = bootstrap.bind(host, port).syncUninterruptibly().channel();
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

	public ServerBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(ServerBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public Channel getChannel() {
		return channel;
	}
}
