package com.inspirecoworks.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class CacheDownCallable  implements Callable<String>{
	private String file;
	private Handler handler;
	public CacheDownCallable(Context context, String file, Handler handler)
	{
		this.file = file;
		this.handler = handler;
	}

	public String call() throws Exception {
		
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		int size = (int) new File(file).length();
		byte[] buffer = new byte[size];

		int total = 0;
		int read;
		while((read = bis.read(buffer, total, size-total)) > 0)
		{
			total += read;
			Message msg = new Message();
			msg.what = 2;
			Bundle bundle = new Bundle();
			bundle.putInt("BYTES", total);
			msg.setData(bundle);

			bundle.putInt("SIZE", size);
			
			handler.sendMessage(msg);
		}
		bis.close();
		String content = new String(buffer);
		return content;
	}
}

