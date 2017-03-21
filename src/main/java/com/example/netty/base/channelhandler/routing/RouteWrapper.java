package com.example.netty.base.channelhandler.routing;

import io.netty.channel.ChannelHandler;

public class RouteWrapper {

	private int order;
	private ChannelHandler channelHandler;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}

	public void setChannelHandler(ChannelHandler channelHandler) {
		this.channelHandler = channelHandler;
	}

	@Override
	public String toString() {
		return "RouteWrapper [order=" + order + ", channelHandler=" + channelHandler + "]";
	}
}
