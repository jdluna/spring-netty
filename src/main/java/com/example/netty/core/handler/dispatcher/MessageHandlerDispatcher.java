package com.example.netty.core.handler.dispatcher;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import com.example.netty.core.handler.AbstractMessageHandler;
import com.example.netty.core.handler.MessageHandler;
import com.example.netty.core.handler.annotation.Routable;
import com.example.netty.core.handler.annotation.RouteMapping;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MessageHandlerDispatcher extends ChannelInboundHandlerAdapter implements ApplicationContextAware, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(MessageHandlerDispatcher.class);
	
	private String groupName;
	
	private ApplicationContext applicationContext;
	
	private Map<String, MessageHandler<?>> handlerMap = new HashMap<>();
	
	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, MessageHandler> handlers = applicationContext.getBeansOfType(MessageHandler.class);
		
		Collection<String> handlerNames = handlers.keySet();
		for (String handlerName : handlerNames) {
			MessageHandler handler = handlers.get(handlerName);
			
			RouteMapping routeMapping = AnnotationUtils.findAnnotation(handler.getClass(), RouteMapping.class);
			if (routeMapping == null) {
				logger.debug("Route mapping is undefined -> {}", handlerName);
				continue;
			}
			
			String routeName = routeMapping.name();
			if (routeName == null || routeName.equals("")) {
				logger.debug("Invalid route name -> {}", routeName);
				continue;
			}
			
			String groupName = routeMapping.group();
			if (groupName == null || groupName.equals("")) {
				logger.debug("Invalid group name -> {}", groupName);
				continue;
			}
			
			if (groupName.equals(this.groupName)) {
				handlerMap.put(routeName, handler);
				
				logger.debug("Register route {} to {}", routeName, handlerName);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Routable) {
			Routable routableMsg = (Routable) msg;
			String routeName = routableMsg.getRouteName();
			
			MessageHandler handler = handlerMap.get(routeName);
			if (handler != null) {
				Type type = null;
				Class<?> supportClass = null;
				
				if (handler instanceof AbstractMessageHandler) {
					type = AopUtils.getTargetClass(handler).getGenericSuperclass();
				} else if (handler instanceof MessageHandler) {
					type = AopUtils.getTargetClass(handler).getGenericInterfaces()[0];
				}
				
				if (type instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) type;
			        Type[] typeArguments = parameterizedType.getActualTypeArguments();
			        supportClass = (Class<?>) typeArguments[0];
				}
				
				if (supportClass != null) {
					logger.debug("Route message {} to handler {}", msg.toString(), handler.getClass().getSimpleName());
					
					if (handler instanceof MessageHandler) {
						try {
							handler.handle(ctx, msg);
						} finally {
							ReferenceCountUtil.release(msg);
						}
					} else {
						handler.handle(ctx, msg);
					}
				}
			}
		} else {
			super.channelRead(ctx, msg);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
