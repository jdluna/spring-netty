package com.example.netty.parser.field;

import com.solab.iso8583.CustomField;

public class ConversionRateConverter implements CustomField<Double> {

	@Override
	public Double decodeField(String encode) {
		Double result = null;
		
		if (encode != null && !encode.equals("")) {
			int pointIndex = Integer.parseInt(encode.charAt(0) + "", 10);
			
			String decodeString = "";
			
			char ch;
			
			for (int i = encode.length() - 1; i > 0; i--) {
				ch = encode.charAt(i);
				decodeString = ch + decodeString;
				
				if (encode.length() - i == pointIndex) {
					decodeString = '.' + decodeString;
				}
			}
			
			result = new Double(decodeString.substring(0));
		}
		
		return result;
	}

	@Override
	public String encodeField(Double value) {
		String result = null;
		
		if (value != null) {
			String valueAsString = String.valueOf(value);
			
			String paddingString = String.format("%-8s", valueAsString).replace(" ", "0");
			
			int pointIndex = paddingString.length() - paddingString.lastIndexOf(".") - 1;
			
			result = pointIndex + paddingString.replace(".", "");
		}
		
		return result;
		
	}
}
