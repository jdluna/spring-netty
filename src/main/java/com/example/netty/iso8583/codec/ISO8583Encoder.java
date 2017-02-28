package com.example.netty.iso8583.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.solab.iso8583.IsoMessage;

public class ISO8583Encoder extends MessageToByteEncoder<IsoMessage> {

	protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) throws Exception {
		if (out.isWritable()) {
			out.writeBytes(isoMessage.writeData());
		}
	}
}
