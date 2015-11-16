package com.example.patientclient01;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class YsxzActivity extends Activity{
	String TAG = "YsxzActivity";
	String http, httpUrl, showStr, resultStr ;
	String queryDoctor = "shlc/patient/doctors/departmentType/";
	String statusString = "?active=true";
	JSONObject joRev;
	MyApp myApp;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_ksjy_list);
		myApp = (MyApp)getApplication();
		initData();
	}
	public void initData(){
		Intent intent = getIntent();
		int id = intent.getIntExtra("departmentID", 0);
		Log.v(TAG, "id:"+String.valueOf(id));
		http = myApp.getHttp();
		httpUrl = http + queryDoctor + String.valueOf(id)+statusString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG, "joRev:"+joRev.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/*
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_HOME){
        	Toast.makeText(this, "请使用退出按钮退出程序！Home键已禁止！",Toast.LENGTH_LONG).show();
        	return false;
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
        	Toast.makeText(this, "请使用按钮操作程序！返回键已禁止！", Toast.LENGTH_LONG).show();
        	return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    */
}
