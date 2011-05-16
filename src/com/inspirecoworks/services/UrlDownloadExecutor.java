package com.inspirecoworks.services;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UrlDownloadExecutor extends ThreadPoolExecutor {

	
	public UrlDownloadExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	
	
}
