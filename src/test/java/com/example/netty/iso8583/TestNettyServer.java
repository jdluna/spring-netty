package com.example.netty.iso8583;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.AbstractTestCase;
import com.example.netty.util.NettyClient;
import com.example.netty.util.NettyServer;

public class TestNettyServer extends AbstractTestCase {
	
	@Autowired
	NettyServer nettyServer;
	
	@Autowired
	NettyClient nettyClient1;

	@Test
	public void doTest() throws InterruptedException {
		nettyServer.start();
		
		nettyClient1.start();
		
		nettyClient1.writeAndFlush("Hello").sync();
	}
}
