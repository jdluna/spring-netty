package com.example.netty.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.example.netty.iso8583.MessageFactory;
import com.example.netty.iso8583.codec.ISO8583Decoder;
import com.example.netty.iso8583.codec.ISO8583Encoder;
import com.solab.iso8583.parse.ConfigParser;

@Configuration
public class ISO8583Config {

	private static Logger logger = LoggerFactory.getLogger(ISO8583Config.class);
	
	@Bean
	public MessageFactory messageFactory() {
		MessageFactory messageFactory = new MessageFactory();
		messageFactory.setAssignDate(true);
		messageFactory.setUseBinaryBitmap(true);
		messageFactory.setUseBinaryMessages(true);
		
		try {
			ConfigParser.configureFromClasspathConfig(messageFactory, "j8583.xml");
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		return messageFactory;
	}
	
	@Bean
	public ISO8583Encoder iso8583Encoder() {
		return new ISO8583Encoder();
	}
	
	@Bean
	@Scope(scopeName = "prototype")
	public ISO8583Decoder iso8583Decoder() {
		return new ISO8583Decoder(messageFactory());
	}
}
