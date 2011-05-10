package com.inspirecoworks.businessdaily;

import com.inspirecoworks.common.business.Cnstock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class SettingAct extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		final SharedPreferences sp = getSharedPreferences("BusinessDaily", 0);
		EditText username = (EditText) this.findViewById(R.id.username);
		username.setText(sp.getString("cnstock_username", ""));
		username.addTextChangedListener(new TextWatcher()
		{


			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Cnstock.setUsername(s+"");
				Editor editor = sp.edit();
				editor.putString("cnstock_username", s+"");
				editor.commit();
			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
		});
		EditText password = (EditText) this.findViewById(R.id.password);
		password.setText(sp.getString("cnstock_password", ""));
		password.addTextChangedListener(new TextWatcher()
		{

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Cnstock.setPassword(s+"");
				Editor editor = sp.edit();
				editor.putString("cnstock_password", s+"");
				editor.commit();
			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

}
