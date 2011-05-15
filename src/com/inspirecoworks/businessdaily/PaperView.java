package com.inspirecoworks.businessdaily;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.Section;
import com.inspirecoworks.common.business.NewsPaper.OnArticleListener;
import com.mobclick.android.MobclickAgent;

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
	private View progress;
	private TextView textview_progress;
	
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
		progress = findViewById(R.id.progress_info_layout);
		textview_progress = (TextView) findViewById(R.id.progress_info_text);
		super.onFinishInflate();
	}



	/**
	 * @param newspaper the newspaper to set
	 */
	public void setNewspaper(NewsPaper newspaper) {
		this.newspaper = newspaper;
		this.textview_paper_title.setText(newspaper.paperName());
		this.textview_paper_date.setText(String.format("%d-%d-%d", newspaper.getYear(), newspaper.getMonth(), newspaper.getDay()));
		
		NewsPaperAdapter adapter = new NewsPaperAdapter(context, newspaper);
		listview_paper.setAdapter(adapter);
		
        listview_paper.setOnChildClickListener(this);
        listview_paper.setOnGroupExpandListener(this);
        if(!newspaper.isEmpty())progress.setVisibility(GONE);
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
		
	    Intent intent = new Intent(context, ReaderActivity.class);
	    intent.putExtra("article", article);
	    
	    MobclickAgent.onEvent(context, "Read News Started.");
	    context.startActivity(intent);
	    MobclickAgent.onEvent(context, "Read News Doned.");
		return false;
	}
	
	
	public void onGroupExpand(int groupPosition) {
		final BaseExpandableListAdapter adapter = 
			(BaseExpandableListAdapter)listview_paper.getExpandableListAdapter();
		final NewsPaper paper = newspaper;
		final int group = groupPosition;
		Section s = paper.readSectionAt(groupPosition);
		if(s.isEmpty())
		{
			this.textview_progress.setText(R.string.inprogress);
			this.progress.setVisibility(VISIBLE);

			paper.readArticle(groupPosition, s.link, new OnArticleListener(){

				
				public void onDone() {
					adapter.notifyDataSetChanged();
					PaperView.this.textview_progress.setText(R.string.inprogress);
					progress.setVisibility(GONE);
					
				}

				
				public void onException(String t) {
					//article reading
					
					adapter.notifyDataSetChanged();
					listview_paper.setVisibility(VISIBLE);
				
				}

				
				public void onStatus(String s) {
					PaperView.this.textview_progress.setText(s);
					adapter.notifyDataSetChanged();
				}
				
			});
		}
		
	}

	
}
