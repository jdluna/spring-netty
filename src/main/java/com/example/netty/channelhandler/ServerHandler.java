package com.example.netty.channelhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.base.channelhandler.RouteMapping;
import com.solab.iso8583.IsoMessage;

@RouteMapping(name = "200")
public class ServerHandler extends SimpleChannelInboundHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage request) throws Exception {
		logger.debug("Server get message : " + request.debugString());
		
		ctx.channel().writeAndFlush(request);
	}
}