package com.inspirecoworks.services;

import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.os.Handler;
import android.util.Log;

public class UrlDownCallable implements Callable<String>
{

	private String url;
	private static DefaultHttpClient client;
	private Handler handler;
	private HttpEntity data;
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
		this.data = data;
		
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