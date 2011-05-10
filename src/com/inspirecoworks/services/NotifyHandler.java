package com.inspirecoworks.services;

import android.os.Handler;
import android.os.Message;

public class NotifyHandler extends Handler
{
	public static abstract class ResultReceiver
	{
		public DownloadTask nextTask;
		public abstract void onDone(String c);
		public abstract void onException(String t);
		public abstract void OnDownloading(int bytesRecv, int size);
		public abstract void onBefore(Runnable task);
		public abstract void onAfter(Runnable task);
	}
	
	private ResultReceiver receiver;
	
	public NotifyHandler(ResultReceiver receiver)
	{
		this.receiver = receiver;
	}

	@Override
	public void handleMessage(Message msg) {
		if(receiver != null)
		{
			switch(msg.what){
			case DownloadTask.DONE:
				receiver.onDone(msg.getData().getString("content"));
				break;
			case DownloadTask.EXCEPTION:
				String t = msg.getData().getString("EXCEPTION");
				receiver.onException(t);
			case DownloadTask.DOWNLOADING:
				int bytesRecv = msg.getData().getInt("BYTES");
				int size = msg.getData().getInt("SIZE");
				receiver.OnDownloading(bytesRecv, size);
				break;
			case DownloadTask.BEFORE:
				receiver.onBefore(null);
				break;
			}
		}
	}
	
};