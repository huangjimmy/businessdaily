package com.inspirecoworks.businessdaily;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.inspirecoworks.common.business.Cbn;
import com.inspirecoworks.common.business.Cnstock;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.Stcn;


public class PaperAdapter extends BaseAdapter {

	Activity home;
	
	String[] paper_names = new String[]{"上海证券报","证券时报", "第一财经"};
	
	NewsPaper[] papers = new NewsPaper[]{new Cnstock(), new Stcn(), new Cbn()};
	
	public NewsPaper[] getPapers() {
		return papers;
	}

	public void setPapers(NewsPaper[] papers) {
		this.papers = papers;
	}

	PaperView[] paper_views = new PaperView[3];
	public PaperAdapter(Activity home) {
		this.home = home;

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
    	else paper_views[position] = (PaperView) home.getLayoutInflater().inflate(R.layout.paper_view, null);
    	
    	paper_views[position].setNewspaper(papers[position]);
    	
        return paper_views[position];
    }


}

