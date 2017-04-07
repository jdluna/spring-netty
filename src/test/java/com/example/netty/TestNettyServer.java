package com.example.netty;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.netty.core.endpoint.Client;
import com.example.netty.core.endpoint.Server;
import com.example.netty.core.j8583.MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	private Server server;
	
	@Autowired
	private MessageFactory messageFactory;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Before
	public void before() {
		server.start();
	}
	
	@Test
	public void testOneMessage() throws InterruptedException {
		Client client = appContext.getBean(Client.class);
		client.start();
		
		IsoMessage message = messageFactory.newMessage(0x200);
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "Route1"));
		message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CUSTOM"));
	}
}