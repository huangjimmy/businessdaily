package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;

public class Cbn extends NewsPaper implements Serializable {

	public Cbn() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String paperName() {
		// TODO Auto-generated method stub
		return "第一财经";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1940968925614455574L;

	protected int getTitleIndex()
	{
		return 3;
	}
	
	protected int getLinkIndex()
	{
		return 1;
	}
	
	protected int getBodyIndex()
	{
		return 2;
	}
	
	protected boolean isSectionLink(String link)
	{
		return link.indexOf("category") >=0;
	}
	protected boolean isArticleLink(String link)
	{
		return link.indexOf("article") >=0;
	}
	
	@Override
	protected Pattern getBodyPattern() {
		// <div class?\"vb\" id?\"pzoom\">(^(</div>))</div>
		return Pattern.compile("<div class=\"vb\" id=\"pzoom\">([[^<>]*|[<>]]+></div></div>)*(([^<>]+|<br>)+)</div>");
	}

	@Override
	protected Pattern getFromPattern() {
		
		return Pattern.compile("<p[^<]+>([^<]+来源[^<>]+((<a[^<>]+>[^<>]+</a>)|[^<>]+)*)</p>");
	}

	@Override
	protected String getBaseUrl(int year, int month, int day) {
		return "http://dycj.ynet.com";
	}

	@Override
	protected String getHomeLink(int year, int month, int day) {
		return String.format("http://dycj.ynet.com/index.jsp?pdid=%d-%02d-%02d", year, month, day);
	}

	@Override
	protected Pattern getTitlePattern() {
		//<a href="/category.jsp?pcid=15938256">头版</a>
		//<a href="/article.jsp?oid=75697187">欧洲大雪机场不堪一击？</a>
		return Pattern.compile("<a href=\"(/(article|category).jsp?[^<>]+)\">([^<>]+)</a>");
	}

	@Override
	protected String getBodyEnd() {
		// TODO Auto-generated method stub
		return "</html>";
	}

	@Override
	protected String getBodyStart() {
		return "正文</div>";
	}

}
