package com.example.netty.sample.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.netty.sample.handler.AbstractMessageHandler;
import com.example.netty.sample.handler.annotation.NonBlocking;
import com.example.netty.sample.handler.annotation.RouteMapping;
import com.solab.iso8583.IsoMessage;

@Component
@RouteMapping(name = "0200_30", group = "server")
public class RequestResponseHandler extends AbstractMessageHandler<IsoMessage> {
	
	private final static Logger logger = LoggerFactory.getLogger(RequestResponseHandler.class);

	@NonBlocking
	@Override
	public IsoMessage handle(IsoMessage message) {
		logger.debug("Server get message : " + message.debugString());
		
		return message;
	}
}
