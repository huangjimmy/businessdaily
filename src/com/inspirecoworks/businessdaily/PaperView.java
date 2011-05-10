package com.inspirecoworks.businessdaily;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.Section;
import com.inspirecoworks.common.business.NewsPaper.OnArticleListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class PaperView extends LinearLayout implements OnChildClickListener, OnGroupExpandListener{

	private ExpandableListView listview_paper;
	private TextView textview_paper_title;
	private TextView textview_paper_date;
	private Context context;
	
	private NewsPaper newspaper ;
	
	
	public PaperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	

	@Override
	protected void onFinishInflate() {
		listview_paper = (ExpandableListView) findViewById(R.id.page_list);
		textview_paper_title = (TextView) findViewById(R.id.paper_title);
		textview_paper_date = (TextView) findViewById(R.id.paper_date);
		super.onFinishInflate();
	}



	/**
	 * @param newspaper the newspaper to set
	 */
	public void setNewspaper(NewsPaper newspaper) {
		this.newspaper = newspaper;
		this.textview_paper_title.setText(newspaper.paperName());
		this.textview_paper_date.setText(String.format("%d-%d-%d", newspaper.getYear(), newspaper.getMonth(), newspaper.getDay()));
		
		List<Map<String, Object>> groupData;
		List<Map<String, Object>> child;
		List<List<Map<String, Object>>> childData;
		
		groupData = new Vector<Map<String, Object>>();
        childData = new Vector<List<Map<String, Object>>>();
     
        for(Section s:newspaper.getSections())
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
        
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(context, 
				groupData, R.layout.page_item, groupFrom, groupTo,
				childData, R.layout.article_item, childFrom, childTo);
        
        listview_paper.setAdapter(adapter);
        
        listview_paper.setOnChildClickListener(this);
        listview_paper.setOnGroupExpandListener(this);
	}

	/**
	 * @return the newspaper
	 */
	public NewsPaper getNewspaper() {
		return newspaper;
	}

	
		public boolean onChildClick(ExpandableListView parent, View v,
				int group, int child, long id) {

			Article article = (Article) parent.getExpandableListAdapter().getChild(group, child);
	        /*final BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) parent.getExpandableListAdapter();
	        if(article.getBody() == null || article.getBody().trim().length() == 0)
			{
	        	NewsPaper paper = newspaper;
	        	paper.readArticle(group, article.getTitle(), new OnArticleListener(){


					public void onDone() {
						if(article.getBody()!=null && article.getBody().length()>5)
						{
							adapter.notifyDataSetChanged();
						}
						else
						{
							Log.i("READ", article.getTitle()+": ERROR content incorrect");
							onException("读取错误");
						}
					}

					
					public void onException(String t) {
						//article reading
						article.setFrom(t);
						adapter.notifyDataSetChanged();
						Log.i("READ", article.getTitle()+": "+t);
						//retry in case of exception after five seconds
						//paper.readArticle(group, a.getTitle(), this);
					}

					
					public void onStatus(String s) {
						article.setFrom(s);
						adapter.notifyDataSetChanged();
					}
					
				});
				return false;
			}*/
	        Intent intent = new Intent(context, Reader.class);
	        intent.putExtra("article", article);
	        
	        context.startActivity(intent);
	        //elv.focusableViewAvailable(v);
			return false;
		}
	
	
		public void onGroupExpand(int groupPosition) {
			final BaseExpandableListAdapter adapter = 
				(BaseExpandableListAdapter)listview_paper.getExpandableListAdapter();
			final NewsPaper paper = newspaper;
			Section s = paper.readSectionAt(groupPosition);
			if(s.isEmpty())
			{
				this.listview_paper.setVisibility(GONE);
			}
		
			/*for(final Article a:s.values())
			paper.readArticle(groupPosition, a.getTitle(), new OnArticleListener(){

				
				public void onDone() {
					if(a.getBody()!=null && a.getBody().length()>5)
					{
						adapter.notifyDataSetChanged();
					}
					else
					{
						Log.i("READ", a.getTitle()+": ERROR content incorrect");
						onException("读取文章错误");
					}
				}

				
				public void onException(String t) {
					//article reading
					a.setFrom(t);
					adapter.notifyDataSetChanged();
					Log.i("READ", a.getTitle()+": "+t);
					//retry in case of exception after five seconds
					//paper.readArticle(group, a.getTitle(), this);
				}

				
				public void onStatus(String s) {
					a.setFrom(s);
					adapter.notifyDataSetChanged();
				}
				
			});*/
			
		}

	
}
