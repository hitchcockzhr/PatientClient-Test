package com.example.patientclient01;

import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.zhifu.FukuanSuccessActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//下订单之详情页
public class DingDanXiangQingActivity extends Activity{
	String TAG = "DingDanXiangQingActivity";
	String joRevString;
	String id ;
	JSONObject joRev, joValue;
	Button fukuanButton, cancelButton, okButton;
	TextView hospitalTextView, keshiTextView, zhichengTextView, nameTextView,zhuanchangTextView;
	TextView dateTextView, weekTextView, timeTextView;
	//病情描述
	EditText descriptionEditText;
	String http, httpUrl, showStr, resultStr ;
	//付款
	String fukuanUrlString = "shlc/patient/payment/medicalRecord/";
	//上传描述
	String descriptionUrlString = "shlc/patient/medicalRecord/description/";
	//取消订单
	String cancelUrlString = "shlc/patient/cancel/medicalRecord/";
	MyApp myApp;
	String scheId;
	//存放已选择医生信息和时间信息
	HashMap<String, String> selectedMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_ddxq);
		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		Intent intent = getIntent();
		joRevString = intent.getStringExtra("yuyue");
		//scheId = intent.getStringExtra("scheId");
		selectedMap = (HashMap<String, String> )intent.getSerializableExtra("selectedMap");
		try {
			joRev = new JSONObject(joRevString);
			joValue = joRev.getJSONObject("value");
			Log.v(TAG, "intent joValue:"+joValue.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hospitalTextView = (TextView) findViewById(R.id.hospital_tv);
		keshiTextView = (TextView)findViewById(R.id.keshi_tv);
		zhichengTextView = (TextView)findViewById(R.id.zhicheng_tv);
		nameTextView = (TextView)findViewById(R.id.name_tv);
		zhuanchangTextView = (TextView)findViewById(R.id.zhuanchang_tv);
		dateTextView = (TextView)findViewById(R.id.date_tv);
		weekTextView =(TextView)findViewById(R.id.week_tv);
		timeTextView = (TextView)findViewById(R.id.time_tv);

		hospitalTextView.setText(selectedMap.get("hospital"));
		keshiTextView.setText(selectedMap.get("keshi"));
		zhichengTextView.setText(selectedMap.get("zhicheng"));
		zhuanchangTextView.setText(selectedMap.get("zhuanchang"));
		nameTextView.setText(selectedMap.get("name"));
		dateTextView.setText(selectedMap.get("date"));
		weekTextView.setText(selectedMap.get("week"));
		timeTextView.setText(selectedMap.get("time"));



		descriptionEditText = (EditText)findViewById(R.id.description_et);

		okButton = (Button)findViewById(R.id.ok_btn);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					id = String.valueOf(joValue.getLong("id"));
					httpUrl = http + descriptionUrlString;
					Log.v(TAG,httpUrl);
					JSONObject joSend = new JSONObject();
					joSend.put("id", Integer.parseInt(id));
					joSend.put("description", descriptionEditText.getText().toString());
					HttpPut put = HttpUtil.getPut(httpUrl, joSend);

					String revString = HttpUtil.getPutString(put);
					Log.v(TAG,"description Rev:"+revString);
					JSONObject joRev = new JSONObject(revString);
					if(joRev.getString("result").equals("200")){
						Intent intent = new Intent(DingDanXiangQingActivity.this, DingDanGuanLiActivity.class);
						startActivity(intent);
						DingDanXiangQingActivity.this.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		fukuanButton = (Button)findViewById(R.id.fukuan_btn);
		fukuanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//病情描述
				try {
					id = String.valueOf(joValue.getLong("id"));
					httpUrl = http + descriptionUrlString;
					Log.v(TAG,httpUrl);
					JSONObject joSend = new JSONObject();
					joSend.put("id", Integer.parseInt(id));
					joSend.put("description", descriptionEditText.getText().toString());
					HttpPut put = HttpUtil.getPut(httpUrl, joSend);

					String revString = HttpUtil.getPutString(put);
					Log.v(TAG,"description Rev:"+revString);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//付款
				try {
					id = String.valueOf(joValue.getLong("id"));
					httpUrl = http + fukuanUrlString + id;
					Log.v(httpUrl,httpUrl);
					HttpPost post = HttpUtil.getPost(httpUrl, null);
					joRev = HttpUtil.getString(post, 3);
					Log.v(TAG,"joRev:"+joRev.toString());
					Intent intent = new Intent(DingDanXiangQingActivity.this, FukuanSuccessActivity.class);
					startActivity(intent);
					DingDanXiangQingActivity.this.finish();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		cancelButton = (Button)findViewById(R.id.cancel_btn);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					id = String.valueOf(joValue.getLong("id"));
					httpUrl = http + cancelUrlString+id;
					HttpPost post = HttpUtil.getPost(httpUrl, null);
					joRev = HttpUtil.getString(post, 3);
					Log.v(TAG,"joRev:"+joRev.toString());
					Intent intent = new Intent(DingDanXiangQingActivity.this, MainActivity.class);
					startActivity(intent);
					DingDanXiangQingActivity.this.finish();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
