package com.jll.common.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {

	private static ThreadPoolManager tpManager;
	
	private ExecutorService executorService;
	
	private ThreadPoolManager() {
		executorService = Executors.newCachedThreadPool();
	}
	
	public static ThreadPoolManager getInstance() {
		if(tpManager == null) {
			tpManager = new ThreadPoolManager();
		}
		
		return tpManager;
	}
	
	public void exeThread(Runnable task) {
		executorService.execute(task);
	}
}
