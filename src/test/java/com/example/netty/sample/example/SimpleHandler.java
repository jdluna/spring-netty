package com.example.netty.sample.example;

import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.netty.sample.handler.MessageHandler;
import com.example.netty.sample.handler.RouteMapping;
import com.solab.iso8583.IsoMessage;

@Component
@RouteMapping("0200_01")
public class SimpleHandler implements MessageHandler<IsoMessage> {

	private final static Logger logger = LoggerFactory.getLogger(SimpleHandler.class);
	
	@Override
	public void handle(ChannelHandlerContext ctx, IsoMessage message) {
		logger.debug("Simple Handler get message -> {}", message);
	}
}
