package com.example.patientclient01;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class YiShengJianJieActivity extends Activity{
	MyApp myApp;
	JSONObject joDoctor;
	TextView ysjjTextView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this); 
		setContentView(R.layout.activity_ysjj);
		myApp = (MyApp)getApplication();
		joDoctor = myApp.getJoDoctor();
		ysjjTextView = (TextView)findViewById(R.id.ysjj_detail_tv);
		try {
			ysjjTextView.setText(joDoctor.getString("description"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
