package com.example.netty.iso8583;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.AbstractTestCase;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class TestNettyServer extends AbstractTestCase implements ChannelFutureListener {
	
	@Autowired
	private NettyServer nettyServer;
	
	@Autowired
	private NettyClient nettyClient1;
	
	@Autowired
	private MessageFactory messageFactory;
	
	private int REQ_TOTAL = 1;
	
	private int counter = 0;

	@Test
	public void test1() throws InterruptedException {
		nettyServer.start();
		
		nettyClient1.start();
		
		for (int i = 0; i < REQ_TOTAL; i++) {
			IsoMessage message = messageFactory.newMessage(0x200);
			message.setField(32, new IsoValue<String>(IsoType.LLVAR, "FIELD_32_" + (i + 1) + "_"));
			message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_" + (i + 1) + "_"));
			
			nettyClient1.writeAndFlush(message.debugString()).addListener(this);
		}
		
		while (counter != REQ_TOTAL) {
			Thread.sleep(200000);
		}
	}

	@Override
	public void operationComplete(ChannelFuture paramF) throws Exception {
		counter++;
	}
}
