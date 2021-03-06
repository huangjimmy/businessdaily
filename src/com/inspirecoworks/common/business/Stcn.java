package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Stcn extends NewsPaper  implements Serializable{

	Pattern about_p = Pattern.compile("<div class=\"from\">([^<>]+)</div>");
	//Pattern text_p = Pattern.compile("[</founder-content>|<???enpcontent??>]([[<P>]|[</P>]|[<p>]|[</p>]|[^<>]]+)[</founder-content>|<???/enpcontent??>]");
	Pattern text_p = Pattern.compile("((<founder-content>)|(<!-- =============================== 版面目录start ================================= -->))([[<P>]|[</P>]|[<p>]|[</p>]|[^<>]]+)((<!-- =========================== 版面目录end ========================================= -->)|(</founder-content>))");
	
	
	public Stcn() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String paperName() {
		// TODO Auto-generated method stub
		return "证券时报";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3688546170791997867L;

	@Override
	protected Pattern getBodyPattern() {
		return text_p;
	}

	@Override
	protected Pattern getFromPattern() {
		return about_p;
	}

	@Override
	public String getBaseUrl(int year, int month, int day) {
		return String.format("http://epaper.stcn.com/paper/zqsb/html/%d-%02d/%02d/",year,month,day);
	}

	@Override
	public String getHomeLink(int year, int month, int day) {
		return String.format("http://210.51.3.35/NewsPaper/column.php?year=%d&mon=%d&day=%d&type=2", year,month,day);
	}

	@Override
	protected Pattern getTitlePattern() {
		return Pattern.compile("<a href=([content|node][^\\s<>]+)[\\s]*[^\\s<>]*>([[a-zA-Z]*[0-9]+]*��)*(([^<>]|(<BR/>)|(<BR>))+)</a>");
	}

	protected int getFromIndex()
	{
		return 1;
	}
	
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
		return 4;
	}

	@Override
	public String getBodyEnd() {
		return "版面目录end";
	}

	@Override
	public String getBodyStart() {
		return "版面目录start";
	}
}
