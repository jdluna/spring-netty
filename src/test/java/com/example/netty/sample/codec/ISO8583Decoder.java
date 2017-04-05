package com.example.netty.sample.codec;

import java.util.List;

import com.example.netty.sample.iso8583.ISO8583;
import com.example.netty.sample.iso8583.MessageFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ISO8583Decoder extends ByteToMessageDecoder {

	private final MessageFactory messageFactory;

	public ISO8583Decoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.isReadable()) {
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			
			ISO8583 isoMessage = messageFactory.parseMessage(bytes, 0);
			if (isoMessage != null) {
				out.add(isoMessage);
			}
		}
	}
}