package com.inspirecoworks.common.business;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class NewsPaper implements Serializable {

	
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
	
	public abstract String getBaseUrl(int year, int month, int day);
	public abstract String getHomeLink(int year, int month, int day);
	public abstract String getBodyStart();
	public abstract String getBodyEnd();
	
	public boolean isSectionLink(String link)
	{
		return link.indexOf("node") >=0;
	}
	public boolean isArticleLink(String link)
	{
		return link.indexOf("content") >=0;
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
	
	
	
	public Section readSectionAt(int i)
	{
		return sections.get(i);
	}

	public boolean isEmpty()
	{
		return sections.isEmpty();
	}
	
	public void clear()
	{
		sections.clear();
		titles.clear();
	}
	
	public void addSection(Section s)
	{
		sections.add(s);
		titles.add(s.title);
	}

}
