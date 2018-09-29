package com.jll.common.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import com.jll.game.mesqueue.WinningNumberListenerImpl;

public class ThreadPoolManager {

	private Logger logger = Logger.getLogger(WinningNumberListenerImpl.class);
	
	private static ThreadPoolManager tpManager;
	
	private ExecutorService executorService;
	
	private ThreadPoolManager() {
		executorService = Executors.newFixedThreadPool(20);
	}
	
	public static ThreadPoolManager getInstance() {
		if(tpManager == null) {
			tpManager = new ThreadPoolManager();
		}
		
		return tpManager;
	}
	
	public void exeThread(Runnable task) {
		logger.debug(String.format("try to run thread pool %s", ""));
		executorService.execute(task);
	}
	
	public int getActiveThreads() {
		int count = ((ThreadPoolExecutor)executorService).getActiveCount();
		
		return count;
	}
	
	public int getPoolSize() {
		int count = ((ThreadPoolExecutor)executorService).getPoolSize();
		
		return count;
	}
}
