package com.inspirecoworks.businessdaily;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Gallery;

import com.inspirecoworks.base.ICBaseActivity;
import com.inspirecoworks.common.business.NewsPaper;
import com.mobclick.android.MobclickAgent;

public class Home extends ICBaseActivity{

	Gallery g;
	PaperAdapter pAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MobclickAgent.update(this);
        
        g = (Gallery) findViewById(R.id.paper_list);
        pAdapter = new PaperAdapter(this);
        try{
        	NewsPaper[] papers = (NewsPaper[]) this.getLastNonConfigurationInstance();
        	if(papers != null)
        	{
        		pAdapter.setPapers(papers);
        	}
        }
        catch(Exception e){}
        g.setAdapter(pAdapter);
        
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return pAdapter.getPapers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tabcontents_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.about:
			MobclickAgent.openFeedbackActivity(this);
			break;
		case R.id.refresh:
			View v = g.getSelectedView();
			if(v!=null)
			{
				((PaperView)v).refreshPaper();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}