package com.example.netty.base.channelhandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.solab.iso8583.IsoMessage;

public class RoutingHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	private Map<String, List<RouteWrapper>> channelHandlerMap = new HashMap<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(RouteMapping.class);
		Collection<String> handlerBeanNames = handlerMap.keySet();

		for (String channelBeanName : handlerBeanNames) {
			Object handler = handlerMap.get(channelBeanName);

			if (handler instanceof ChannelHandler) {
				ChannelHandler channelHander = (ChannelHandler) handler;

				RouteMapping mapping = applicationContext.findAnnotationOnBean(channelBeanName, RouteMapping.class);
				String mappingValue = mapping.name();

				List<RouteWrapper> nextHandlers = channelHandlerMap.get(mappingValue);
				if (nextHandlers == null) {
					nextHandlers = new ArrayList<>();
				}
				
				RouteWrapper handlerWrapper = new RouteWrapper();
				handlerWrapper.setMapping(mapping);
				handlerWrapper.setChannelHandler(channelHander);

				nextHandlers.add(handlerWrapper);

				channelHandlerMap.put(mappingValue, nextHandlers);
			}
		}
	
		RouteWrapperComparator comparator = new RouteWrapperComparator();
		
		Collection<String> mappingNames = channelHandlerMap.keySet();
		for (String mappingName : mappingNames) {
			List<RouteWrapper> mappingHandlers = channelHandlerMap.get(mappingName);
			Collections.sort(mappingHandlers, comparator);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String mappingValue = findRoute(msg);
		if (mappingValue != null) {
			List<RouteWrapper> handlerWrappers = channelHandlerMap.get(mappingValue);
			
			if (handlerWrappers != null) {
				for (RouteWrapper handlerWrapper : handlerWrappers) {
					ctx.pipeline().addLast(handlerWrapper.getChannelHandler());
				}
			}
		}
		super.channelRead(ctx, msg);
	}

	protected String findRoute(Object msg) {
		String mappingValue = null;
		if (msg instanceof IsoMessage) {
			mappingValue = "200";
		}
		return mappingValue;
	}
}
