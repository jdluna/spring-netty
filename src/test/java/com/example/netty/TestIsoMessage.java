package com.example.netty;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.core.j8583.MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class TestIsoMessage extends AbstractTestCase {

	final Logger logger = LoggerFactory.getLogger(TestIsoMessage.class);

	@Autowired
	MessageFactory messageFactory;

	@Test
	public void testGetMessageTemplate() {
		IsoMessage messageTemplate = messageFactory.getMessageTemplate(0x200);
		Assert.assertNotNull(messageTemplate);
	}

	@Test
	public void testNewMessage() {
		IsoMessage message = messageFactory.newMessage(0x200);
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "700"));
		
		logger.debug(message.debugString());
	}

	@Test
	public void testParseMessage() throws UnsupportedEncodingException, ParseException {
		IsoMessage message = messageFactory.newMessage(0x200);
		message.setField(32, new IsoValue<String>(IsoType.LLVAR, "700"));
		
		logger.debug("New Message -> {}", message.debugString());

		byte[] messageData = message.writeData();

		IsoMessage parseMessage = messageFactory.parseMessage(messageData, 0);
		logger.debug("Parse Message -> {}", parseMessage.debugString());

		Assert.assertEquals(message.debugString(), parseMessage.debugString());
	}
}
