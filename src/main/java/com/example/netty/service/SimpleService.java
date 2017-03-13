package com.example.netty.service;

import com.solab.iso8583.IsoMessage;

public interface SimpleService {

	void processMessage(IsoMessage message);
}
