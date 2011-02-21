package com.inspirecoworks.common.business;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;

//需要登陆了
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
	protected String getBaseUrl(int year, int month, int day) {
		return String.format("http://paper.cnstock.com/html/%d-%02d/%02d/", year,month,day);
	}

	@Override
	protected String getHomeLink(int year, int month, int day) {
		
		return getBaseUrl(year,month,day)+"node_3.htm";
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
	protected String getBodyEnd() {
		return "</html>";
	}

	@Override
	protected String getBodyStart() {
		return "<div id=\"nlist\">";
	}

	boolean needLogin(String content)
	{
		return content.contains("http://passport.cnstock.com/http/formlogin.aspx");
	}
	
	//需要登陆了
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
		
        nvps.add(new BasicNameValuePair("name", username));  
        nvps.add(new BasicNameValuePair("password", password)); 
		try {
			return new UrlEncodedFormEntity(nvps, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
