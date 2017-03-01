package com.example.netty.iso8583.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.iso8583.MessageFactory;
import com.solab.iso8583.IsoMessage;

public class ISO8583Decoder extends ByteToMessageDecoder {
	
	private static Logger logger = LoggerFactory.getLogger(ISO8583Decoder.class);
	
	private final MessageFactory messageFactory;

	public ISO8583Decoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
		if (byteBuf.isReadable()) {
			try {
				byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				
				IsoMessage isoMessage = this.messageFactory.parseMessage(bytes, 0);

				out.add(isoMessage);
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}