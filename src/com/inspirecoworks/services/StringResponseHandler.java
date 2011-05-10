package com.inspirecoworks.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class StringResponseHandler implements ResponseHandler<String> {

	private Handler handler;
	public StringResponseHandler(Handler handler)
	{
		super();
		this.handler = handler;
	}


	public String handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		
		Message msg = new Message();
		msg.what = DownloadTask.DOWNLOADING;
		Bundle bundle = new Bundle();
		bundle.putInt("BYTES", 0);
		bundle.putInt("SIZE", -1);
		handler.sendMessage(msg);
		
		try{
		HttpEntity entity = response.getEntity();
		
		Header content_length = response.getFirstHeader("Content-Length");
		
		for(Header h:response.getAllHeaders()){
			Log.i("DefaultHttpClient"+this.hashCode(), h.getName()+":"+h.getValue());
		}
		int size = 256000;
		boolean hasSize = true;
		try{
			String lenStr = content_length.getValue();
			size = Integer.parseInt(lenStr);
		}catch(Exception ex){hasSize = false;}
		
		BufferedInputStream sis = new BufferedInputStream(entity.getContent(), 65536);

		byte[] cb = new byte[size+5];
	
		int nr;
		int total = 0;
		Log.i("Response", "Downloading");
		while(total < size)
		{
			nr = sis.read(cb, total, size-total);
			Log.i("Response", "Downloading recv "+nr+" bytes");
			if(nr <0)
			{
				break;
			}
			total += nr;
			Log.i("DefaultHttpClient"+this.hashCode(),"read "+nr+":"+size);
			
			msg = new Message();
			msg.what = DownloadTask.DOWNLOADING;
			bundle = new Bundle();
			bundle.putInt("BYTES", total);
			msg.setData(bundle);
			
			bundle.putInt("SIZE", hasSize?size:total);
			
			handler.sendMessage(msg);
			
		}
		
		byte[] result = new byte[total];
		System.arraycopy(cb, 0, result, 0, total);
		String content = new String(result);
		
		String charset = "utf-8";
		Matcher m = Pattern.compile("charset=([a-z0-9\\-]+)").matcher(content);
		if(m.find())
		{
			charset = m.group(1);
			if(charset.compareTo("utf-8") != 0)
			{
				Log.i("html downloaded", "charset="+charset);
				content = new String(result, charset);
			}
		}	
		
		sis.close();
		Log.i("DefaultHttpClient"+this.hashCode(), "Read End");

		return content;
		}catch(ClientProtocolException ex)
		{

			throw ex;
		}catch(IOException ex)
		{

			throw ex;
		}
		
	}

};