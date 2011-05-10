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
    	
    	final PaperView paper_view = (PaperView) v.findViewById(R.id.paper_view);
    	
    	
    	
    	final NewsPaper paper = papers[position];

    	NewsPaper.OnPaperListener listener = new NewsPaper.OnPaperListener()
    	{

			public void onDone() {
				Log.i(paper.getClass().getSimpleName(), "Show newspaper");
	
				paper_view.setNewspaper(paper);
			}

	
			public void onException(String t) {
				//TextView paper_date = (TextView) v1.findViewById(R.id.paper_date);
				//paper_date.setText("发生异常:"+t);
			}
    		
    	};
    	
    	int year, month, day;
    	year = paper_dates[position].get(Calendar.YEAR);
    	month = paper_dates[position].get(Calendar.MONTH)+1;
    	day = paper_dates[position].get(Calendar.DATE);
    	paper_view.setNewspaper(paper);
    	papers[position].readPaper(year, month, day, listener);
    	
    	
        return v;
    }


}

