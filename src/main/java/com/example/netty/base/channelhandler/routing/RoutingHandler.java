package com.example.netty.base.channelhandler.routing;

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
import org.springframework.util.Assert;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

@Sharable
public class RoutingHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware, InitializingBean {

	private String name = StringUtil.simpleClassName(RoutingHandler.class) + "#0";
	
	private ApplicationContext applicationContext;

	private Map<String, List<RouteWrapper>> channelHandlerMap = new HashMap<>();

	public RoutingHandler() {}
	
	public RoutingHandler(String name) {
		this.name = name;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(RouteMapping.class);
		
		Collection<String> handlerNames = handlerMap.keySet();
		
		for (String handlerName : handlerNames) {
			Object handler = handlerMap.get(handlerName);

			if (handler instanceof ChannelHandler) {
				ChannelHandler channelHander = (ChannelHandler) handler;

				RouteMapping routeMapping = applicationContext.findAnnotationOnBean(handlerName, RouteMapping.class);
				String routeName = routeMapping.name();
				
				Assert.notNull(routeName, "Route name cannot be null inside " + StringUtil.simpleClassName(channelHander));

				List<RouteWrapper> routeHandlers = channelHandlerMap.get(routeName);
				if (routeHandlers == null) {
					routeHandlers = new ArrayList<>();
				}
				
				RouteWrapper handlerWrapper = new RouteWrapper();
				handlerWrapper.setOrder(routeMapping.order());
				handlerWrapper.setChannelHandler(channelHander);

				routeHandlers.add(handlerWrapper);

				channelHandlerMap.put(routeName, routeHandlers);
			}
		}
	
		// order route handlers based on order field
		
		RouteWrapperComparator comparator = new RouteWrapperComparator();
		
		Collection<String> mappingNames = channelHandlerMap.keySet();
		for (String mappingName : mappingNames) {
			List<RouteWrapper> mappingHandlers = channelHandlerMap.get(mappingName);
			Collections.sort(mappingHandlers, comparator);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Routable) {
			String routeName = ((Routable) msg).getName();

			// register route handlers
			List<RouteWrapper> handlerWrappers = channelHandlerMap.get(routeName);			
			if (handlerWrappers != null) {
				for (RouteWrapper handlerWrapper : handlerWrappers) {
					ChannelHandler channelHander = handlerWrapper.getChannelHandler();
					
					ctx.pipeline().addAfter(
						name,
						StringUtil.simpleClassName(channelHander),
						channelHander
					);
				}
			}
			
			// invoke next handler
			super.channelRead(ctx, msg);
			
			// clear route handlers
			if (handlerWrappers != null) {
				for (RouteWrapper handlerWrapper : handlerWrappers) {
					ctx.pipeline().remove(handlerWrapper.getChannelHandler());
				}
		
			}
			
		} else {
			super.channelRead(ctx, msg);
		}
	}
}
