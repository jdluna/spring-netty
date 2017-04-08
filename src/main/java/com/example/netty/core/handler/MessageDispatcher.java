package com.example.netty.core.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import com.example.netty.core.util.ClassUtils;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

@Sharable
@SuppressWarnings("rawtypes")
public class MessageDispatcher extends ChannelInboundHandlerAdapter implements ApplicationContextAware, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
	
	private String name;
	
	private ApplicationContext applicationContext;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private Map<String, MessageHandlerWrapper> handlerMap = new HashMap<String, MessageHandlerWrapper>();
	
	private List<RouteExtractor> routeExtractors = new ArrayList<RouteExtractor>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("Dispatcher name is empty");
		}
		
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(Handler.class);
		
		Collection<String> beanNames = map.keySet();
		for (String beanName : beanNames) {
			
			Object handler = map.get(beanName);

			if (!(handler instanceof MessageHandler)) {
				continue;
			}
			
			Handler annotation = AnnotationUtils.findAnnotation(handler.getClass(), Handler.class);
			if (annotation == null) {
				logger.warn("@Handler is undefined -> {}", beanName);
				continue;
			}
			
			String routeName = annotation.value();
			if (routeName == null || routeName.equals("")) {
				logger.warn("Route name must not be empty -> {}", beanName);
				continue;
				
			} else if (handlerMap.get(routeName) != null) {
				logger.warn("Route name is duplicated : {} -> {}", routeName, beanName);
				continue;
			}
			
			String dispatcher = annotation.dispatcher();
			if (dispatcher == null || dispatcher.equals("")) {
				logger.warn("Dispatcher name must not be empty -> {}", beanName);
				continue;
			}
			
			if (dispatcher.equals(name)) {
				MessageHandlerWrapper messageHandlerWrapper = new MessageHandlerWrapper();
				messageHandlerWrapper.setAsyn(annotation.asyn());
				messageHandlerWrapper.setHandler((MessageHandler) handler);
				messageHandlerWrapper.setSupportClass(ClassUtils.getInterfaceGenericType(handler.getClass(), 0));
				
				handlerMap.put(routeName, messageHandlerWrapper);
				
				logger.debug("Register {} to dispatcher {}", beanName, name);
			}
				
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object request) throws Exception {
		boolean isHandled = false;
		
		for (RouteExtractor routeExtractor : routeExtractors) {
			
			Class supportClass = ClassUtils.getInterfaceGenericType(routeExtractor.getClass(), 0);
			if (supportClass != null && !supportClass.isAssignableFrom(request.getClass())) {
				continue;
			}
			
			String routeName = routeExtractor.extract(request);
			if (routeName != null) {
				MessageHandlerWrapper messageHandlerWrapper = handlerMap.get(routeName);
				
				if (messageHandlerWrapper != null) {
					final MessageHandler handler = messageHandlerWrapper.getHandler();
					
					logger.debug("Route message {} to handler {}", request, handler.getClass().getSimpleName());

					if (messageHandlerWrapper.isAsyn()) {
						executor.submit(new Callable<Object>() {

							@Override
							public Object call() throws Exception {
								Object response;
								try {
									response = handler.handle(request);
									if (response != null) {
										ctx.writeAndFlush(response);
									}
								} finally {
									ReferenceCountUtil.release(request);
								}
								return response;
							}
						});
					} else {
						try {
							Object response = handler.handle(request);
							if (response != null) {
								ctx.writeAndFlush(response);
							}
						} finally {
							ReferenceCountUtil.release(request);
						}
					}
					
					isHandled = true;
					
					break;
				}
			}
		} 
		
		if (!isHandled) {
			super.channelRead(ctx, request);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public List<? extends RouteExtractor> getRouteExtractors() {
		return routeExtractors;
	}

	public void setRouteExtractors(RouteExtractor... routeExtractors) {
		Assert.notEmpty(routeExtractors, "RouteExtractors must not be empty");
		for (RouteExtractor extractor : routeExtractors) {
			this.routeExtractors.add(extractor);
		}
	}
}
