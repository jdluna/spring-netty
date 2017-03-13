package com.example.netty.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.ClassUtils;

import com.example.netty.channelhandler.ClientHandler;
import com.example.netty.channelhandler.IdleHandler;
import com.example.netty.channelhandler.ServerHandler;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;

@Configuration
public class NettyConfig {
	
	@Value("${server.host}")
	private String host;
	
	@Value("${server.port}")
	private int port;
	
	@Autowired
	private ISO8583Config iso8583Config;
	
	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
	@Bean
	public ObjectEncoder objectEncoder() {
		return new ObjectEncoder();
	}
	
	// Server
	
	@Bean
	public ServerBootstrap serverBootstrap() {
		ServerBootstrap bootstrap = new ServerBootstrap()
			.group(new NioEventLoopGroup())
			.channel(NioServerSocketChannel.class)
			.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
			.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
			.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		
			.handler(loggingHandler())
			.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
					  .addLast(objectEncoder())
					  .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(ClassUtils.getDefaultClassLoader())))
						
					  .addLast(iso8583Config.iso8583Encoder())
					  .addLast(iso8583Config.iso8583Decoder())
					
					  //.addLast(new ISO8583ServerHandler())
					
					  .addLast(new ServerHandler());
				}
			});
		
		return bootstrap;
	}
	
	@Bean(destroyMethod = "stop")
	public NettyServer nettyServer() {
		NettyServer server = new NettyServer();
		server.setHost(host);
		server.setPort(port);
		server.setBootstrap(serverBootstrap());
		return server;
	}
	
	// Client
	
	@Bean
	@Scope(scopeName = "prototype")
	public Bootstrap clientBootstrap() {
		Bootstrap bootstrap = new Bootstrap()
			.group(new NioEventLoopGroup())
			.channel(NioSocketChannel.class)
			.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
			.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		
			.handler(new ChannelInitializer<SocketChannel>() {
			
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					 ch.pipeline()
					   .addLast(new IdleStateHandler(30, 60, 0))
					   .addLast(new IdleHandler())
					   .addLast(loggingHandler())
						
				 	   .addLast(objectEncoder())
				 	   .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(ClassUtils.getDefaultClassLoader())))
					
				 	   .addLast(iso8583Config.iso8583Encoder())
				 	   .addLast(iso8583Config.iso8583Decoder())
					
				 	   .addLast(new ClientHandler());
				}
			});
		
		return bootstrap;
	}
	
	@Bean(destroyMethod = "stop")
	@Scope(scopeName = "prototype")
	public NettyClient nettyClient() {
		NettyClient nettyClient = new NettyClient();
		nettyClient.setHost(host);
		nettyClient.setPort(port);
		nettyClient.setBootstrap(clientBootstrap());
		return nettyClient;
	}
}
