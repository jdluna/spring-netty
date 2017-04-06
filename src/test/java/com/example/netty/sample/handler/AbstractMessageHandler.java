package com.example.netty.sample.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageHandler<IN> implements MessageHandler<IN> {

	private final static Logger logger = LoggerFactory.getLogger(AbstractMessageHandler.class);
	
	@Override
	public void handle(ChannelHandlerContext ctx, IN request) {
		try {
			logger.debug("Invoke method handle()");
			Object response = handle(request);
			
			logger.debug("Write response");
			if (response != null) {
				ctx.writeAndFlush(response);
			}
			
		} finally {
			logger.debug("Release resource");
			ReferenceCountUtil.release(request);
		}
	}
	
	public abstract Object handle(IN message);
}
