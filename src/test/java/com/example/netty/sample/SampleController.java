package com.example.netty.sample;

import com.example.netty.core.handler.Handler;
import com.example.netty.core.handler.MessageHandler;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandlerContext;

@Handler(value = "200_01", asyn = false)
public class SampleController implements MessageHandler<IsoMessage> {

	@Override
	public void handle(ChannelHandlerContext ctx, IsoMessage message) throws Exception {
		
	}
}
