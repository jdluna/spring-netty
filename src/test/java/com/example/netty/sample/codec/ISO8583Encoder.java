package com.example.netty.sample.codec;

import com.example.netty.sample.iso8583.ISO8583;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class ISO8583Encoder extends MessageToByteEncoder<ISO8583> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ISO8583 msg, ByteBuf out) throws Exception {
		if (out.isWritable()) {
			byte[] bytes = msg.writeData();
			out.writeBytes(bytes);
		}
	}
}
