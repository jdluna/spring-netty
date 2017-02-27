package com.example.netty.iso8583.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext paramChannelHandlerContext, String paramI) throws Exception {
		logger.debug("SUCCESS");
	}
}
