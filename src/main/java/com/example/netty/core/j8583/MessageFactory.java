package com.example.netty.core.j8583;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.parse.ConfigParser;

public class MessageFactory extends com.solab.iso8583.MessageFactory<IsoMessage> {

	private final static Logger logger = LoggerFactory.getLogger(MessageFactory.class);
	
	public void setConfigResource(Resource resource) throws IOException {
		Assert.notNull(resource, "Resource is undefined");
		ConfigParser.configureFromUrl(this, resource.getURL());
		
		logger.debug("Register configuration {} to message factory", resource.getURL().getPath());
	}
	
	public void setConfigResources(List<? extends Resource> resources) throws IOException {
		Assert.notEmpty(resources, "Resources is empty");
		for (Resource resource : resources) {
			setConfigResource(resource);
		}
	}
}
