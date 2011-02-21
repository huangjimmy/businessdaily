package com.inspirecoworks.businessdaily;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.inspirecoworks.common.business.Article;
import com.inspirecoworks.common.business.NewsPaper;
import com.inspirecoworks.common.business.NewsPaper.OnArticleListener;

public class NewsPaperAdapter extends SimpleExpandableListAdapter {

	public NewsPaperAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
	}
	
	NewsPaper paper;
	public void setNewsPaper(NewsPaper paper)
	{
		this.paper = paper;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v = super.getChildView(groupPosition, childPosition, isLastChild,
				convertView, parent);
		final TextView from = (TextView) v.findViewById(R.id.article_from);
		final TextView preview = (TextView) v.findViewById(R.id.article_preview);
		preview.setClickable(false);
		from.setClickable(false);
		
		try{
		String text = from.getText().toString();
		if(text.length() == 0)from.setText("正在载入...");
		}catch(Exception ex)
		{
			from.setText("正在载入...");
		}
		final Article a = (Article) this.getChild(groupPosition, childPosition);
		if(a.getBody()!=null && a.getBody().length()>5)
		{
			from.setText(a.getFrom().replaceAll("<[^<>]+>", "").replace("&nbsp;", " ").replace("&gt;", ">").trim());
			preview.setText(a.getPreview());
		}
		
		paper.readArticle(groupPosition, a.getTitle(), new OnArticleListener(){

			@Override
			public void onDone() {
				if(a.getBody()!=null && a.getBody().length()>5)
				{
					NewsPaperAdapter.this.notifyDataSetChanged();
				}
			}

			@Override
			public void onException(String t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatus(String s) {
				a.setFrom(s);
				NewsPaperAdapter.this.notifyDataSetChanged();
			}
			
		});
		
		return v;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		return super.getGroupView(groupPosition, isExpanded, convertView, parent);
	}

	


}
