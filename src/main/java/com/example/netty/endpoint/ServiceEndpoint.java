package com.example.netty.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import com.example.netty.service.SimpleService;
import com.solab.iso8583.IsoMessage;

@MessageEndpoint
public class ServiceEndpoint {

	@Autowired
	private SimpleService simpleService;
	
	@ServiceActivator(inputChannel = "testChannel")
	public IsoMessage handle(IsoMessage message) {
		simpleService.processMessage(message);

		return message;
	}
}
