package com.inspirecoworks.businessdaily;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.Cbn;
import com.inspirecoworks.common.business.Cnstock;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.Section;
import com.inspirecoworks.common.business.Stcn;
import com.inspirecoworks.common.business.NewsPaper.OnArticleListener;


public class PaperAdapter extends BaseAdapter {

	Activity home;
	View[] inprogress = new View[3];
	String[] paper_names = new String[]{"上海证券报","证券时报", "第一财经"};
	Calendar[] paper_dates = new Calendar[]{Calendar.getInstance(), Calendar.getInstance(),
			Calendar.getInstance()};
	NewsPaper[] papers = new NewsPaper[]{new Cnstock(), new Stcn(), new Cbn()};
	
	public NewsPaper[] getPapers() {
		return papers;
	}

	public void setPapers(NewsPaper[] papers) {
		this.papers = papers;
	}

	View[] paper_views = new View[3];
	public PaperAdapter(Activity home) {
		this.home = home;
		for(int i=0;i<3;i++){
			inprogress[i] = home.getLayoutInflater().inflate(R.layout.progress, null);
			papers[i].setContext(home);
		}
		
		SharedPreferences sp = home.getSharedPreferences("BusinessDaily", 0);
		if(sp.getString("cnstock_username", "").length() == 0)
		{
			Intent intent = new Intent(home, SettingAct.class);
			home.startActivity(intent);
		}
        Cnstock.setUsername(sp.getString("cnstock_username", ""));
        Cnstock.setPassword(sp.getString("cnstock_password", ""));

		
		for(int i=0;i<3;i++)
		if(paper_dates[i].get(Calendar.HOUR_OF_DAY)<7)
    	{
    		paper_dates[i].add(Calendar.DAY_OF_MONTH, -1);
    	}
		//home.addContentView(inprogress, new LayoutParams(500, 100));
	}

	public int getCount() {
        return paper_names.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	if(convertView != null)return convertView;
    	//position = 1;
    	if(paper_views[position] != null)return paper_views[position];
    	else paper_views[position] = home.getLayoutInflater().inflate(R.layout.paper_view, null);
    	
    	View v = paper_views[position];
    	final ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.page_list);
    	final View progress = v.findViewById(R.id.paper_progress);
    	elv.setTag(papers[position]);//将报纸对象存放在Tag中。
    	TextView paper_title = (TextView) v.findViewById(R.id.paper_title);
    	TextView paper_date = (TextView) v.findViewById(R.id.paper_date);
    	paper_title.setText(paper_names[position]);
    	paper_date.setText(DateFormat.format("yyyy-MM-dd", paper_dates[position]));    	
    	
    	
    	final NewsPaper paper = papers[position];
    	final View v1 = v;
    	NewsPaper.OnPaperListener listener = new NewsPaper.OnPaperListener()
    	{

			public void onDone() {
				Log.i(paper.getClass().getSimpleName(), "Show newspaper");
				elv.setAdapter(paper.getAdapter(home));
				progress.setVisibility(View.GONE);
			}

			@Override
			public void onException(String t) {
				TextView paper_date = (TextView) v1.findViewById(R.id.paper_date);
				paper_date.setText("发生读取错误:"+t);
			}
    		
    	};
    	
    	elv.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int group, int child, long id) {

				final Article article = (Article) elv.getExpandableListAdapter().getChild(group, child);
		        final BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) elv.getExpandableListAdapter();
		        if(article.getBody() == null || article.getBody().trim().length() == 0)
				{
		        	NewsPaper paper = (NewsPaper) elv.getTag();
		        	paper.readArticle(group, article.getTitle(), new OnArticleListener(){

						@Override
						public void onDone() {
							if(article.getBody()!=null && article.getBody().length()>5)
							{
								adapter.notifyDataSetChanged();
							}
							else
							{
								Log.i("READ", article.getTitle()+": ERROR content incorrect");
								onException("读取数据错误");
							}
						}

						@Override
						public void onException(String t) {
							//article reading
							article.setFrom(t);
							adapter.notifyDataSetChanged();
							Log.i("READ", article.getTitle()+": "+t);
							//retry in case of exception after five seconds
							//paper.readArticle(group, a.getTitle(), this);
						}

						@Override
						public void onStatus(String s) {
							article.setFrom(s);
							adapter.notifyDataSetChanged();
						}
						
					});
					return false;
				}
		        Intent intent = new Intent(home, Reader.class);
		        intent.putExtra("article", article);
		        home.startActivity(intent);
		        elv.focusableViewAvailable(v);
				return false;
			}
    		
    	});
    	
    	elv.setOnGroupExpandListener(new OnGroupExpandListener(){

			@Override
			public void onGroupExpand(int groupPosition) {
				final BaseExpandableListAdapter adapter = 
					(BaseExpandableListAdapter)elv.getExpandableListAdapter();
				final NewsPaper paper = (NewsPaper) elv.getTag();
				Section s = paper.readSectionAt(groupPosition);
				
				final int group = groupPosition;
				
				for(final Article a:s.values())
				paper.readArticle(groupPosition, a.getTitle(), new OnArticleListener(){

					@Override
					public void onDone() {
						if(a.getBody()!=null && a.getBody().length()>5)
						{
							adapter.notifyDataSetChanged();
						}
						else
						{
							Log.i("READ", a.getTitle()+": ERROR content incorrect");
							onException("读取数据错误");
						}
					}

					@Override
					public void onException(String t) {
						//article reading
						a.setFrom(t);
						adapter.notifyDataSetChanged();
						Log.i("READ", a.getTitle()+": "+t);
						//retry in case of exception after five seconds
						//paper.readArticle(group, a.getTitle(), this);
					}

					@Override
					public void onStatus(String s) {
						a.setFrom(s);
						adapter.notifyDataSetChanged();
					}
					
				});
				
			}
    		
    	});
    	
    	int year, month, day;
    	year = paper_dates[position].get(Calendar.YEAR);
    	month = paper_dates[position].get(Calendar.MONTH)+1;
    	day = paper_dates[position].get(Calendar.DATE);
    	papers[position].readPaper(year, month, day, listener);
    	
    	
        return v;
    }


}

