package com.example.netty.core.handler;

public interface MessageHandler<T> {
	
	Object handle(T message) throws Exception;
}
