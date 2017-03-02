package com.example.netty.jmeter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.netty.config.AppConfig;
import com.example.netty.iso8583.MessageFactory;
import com.example.netty.util.NettyClient;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class ClientSample {
	
	static int counter = 0;
	static int msgNum;
	
	static ChannelFutureListener listener = new ChannelFutureListener() {
		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			counter++;
		}
	};
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		msgNum = Integer.valueOf(args[0]);
		
		MessageFactory msgFacotry = appContext.getBean(MessageFactory.class);
		
		NettyClient nettyClient = appContext.getBean(NettyClient.class);
		nettyClient.start();
		
		InetSocketAddress remoteAddress = (InetSocketAddress) nettyClient.getChannel().remoteAddress();
		String host = remoteAddress.getHostName();
		int port = remoteAddress.getPort();
		
		
		for (int i = 0; i < msgNum; i++) {
			IsoMessage message = msgFacotry.newMessage(0x200);
			message.setField(32, new IsoValue<String>(IsoType.LLVAR, String.format("#%s:%d#_", host, port) + i));
			
			nettyClient.writeAndFlush(message).addListener(listener);
		}
		
		while (counter != msgNum) {
			Thread.sleep(1000);
		}
	}
}
