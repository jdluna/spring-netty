package com.example.netty.handler.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.netty.Constant;
import com.example.netty.core.handler.Handler;
import com.example.netty.core.handler.MessageHandler;
import com.solab.iso8583.IsoMessage;

@Component
@Handler(value = "0200_01", dispatcher = Constant.DISPATCHER_CLIENT)
public class ClientHandler implements MessageHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	@Override
	public Object handle(IsoMessage message) throws Exception {
		logger.debug("Client get message -> {}", message.debugString());
		
		return null;
	}
}
