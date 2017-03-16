package com.example.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.base.channelhandler.routing.RouteMapping;
import com.example.netty.base.iso8583.ISO8583;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@RouteMapping(name = "300")
@Sharable
public class ServerHandler2 extends SimpleChannelInboundHandler<ISO8583> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler2.class);
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ISO8583 request) throws Exception {
		logger.debug("Server 2 get message : " + request.debugString());
		
		ctx.channel().writeAndFlush(request);
		
	}
}