package com.example.netty.sample.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ClientConfiguration {

	private Class<? extends SocketChannel> channel;
	private Integer workerThreadNum;

	private Map<ChannelOption, Object> channelOptions = new LinkedHashMap<ChannelOption, Object>();

	private ChannelHandler workerHandler;

	public ClientConfiguration channel(Class<? extends SocketChannel> channel) {
		this.channel = channel;
		return this;
	}

	public ClientConfiguration workerThreadNum(int threadNum) {
		this.workerThreadNum = threadNum;
		return this;
	}

	public <T> ClientConfiguration channelOption(ChannelOption<T> channelOption, T value) {
		this.channelOptions.put(channelOption, value);
		return this;
	}

	public ClientConfiguration workerHandler(ChannelHandler workerHandler) {
		this.workerHandler = workerHandler;
		return this;
	}

	public Bootstrap build() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(channel);
		bootstrap.handler(workerHandler);

		Set<ChannelOption> keySet = channelOptions.keySet();
		
		for (ChannelOption option : keySet) {
			bootstrap.option(option, channelOptions.get(option));
		}

		boolean isNio = channel.isAssignableFrom(channel);

		if (isNio) {
			buildNioBootstrap(bootstrap);
		} else {
			buildOioBootstrap(bootstrap);
		}

		return bootstrap;
	}

	protected void buildNioBootstrap(Bootstrap bootstrap) {
		NioEventLoopGroup workerEventLoop = (workerThreadNum != null) ? new NioEventLoopGroup(workerThreadNum) : new NioEventLoopGroup();
		bootstrap.group(workerEventLoop);
	}

	protected void buildOioBootstrap(Bootstrap bootstrap) {
		OioEventLoopGroup workerEventLoop = (workerThreadNum != null) ? new OioEventLoopGroup(workerThreadNum) : new OioEventLoopGroup();
		bootstrap.group(workerEventLoop);
	}
}
