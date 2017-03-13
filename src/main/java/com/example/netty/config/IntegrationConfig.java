package com.example.netty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@IntegrationComponentScan("com.example.netty.endpoint")
public class IntegrationConfig {

	@Bean
	public MessageChannel messageChannel() {
		return new DirectChannel();
	}
}
