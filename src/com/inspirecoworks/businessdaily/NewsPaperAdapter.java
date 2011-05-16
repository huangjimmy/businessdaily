package com.inspirecoworks.businessdaily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.NewsPaper;

public class NewsPaperAdapter extends BaseExpandableListAdapter {

	
	public NewsPaperAdapter(Context context, NewsPaper paper) {
		this.paper = paper;
		this.context = context;
	}
	
	Context context;
	NewsPaper paper;
	public void setNewsPaper(NewsPaper paper)
	{
		this.paper = paper;
	}

	
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView == null?inflater.inflate(R.layout.article_item, parent, false):convertView;
		TextView title = (TextView) v.findViewById(R.id.article_title);
		TextView from = (TextView) v.findViewById(R.id.article_from);
		TextView preview = (TextView) v.findViewById(R.id.article_preview);

		Article a = (Article) this.getChild(groupPosition, childPosition);
		title.setText(a.getTitle());
		from.setText(a.getFrom());
		preview.setText(a.getPreview());
		
		return v;
	}

	
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView == null?inflater.inflate(R.layout.page_item, parent, false):convertView;
		TextView page_name = (TextView) v.findViewById(R.id.page_name);
		page_name.setText(paper.readSectionAt(groupPosition).title);
		return v;
	}


	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return paper.readSectionAt(groupPosition).values().toArray()[childPosition];
	}


	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}


	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return paper.readSectionAt(groupPosition).values().size();
	}


	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return paper.readSectionAt(groupPosition);
	}


	public int getGroupCount() {
		// TODO Auto-generated method stub
		return paper.getSections().size();
	}


	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}


	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	


}
