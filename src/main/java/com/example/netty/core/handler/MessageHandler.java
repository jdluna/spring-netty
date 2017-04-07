package com.example.netty.core.handler;

import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler<T> {
	
	void handle(ChannelHandlerContext ctx, T message) throws Exception;
}
