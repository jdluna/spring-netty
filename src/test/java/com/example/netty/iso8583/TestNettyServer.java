package com.example.netty.iso8583;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.netty.AbstractTestCase;
import com.example.netty.util.NettyClient;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	ApplicationContext appContext;

	@Test
	public void doTest() {
		NettyClient nettyClient = appContext.getBean(NettyClient.class);
		
		nettyClient.start();
		
		
		nettyClient.stop();
	}
}
