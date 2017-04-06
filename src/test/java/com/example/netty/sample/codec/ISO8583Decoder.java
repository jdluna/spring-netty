package com.example.netty.sample.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.sample.iso8583.ISO8583;
import com.example.netty.sample.iso8583.MessageFactory;

public class ISO8583Decoder extends ByteToMessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(ISO8583Decoder.class);
	
	private final MessageFactory messageFactory;

	public ISO8583Decoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.isReadable()) {
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			
			try {
				ISO8583 isoMessage = messageFactory.parseMessage(bytes, 0);
				if (isoMessage != null) {
					out.add(isoMessage);
				}

			} catch (ParseException | UnsupportedEncodingException e) {
				logger.warn("Message is not iso8583 -> forward to next decoder");
			}
		}
	}
}