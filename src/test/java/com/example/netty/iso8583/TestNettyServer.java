package com.example.netty.iso8583;

import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.netty.AbstractTestCase;
import com.example.netty.base.iso8583.MessageFactory;
import com.example.netty.util.FutureAggregator;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	private NettyServer nettyServer;
	
	@Autowired
	private MessageFactory messageFactory;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Before
	public void before() {
		nettyServer.start();
	}
	
	@Test
	public void testOneMessage() throws InterruptedException {
		NettyClient client = appContext.getBean(NettyClient.class);
		client.start();
		
		IsoMessage message = messageFactory.newMessage(0x200);
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "Route1"));
		message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CUSTOM"));
		
		client.writeAndFlush(message).sync();
	}
	
	@Test
	public void testMultipleMessages() throws InterruptedException, ExecutionException {
		NettyClient client = appContext.getBean(NettyClient.class);
		client.start();
		
		int nbMessage = 1000;
		
		FutureAggregator<Void> futureAggregator = new FutureAggregator<>();
		
		for (int i = 0; i < nbMessage; i++) {
			IsoMessage message = messageFactory.newMessage(0x200);
			message.setField(32, new IsoValue<String>(IsoType.LLVAR, "Route1"));
			message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CUSTOM"));
			
			futureAggregator.addFuture(client.writeAndFlush(message));
		}
		
		futureAggregator.syn();
	}
	
	@Test
	public void testMultipleMessageWithMultiplesClients() throws InterruptedException, ExecutionException {
		NettyClient client = appContext.getBean(NettyClient.class);
		client.start();
		
		NettyClient client2 = appContext.getBean(NettyClient.class);
		client2.start();
		
		int nbMessage = 10;
		
		FutureAggregator<Void> futureAggregator = new FutureAggregator<>();
		
		for (int i = 0; i < nbMessage; i++) {
			IsoMessage message = messageFactory.newMessage(0x200);
			message.setField(32, new IsoValue<String>(IsoType.LLVAR, "FIELD_32_CLIENT1_" + i));
			message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CLIENT1_" + i));
			
			futureAggregator.addFuture(client.writeAndFlush(message));
		}

		for (int i = 0; i < nbMessage; i++) {
			IsoMessage message = messageFactory.newMessage(0x200);
			message.setField(32, new IsoValue<String>(IsoType.LLVAR, "FIELD_32_CLIENT2_" + i));
			message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CLIENT2_" + i));
			
			futureAggregator.addFuture(client2.writeAndFlush(message));
		}
		
		futureAggregator.syn();
	}
}