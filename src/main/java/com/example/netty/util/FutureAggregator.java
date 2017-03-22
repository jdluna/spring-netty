package com.example.netty.util;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.ArrayList;
import java.util.List;

public class FutureAggregator<T> implements FutureListener<T> {

	private int counter = 0;
	
	private List<Future<T>> futures = new ArrayList<>();
	
	private FutureListener<T> listener;
	
	public synchronized void addFuture(Future<T> future) {
		future.addListener(this);
		futures.add(future);
	}
	
	public void addListener(FutureListener<T> listener) {
		this.listener = listener;
	}
	
	@Override
	public synchronized void operationComplete(Future<T> future) throws Exception {
		counter++;
		if (counter == futures.size()) {
			if (listener != null) {
				listener.operationComplete(future);
			}
		}
	}
	
	public boolean isComplete() {
		return counter == futures.size();
	}
	
	public void syn() {
		while (!isComplete()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
