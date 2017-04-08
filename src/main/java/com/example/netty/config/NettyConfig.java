package com.example.netty.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import com.example.netty.Constant;
import com.example.netty.core.codec.IsoMessageDecoder;
import com.example.netty.core.codec.IsoMessageEncoder;
import com.example.netty.core.configuration.ClientConfiguration;
import com.example.netty.core.configuration.ServerConfiguration;
import com.example.netty.core.endpoint.TcpClient;
import com.example.netty.core.endpoint.TcpServer;
import com.example.netty.core.handler.MessageDispatcher;
import com.example.netty.core.handler.RouteExtractor;
import com.example.netty.core.j8583.MessageFactory;
import com.example.netty.core.util.SSLContextBuilder;
import com.example.netty.handler.IsoMessageRouteExractor;
import com.solab.iso8583.IsoMessage;
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
import io.netty.handler.ssl.SslHandler;

@Configuration
public class NettyConfig {

	private static Logger logger = LoggerFactory.getLogger(NettyConfig.class);
	
	@Autowired
	private Environment env;
	
	
	@Bean
	public ThreadPoolExecutorFactoryBean threadPool() {
		ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
		factory.setCorePoolSize(2);
		factory.setMaxPoolSize(5);
		factory.setAllowCoreThreadTimeOut(true);
		
		return factory;
	}
	
	@Bean
	public LoggingHandler loggingHandler() {
		return new LoggingHandler(LogLevel.INFO);
	}
	
	@Bean
	public LengthFieldPrepender lengthFieldEncoder() {
		return new LengthFieldPrepender(4);
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public LengthFieldBasedFrameDecoder lengthFieldDecoder() {
		return new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4);
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
	public RouteExtractor<IsoMessage> isoMessageRouteExtractor() {
		return new IsoMessageRouteExractor();
	}
	
	// Server
	
	public SslHandler serverSSLHandler() throws GeneralSecurityException, IOException {
		SSLContext sslContext = new SSLContextBuilder()
			.keyStore(env.getProperty("server.keystore.path"))
			.keyStorePassword(env.getProperty("server.keystore.password"))
			.trustStore(env.getProperty("server.truststore.path"))
			.trustStorePassword(env.getProperty("server.truststore.password"))
			.build();
		
		SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslEngine.setNeedClientAuth(true);
		
		SslHandler handler = new SslHandler(sslEngine);
		return handler;
	}
	
	@Bean
	public MessageDispatcher serverDispatcher() {
		MessageDispatcher dispatcher = new MessageDispatcher();
		dispatcher.setName(Constant.DISPATCHER_SERVER);
		dispatcher.setRouteExtractors(isoMessageRouteExtractor());
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
					  .addFirst(serverSSLHandler())
					  
					  .addLast(loggingHandler())
					  
					  .addLast(lengthFieldEncoder())
					  .addLast(lengthFieldDecoder())
						
					  .addLast(iso8583Encoder())
					  .addLast(iso8583Decoder())
					  
					  .addLast(serverDispatcher());
				}
			});
	}
	
	@Bean(destroyMethod = "stop")
	public TcpServer tcpServer() {
		TcpServer tcpServer = new TcpServer();
		tcpServer.setHost(env.getProperty("server.host"));
		tcpServer.setPort(env.getProperty("server.port", Integer.class));
		tcpServer.setConfiguration(serverConfiguration());
		return tcpServer;
	}
	
	// Client
	
	public SslHandler clientSSLHandler() throws GeneralSecurityException, IOException {
		SSLContext sslContext = new SSLContextBuilder()
			.keyStore(env.getProperty("client.keystore.path"))
			.keyStorePassword(env.getProperty("client.keystore.password"))
			.trustStore(env.getProperty("client.truststore.path"))
			.trustStorePassword(env.getProperty("client.truststore.password"))
			.build();
		
		SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(true);
		
		SslHandler handler = new SslHandler(sslEngine);
		return handler;
	}
	
	@Bean
	public MessageDispatcher clientDispatcher() {
		MessageDispatcher dispatcher = new MessageDispatcher();
		dispatcher.setRouteExtractors(isoMessageRouteExtractor());
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
					  .addFirst(clientSSLHandler())
					 
					  .addLast(loggingHandler())
					  
					  .addLast(lengthFieldEncoder())
					  .addLast(lengthFieldDecoder())
						
					  .addLast(iso8583Encoder())
					  .addLast(iso8583Decoder())

					  .addLast(clientDispatcher());
				}
			});
	}
	
	@Bean(destroyMethod = "stop")
	public TcpClient tcpClient() {
		TcpClient tcpClient = new TcpClient();
		tcpClient.setHost(env.getProperty("server.host"));
		tcpClient.setPort(env.getProperty("server.port", Integer.class));
		tcpClient.setConfiguration(clientConfiguration());
		return tcpClient;
	}
}
