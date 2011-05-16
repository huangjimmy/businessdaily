package com.inspirecoworks.businessdaily.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.Section;
import com.inspirecoworks.services.DownloadExecutorService;
import com.inspirecoworks.services.DownloadTask;
import com.inspirecoworks.services.NotifyHandler;



public class NewsPaperReader  {

	private NewsPaper paper;
	
	public NewsPaper getPaper() {
		return paper;
	}

	public void setPaper(NewsPaper paper) {
		this.paper = paper;
	}

	private transient Context context;
	//BaseExpandableListAdapter adapter;
	
	public void setContext(Context context){this.context = context;}
	
	public void BeginRead()
	{
		
	}
	
	public void Cancel()
	{
		
	}
	
	ThreadPoolExecutor executor = new DownloadExecutorService(10,50,60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	public interface OnPaperListener
	{
		public void onDone();
		public void onException(String t);
	}
	public void readPaper(int year, int month, int day, final OnPaperListener listener)
	{
		if(year == paper.getYear() && month == paper.getMonth() && day == paper.getDay())
		{
			return;
		}
		paper.setYear(year);paper.setMonth(month);paper.setDay(day);
		final String base_url = paper.getBaseUrl(year, month, day);
		final String url = paper.getHomeLink(year, month, day);
		
	
		
		NotifyHandler.ResultReceiver receiver = new NotifyHandler.ResultReceiver(){
			@Override
			public void onDone(String content) {
				try {
					//String content = task.get();
					Log.i(paper.getClass().getSimpleName(), "Newspaper data downloaded.");
					
					 
					Log.i(paper.getClass().getSimpleName(),":parsing paper data...");
					if(populatePaper(content, base_url))
					{
						writeCache(url, content);
						Log.i(paper.getClass().getSimpleName(),":paper data parsed");
						listener.onDone();
					}
					else
					{
						Log.i(paper.getClass().getSimpleName(),":paper data incorrect");
						if(paper.isEmpty())
							listener.onException("EMPTY");
						else 
							listener.onException("报纸数据不正确.");
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

			@Override
			public void onException(String t) {
				Log.i(paper.getClass().getSimpleName(), t);
				listener.onException(t);
			}

			@Override
			public void OnDownloading(int bytesRecv,int size) {
				
			}

			@Override
			public void onAfter(Runnable task) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBefore(Runnable task) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		final DownloadTask task = new DownloadTask(url, new NotifyHandler(receiver));

		Log.i(paper.getClass().getSimpleName(),"start downloading newspaper");
		String content = this.readCache(url);
		
		if(content != null && populatePaper(content, base_url)){
			listener.onDone();
		}
		else{
		
			this.executor.submit(task);
		}
	}
	

    protected boolean populatePaper(String body, String base_url)
    {
		paper.clear();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(body)));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getDocumentElement().getChildNodes();
			for(int i=0; i< list.getLength(); i++)
			{
				Node c = list.item(i);
				String id = c.getChildNodes().item(0).getTextContent();
				String name = c.getChildNodes().item(1).getTextContent();
				Section s = new Section();
				s.title = name;
				s.link = id;
				paper.addSection(s);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return !paper.isEmpty();
	}

    public interface OnArticleListener
    {
    	public void onDone();
    	public void onException(String t);
    	public void onStatus(String s);
    }
    public void readArticle(final int section, final String section_id, final OnArticleListener listener)
    {
    	readArticle(section, section_id, listener, false);
    }
    public void readArticle(final int section, final String section_id, final OnArticleListener listener, boolean forceRefresh)
	{
		
		final String url = "http://210.51.3.35/NewsPaper/article.php?id="+section_id;

		final String cache_file = locateCache(url);
		
		
		NotifyHandler.ResultReceiver receiver = new NotifyHandler.ResultReceiver(){

			@Override
			public void onDone(String content) {
				try {
					if(cache_file==null)writeCache(url, content);
					
					loadArticles(section, content);
					listener.onDone();
				} catch (Exception e) {
					
					e.printStackTrace();
				} 
			}

			@Override
			public void onException(String t) {
				Log.i(paper.getClass().getSimpleName(), t);
				listener.onException(t);
				
			}

			@Override
			public void OnDownloading(int bytesRecv, int size) {
				
				//adapter.notifyDataSetChanged();
				
			}

			@Override
			public void onAfter(Runnable task) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBefore(Runnable task) {
				listener.onStatus("开始下载");
			}
			
		};
		
		final DownloadTask task = 
			cache_file == null || forceRefresh?
					new DownloadTask(url, new NotifyHandler(receiver)):
					new DownloadTask(context, cache_file, new NotifyHandler(receiver));
		
		this.executor.submit(task);
	}
    
    public String readCache(String url)
	{
		try{
			String ddmmyy;
			Calendar cd = Calendar.getInstance();
			ddmmyy = cd.get(Calendar.YEAR)+"-"+(cd.get(Calendar.MONTH)+1)+cd.get(Calendar.DAY_OF_MONTH);
			
			String file = "/sdcard/BusinessDaily/"+ddmmyy+url.replace("/", "_").replace(":", "").replace("?", "");
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			int size = 256000;
			byte[] buffer = new byte[size];
			
			int total = 0;
			int read;
			while((read = bis.read(buffer, total, size-total)) > 0)
			{
				total += read;
			}
			bis.close();
			return new String(buffer);
		}
		catch(Exception ex)
		{
			Log.e(this.getClass().getSimpleName(), ex.getLocalizedMessage());
			return null;
		}
	}
    
	public void writeCache(String url, String content)
	{
		try{
			String ddmmyy;
			Calendar cd = Calendar.getInstance();
			ddmmyy = cd.get(Calendar.YEAR)+"-"+(cd.get(Calendar.MONTH)+1)+cd.get(Calendar.DAY_OF_MONTH);
			String file = "/sdcard/BusinessDaily/"+ddmmyy+url.replace("/", "_").replace(":", "").replace("?", "");
			new File("/sdcard/BusinessDaily/").mkdir();
			FileOutputStream fis = new FileOutputStream(file);
			fis.write(content.getBytes());
			fis.close();
		}
		catch(Exception ex){
			Log.e(this.getClass().getSimpleName(), ex.getLocalizedMessage());
		}
	}
	
	private String locateCache(String url) {
		try{
			String ddmmyy;
			new File("/sdcard/BusinessDaily/").mkdir();
			Calendar cd = Calendar.getInstance();
			ddmmyy = cd.get(Calendar.YEAR)+"-"+(cd.get(Calendar.MONTH)+1)+cd.get(Calendar.DAY_OF_MONTH);
			String file = "/sdcard/BusinessDaily/"+ddmmyy+url.replace("/", "_").replace(":", "").replace("?", "");
			FileInputStream fis = new FileInputStream(file);
			fis.close();
			
			return file;
		}
		catch(Exception ex){}
		return null;
	}
	
	public void loadArticles(int section, String content)
	{
		Section s = paper.readSectionAt(section);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(content)));
			doc.getDocumentElement().normalize();
			NodeList list = doc.getDocumentElement().getChildNodes();
			for(int i=0; i< list.getLength(); i++)
			{
				Node c = list.item(i);
				//String id = c.getChildNodes().item(0).getTextContent();
				String title = c.getChildNodes().item(1).getTextContent();
				String from = c.getChildNodes().item(2).getTextContent().replace("</P>", "").replace("</p>", "").replaceAll("<[^<>]+>", "\r\n").replaceAll("&nbsp;", " ").replace("\r\n", "").trim();
				String body = c.getChildNodes().item(3).getTextContent();
				String link = c.getChildNodes().item(4).getTextContent();
				Article a = new Article();
				a.setLink(link);a.setBody(body);a.setFrom(from);a.setTitle(title);
				a.setPreview(a.getBody().replace("</P>", "").replace("</p>", "").replaceAll("<[^<>]+>", "\r\n").replaceAll("&nbsp;", " ").replace("\r\n\r\n", "\r\n").trim());
				if(a.getPreview().length() > 100)
					a.setPreview(a.getPreview().substring(0,100));
				
				s.put(title, a);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
