package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.util.HashMap;

public class Article extends HashMap<String, Object>  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8123736169880558104L;
	public final static String TITLE="TITLE";
	public final static String FROM="FROM";
	public final static String BODY="BODY";
	public final static String LINK="LINK";
	public final static String PREVIEW="PREVIEW";
	
	public String getFrom()
	{
		Object obj = this.get(FROM);
		if(obj!=null)return obj.toString();
		
		return null;
	}
	
	public void setFrom(String value)
	{
		put(FROM, value);
	}
	
	public String getLink()
	{
		Object obj = this.get(LINK);
		if(obj!=null)return obj.toString();
		
		return null;
	}
	
	public void setLink(String value)
	{
		put(LINK, value);
	}
	
	public String getBody()
	{
		Object obj = this.get(BODY);
		if(obj!=null)return obj.toString();
		
		return null;
	}
	
	public void setBody(String value)
	{
		put(BODY, value);
	}
	
	public String getTitle()
	{
		Object obj = this.get(TITLE);
		if(obj!=null)return obj.toString();
		
		return null;
	}
	
	public void setTitle(String value)
	{
		put(TITLE, value);
	}
	
	public String getPreview()
	{
		Object obj = this.get(PREVIEW);
		if(obj!=null)return obj.toString();
		
		return null;
	}
	
	public void setPreview(String value)
	{
		put(PREVIEW, value);
	}
	
	public boolean isCache()
	{
		return getBody() != null;
	}
}
