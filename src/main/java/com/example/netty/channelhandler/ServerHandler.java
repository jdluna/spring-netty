package com.example.netty.channelhandler;

import com.example.netty.endpoint.ServiceGateway;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<IsoMessage> {

	private ServiceGateway serviceGateway;
	
	public ServerHandler(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage request) throws Exception {
		IsoMessage response = serviceGateway.routeMessage(request);
		
		ctx.channel().writeAndFlush(response);
	}
}