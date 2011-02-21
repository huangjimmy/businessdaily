package com.inspirecoworks.base;

import android.app.Activity;
import android.os.Bundle;

import com.mobclick.android.MobclickAgent;

public class ICBaseActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		}
	
}
