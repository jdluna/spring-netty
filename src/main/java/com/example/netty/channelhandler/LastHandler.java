package com.example.netty.channelhandler;

import com.example.netty.base.channel.handler.MessageMapping;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@MessageMapping(name = "200", order = 1)
public class LastHandler extends SimpleChannelInboundHandler<IsoMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage msg) throws Exception {
		ctx.channel().writeAndFlush(msg);
	}
}
