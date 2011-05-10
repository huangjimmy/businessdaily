package com.inspirecoworks.services;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadExecutorService extends ThreadPoolExecutor {

	public DownloadExecutorService(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		if(r instanceof DownloadTask)
		{
			((DownloadTask)r).handler.sendEmptyMessage(DownloadTask.BEFORE);
		}
		super.beforeExecute(t, r);
		final Thread th = t;
		new Thread(){
			public void run(){
				try {
					th.join(20000);
					if(th.isAlive())
						th.interrupt();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}



	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if(r instanceof DownloadTask)
		{
			((DownloadTask)r).handler.sendEmptyMessage(DownloadTask.AFTER);
		}
		final Runnable task = r;
		if(t != null)
		{
			new Thread(){
				public void run(){
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					submit(task);
				}
			}.start();
		}
	}

	

}
