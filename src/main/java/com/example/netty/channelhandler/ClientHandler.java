package com.example.netty.channelhandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;

@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage message) throws Exception {
		logger.debug("Client get message : " + message.debugString());		
	}
}
