package com.example.netty.iso8583.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.solab.iso8583.IsoMessage;

public class ISO8583Encoder extends MessageToByteEncoder<IsoMessage> {

	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) throws Exception {
		int startIdx = out.writerIndex();

		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		bout.write(LENGTH_PLACEHOLDER);
		bout.write(isoMessage.writeData());
		bout.close();

		int endIdx = out.writerIndex();

		out.setInt(startIdx, endIdx - startIdx - 4);
	}
}
