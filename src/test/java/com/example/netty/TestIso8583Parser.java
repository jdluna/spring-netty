package com.example.netty;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.netty.core.j8583.MessageFactory;
import com.example.netty.vo.BalanceInquiryVO;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;


public class TestIso8583Parser extends AbstractTestCase {

	@Autowired
	private MessageFactory msgF;
	
	@Test
	public void doTest() throws Exception {
		BalanceInquiryVO pojo = new BalanceInquiryVO();
		pojo.setTransactionAmount(new BigDecimal(5.35));
		pojo.setTransactionDate(new Date());
		
		IsoMessage encodeMessage = msgF.encode(pojo);
		
		IsoMessage isoMessage = msgF.getMessageTemplate(0x200);
		
		IsoValue<BigDecimal> field4 = new IsoValue<BigDecimal>(IsoType.AMOUNT, new BigDecimal(5.35));
		IsoValue<Date> field7 = new IsoValue<Date>(IsoType.DATE4, new Date());
		
		isoMessage.putAt(4, field4);
		isoMessage.putAt(7, field7);

		System.out.println(encodeMessage.debugString());
		System.out.println(isoMessage.debugString());
		
		Assert.assertEquals(isoMessage.debugString(), encodeMessage.debugString());
		
		BalanceInquiryVO decodeMessage = msgF.decode(encodeMessage, BalanceInquiryVO.class);
		
		System.out.println(pojo);
		System.out.println(decodeMessage);
	}
}
