package com.example.netty.sample.example;

import org.springframework.stereotype.Component;

import com.example.netty.sample.handler.MessageHandler;
import com.example.netty.sample.handler.RouteMapping;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandlerContext;

@Component
@RouteMapping("0200_01")
public class SimpleHandler implements MessageHandler<IsoMessage> {

	@Override
	public void handle(ChannelHandlerContext ctx, IsoMessage message) {
		
	}
}
