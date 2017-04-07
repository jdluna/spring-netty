package com.example.netty.core.codec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.core.j8583.MessageFactory;
import com.solab.iso8583.IsoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class IsoMessageDecoder extends ByteToMessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(IsoMessageDecoder.class);
	
	private final MessageFactory messageFactory;

	public IsoMessageDecoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.isReadable()) {
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			
			try {
				IsoMessage isoMessage = messageFactory.parseMessage(bytes, 0);
				if (isoMessage != null) {
					out.add(isoMessage);
				}

			} catch (ParseException | UnsupportedEncodingException e) {
				logger.warn("Message is not iso8583 -> forward to next decoder");
			}
		}
	}
}