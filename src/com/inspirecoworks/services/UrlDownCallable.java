package com.inspirecoworks.services;

import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.util.Log;

public class UrlDownCallable implements Callable<String>
{

	private String url;
	private static DefaultHttpClient client;
	private Handler handler;
	private HttpUriRequest get;
	
	public UrlDownCallable(String url, Handler handler)
	{
		this.url = url;
		this.handler = handler;
	}
	
	public UrlDownCallable(String url, HttpEntity data, String redirectUrl, Handler handler)
	{
		this.handler = handler;
		this.url = url;
		
	}
	public String call() throws Exception {
		if(client == null){
			// Create and initialize HTTP parameters
	        client = new DefaultHttpClient();

		}

		get = new HttpGet(url);
		
		Log.i(this.getClass().getSimpleName(), "downloading "+url);
		String content = client.execute(get, new StringResponseHandler(handler));
		
		Log.i(this.getClass().getSimpleName(), "download "+content.length()+" chars");
		return content;
	}
	
}