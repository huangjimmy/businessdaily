package com.inspirecoworks.businessdaily;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inspirecoworks.businessdaily.io.NewsPaperReader;
import com.inspirecoworks.businessdaily.io.NewsPaperReader.OnPaperListener;
import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.Section;
import com.mobclick.android.MobclickAgent;

public class PaperView extends LinearLayout implements OnChildClickListener, OnGroupExpandListener, OnPaperListener, View.OnClickListener{

	private ExpandableListView listview_paper;
	private TextView textview_paper_title;
	private TextView textview_paper_date;
	private Context context;
	private View progress;
	private TextView textview_progress;
	private TextView textview_empty;
	private TextView textview_timeout;
	
	private NewsPaper newspaper ;
	private NewsPaperReader reader = new NewsPaperReader();
	
	private Calendar calendar = null;
	
	public PaperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(this.isInEditMode())return;
		
		listview_paper = (ExpandableListView) findViewById(R.id.page_list);
		textview_paper_title = (TextView) findViewById(R.id.paper_title);
		textview_paper_date = (TextView) findViewById(R.id.paper_date);
		progress =  findViewById(R.id.progress_info_layout);
		
		textview_progress = (TextView) findViewById(R.id.progress_info_text);
		textview_empty = (TextView) findViewById(R.id.progress_info_text_empty);
		textview_timeout = (TextView) findViewById(R.id.progress_info_text_timeout);
		
		//textview_timeout.setOnClickListener(this);
		//textview_empty.setOnClickListener(this);
		progress.setOnClickListener(this);
		
		final DatePickerDialog.OnDateSetListener odsl = new DatePickerDialog.OnDateSetListener(){

			public void onDateSet(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DATE, dayOfMonth);
				
				showInProgress();
				setNewspaper(newspaper);
			}
			
		};
		
		OnClickListener ocl = new OnClickListener(){
			
			public void onClick(View v) {
				DatePickerDialog dlg = new DatePickerDialog(context, odsl, newspaper.getYear(), newspaper.getMonth()-1, newspaper.getDay());
				dlg.show();
			}

		};
		
		this.textview_paper_date.setOnClickListener(ocl);
		
		this.textview_paper_title.setOnClickListener(ocl);
		
	}



	/**
	 * @param newspaper the newspaper to set
	 */
	public void setNewspaper(NewsPaper newspaper) {
		
		this.newspaper = newspaper;
		
		if(calendar == null)
		{
			calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
			if(calendar.get(Calendar.HOUR_OF_DAY) <=6)
			{
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			}	
		}
		
		//calendar.set(Calendar.DATE, 12);
		
		
		reader.setPaper(newspaper);
		
        this.hideProgress();
        
        if(!(newspaper.getDay() == calendar.get(Calendar.DAY_OF_MONTH) && 
				newspaper.getMonth() == calendar.get(Calendar.MONTH)+1 &&
				newspaper.getYear() == calendar.get(Calendar.YEAR)))
		{	
        	NewsPaperAdapter adapter;
        	if(listview_paper.getExpandableListAdapter() == null)
        	{
        		adapter = new NewsPaperAdapter(context, newspaper);
        		listview_paper.setAdapter(adapter);
        	}
        	else
        	{
        		adapter = (NewsPaperAdapter) listview_paper.getExpandableListAdapter();
        	}
        	showInProgress();
			reader.readPaper(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), this);
			

		}

        
		this.textview_paper_title.setText(newspaper.paperName());
		this.textview_paper_date.setText(String.format("%d-%d-%d", newspaper.getYear(), newspaper.getMonth(), newspaper.getDay()));
		
		
		
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

		Section s = paper.readSectionAt(groupPosition);
		if(s.isEmpty())
		{
			this.textview_progress.setText(R.string.inprogress);
			this.showInProgress();

			reader.readArticle(groupPosition, s.link, new NewsPaperReader.OnArticleListener(){

				
				public void onDone() {
					adapter.notifyDataSetChanged();
					
						PaperView.this.hideProgress();
				}

				
				public void onException(String t) {
					//article reading
					Log.d("PaperView:", paper.getClass().getSimpleName()+":"+t);
					adapter.notifyDataSetChanged();
					PaperView.this.showReadPaperTimeout();
				
				}

				
				public void onStatus(String s) {
					
					adapter.notifyDataSetChanged();
				}
				
			});
		}
		
	}

	void showInProgress()
	{
		progress.setVisibility(VISIBLE);
		this.textview_empty.setVisibility(GONE);
		this.textview_timeout.setVisibility(GONE);
		this.textview_progress.setVisibility(VISIBLE);
	}
	void showEmptyPaper()
	{
		progress.setVisibility(VISIBLE);
		this.textview_empty.setVisibility(VISIBLE);
		this.textview_timeout.setVisibility(GONE);
		this.textview_progress.setVisibility(GONE);
	}
	
	void showReadPaperTimeout()
	{
		progress.setVisibility(VISIBLE);
		this.textview_empty.setVisibility(GONE);
		this.textview_timeout.setVisibility(VISIBLE);
		this.textview_progress.setVisibility(GONE);
	}
	
	void hideProgress()
	{
		progress.setVisibility(GONE);
	}

	public void onDone() {
		BaseExpandableListAdapter adapter = 
			(BaseExpandableListAdapter)listview_paper.getExpandableListAdapter();
		adapter.notifyDataSetChanged();
		if(newspaper.isEmpty())
			showEmptyPaper();
		else 
			hideProgress();
	}



	public void onException(String t) {
		if(t.startsWith("EMPTY"))
		{
			showEmptyPaper();
		}
		if(t.toLowerCase().contains("org.apache"))
		{
			this.showReadPaperTimeout();
		}
	}


	public void refreshPaper()
	{	
		newspaper.setYear(0);
		showInProgress();
		reader.readPaper(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), this);

	}
	//
	public void onClick(View v) {
		if(this.textview_progress.getVisibility() == GONE)
		{
			refreshPaper();
		}
	}


	
}
