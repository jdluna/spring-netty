package com.example.netty.core.handler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.example.netty.core.handler.annotation.NonBlocking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public abstract class AbstractMessageHandler<IN> implements MessageHandler<IN> {

	private final static Logger logger = LoggerFactory.getLogger(AbstractMessageHandler.class);
	private final Class<?> clazz;
	
	@SuppressWarnings("unchecked")
	public AbstractMessageHandler() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        clazz = (Class<IN>) typeArguments[0];
	}
	
	@Override
	public void handle(final ChannelHandlerContext ctx, final IN request) {
		logger.debug("Invoke method handle()");
		
        final Method method = BeanUtils.findMethod(this.getClass(), "handle", clazz);
        NonBlocking asynAnnotation = method.getAnnotation(NonBlocking.class);
        
        if (asynAnnotation == null) {
        	try {
        		Object response = method.invoke(this, request);
        		if (response != null) {
            		ctx.writeAndFlush(response);
            	}
        		
        	} catch (Exception e) {
        		logger.error(e.getMessage(), e);
        		
        	} finally {
        		ReferenceCountUtil.release(request);
			}
        	
        } else {
        	ctx.executor().execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						Object response = method.invoke(AbstractMessageHandler.this, request);
						if (response != null) {
			        		ctx.writeAndFlush(response);
			        	}
						
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						
					} finally {
		        		ReferenceCountUtil.release(request);
					}
				}
			});
        }
	}
	
	public abstract Object handle(IN message);
}
