package com.example.netty.iso8583;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.solab.iso8583.parse.ConfigParser;

@Configuration
@ComponentScan(
	basePackages = "com.example.netty", 
	excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = "com.example.netty.iso20022..*")
	}
)
public class AppConfig {
	
	@Bean
	public MessageFactory messageFactory() throws IOException {
		MessageFactory messageFactory = new MessageFactory();
		messageFactory.setAssignDate(true);
		messageFactory.setUseBinaryBitmap(true);
		messageFactory.setUseBinaryMessages(true);
		
		ConfigParser.configureFromClasspathConfig(messageFactory, "j8583.xml");
		
		return messageFactory;
	}
	
	/* Server */
	@Bean
	public EventLoopGroup serverBossEventLoopGroup() {
		return new NioEventLoopGroup();
	}
	
	@Bean
	public EventLoopGroup serverWorkerEventLoopGroup() {
		return new NioEventLoopGroup();
	}
	
	@Bean
	public ServerBootstrap serverBootstrap() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		return bootstrap;
	}
	
	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
	@Bean
	public NettyServer nettyServer(List<ChannelHandler> channelHandlers) {
		NettyServer server = new NettyServer();
		server.setIp("localhost");
		server.setPort(5000);
		server.setBossGroup(serverBossEventLoopGroup());
		server.setWorkerGroup(serverWorkerEventLoopGroup());
		server.setBootstrap(serverBootstrap());
		server.setChannelHandlers(channelHandlers);
		return server;
	}
	
	/* Client */
	
	@Bean
	public EventLoopGroup clientWorkerEventLoopGroup() {
		return new NioEventLoopGroup();
	}
	
	@Bean
	public Bootstrap clientBootstrap() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioServerSocketChannel.class);
		return bootstrap;
	}
	
	@Bean
	public NettyClient nettyClient() {
		List<ChannelHandler> channelHandlers = new ArrayList<>();
		channelHandlers.add(new LoggingHandler(LogLevel.INFO));
		
		NettyClient client = new NettyClient();
		client.setIp("localhost");
		client.setPort(5000);
		client.setWorkerGroup(clientWorkerEventLoopGroup());
		client.setBootstrap(clientBootstrap());
		client.setChannelHandlers(channelHandlers);
		return client;
	}
}
