package com.example.netty.core.j8583.parser;

import com.example.netty.core.j8583.Iso8583;
import com.example.netty.core.j8583.MessageFactory;

public abstract class AbstractIsoMessageParser<T extends Iso8583> implements IsoMessageParser<T>{

	private final MessageFactory messageFactory;

	public AbstractIsoMessageParser(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}
}
