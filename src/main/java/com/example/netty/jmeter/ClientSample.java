package com.example.netty.jmeter;

import java.net.InetSocketAddress;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.netty.base.iso8583.MessageFactory;
import com.example.netty.config.AppConfig;
import com.example.netty.util.FutureAggregator;
import com.example.netty.util.NettyClient;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class ClientSample {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		
		NettyClient nettyClient = appContext.getBean(NettyClient.class);
		nettyClient.start();
		
		InetSocketAddress remoteAddress = (InetSocketAddress) nettyClient.getChannel().remoteAddress();
		String host = remoteAddress.getHostName();
		int port = remoteAddress.getPort();
		
		FutureAggregator<Void> futureAggregator = new FutureAggregator<>();
		
		MessageFactory msgFacotry = appContext.getBean(MessageFactory.class);
		
		int msgNum = Integer.valueOf(args[0]);
		
		for (int i = 0; i < msgNum; i++) {
			IsoMessage message = msgFacotry.newMessage(0x200);
			message.setField(32, new IsoValue<String>(IsoType.LLVAR, String.format("#%s:%d#_", host, port) + i));
			
			futureAggregator.addFuture(nettyClient.writeAndFlush(message));
		}
		
		futureAggregator.syn();
		
		nettyClient.stop();
	}
}
