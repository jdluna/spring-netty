package com.example.netty.iso8583.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage message) throws Exception {
		logger.debug("Server get message : " + message.debugString());
		
		File file = new File("test.txt");
		FileWriter fw = new FileWriter(file, true);
		
		fw.write(message.debugString());
		fw.write("\n");
		
		fw.close();
		
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "FIELD_32_NEW"));
		message.setField(48, new IsoValue<String>(IsoType.LLLVAR, "FIELD_48_NEW"));
		
		ctx.channel().writeAndFlush(message);
	}
}