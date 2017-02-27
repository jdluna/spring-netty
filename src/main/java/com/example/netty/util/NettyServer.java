package com.example.netty.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

public class NettyServer {
	
	private String host;
	
	private int port;

	private ServerBootstrap bootstrap;
	
	private Channel channel;

	private List<ChannelHandler> channelHandlers;
	
	public synchronized void start() throws InterruptedException {
		bootstrap
			.childHandler(new ChannelInitializer<SocketChannel>() {
				
                 @Override
                 public void initChannel(SocketChannel ch) {
                     ChannelPipeline channelPipeline = ch.pipeline();
                     
                     for (ChannelHandler channelHanlder : channelHandlers) {
                    	 channelPipeline.addLast(channelHanlder);
                     }
                 }
             });
		
        channel = bootstrap.bind(host, port).sync().channel();
	}
	
	public synchronized void stop() {
		bootstrap.group().shutdownGracefully().syncUninterruptibly();
	}
	
	public ChannelFuture write(Object object) {
		return channel.write(object);
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

	public Channel getChannel() {
		return channel;
	}
}
