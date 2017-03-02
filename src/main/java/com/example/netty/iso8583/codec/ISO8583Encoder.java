package com.example.netty.iso8583.codec;

import java.io.ObjectOutputStream;

import com.example.netty.iso8583.ISO8583BytesWrapper;
import com.example.netty.util.CompactObjectOutputStream;
import com.solab.iso8583.IsoMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ISO8583Encoder extends MessageToByteEncoder<IsoMessage> {

	protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) throws Exception {
		int startIdx = out.writerIndex();

		ISO8583BytesWrapper message = new ISO8583BytesWrapper();
		message.setBytes(isoMessage.writeData());
		
		ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(new byte[4]);
        
        ObjectOutputStream oout = new CompactObjectOutputStream(bout);
        oout.writeObject(message);
        oout.flush();
        oout.close();
        
		int endIdx = out.writerIndex();

		out.setInt(startIdx, endIdx - startIdx - 4);
	}
}
