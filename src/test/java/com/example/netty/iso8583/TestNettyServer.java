package com.example.netty.iso8583;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.AbstractTestCase;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	private NettyServer nettyServer;
	
	@Autowired
	private NettyClient nettyClient1;
	
	@Autowired
	private MessageFactory messageFactory;
	
	@Before
	public void before() {
		nettyServer.start();
		nettyClient1.start();
	}
	
	@Test
	public void testOneMessage() throws InterruptedException {
		IsoMessage message = messageFactory.newMessage(0x200);
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "FIELD_32_CUSTOM"));
		message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_CUSTOM"));
		
		nettyClient1.writeAndFlush(message).sync();
	}
}
