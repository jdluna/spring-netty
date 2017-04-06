package com.example.netty.sample.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.netty.sample.handler.AbstractMessageHandler;
import com.example.netty.sample.handler.RouteMapping;
import com.solab.iso8583.IsoMessage;

@Component
@RouteMapping("0200_30")
public class RequestResponseHandler extends AbstractMessageHandler<IsoMessage> {
	
	private final static Logger logger = LoggerFactory.getLogger(RequestResponseHandler.class);
	
	@Override
	public IsoMessage handle(IsoMessage message) {
		logger.debug("Client get message : " + message.debugString());
		
		return message;
	}
}
