package com.example.netty.endpoint;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import com.solab.iso8583.IsoMessage;

@MessageEndpoint
public class ServiceEndpoint {

	@ServiceActivator(inputChannel = "testChannel")
	public IsoMessage handle(IsoMessage message) {
		return message;
	}
}
