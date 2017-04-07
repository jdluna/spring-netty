package com.example.netty.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.example.netty.core.codec.IsoMessageDecoder;
import com.example.netty.core.codec.IsoMessageEncoder;
import com.example.netty.core.configuration.ClientConfiguration;
import com.example.netty.core.configuration.ServerConfiguration;
import com.example.netty.core.endpoint.Client;
import com.example.netty.core.endpoint.Server;
import com.example.netty.core.handler.MessageDispatcher;
import com.example.netty.core.j8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

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
	public IsoMessageEncoder iso8583Encoder() {
		return new IsoMessageEncoder();
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public IsoMessageDecoder iso8583Decoder() {
		return new IsoMessageDecoder(messageFactory());
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
	public MessageDispatcher serverDispatcher() {
		MessageDispatcher dispatcher = new MessageDispatcher("server");
		return dispatcher;
	}
	
	@Bean
	public ServerConfiguration serverConfiguration() {
		return new ServerConfiguration()
			.channel(NioServerSocketChannel.class)
			.channelOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
			.channelOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
			.channelOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			
			.workerHandler(new ChannelInitializer<Channel>() {

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
	}
	
	@Bean
	public Server server() {
		Server server = new Server();
		server.setHost(env.getProperty("server.host"));
		server.setPort(env.getProperty("server.port", Integer.class));
		server.setConfiguration(serverConfiguration());
		return server;
	}
	
	// Client
	
	@Bean
	public MessageDispatcher clientDispatcher() {
		MessageDispatcher dispatcher = new MessageDispatcher("client");
		return dispatcher;
	}
	
	@Bean
	public ClientConfiguration clientConfiguration() {
		return new ClientConfiguration()
			.channel(NioSocketChannel.class)
			.channelOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
			.channelOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
			.channelOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			
			.workerHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline()
					  .addFirst(loggingHandler())
					  
					  .addLast(lengthFieldEncoder())
					  .addLast(lengthFieldDecoder())
						
					  .addLast(iso8583Encoder())
					  .addLast(iso8583Decoder())

					  .addLast(clientDispatcher());
				}
			});
	}
	
	@Bean(destroyMethod = "stop")
	public Client client() {
		Client client = new Client();
		client.setHost(env.getProperty("server.host"));
		client.setPort(env.getProperty("server.port", Integer.class));
		client.setConfiguration(clientConfiguration());
		return client;
	}
}
