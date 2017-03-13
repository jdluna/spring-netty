package com.example.netty.endpoint;

import org.springframework.integration.annotation.Gateway;

import com.solab.iso8583.IsoMessage;

public interface ServiceGateway {

	@Gateway(requestChannel = "messageChannel")
	IsoMessage routeMessage(IsoMessage request);
}
