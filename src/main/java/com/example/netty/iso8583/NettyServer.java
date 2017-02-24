package com.example.netty.iso8583;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class NettyServer {
	
	private String ip;
	private int port;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private ServerBootstrap bootstrap;

	private List<ChannelHandler> channelHandlers;
	
	public void start() throws InterruptedException {
		bootstrap
			.group(bossGroup, workerGroup)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				
                 @Override
                 public void initChannel(SocketChannel ch) {
                     ChannelPipeline channelPipeline = ch.pipeline();
                     
                     for (ChannelHandler channelHanlder : channelHandlers) {
                    	 channelPipeline.addLast(channelHanlder);
                     }
                 }
             });
		
		bootstrap.bind(ip, port);
	}
	
	public void stop() {
		bossGroup.shutdownGracefully();
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

	public EventLoopGroup getBossGroup() {
		return bossGroup;
	}

	public void setBossGroup(EventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
	}

	public EventLoopGroup getWorkerGroup() {
		return workerGroup;
	}

	public void setWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
	}

	public List<ChannelHandler> getChannelHandlers() {
		return channelHandlers;
	}

	public void setChannelHandlers(List<ChannelHandler> channelHandlers) {
		this.channelHandlers = channelHandlers;
	}

	public ServerBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(ServerBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}
}
