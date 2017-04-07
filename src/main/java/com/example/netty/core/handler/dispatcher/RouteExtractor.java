package com.example.netty.core.handler.dispatcher;

public interface RouteExtractor<T> {

	String extract(T message);
}
