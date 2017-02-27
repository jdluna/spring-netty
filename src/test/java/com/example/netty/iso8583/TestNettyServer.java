package com.example.netty.iso8583;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.netty.AbstractTestCase;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	private ApplicationContext appContext;

	@Test
	public void doTest() throws InterruptedException {
		NettyServer nettyServer = appContext.getBean(NettyServer.class);
		nettyServer.start();
		
		NettyClient nettyClient = appContext.getBean(NettyClient.class);
		nettyClient.start();

		nettyClient.writeAndFlush("Hello");
	}
}
