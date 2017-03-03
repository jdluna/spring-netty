package com.example.netty.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@ComponentScan("com.example.netty")
@PropertySource("classpath:application.properties")
public class AppConfig {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public Jaxb2Marshaller jaxbMarshaller() {
	  Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
	  jaxb2Marshaller.setPackagesToScan("com.example.netty.iso20022");
	  
	  Map<String, Object> map = new HashMap<String, Object>();
	  map.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	  
	  jaxb2Marshaller.setMarshallerProperties(map);
      return jaxb2Marshaller;
	}
}