package com.example.netty.sample.example.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.netty.sample.handler.MessageHandler;
import com.example.netty.sample.handler.annotation.RouteMapping;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandlerContext;

@Component
@RouteMapping(name = "0200_01", group = "client")
public class ClientHandler implements MessageHandler<IsoMessage> {

	private final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	public void handle(ChannelHandlerContext ctx, IsoMessage message) {
		logger.debug("Client handler get message -> {}", message);
	}
}
