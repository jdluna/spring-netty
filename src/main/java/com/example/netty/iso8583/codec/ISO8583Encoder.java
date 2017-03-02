package com.example.netty.iso8583.codec;

import java.util.List;

import com.example.netty.iso8583.ISO8583BytesWrapper;
import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class ISO8583Encoder extends MessageToMessageEncoder<IsoMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IsoMessage msg, List<Object> out) throws Exception {
		ISO8583BytesWrapper wrapper = new ISO8583BytesWrapper();
		wrapper.setBytes(msg.writeData());
		
		out.add(wrapper);
	}
}
