package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.util.regex.Pattern;

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
	
	public boolean isSectionLink(String link)
	{
		return link.indexOf("category") >=0;
	}
	public boolean isArticleLink(String link)
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
		
		return Pattern.compile("<p[^<]+>([^<]+��Դ[^<>]+((<a[^<>]+>[^<>]+</a>)|[^<>]+)*)</p>");
	}

	@Override
	public String getBaseUrl(int year, int month, int day) {
		return "http://dycj.ynet.com";
	}

	@Override
	public String getHomeLink(int year, int month, int day) {
		return String.format("http://210.51.3.35/NewsPaper/column.php?year=%d&mon=%d&day=%d&type=3", year,month,day);
	}

	@Override
	protected Pattern getTitlePattern() {
		//<a href="/category.jsp?pcid=15938256">ͷ��</a>
		//<a href="/article.jsp?oid=75697187">ŷ�޴�ѩ����һ����</a>
		return Pattern.compile("<a href=\"(/(article|category).jsp?[^<>]+)\">([^<>]+)</a>");
	}

	@Override
	public String getBodyEnd() {
		// TODO Auto-generated method stub
		return "</html>";
	}

	@Override
	public String getBodyStart() {
		return "����</div>";
	}

}
