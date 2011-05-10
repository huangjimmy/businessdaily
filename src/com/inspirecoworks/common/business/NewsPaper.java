package com.inspirecoworks.common.business;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.inspirecoworks.businessdaily.R;
import com.inspirecoworks.services.DownloadExecutorService;
import com.inspirecoworks.services.DownloadTask;
import com.inspirecoworks.services.NotifyHandler;

public abstract class NewsPaper implements Serializable {

	private transient Context context;
	//BaseExpandableListAdapter adapter;
	
	public void setContext(Context context){this.context = context;}
	/**
	 * 
	 */
	protected static final long serialVersionUID = -3165845216126748460L;
	protected Vector<Section> sections = new Vector<Section>();
	protected Vector<String> titles = new Vector<String>();
	int _year, _month, _day;

	public int getYear() {
		return _year;
	}


	public void setYear(int _year) {
		this._year = _year;
	}


	public int getMonth() {
		return _month;
	}


	public void setMonth(int _month) {
		this._month = _month;
	}


	public int getDay() {
		return _day;
	}


	public void setDay(int _day) {
		this._day = _day;
	}


	/*public BaseExpandableListAdapter getAdapter(Activity context)
	{
		
		List<Map<String, Object>> groupData;
		List<Map<String, Object>> child;
		List<List<Map<String, Object>>> childData;
		
		groupData = new Vector<Map<String, Object>>();
        childData = new Vector<List<Map<String, Object>>>();
     
        for(Section s:sections)
        {
        	Map<String, Object> group = new LinkedHashMap<String, Object>();
        	child = new Vector<Map<String, Object>>();
        	for(String atitle:s.keySet())
        	{
                child.add(s.get(atitle));
        	}
        	group.put("TITLE", s.title);
        	groupData.add(group);
        	childData.add(child);
        }
        
        String[] groupFrom = new String[]{"TITLE"};
        int[] groupTo = new int[]{R.id.page_name};
        String[] childFrom = new String[]{Article.TITLE,Article.PREVIEW,Article.FROM};
        int[] childTo = new int[]{R.id.article_title,R.id.article_preview,R.id.article_from};
        
        adapter = new SimpleExpandableListAdapter(context, 
				groupData, R.layout.page_item, groupFrom, groupTo,
				childData, R.layout.article_item, childFrom, childTo);
		
        
		//adapter.setNewsPaper(this);
		return adapter;
	}*/
	
	
	public NewsPaper()
	{
		super();
		
	}
	
	public List<String> listSections()
	{
		return titles;
	}
	public Vector<Section> getSections()
	{
		return sections;
	}

	
	public  String paperName()
	{
		return "";
	}
	
	protected abstract String getBaseUrl(int year, int month, int day);
	protected abstract String getHomeLink(int year, int month, int day);
	protected abstract String getBodyStart();
	protected abstract String getBodyEnd();
	
	protected boolean isSectionLink(String link)
	{
		return link.indexOf("node") >=0;
	}
	protected boolean isArticleLink(String link)
	{
		return link.indexOf("content") >=0;
	}

