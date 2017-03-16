package com.example.netty.base.iso8583;

import com.example.netty.base.channelhandler.routing.Routable;
import com.solab.iso8583.IsoMessage;

public class ISO8583 extends IsoMessage implements Routable {

	public ISO8583(String header) {
		super(header);
	}

	public ISO8583(byte[] binaryHeader) {
		super(binaryHeader);
	}

	@Override
	public String getName() {
		return Integer.toHexString(getType());
	}
}
