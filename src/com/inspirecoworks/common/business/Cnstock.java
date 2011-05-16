package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

//上海证券报
//http://passport.cnstock.com/http/formlogin.aspx
//name, password
public class Cnstock extends NewsPaper implements Serializable {

	public Cnstock() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String paperName() {
		// TODO Auto-generated method stub
		return "上海证券报";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -694147152709018731L;

	@Override
	protected Pattern getTitlePattern() {
		return Pattern.compile("<a href=([content|node][^\\s<>]+)[\\s]*[^\\s<>]*>(([^<>]|(<BR/>)|(<BR>))+)</a>");
	}

	@Override
	protected Pattern getBodyPattern() {
		//Pattern.compile("<div id=[^<>]+>([^<>]+<a href=[^<>]+>[^<>]+</a>[^<>]+)</div>");
		//Pattern text_p = Pattern.compile("[</founder-content>|<???enpcontent??>]([[<P>]|[</P>]|[<p>]|[</p>]|[^<>]]+)[</founder-content>|<???/enpcontent??>]");
		return Pattern.compile("((<founder-content>)|(<!--enpcontent-->))([[<P>]|[</P>]|[<p>]|[</p>]|[^<>]]+)((<!--/enpcontent-->)|(</founder-content>))");
		
	}

	@Override
	protected Pattern getFromPattern() {
		return Pattern.compile("<div id=[^<>]+>([^<>]+<a href=[^<>]+>[^<>]+</a>[^<>]+)</div>");
	}

	@Override
	public String getBaseUrl(int year, int month, int day) {
		return String.format("http://paper.cnstock.com/html/%d-%02d/%02d/", year,month,day);
	}

	@Override
	public String getHomeLink(int year, int month, int day) {
		
		return String.format("http://210.51.3.35/NewsPaper/column.php?year=%d&mon=%d&day=%d&type=1", year,month,day);
	}

	
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

	@Override
	public String getBodyEnd() {
		return "</html>";
	}

	@Override
	public String getBodyStart() {
		return "<div id=\"nlist\">";
	}

	boolean needLogin(String content)
	{
		return content.contains("http://passport.cnstock.com/http/formlogin.aspx");
	}
	
	//登录上海证券报
	//http://passport.cnstock.com/http/formlogin.aspx
	//name, password
	
	String getLoginUrl()
	{
		return "http://passport.cnstock.com/http/formlogin.aspx";
	}
	
	boolean needLogin()
	{
		return true;
	}
	
	HttpEntity getLoginEntity()
	{
		List<BasicNameValuePair> nvps = new Vector<BasicNameValuePair>();  
		
        //nvps.add(new BasicNameValuePair("name", username));  
        //nvps.add(new BasicNameValuePair("password", password)); 
		try {
			return new UrlEncodedFormEntity(nvps, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
