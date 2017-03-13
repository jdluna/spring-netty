package com.example.netty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.messaging.MessageChannel;

import com.example.netty.endpoint.ServiceGateway;

@Configuration
@EnableIntegration
@IntegrationComponentScan("com.example.netty.endpoint")
public class IntegrationConfig {

	@Bean
	public MessageChannel messageChannel() {
		return new DirectChannel();
	}

	@Bean
	public GatewayProxyFactoryBean serviceGateway() {
		GatewayProxyFactoryBean factoryBean = new GatewayProxyFactoryBean(ServiceGateway.class);
		return factoryBean;
	}
}
