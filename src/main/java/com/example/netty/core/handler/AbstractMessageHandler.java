package com.example.netty.core.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

@SuppressWarnings("unchecked")
public abstract class AbstractMessageHandler<IN> implements MessageHandler<IN> {

	private final static Logger logger = LoggerFactory.getLogger(AbstractMessageHandler.class);
	
	private final Class<?> clazz;
	
	private final boolean asyn;
	
	public AbstractMessageHandler() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        clazz = (Class<IN>) typeArguments[0];
        
        Handler handler = AnnotationUtils.findAnnotation(this.getClass(), Handler.class);
        asyn = handler.asyn();
	}
	
	@Override
	public void handle(final ChannelHandlerContext ctx, final IN request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug("Invoke method handle()");
		
        final Method method = BeanUtils.findMethod(this.getClass(), "handle", clazz);
        
        if (asyn) {
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
        } else {
        	try {
        		Object response = method.invoke(this, request);
        		if (response != null) {
            		ctx.writeAndFlush(response);
            	}
        	} finally {
        		ReferenceCountUtil.release(request);
			}
        }
	}
	
	public abstract Object handle(IN message);
}
