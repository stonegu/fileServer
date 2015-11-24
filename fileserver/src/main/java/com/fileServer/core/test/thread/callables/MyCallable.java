package com.fileServer.core.test.thread.callables;

import java.util.concurrent.Callable;

public class MyCallable implements Callable<Long> {
	private int count;
	  public MyCallable(int count) {
		super();
		this.count = count;
	}

	@Override
	  public Long call() throws Exception {
		
		System.err.println("call: "+count);
		  
	    long sum = 0;
	    for (long i = 0; i <= 1000000; i++) {
	      sum += i;
	    }
	    return sum;
	  }

	}