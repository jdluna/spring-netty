package com.example.netty.channelhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.netty.endpoint.ServiceGateway;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
@Component
public class ServerHandler extends SimpleChannelInboundHandler<IsoMessage> {

	@Autowired
	private ServiceGateway serviceGateway;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage request) throws Exception {
		IsoMessage response = serviceGateway.routeMessage(request);
		
		ctx.channel().writeAndFlush(response);
	}
}