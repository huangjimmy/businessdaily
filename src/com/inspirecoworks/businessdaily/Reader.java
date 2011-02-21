package com.inspirecoworks.businessdaily;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.inspirecoworks.base.ICBaseActivity;
import com.inspirecoworks.common.business.Article;

public class Reader extends ICBaseActivity {
	private HashMap<String, String> article;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.article);
        
        
        Intent intent = this.getIntent();
    	Bundle bundle = intent.getExtras();
        
        try{
        	TextView article_view = (TextView) this.findViewById(R.id.body);
            //TextView article_title = (TextView) this.findViewById(R.id.title); 
            TextView article_from = (TextView) this.findViewById(R.id.from); 
        	
        	article = (HashMap<String, String>) bundle.get("article");
        	this.setTitle(article.get(Article.TITLE));
        	
	        //article_title.setText(article.get(Article.TITLE));
	        article_from.setText(article.get(Article.FROM).replaceAll("<[/]*[P|p]>", "\r\n").replaceAll("<[B|b][R|r][/]*>", "\r\n").replaceAll("<[^<>]+>", "").replace("&nbsp;", " ").replace("&gt;", ">").trim());
	        
	        article_view.setText(article.get(Article.BODY).replaceAll("<[/]*[P|p]>", "\r\n").replaceAll("<[B|b][R|r][/]*>", "\r\n").replaceAll("<[^<>]+>", "").replace("&nbsp;", " ").replace("&gt;", ">").trim());
	        
	        
	        //用下面这一行代码会乱码的
	        //article_view.loadData(content, "text/html", "utf-8");
        }
        catch(Exception e)
        {
        	Toast.makeText(this, e.getLocalizedMessage()+":"+bundle.get("article").getClass().getName(), Toast.LENGTH_LONG).show();
        }
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId())
		{
		case R.id.send:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/html");
			intent.putExtra(Intent.EXTRA_SUBJECT, article.get(Article.TITLE));
			intent.putExtra(Intent.EXTRA_TEXT, article.get(Article.LINK) + "\r\n" + article.get(Article.BODY).replace("&nbsp;", " ")
					.replaceAll("<[P|p]>", "\r\n\r\n").replaceAll("</[P|p]>|", "").replaceAll("(<BR[\\s]*/*>)|(<br[\\s]*/*>)", "\r\n")
					.replaceAll("<[^<>]+>", "").trim()
					);
			startActivity(Intent.createChooser(intent, "分享"));
			break;
		case R.id.share:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, article.get(Article.TITLE));
			intent.putExtra(Intent.EXTRA_TEXT, article.get(Article.TITLE) + " " + article.get(Article.LINK));
			startActivity(Intent.createChooser(intent, "分享"));
			break;
		}
		return super.onOptionsItemSelected(item);
	}	
}
