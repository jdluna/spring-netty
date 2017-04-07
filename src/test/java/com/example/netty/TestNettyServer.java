package com.example.netty;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.netty.core.endpoint.NettyClient;
import com.example.netty.core.endpoint.NettyServer;
import com.example.netty.core.j8583.MessageFactory;
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
	}
}