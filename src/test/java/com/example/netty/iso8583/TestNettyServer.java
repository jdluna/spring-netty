package com.example.netty.iso8583;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

import org.junit.Test;

public class TestNettyServer {

	@Test
	public void doTest() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap
				.group(workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true) 
				.handler(new ChannelInitializer<SocketChannel>() {
				
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						 ChannelPipeline channelPipeline = ch.pipeline();
						 channelPipeline.addLast(new LoggingHandler(LogLevel.INFO));
					}
			});
		
			ChannelFuture f = bootstrap.connect(new InetSocketAddress("localhost", 5000)).sync();
			f.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
