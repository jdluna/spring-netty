package com.example.netty.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
	
	@Autowired
	private ApplicationContext appContext;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public Jaxb2Marshaller jaxbMarshaller() {
	  Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	  marshaller.setPackagesToScan("com.example.netty.iso20022");
	  marshaller.setSchema(appContext.getResource("classpath:schema/pain.001.001.05.xsd"));
	  
	  Map<String, Object> map = new HashMap<String, Object>();
	  map.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	  
	  marshaller.setMarshallerProperties(map);
      return marshaller;
	}
}