	ThreadPoolExecutor executor = new DownloadExecutorService(10,50,60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	public interface OnPaperListener
	{
		public void onDone();
		public void onException(String t);
	}
	public void readPaper(int year, int month, int day, final OnPaperListener listener)
	{
		if(year == _year && month == _month && day == _day)
		{
			return;
		}
		_year = year;
		_month = month;
		_day = day;
		try{
		final String base_url = getBaseUrl(year, month, day);
		final String url = getHomeLink(year, month, day);
		final NewsPaper paper = this;
		
		NotifyHandler.ResultReceiver loginReceiver = new NotifyHandler.ResultReceiver(){
		
			@Override
			public void onDone(String loginResult) {
				try {
					//String loginResult = loginTask.get();
					if(needLogin(loginResult))
					{
						Log.i(paper.getClass().getSimpleName(),":login failed");
						listener.onException("login failed.");
					}
					else
					{
						/*Log.i(paper.getClass().getSimpleName(),":login success");
						Log.i(paper.getClass().getSimpleName(),":parsing paper data...");
						paper.populatePaper(loginResult, base_url);
						Log.i(paper.getClass().getSimpleName(),":paper data parsed");
						listener.onDone();*/
						executor.submit(nextTask);
					}
				} catch (Exception e) {
					Log.i(paper.getClass().getSimpleName(),":login Exception:"+e.getLocalizedMessage());
					e.printStackTrace();
					listener.onException(e.getLocalizedMessage());
				} 
				
			}

			@Override
			public void onException(String t) {
				Log.i(paper.getClass().getSimpleName(),":login exception:"+t);
			}

			@Override
			public void OnDownloading(int bytesRecv, int size) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAfter(Runnable task) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBefore(Runnable task) {
				
			}
			
		};
		DownloadTask loginTask = new DownloadTask(paper.getLoginUrl(), paper.getLoginEntity(), url,
				new NotifyHandler(loginReceiver)); 
		
		NotifyHandler.ResultReceiver receiver = new NotifyHandler.ResultReceiver(){
			@Override
			public void onDone(String content) {
				try {
					//String content = task.get();
					Log.i(paper.getClass().getSimpleName(), "Newspaper data downloaded.");
					if(content.contains("Exception") && content.contains("Form_"))
					{
						listener.onException("登录发生错误");
						return;
					}
					else if(paper.needLogin(content))
					{
						Log.i(paper.getClass().getSimpleName(),":logging in");
						executor.submit(nextTask);
						return;
					}
					 
					Log.i(paper.getClass().getSimpleName(),":parsing paper data...");
					if(paper.populatePaper(content, base_url))
					{
						NewsPaper.this.writeCache(url, content);
						Log.i(paper.getClass().getSimpleName(),":paper data parsed");
						listener.onDone();
					}
					else
					{
						Log.i(paper.getClass().getSimpleName(),":paper data incorrect");
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
		//TODO ��ȡhttp��������
		Log.i(paper.getClass().getSimpleName(),"start downloading newspaper");
		String content = this.readCache(url);
		
		if(content != null && paper.populatePaper(content, base_url)){
			listener.onDone();
		}
		else{
			loginReceiver.nextTask = task;
			receiver.nextTask = loginTask;
			this.executor.submit(task);
		}
		
		}catch(Exception ex)
		{
			Log.d("ERR", ex.getLocalizedMessage());
		}
	}
	

    protected boolean populatePaper(String body, String base_url)
    {
        int content_start = body.indexOf(getBodyStart());//;"<div id=\"nlist\">");
		int content_end = body.indexOf(getBodyEnd());//"����Ŀ¼end");
		
		if(content_start < 0)content_start = 0;
		
		if(content_end < 0)content_end = body.length()-1;
		
		body = body.substring(content_start, content_end);

		
		this.sections.clear();
		this.titles.clear();
		
		Pattern p = getTitlePattern();
		Matcher m = p.matcher(body);
		
		Section cur = null;
		while(m.find())
		{
			String section = m.group(this.getTitleIndex());//.replace("<BR>", "\r\n")
			//.replace("<BR/>", "\r\n");
			 if(isSectionLink(m.group(this.getLinkIndex())))
			 {
				 cur = new Section();
				 cur.title = section;
				 cur.link = base_url+m.group(this.getLinkIndex());
				 sections.add(cur);
				 titles.add(section);
			 }
			 else
			 {
				 Article a = new Article();
				 a.setLink(base_url+m.group(this.getLinkIndex()));
				 a.setTitle(section);
				 cur.put(section, a);
			 }
		}
		
		return !titles.isEmpty();
	}

    public interface OnArticleListener
    {
    	public void onDone();
    	public void onException(String t);
    	public void onStatus(String s);
    }
    public void readArticle(final int section, final String article_title, final OnArticleListener listener)
    {
    	readArticle(section, article_title, listener, false);
    }
    public void readArticle(final int section, final String article_title, final OnArticleListener listener, boolean forceRefresh)
	{
		final Article article = (Article)sections.get(section).get(article_title);
		final String url = article.getLink();
		
		if(article.getBody() != null && article.getBody() != "")
		{
			listener.onDone();
			return;
		}

		final String cache_file = NewsPaper.this.locateCache(url);
		
		NotifyHandler.ResultReceiver loginReceiver = new NotifyHandler.ResultReceiver(){
			
			@Override
			public void onDone(String loginResult) {
				try {
					//String loginResult = loginTask.get();
					if(needLogin(loginResult))
					{
						Log.i(NewsPaper.this.getClass().getSimpleName(),":login failed");
						listener.onException("login failed.");
					}
					else
					{
						executor.submit(nextTask);
					}
				} catch (Exception e) {
					Log.i(NewsPaper.this.getClass().getSimpleName(),":login Exception:"+e.getLocalizedMessage());
					e.printStackTrace();
					listener.onException(e.getLocalizedMessage());
				} 
				
			}

			@Override
			public void onException(String t) {
				Log.i(NewsPaper.this.getClass().getSimpleName(),":login exception:"+t);
				listener.onException("登录发生错误");
			}

			@Override
			public void OnDownloading(int bytesRecv, int size) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAfter(Runnable task) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBefore(Runnable task) {
				listener.onStatus("��½��...");
			}
			
		};
		DownloadTask loginTask = new DownloadTask(getLoginUrl(), getLoginEntity(), url,
				new NotifyHandler(loginReceiver)); 
		
		NotifyHandler.ResultReceiver receiver = new NotifyHandler.ResultReceiver(){

			@Override
			public void onDone(String content) {
				try {
					if(cache_file==null)NewsPaper.this.writeCache(url, content);
					
					if(needLogin(content))
					{
						Log.i(NewsPaper.this.getClass().getSimpleName(),":logging in");
						executor.submit(nextTask);
						return;
					}
					
					NewsPaper.this.loadArticle(article, content);
					listener.onDone();
				} catch (Exception e) {
					article.setBody("");
					article.setPreview("");
					e.printStackTrace();
				} 
			}

			@Override
			public void onException(String t) {
				Log.i(NewsPaper.this.getClass().getSimpleName(), t);
				listener.onException(t);
				
			}

			@Override
			public void OnDownloading(int bytesRecv, int size) {
				article.setFrom(bytesRecv+"bytes("+size+" bytes) received.");
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
		
		
		loginReceiver.nextTask = task;
		receiver.nextTask = loginTask;

		this.executor.submit(task);
	}



	protected abstract Pattern getTitlePattern();
	protected abstract Pattern getFromPattern();
	protected abstract Pattern getBodyPattern();
	
	protected int getFromIndex()
	{
		return 1;
	}
	
	protected int getTitleIndex()
	{
		return 2;
	}
	
	protected int getLinkIndex()
	{
		return 1;
	}
	
	protected int getBodyIndex()
	{
		return 4;
	}
	
	protected void loadArticle(Article article, String content)
	{
		//Pattern title_p = Pattern.compile("<div id=\\\"[content\\_title][^<>]*>(([^<>]|(<BR/>)|(<BR>))+)</div>");
		Pattern about_p = getFromPattern();//Pattern.compile("<div id=[^<>]+>([^<>]+<a href=[^<>]+>[^<>]+</a>[^<>]+)</div>");
		//Pattern text_p = Pattern.compile("[</founder-content>|<???enpcontent??>]([[<P>]|[</P>]|[<p>]|[</p>]|[^<>]]+)[</founder-content>|<???/enpcontent??>]");
		Pattern text_p = getBodyPattern();//Pattern.compile("((<founder-content>)|(<!--enpcontent-->))([[<P>]|[</P>]|[<p>]|[</p>]|[^<>]]+)((<!--/enpcontent-->)|(</founder-content>))");
		
		Matcher m;

		//m = title_p.matcher(content);
		//if(m.find())
		{
			//article.t = m.group(1).replace("<BR>", "\r\n")
			//.replace("<BR/>", "\r\n");;
		}
		
		m = about_p.matcher(content);
		if(m.find())
		{
			article.setFrom(m.group(getFromIndex()).replaceAll("<[/]*[P|p]>", "\r\n").replaceAll("<[B|b][R|r][/]*>", "\r\n").replaceAll("<[^<>]+>", "").replace("&nbsp;", " ").replace("&gt;", ">").trim());
		}
		
		m = text_p.matcher(content);
		if(m.find())
		{
			article.setBody(m.group(getBodyIndex()));//.replaceAll("<[/]*[P|p]>", "\r\n").replaceAll("<[B|b][R|r][/]*>", "\r\n").replaceAll("<[^<>]+>", "").replace("&nbsp;", " ").replace("&gt;", ">").trim());
			article.setPreview(article.getBody().replace("</P>", "").replace("</p>", "").replaceAll("<[^<>]+>", "\r\n").replaceAll("&nbsp;", " ").replace("\r\n\r\n", "\r\n").trim());
			if(article.getPreview().length() > 100){
				article.setPreview(article.getPreview().substring(0,100));
			}
		}
		
	}
	
	public Section readSectionAt(int i)
	{
		return sections.get(i);
	}

	public boolean isEmpty()
	{
		return sections.isEmpty();
	}
	
	
	boolean isLogin()
	{
		return true;
	}
	
	boolean needLogin(String content)
	{
		return false;
	}
	boolean needLogin()
	{
		return false;
	}
	
	String getLoginUrl()
	{
		return "";
	}
	
	static String username = "";
	static String password = "";
	
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String name) {
		username = name;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String pass) {
		password = pass;
	}

	HttpEntity getLoginEntity()
	{
		List<BasicNameValuePair> nvps = new Vector<BasicNameValuePair>();  
		
        nvps.add(new BasicNameValuePair("name", username));  
        nvps.add(new BasicNameValuePair("password", password)); 
		try {
			return new UrlEncodedFormEntity(nvps, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	
	public void processLogin()
	{
		String url = getLoginUrl();//"http://passport.cnstock.com/http/formlogin.aspx";
        
        HttpPost request = new HttpPost(url);
 
		request.setEntity(getLoginEntity());//
		
		return;
	

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

}
