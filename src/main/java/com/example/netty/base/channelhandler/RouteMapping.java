package com.example.netty.base.channel.handler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Sharable
@Component
public @interface MessageMapping {

	String name() default "";
	
	int order() default 0;
}
