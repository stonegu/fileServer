package com.fileServer.core.test.thread.nonblockingAlgorithms;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
  private AtomicInteger value = new AtomicInteger(); 
  public int getValue(){
    return value.get();
  }
  public int increment(){
	  System.err.println("+++");
    return value.incrementAndGet();
  }
  
  // Alternative implementation as increment but just make the 
  // implementation explicit
  public int incrementLongVersion(){
	  System.err.println("***");
    int oldValue = value.get();
    while (!value.compareAndSet(oldValue, oldValue+1)){
       oldValue = value.get();
    }
    return oldValue+1;
  }
  
} 