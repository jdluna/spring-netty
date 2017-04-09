
package com.example.netty.core.j8583.parser;

import com.example.netty.core.j8583.Iso8583;
import com.solab.iso8583.IsoMessage;

public interface IsoMessageParser<T extends Iso8583> {

	IsoMessage encode(T object) throws Exception;
	
	T decode(IsoMessage isoMessage) throws Exception;
}
