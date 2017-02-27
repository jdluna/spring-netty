package com.example.netty.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.example.netty.iso8583.handler.ClientHandler;
import com.example.netty.iso8583.handler.ServerHandler;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;

@Configuration
public class NettyConfig {
	
	@Value("${server.host}")
	private String host;
	
	@Value("${server.port}")
	private int port;

	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
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
	public List<ChannelHandler> serverHandlers() {
		List<ChannelHandler> channelHandlers = new ArrayList<ChannelHandler>();
		channelHandlers.add(loggingHandler());
		channelHandlers.add(new StringDecoder(CharsetUtil.UTF_8));
		channelHandlers.add(new ServerHandler());
		channelHandlers.add(new StringEncoder(CharsetUtil.UTF_8));
		return channelHandlers;
	}
	
	@Bean(destroyMethod = "stop")
	public NettyServer nettyServer() {
		NettyServer server = new NettyServer();
		server.setHost(host);
		server.setPort(port);
		server.setBootstrap(serverBootstrap());
		server.setChannelHandlers(serverHandlers());
		return server;
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public Bootstrap clientBootstrap() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		return bootstrap;
	}
	
	@Bean
	public List<ChannelHandler> clientHandlers() {
		List<ChannelHandler> channelHandlers = new ArrayList<ChannelHandler>();
		channelHandlers.add(loggingHandler());
		channelHandlers.add(new StringDecoder(CharsetUtil.UTF_8));
		channelHandlers.add(new ClientHandler());
		channelHandlers.add(new StringEncoder(CharsetUtil.UTF_8));
		return channelHandlers;
	}
	
	@Bean(destroyMethod = "stop")
	@Scope(scopeName = "prototype")
	public NettyClient nettyClient(List<ChannelHandler> channelHandlers) {
		NettyClient nettyClient = new NettyClient();
		nettyClient.setHost(host);
		nettyClient.setPort(port);
		nettyClient.setBootstrap(clientBootstrap());
		nettyClient.setChannelHandlers(clientHandlers());
		return nettyClient;
	}
}
