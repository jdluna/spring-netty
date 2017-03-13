package com.example.netty.endpoint;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import com.solab.iso8583.IsoMessage;

@MessagingGateway
public interface ServiceGateway {

	@Gateway(requestChannel = "messageChannel")
	IsoMessage routeMessage(IsoMessage request);
}
