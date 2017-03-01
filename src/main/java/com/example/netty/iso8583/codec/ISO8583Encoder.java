package com.example.netty.iso8583.codec;

import com.solab.iso8583.IsoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ISO8583Encoder extends MessageToByteEncoder<IsoMessage> {

	protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) throws Exception {
		int startIdx = out.writerIndex();

		out.writeBytes(isoMessage.writeToBuffer(4));
		
		int endIdx = out.writerIndex();

		out.setInt(startIdx, endIdx - startIdx - 4);
	}
}
