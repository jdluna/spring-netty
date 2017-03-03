package com.example.netty.iso20022;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.example.netty.AbstractTestCase;

public class TestISO20022 extends AbstractTestCase {

	@Autowired
	private Jaxb2Marshaller marshaller;
	
	@Autowired
	private ObjectFactory objectFactory;
	
	@Test
	public void testEncode() throws XmlMappingException, IOException {
		GroupHeader48 groupHeader48 = objectFactory.createGroupHeader48();
		groupHeader48.setCtrlSum(new BigDecimal(200));
		groupHeader48.setMsgId("0001223");
		
		CustomerCreditTransferInitiationV05 cstmrCdtTrfInitn = objectFactory.createCustomerCreditTransferInitiationV05();
		cstmrCdtTrfInitn.setGrpHdr(groupHeader48);
		
		Document document = objectFactory.createDocument();
		document.setCstmrCdtTrfInitn(cstmrCdtTrfInitn);
		
		Object object = new JAXBElement<Document>(new QName("uri","local"), Document.class, document);
		
		marshaller.marshal(object, new StreamResult(new FileWriter("schema.xml")));
	}
}
