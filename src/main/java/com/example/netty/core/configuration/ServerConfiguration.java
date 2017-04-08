package com.example.netty.core.configuration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ServerConfiguration {

	private Class<? extends ServerSocketChannel> channel;
	private Integer bossThreadNum;
	private Integer workerThreadNum;

	private Map<ChannelOption, Object> channelOptions = new LinkedHashMap<ChannelOption, Object>();

	private ChannelHandler bossHandler;
	private ChannelHandler workerHandler;

	public ServerConfiguration channel(Class<? extends ServerSocketChannel> channel) {
		this.channel = channel;
		return this;
	}

	public ServerConfiguration bossThreadNum(int threadNum) {
		this.bossThreadNum = threadNum;
		return this;
	}

	public ServerConfiguration workerThreadNum(int threadNum) {
		this.workerThreadNum = threadNum;
		return this;
	}

	public <T> ServerConfiguration channelOption(ChannelOption<T> channelOption, T value) {
		this.channelOptions.put(channelOption, value);
		return this;
	}

	public ServerConfiguration bossHandler(ChannelHandler bossHandler) {
		this.bossHandler = bossHandler;
		return this;
	}
	
	public ServerConfiguration workerHandler(ChannelHandler workerHandler) {
		this.workerHandler = workerHandler;
		return this;
	}

	public ServerBootstrap build() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.channel(channel);
		
		if (bossHandler != null) {
			bootstrap.handler(bossHandler);
		}
		
		bootstrap.childHandler(workerHandler);

		Set<ChannelOption> keySet = channelOptions.keySet();
		
		for (ChannelOption option : keySet) {
			bootstrap.childOption(option, channelOptions.get(option));
		}

		boolean isNio = channel.isAssignableFrom(channel);

		if (isNio) {
			buildNioConfiguration(bootstrap);
		} else {
			buildOioConfiguration(bootstrap);
		}

		return bootstrap;
	}

	protected void buildNioConfiguration(ServerBootstrap bootstrap) {
		NioEventLoopGroup bossEventLoop = (bossThreadNum != null) ? new NioEventLoopGroup(bossThreadNum) : new NioEventLoopGroup();
		NioEventLoopGroup workerEventLoop = (workerThreadNum != null) ? new NioEventLoopGroup(workerThreadNum) : new NioEventLoopGroup();
		
		bootstrap.group(bossEventLoop, workerEventLoop);
	}

	protected void buildOioConfiguration(ServerBootstrap bootstrap) {
		OioEventLoopGroup bossEventLoop = (bossThreadNum != null) ? new OioEventLoopGroup(bossThreadNum) : new OioEventLoopGroup();
		OioEventLoopGroup workerEventLoop = (workerThreadNum != null) ? new OioEventLoopGroup(workerThreadNum) : new OioEventLoopGroup();
		
		bootstrap.group(bossEventLoop, workerEventLoop);
	}
}
