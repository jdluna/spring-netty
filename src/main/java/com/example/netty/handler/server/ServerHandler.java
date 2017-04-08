package com.example.netty.handler.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.netty.Constant;
import com.example.netty.core.handler.Handler;
import com.example.netty.core.handler.MessageHandler;
import com.solab.iso8583.IsoMessage;

@Component
@Handler(value = "0200_01", dispatcher = Constant.DISPATCHER_SERVER, asyn = true)
public class ServerHandler implements MessageHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	@Override
	public Object handle(IsoMessage message) throws InterruptedException {
		logger.debug("Server get message -> {}", message.debugString());
		
		Thread.sleep(10000);
		
		return message;
	}
}
