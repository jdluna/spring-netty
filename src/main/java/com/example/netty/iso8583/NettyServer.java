package com.example.netty.iso8583;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
	
	final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private int port;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private ServerBootstrap bootstrap;

	private List<ChannelHandler> channelHandlers;
	
	public void start() {
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
		
		try {
			ChannelFuture f = bootstrap.bind(port).sync();
			f.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			
		} finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
	}
	
	public void stop() {
		
	}

	public NettyServer(int port) {
		this.port = port;
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
