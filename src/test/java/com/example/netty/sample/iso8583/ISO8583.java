package com.example.netty.sample.iso8583;

import com.example.netty.sample.handler.Routable;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;

public class ISO8583 extends IsoMessage implements Routable {

	public ISO8583(String header) {
		super(header);
	}

	public ISO8583(byte[] binaryHeader) {
		super(binaryHeader);
	}

	@Override
	public String getRouteName() {
		String msgType = Integer.toHexString(getType());
		IsoValue<String> processingCodeField = getField(3);
		return new String(msgType + "_" + processingCodeField.getValue()).substring(0, 2);
	}
	
	@Override
	public String toString() {
		return debugString();
	}
}
