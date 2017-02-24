package com.example.netty.iso8583;

import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class NettyClient {

	private String ip;
	private int port;

	private EventLoopGroup workerGroup;

	private Bootstrap bootstrap;

	private List<ChannelHandler> channelHandlers;

	public void start() {
		bootstrap
			.group(workerGroup)
			.handler(new ChannelInitializer<SocketChannel>() {
			
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					 ChannelPipeline channelPipeline = ch.pipeline();
					 
					 for (ChannelHandler channelHanlder : channelHandlers) {
                    	 channelPipeline.addLast(channelHanlder);
                     }
				}
		});
	
		bootstrap.connect(ip, port);
	}

	public void stop() {
		workerGroup.shutdownGracefully();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public EventLoopGroup getWorkerGroup() {
		return workerGroup;
	}

	public void setWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
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
