package com.example.netty.core.util;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;

public class Iso8583Utils {

	public static String getTxId(IsoMessage message) {
		if (message != null) {
			String msgType = String.format("%04d", new Integer(Integer.toHexString(message.getType())));
			IsoValue<String> processingCodeField = message.getField(3);
			
			return new String(msgType + "_" + processingCodeField.getValue().substring(0, 2));
		}
		return null;
	}
}
