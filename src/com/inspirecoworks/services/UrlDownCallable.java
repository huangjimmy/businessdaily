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
	@Override
	public String call() throws Exception {
		if(client == null){
			// Create and initialize HTTP parameters
	        HttpParams params = new BasicHttpParams();
	        ConnManagerParams.setMaxTotalConnections(params, 100);
	        ConnPerRoute connPerRoute = new ConnPerRouteBean(100); 
	        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        ConnManagerParams.setTimeout(params, 6);
	        // Create and initialize scheme registry
	        SchemeRegistry schemeRegistry = new SchemeRegistry();
	        schemeRegistry.register(
	                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	
	        // Create an HttpClient with the ThreadSafeClientConnManager.
	        // This connection manager must be used if more than one thread will
	        // be using the HttpClient.
	        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		
	        client = new DefaultHttpClient(cm, params);

		}
		
		ThreadSafeClientConnManager m = (ThreadSafeClientConnManager) client.getConnectionManager();
		Log.i("UrlDownCallable", "Connections in pool:"+m.getConnectionsInPool());
		
		get = new HttpGet(url);
		if(data != null)
		{
			get = new HttpPost(url);
			((HttpPost)get).setEntity(data);
		}
		
		get.addHeader("User-Agent", "Baiduspider-news");
		get.addHeader("Pragma", "no-cache");
		
		Log.i(this.getClass().getSimpleName(), "downloading "+url);
		String content = client.execute(get, new StringResponseHandler(handler));
		
		Log.i(this.getClass().getSimpleName(), "download "+content.length()+" chars");
		return content;
	}
	
}