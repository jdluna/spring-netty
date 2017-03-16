package com.example.netty.base.channelhandler;

import io.netty.channel.ChannelHandler;

public class RouteWrapper {

	private RouteMapping mapping;
	private ChannelHandler channelHandler;

	public RouteMapping getMapping() {
		return mapping;
	}

	public void setMapping(RouteMapping mapping) {
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
		return "MessageHandlerWrapper [mapping=" + mapping
				+ ", channelHandler=" + channelHandler + "]";
	}
}
