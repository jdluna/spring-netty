package com.example.netty.iso8583.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.netty.iso8583.ISO8583BytesWrapper;
import com.example.netty.iso8583.MessageFactory;
import com.solab.iso8583.IsoMessage;

public class ISO8583Decoder extends SimpleChannelInboundHandler<ISO8583BytesWrapper> {

	private static Logger logger = LoggerFactory.getLogger(ISO8583Decoder.class);

	private final MessageFactory messageFactory;

	public ISO8583Decoder(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ISO8583BytesWrapper msg) throws Exception {
		try {
			IsoMessage isoMessage = this.messageFactory.parseMessage(msg.getBytes(), 0);
			
			ctx.fireChannelRead(isoMessage);

		} catch (ParseException | UnsupportedEncodingException e) {
			logger.warn("message is not iso8583 -> forward to next handler");
			
			ctx.fireChannelRead(msg);
		}
	}
}