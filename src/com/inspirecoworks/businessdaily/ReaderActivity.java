package com.inspirecoworks.businessdaily;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inspirecoworks.base.ICBaseActivity;
import com.inspirecoworks.common.business.Article;

public class ReaderActivity extends ICBaseActivity {
	
	private ArticleView view;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);
        
        Intent intent = this.getIntent();
    	Bundle bundle = intent.getExtras();
        
        try{
        	
        	Article article = new Article();
        	article.putAll((HashMap<String, Object>) bundle.get("article"));
        	view = (ArticleView) findViewById(R.id.reader_view);
        	view.setArticle(article);
        	
        	Button btn_share = (Button) view.findViewById(R.id.reader_button_share);
        	btn_share.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					view.share(ReaderActivity.this);
					
				}
			});
        	Button btn_fav = (Button) view.findViewById(R.id.reader_button_fav);
        	btn_fav.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					view.send(ReaderActivity.this);
					
				}
			});
        	
        	
        }
        catch(Exception e)
        {
        	Toast.makeText(this, e.getLocalizedMessage()+":"+bundle.get("article").getClass().getName(), Toast.LENGTH_LONG).show();
        }
        
    }
    
    
}
