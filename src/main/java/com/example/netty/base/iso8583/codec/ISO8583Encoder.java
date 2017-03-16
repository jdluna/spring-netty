package com.example.netty.base.iso8583.codec;

import java.util.List;

import com.example.netty.base.iso8583.ISO8583;
import com.example.netty.base.iso8583.ISO8583BytesWrapper;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class ISO8583Encoder extends MessageToMessageEncoder<ISO8583> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ISO8583 msg, List<Object> out) throws Exception {
		ISO8583BytesWrapper wrapper = new ISO8583BytesWrapper();
		wrapper.setBytes(msg.writeData());
		
		out.add(wrapper);
	}
}
