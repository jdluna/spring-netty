package com.example.netty.endpoint;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;

import com.solab.iso8583.IsoMessage;

@MessageEndpoint
public class MessageRouter {

	@Router(inputChannel = "messageChannel")
	public String routeMessage(IsoMessage isoMessage) {
		return "testChannel";
	}
}
