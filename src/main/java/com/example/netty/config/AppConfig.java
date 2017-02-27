package com.example.netty.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.example.netty.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

@Configuration
@ComponentScan(
	basePackages = "com.example.netty", 
	excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = "com.example.netty.iso20022..*")
	}
)
public class AppConfig {
	
	@Bean
	public MessageFactory messageFactory() throws IOException {
		MessageFactory messageFactory = new MessageFactory();
		messageFactory.setAssignDate(true);
		messageFactory.setUseBinaryBitmap(true);
		messageFactory.setUseBinaryMessages(true);
		
		ConfigParser.configureFromClasspathConfig(messageFactory, "j8583.xml");
		
		return messageFactory;
	}
}
