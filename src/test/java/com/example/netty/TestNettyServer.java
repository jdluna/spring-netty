package com.example.netty;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.netty.core.endpoint.TcpClient;
import com.example.netty.core.endpoint.TcpServer;
import com.example.netty.core.j8583.MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	private TcpServer tcpServer;
	
	@Autowired
	private MessageFactory messageFactory;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Before
	public void before() {
		tcpServer.start();
	}
	
	@Test
	public void testOneMessage() throws InterruptedException {
		TcpClient tcpClient = appContext.getBean(TcpClient.class);
		tcpClient.start();
		
		IsoMessage message = messageFactory.newMessage(0x200);
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "Route1"));
		message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CUSTOM"));
		
		Thread.sleep(5000);
	}
}