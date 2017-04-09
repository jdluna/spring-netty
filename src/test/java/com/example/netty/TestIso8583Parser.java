package com.example.netty;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.core.j8583.MessageFactory;
import com.example.netty.vo.BalanceInquiryVO;
import com.solab.iso8583.IsoMessage;


public class TestIso8583Parser extends AbstractTestCase {

	@Autowired
	private MessageFactory msgF;
	
	@Test
	public void doTest() throws Exception {
		BalanceInquiryVO pojo = new BalanceInquiryVO();
		pojo.setTransactionAmount(new BigDecimal(5.35));
		pojo.setTransactionDate(new Date());
		pojo.setConversionRate(4.05);
		
		IsoMessage encodeMessage = msgF.encode(pojo);

		System.out.println(encodeMessage.debugString());
		
		BalanceInquiryVO decodeMessage = msgF.decode(encodeMessage, BalanceInquiryVO.class);
		
		System.out.println(decodeMessage);
	}
}
