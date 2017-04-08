package com.example.netty.core.handler;

public class MessageHandlerWrapper {

	private Class<?> supportClass;
	
	private MessageHandler<?> handler;
	
	private boolean asyn;
	
	public Class<?> getSupportClass() {
		return supportClass;
	}

	public void setSupportClass(Class<?> supportClass) {
		this.supportClass = supportClass;
	}

	public MessageHandler<?> getHandler() {
		return handler;
	}

	public void setHandler(MessageHandler<?> handler) {
		this.handler = handler;
	}

	public boolean isAsyn() {
		return asyn;
	}

	public void setAsyn(boolean asyn) {
		this.asyn = asyn;
	}
}
