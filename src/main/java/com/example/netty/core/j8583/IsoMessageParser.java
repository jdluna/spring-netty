
package com.example.netty.core.j8583;

import com.solab.iso8583.IsoMessage;

public interface IsoMessageParser<T> {

	IsoMessage encode(T object);
	
	T decode(IsoMessage message, Class<T> clazz);
}
