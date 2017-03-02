package com.example.netty.iso8583;

import java.io.Serializable;

public class ISO8583BytesWrapper implements Serializable {

	private static final long serialVersionUID = -5720950150014146659L;
	
	private byte[] bytes;

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}