package com.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;

import java.util.List;

public class NettyClient {

	private String host;
	private int port;

	private Bootstrap bootstrap;

	private List<ChannelHandler> channelHandlers;

	public synchronized ChannelFuture start() {
		bootstrap
			.handler(new ChannelInitializer<SocketChannel>() {
			
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					 ChannelPipeline channelPipeline = ch.pipeline();
					 
					 for (ChannelHandler channelHanlder : channelHandlers) {
                    	 channelPipeline.addLast(channelHanlder);
                     }
				}
		});
	
		return bootstrap.connect(host, port).syncUninterruptibly();
	}
	
	public synchronized Future<?> stop() {
		return bootstrap.group().shutdownGracefully().syncUninterruptibly();
	}
	
	public void send(byte[] bytes) {
		
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

	public List<ChannelHandler> getChannelHandlers() {
		return channelHandlers;
	}

	public void setChannelHandlers(List<ChannelHandler> channelHandlers) {
		this.channelHandlers = channelHandlers;
	}
}
