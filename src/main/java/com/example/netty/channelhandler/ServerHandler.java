package com.example.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.base.channelhandler.routing.RouteMapping;
import com.example.netty.base.iso8583.ISO8583;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
@RouteMapping(name = "200")
public class ServerHandler extends SimpleChannelInboundHandler<ISO8583> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, ISO8583 request) throws Exception {
		logger.debug("Server 1 get message : " + request.debugString());
		
		//Thread.sleep(10000);
		
		ctx.channel().writeAndFlush(request);	
	}
}