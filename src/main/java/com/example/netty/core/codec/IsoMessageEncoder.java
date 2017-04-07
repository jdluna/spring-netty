package com.example.netty.core.codec;

import com.solab.iso8583.IsoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class IsoMessageEncoder extends MessageToByteEncoder<IsoMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IsoMessage msg, ByteBuf out) throws Exception {
		if (out.isWritable()) {
			byte[] bytes = msg.writeData();
			out.writeBytes(bytes);
		}
	}
}
