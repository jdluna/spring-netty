package com.example.netty.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class FutureAggregator<T> implements FutureListener<T> {

	private int counter = 0;
	
	private List<Future<T>> futures = new ArrayList<>();
	
	private FutureListener<T> listener;
	
	public void addFuture(Future<T> future) {
		futures.add(future);
	}
	
	public void addListener(FutureListener<T> listener) {
		this.listener = listener;
	}
	
	@Override
	public void operationComplete(Future<T> future) throws Exception {
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
		for (Future<T> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
