package com.echoman.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	private static ScheduledExecutorService executor;
	private static ConcurrentHashMap<String, ScheduledFuture<?>> futures;
	
	static{
		executor = Executors.newScheduledThreadPool(10);
		futures = new ConcurrentHashMap<>();
	}
	
	public static void execute(Runnable task, String taskId, long perid){
		ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, 10, perid, TimeUnit.SECONDS);
		System.out.println("add " + taskId);
		futures.put(taskId, future);
	}
	
	public static boolean isRunning(String taskId){
		System.out.println("contains " + taskId);
		return futures.containsKey(taskId);
	}
}
