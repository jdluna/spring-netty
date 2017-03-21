package com.example.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.base.channelhandler.routing.RouteMapping;
import com.example.netty.base.iso8583.ISO8583;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
@RouteMapping(name = "200", group = "client")
public class ClientHandler extends SimpleChannelInboundHandler<ISO8583> {

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ISO8583 message) throws Exception {
		logger.debug("Client get message : " + message.debugString());		
	}
}
