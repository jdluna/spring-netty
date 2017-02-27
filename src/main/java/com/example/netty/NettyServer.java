package com.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;

import java.util.List;

public class NettyServer {
	
	private String host;
	
	private int port;

	private ServerBootstrap bootstrap;

	private List<ChannelHandler> channelHandlers;
	
	public synchronized ChannelFuture start() throws InterruptedException {
		return bootstrap
			.childHandler(new ChannelInitializer<SocketChannel>() {
				
                 @Override
                 public void initChannel(SocketChannel ch) {
                     ChannelPipeline channelPipeline = ch.pipeline();
                     
                     for (ChannelHandler channelHanlder : channelHandlers) {
                    	 channelPipeline.addLast(channelHanlder);
                     }
                 }
             })
             .bind(host, port).sync();
	}
	
	public synchronized Future<?> stop() {
		return bootstrap.group().shutdownGracefully().syncUninterruptibly();
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
}
