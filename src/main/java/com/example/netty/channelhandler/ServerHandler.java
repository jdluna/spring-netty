package com.example.netty.iso8583.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileWriter;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

@Sharable
public class ISO8583ServerHandler extends SimpleChannelInboundHandler<IsoMessage> {

	private static Logger logger = LoggerFactory.getLogger(ISO8583ServerHandler.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IsoMessage message) throws Exception {
		logger.debug("Server get message : " + message.debugString());
		
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		
		String remoteHost = socketAddress.getHostName();
		int remotePort = socketAddress.getPort();
		
		File folder = new File("messages");
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		File file = new File(String.format("messages/iso8583_%s_%d.txt", remoteHost, remotePort));
		
		FileWriter fw = new FileWriter(file, true);
		
		fw.write(message.debugString());
		fw.write("\n");
		fw.close();
		
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, message.getField(32).getValue() + "_BACK"));
		message.setField(48, new IsoValue<String>(IsoType.LLLVAR, message.getField(32).getValue() + "_BACK"));
		
		ctx.channel().writeAndFlush(message);
	}
}