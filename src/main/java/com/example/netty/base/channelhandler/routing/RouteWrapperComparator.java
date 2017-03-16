package com.example.netty.base.channelhandler.routing;

import java.util.Comparator;

public class RouteWrapperComparator implements Comparator<RouteWrapper> {

	@Override
	public int compare(RouteWrapper o1, RouteWrapper o2) {
		return o1.getMapping().order() > o2.getMapping().order() ? 1 : -1;
	}
}
