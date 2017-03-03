package com.example.netty.iso20022;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
	public void testEncode() throws XmlMappingException, IOException, DatatypeConfigurationException {
		GroupHeader48 groupHeader48 = objectFactory.createGroupHeader48();
		groupHeader48.setCtrlSum(new BigDecimal(1));
		groupHeader48.setMsgId("0001223");
		groupHeader48.nbOfTxs = "000121";
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		groupHeader48.creDtTm = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		
		Authorisation1Choice authChoice = new Authorisation1Choice();
		authChoice.prtry = "001";
		authChoice.cd = Authorisation1Code.ILEV;
		
		List<Authorisation1Choice> authstns = new ArrayList<>();
		authstns.add(authChoice);
		
		groupHeader48.authstns = authstns;
		
		PartyIdentification43 partyId = new PartyIdentification43();
		partyId.nm = "0232";
		
		groupHeader48.initgPty = partyId;
	
		CustomerCreditTransferInitiationV05 cstmrCdtTrfInitn = new CustomerCreditTransferInitiationV05();
		cstmrCdtTrfInitn.setGrpHdr(groupHeader48);
		

		Document document = objectFactory.createDocument();
		document.setCstmrCdtTrfInitn(cstmrCdtTrfInitn);
		
		marshaller.marshal(document, new StreamResult(new FileWriter("schema.xml")));
	}
}
