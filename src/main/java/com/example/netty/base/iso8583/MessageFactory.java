package com.example.netty.base.iso8583;

public class MessageFactory extends com.solab.iso8583.MessageFactory<ISO8583> {

	@Override
	protected ISO8583 createIsoMessage(String header) {
		return new ISO8583(header);
	}
	
	@Override
	protected ISO8583 createIsoMessageWithBinaryHeader(byte[] binHeader) {
		return new ISO8583(binHeader);
	}
}
