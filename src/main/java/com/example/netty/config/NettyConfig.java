package com.example.netty.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;

@Configuration
public class NettyConfig {

	@Bean
	public EventLoopGroup serverEventLoop() {
		return new NioEventLoopGroup();
	}
	
	@Bean
	public ServerBootstrap serverBootstrap() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(serverEventLoop());
		bootstrap.channel(NioServerSocketChannel.class);
		return bootstrap;
	}
	
	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public NettyServer nettyServer(List<ChannelHandler> channelHandlers) {
		NettyServer server = new NettyServer();
		server.setHost("localhost");
		server.setPort(5000);
		server.setBootstrap(serverBootstrap());
		server.setChannelHandlers(channelHandlers);
		return server;
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public EventLoopGroup clientEventLoop() {
		return new NioEventLoopGroup();
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public Bootstrap clientBootstrap() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(clientEventLoop());
		bootstrap.channel(NioSocketChannel.class);
		return bootstrap;
	}
	
	@Bean(destroyMethod = "stop")
	@Scope(scopeName = "prototype")
	public NettyClient nettyClient(List<ChannelHandler> channelHandlers) {
		NettyClient nettyClient = new NettyClient();
		nettyClient.setHost("localhost");
		nettyClient.setPort(5000);
		nettyClient.setBootstrap(clientBootstrap());
		nettyClient.setChannelHandlers(channelHandlers);
		return nettyClient;
	}
}
