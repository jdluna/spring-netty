package com.example.netty.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.netty.core.j8583.MessageFactory;
import com.example.netty.core.j8583.parser.AbstractIsoMessageParser;
import com.example.netty.vo.BalanceInquiryVO;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

@Component
public class BalanceInquiryParser extends AbstractIsoMessageParser<BalanceInquiryVO> {

	@Autowired(required = false)
	public BalanceInquiryParser(MessageFactory messageFactory) {
		super(messageFactory);
	}
	
	@Override
	public IsoMessage encode(BalanceInquiryVO object) throws Exception {
		IsoMessage isoMessage = getMessageFactory().encode(object);
		
		IsoValue<String> processingCode = new IsoValue<String>(IsoType.ALPHA, "330000", 6); 
		isoMessage.putAt(3, processingCode);
		
		return isoMessage;
	}

	@Override
	public BalanceInquiryVO decode(IsoMessage isoMessage) throws Exception {
		BalanceInquiryVO pojo = getMessageFactory().decode(isoMessage, BalanceInquiryVO.class);
		
		return pojo;
	}
}
