package com.example.netty.core.handler.dispatcher;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import com.example.netty.core.handler.Handler;
import com.example.netty.core.handler.MessageHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

@SuppressWarnings("rawtypes")
public class MessageDispatcher extends ChannelInboundHandlerAdapter implements ApplicationContextAware, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
	
	private final String name;
	
	private ApplicationContext applicationContext;
	
	private Map<String, MessageHandler> handlerMap = new HashMap<String, MessageHandler>();
	
	private List<? extends RouteExtractor> routeExtractors = new ArrayList<RouteExtractor>();
	
	public MessageDispatcher(String name) {
		this.name = name;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(Handler.class);
		
		Collection<String> beanNames = map.keySet();
		for (String beanName : beanNames) {
			
			Object handler = map.get(beanName);

			if (!(handler instanceof MessageHandler)) {
				continue;
			}
			
			Handler annotation = AnnotationUtils.findAnnotation(handler.getClass(), Handler.class);
			if (annotation == null) {
				logger.debug("@Handler is undefined -> {} - ", beanName, handler.getClass().getSimpleName());
				continue;
			}
			
			String routeName = annotation.value();
			if (routeName == null || routeName.equals("")) {
				logger.debug("Invalid route name -> {} - {}", routeName, handler.getClass().getSimpleName());
				continue;
			}
			
			String dispatcher = annotation.dispatcher();
			if (dispatcher == null || dispatcher.equals("")) {
				logger.debug("Invalid dispatcher name -> {} - {}", dispatcher, handler.getClass().getSimpleName());
				continue;
			}
			
			if (dispatcher.equals(name)) {
				handlerMap.put(routeName, (MessageHandler) handler);
				
				logger.debug("Register {} to dispatcher {}", handler.getClass().getSimpleName(), name);
			}
				
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		boolean isHandled = false;
		
		for (RouteExtractor routeExtractor : routeExtractors) {
			
			Type type = AopUtils.getTargetClass(routeExtractor).getGenericInterfaces()[0];
			
			Class supportClass = null;
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
		        Type[] typeArguments = parameterizedType.getActualTypeArguments();
		        supportClass = (Class<?>) typeArguments[0];
			}
			
			if (supportClass != null && !supportClass.isAssignableFrom(msg.getClass())) {
				continue;
			}
			
			String routeName = routeExtractor.extract(msg);
			if (routeName != null) {
				MessageHandler handler = handlerMap.get(routeName);
				
				if (handler instanceof AbstractMessageHandler) {
					handler.handle(ctx, msg);
				} else {
					try {
						handler.handle(ctx, msg);
					} finally {
						ReferenceCountUtil.release(msg);
					}
				}
				
				isHandled = true;
				
				break;
			}
		} 
		
		if (!isHandled) {
			super.channelRead(ctx, msg);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public List<? extends RouteExtractor> getRouteMatchers() {
		return routeExtractors;
	}

	public void setRouteMatchers(List<? extends RouteExtractor> routeExtractors) {
		this.routeExtractors = routeExtractors;
	}
}
