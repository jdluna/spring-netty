package com.example.netty.iso8583.codec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.iso8583.MessageFactory;
import com.solab.iso8583.IsoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ISO8583Decoder extends LengthFieldBasedFrameDecoder {

	private static Logger logger = LoggerFactory.getLogger(ISO8583Decoder.class);

	private final MessageFactory messageFactory;

	public ISO8583Decoder(MessageFactory messageFactory) {
		super(1048576, 0, 4, 0, 4);
		this.messageFactory = messageFactory;
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}

		IsoMessage result = null;

		try {
			byte[] bytes = new byte[frame.readableBytes()];

			frame.getBytes(0, bytes);

			result = this.messageFactory.parseMessage(bytes, 0);

		} catch (ParseException | UnsupportedEncodingException e) {
			logger.warn("message is not iso8583");
		}

		return result;
	}

	@Override
	protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
		return buffer.slice(index, length);
	}
}