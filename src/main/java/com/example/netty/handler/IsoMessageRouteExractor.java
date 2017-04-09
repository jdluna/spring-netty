package com.example.netty.handler;

import com.example.netty.core.handler.RouteExtractor;
import com.example.netty.core.util.Iso8583Utils;
import com.solab.iso8583.IsoMessage;

public class IsoMessageRouteExractor implements RouteExtractor<IsoMessage> {

	@Override
	public String extract(IsoMessage message) {
		return Iso8583Utils.getTxId(message);
	}
}
