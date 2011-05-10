package com.inspirecoworks.services;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DownloadTask extends FutureTask<String> {

	public static final int DONE = 0;
	public static final int EXCEPTION = 1;
	public static final int DOWNLOADING = 2;
	public static final int BEFORE = 3;
	public static final int AFTER = 4;
	public Handler handler;
	
	public DownloadTask(Callable<String> callable) {
		super(callable);
	}
	
	
	@Override
	protected void set(String v) {
		super.set(v);
		Message msg = new Message();
		msg.what = DONE;
		Bundle bundle = new Bundle();
		bundle.putString("content", v);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	static DefaultHttpClient client;
	Callable<String> callable;

	public DownloadTask(Context context, String cache_file, NotifyHandler handler)
	{
		super(new CacheDownCallable(context, cache_file, handler));
		this.handler = handler;
	}
	
	
	
	public DownloadTask(String url, NotifyHandler handler) {
		super(new UrlDownCallable(url, handler));
		this.handler = handler;
	}

	public DownloadTask(String url, HttpEntity data, String redirectUrl, NotifyHandler handler) {
		super(new UrlDownCallable(url, data, null, handler));
		this.handler = handler;
	}
	
	@Override
	protected void setException(Throwable t) {
		super.setException(t);
		Message msg = new Message();
		msg.what = EXCEPTION;
		Bundle bundle = new Bundle();
		bundle.putString("EXCEPTION", t.getLocalizedMessage()+":"+t.getStackTrace()[0].getClassName()+":"+t.getStackTrace()[0].getLineNumber());
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
		handler.sendEmptyMessage(BEFORE);
		super.run();
		handler.sendEmptyMessage(AFTER);
	}
}


