package com.example.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.base.channelhandler.routing.RouteMapping;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@RouteMapping(name = "Route2")
@Sharable
public class ServerHandler2 extends SimpleChannelInboundHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler2.class);
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, IsoMessage request) throws Exception {
		logger.debug("Server 2 get message : " + request.debugString());
		
		ctx.channel().writeAndFlush(request);
		
	}
}