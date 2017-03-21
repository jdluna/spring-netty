package com.example.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.base.channelhandler.routing.RouteMapping;
import com.example.netty.base.iso8583.ISO8583;
import com.example.netty.service.SimpleService;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
@RouteMapping(name = "200", group = "server")
public class ServerHandler extends SimpleChannelInboundHandler<ISO8583> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	@Autowired
	private SimpleService simpleService;
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ISO8583 message) throws Exception {
		logger.debug("Server get message : " + message.debugString());
		
		simpleService.processMessage(message);
		
		ctx.channel().writeAndFlush(message);	
	}
}