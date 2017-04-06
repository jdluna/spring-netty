package com.example.netty.sample.conf;

import java.io.IOException;

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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.example.netty.sample.codec.ISO8583Decoder;
import com.example.netty.sample.codec.ISO8583Encoder;
import com.example.netty.sample.handler.MessageHandlerDispatcher;
import com.example.netty.sample.iso8583.MessageFactory;
import com.example.netty.sample.util.NettyClient;
import com.example.netty.sample.util.NettyServer;
import com.solab.iso8583.parse.ConfigParser;

@Configuration
public class NettyConfig {

	private static Logger logger = LoggerFactory.getLogger(NettyConfig.class);
	
	@Autowired
	private Environment env;
	
	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
	@Bean
	public LengthFieldBasedFrameDecoder lengthFieldDecoder() {
		return new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4);
	}
	
	@Bean
	public LengthFieldPrepender lengthFieldEncoder() {
		return new LengthFieldPrepender(4);
	}
	
	@Bean
	public ISO8583Encoder iso8583Encoder() {
		return new ISO8583Encoder();
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public ISO8583Decoder iso8583Decoder() {
		return new ISO8583Decoder(messageFactory());
	}
	
	@Bean
	public MessageFactory messageFactory() {
		MessageFactory messageFactory = new MessageFactory();
		messageFactory.setAssignDate(true);
		
		try {
			ConfigParser.configureFromClasspathConfig(messageFactory, "j8583.xml");
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		return messageFactory;
	}
	
	@Bean
	public MessageHandlerDispatcher serverDispatcher() {
		return new MessageHandlerDispatcher();
	}
	
	@Bean
	public ServerBootstrap serverBootstrap() {
		ServerBootstrap bootstrap = new ServerBootstrap()
			.group(new NioEventLoopGroup(), new NioEventLoopGroup())
			.channel(NioServerSocketChannel.class)
			.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
			.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
			.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		
			
			.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
					  .addFirst(loggingHandler())
					  .addLast(lengthFieldEncoder())
					  .addLast(lengthFieldDecoder())
						
					  .addLast(iso8583Encoder())
					  .addLast(iso8583Decoder())
					  
					  .addLast(serverDispatcher());
				}
			});
		
		return bootstrap;
	}
	
	@Bean(destroyMethod = "stop")
	public NettyServer nettyServer() {
		NettyServer server = new NettyServer();
		server.setHost(env.getProperty("server.host"));
		server.setPort(env.getProperty("server.port", Integer.class));
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
					   .addLast(loggingHandler())
					   
				 	   .addLast(lengthFieldEncoder())
				 	   .addLast(lengthFieldDecoder())
					
				 	   .addLast(iso8583Encoder())
				 	   .addLast(iso8583Decoder());
				}
			});
		
		return bootstrap;
	}
	
	@Bean(destroyMethod = "stop")
	@Scope(scopeName = "prototype")
	public NettyClient nettyClient() {
		NettyClient nettyClient = new NettyClient();
		nettyClient.setHost(env.getProperty("server.host"));
		nettyClient.setPort(env.getProperty("server.port", Integer.class));
		nettyClient.setBootstrap(clientBootstrap());
		return nettyClient;
	}
}
