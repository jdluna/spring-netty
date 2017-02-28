package com.example.netty.iso8583.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.example.netty.iso8583.MessageFactory;
import com.solab.iso8583.IsoMessage;

public class ISO8583Decoder extends ByteToMessageDecoder {
	
	private final MessageFactory messageFactory;

	public ISO8583Decoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
		if (byteBuf.isReadable()) {
			
			byte[] bytes = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(bytes);
			
			IsoMessage isoMessage = this.messageFactory.parseMessage(bytes, 0);
			
			if (isoMessage != null) {
				out.add(isoMessage);
			}
		}
	}
}