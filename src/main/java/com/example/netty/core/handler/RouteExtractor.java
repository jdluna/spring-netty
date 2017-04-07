package com.example.netty.core.handler;

public interface RouteExtractor<T> {

	String extract(T message);
}
