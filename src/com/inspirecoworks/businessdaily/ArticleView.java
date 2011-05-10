package com.inspirecoworks.businessdaily;

import com.inspirecoworks.common.business.Article;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TableLayout;

public class ArticleView extends TableLayout {

	public ArticleView(Context context) {
		super(context);
	}

	public ArticleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		content = (WebView) findViewById(R.id.webview_reader);
		super.onFinishInflate();
	}

	private WebView content;
	//private View container;
	
	//article object
	private Article article;
	

	/**
	 * @param article the article to set
	 */
	public void setArticle(Article article) {
		this.article = article;
		content.loadDataWithBaseURL(null,
	        	String.format("<H1>%s</H1><P>%s</P><BR>%s", article.getTitle(), article.getFrom(), article.getBody())
	        	, "text/html", "utf-8", null);
	}

	/**
	 * @return the article
	 */
	public Article getArticle() {
		return article;
	}
	
	public void share(Activity act)
	{
		Intent intent;
		intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
		intent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + " " + article.getLink());
		act.startActivity(Intent.createChooser(intent, "分享"));
	}
	
	public void send(Activity act)
	{
		Intent intent;
		intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
		intent.putExtra(Intent.EXTRA_TEXT, article.getLink() + "\r\n" + article.getBody().replace("&nbsp;", " ")
				.replaceAll("<[P|p]>", "\r\n\r\n").replaceAll("</[P|p]>|", "").replaceAll("(<BR[\\s]*/*>)|(<br[\\s]*/*>)", "\r\n")
				.replaceAll("<[^<>]+>", "").trim()
				);
		act.startActivity(Intent.createChooser(intent, "分享"));
	}

	public void favorite()
	{
		
	}
}
