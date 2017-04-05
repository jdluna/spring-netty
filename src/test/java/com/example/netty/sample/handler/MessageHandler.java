package com.example.netty.sample.handler;

import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler<T> {
	
	void handle(ChannelHandlerContext ctx, T message);
}
