package com.example.netty.base.channel.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.solab.iso8583.IsoMessage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RoutingHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	private Map<String, List<MessageHandlerWrapper>> channelHandlerMap = new HashMap<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(MessageMapping.class);
		Collection<String> handlerBeanNames = handlerMap.keySet();

		for (String channelBeanName : handlerBeanNames) {
			Object handler = handlerMap.get(channelBeanName);

			if (handler instanceof ChannelHandler) {
				ChannelHandler channelHander = (ChannelHandler) handler;

				MessageMapping mapping = applicationContext.findAnnotationOnBean(channelBeanName, MessageMapping.class);
				String mappingValue = mapping.name();

				List<MessageHandlerWrapper> nextHandlers = channelHandlerMap.get(mappingValue);
				if (nextHandlers == null) {
					nextHandlers = new ArrayList<>();
				}
				
				MessageHandlerWrapper handlerWrapper = new MessageHandlerWrapper();
				handlerWrapper.setMapping(mapping);
				handlerWrapper.setChannelHandler(channelHander);

				nextHandlers.add(handlerWrapper);

				channelHandlerMap.put(mappingValue, nextHandlers);
			}
		}
	
		MessageHandlerWrapperComparator comparator = new MessageHandlerWrapperComparator();
		
		Collection<String> mappingNames = channelHandlerMap.keySet();
		for (String mappingName : mappingNames) {
			List<MessageHandlerWrapper> mappingHandlers = channelHandlerMap.get(mappingName);
			Collections.sort(mappingHandlers, comparator);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String mappingValue = findMapping(msg);
		if (mappingValue != null) {
			List<MessageHandlerWrapper> handlerWrappers = channelHandlerMap.get(mappingValue);
			for (MessageHandlerWrapper handlerWrapper : handlerWrappers) {
				ctx.pipeline().addLast(handlerWrapper.getChannelHandler());
			}
		}
		super.channelRead(ctx, msg);
	}

	protected String findMapping(Object msg) {
		String mappingValue = null;
		if (msg instanceof IsoMessage) {
			mappingValue = "200";
		}
		return mappingValue;
	}

	public class MessageHandlerWrapperComparator implements Comparator<MessageHandlerWrapper> {

		@Override
		public int compare(MessageHandlerWrapper o1, MessageHandlerWrapper o2) {
			return o1.getMapping().order() > o2.getMapping().order() ? 1 : -1;
		}
	}

	public class MessageHandlerWrapper {
		
		private MessageMapping mapping;
		private ChannelHandler channelHandler;
		
		public MessageMapping getMapping() {
			return mapping;
		}

		public void setMapping(MessageMapping mapping) {
			this.mapping = mapping;
		}

		public ChannelHandler getChannelHandler() {
			return channelHandler;
		}

		public void setChannelHandler(ChannelHandler channelHandler) {
			this.channelHandler = channelHandler;
		}

		@Override
		public String toString() {
			return "MessageHandlerWrapper [mapping=" + mapping + ", channelHandler=" + channelHandler + "]";
		}
	}
}
