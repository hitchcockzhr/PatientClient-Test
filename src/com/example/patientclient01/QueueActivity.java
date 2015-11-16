package com.example.patientclient01;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.patientclient01.*;

//排队信息
public class QueueActivity extends Activity {
	String medicalRecordId;
	//总人数、已完成人数、需等待人数
	TextView total_tv, done_tv, wait_tv;
	Button enterBtn, backBtn;
	String http, httpUrl;
	//获取排队队列信息
	String getQueueUrl = "shlc/patient/queue/";
	//获取当前订单信息
	String getStatusUrl = "shlc/patient/medicalRecord/";
	//进入诊室
	String jinruUrlString = "shlc/patient/enter/medicalRecord/";
	MyApp myApp;
	String TAG = "QueueActivity";
	static String status;
	Handler mHandler;
	Dialog dialog;
	Button dialogGetInButton;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_queue);
		initData();
		new QueueThread().start();
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						//Toast.makeText(getActivity(), "患者"+patient_name+"已进入诊室!",
						//		Toast.LENGTH_LONG).show();
						getInDialog();
						dialog.show();
						break;
					case 2:

						break;

				}
				super.handleMessage(msg);
			}
		};
	}
	void initData(){
		total_tv = (TextView) findViewById(R.id.total_tv);
		done_tv = (TextView) findViewById(R.id.done_tv);
		wait_tv = (TextView)findViewById(R.id.wait_tv);
		enterBtn = (Button)findViewById(R.id.enter_btn);
		//backBtn = (Button)findViewById(R.id.back_btn);
		Intent intent = getIntent();
		medicalRecordId = intent.getStringExtra("medicalRecordId");
		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		getQueue(medicalRecordId);
		myApp.setMedicalRecordId(medicalRecordId);
		enterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getInProgress(medicalRecordId);
			}
		});

	}
	void getQueue(String medicalRecordId){
		httpUrl = http+getQueueUrl+medicalRecordId;
		Log.v(TAG, "getQueueUrl:"+httpUrl);
		try {
			JSONObject joGet = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG, "joGet:"+joGet.toString());
			JSONObject joValue = joGet.getJSONObject("value");
			total_tv.setText(String.valueOf(joValue.getInt("total")));
			wait_tv.setText(String.valueOf(joValue.getInt("order")));
			done_tv.setText(String.valueOf(joValue.getInt("done")));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void getStatus(String medicalRecordId){
		httpUrl = http+getStatusUrl+medicalRecordId;
		Log.v(TAG, "getStatusUrl:"+httpUrl);

		JSONObject joGet = new JSONObject();
		try {
			joGet = HttpUtil.getResultForHttpGet(httpUrl);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v(TAG, "joGet:"+joGet.toString());
		try {
			status = joGet.getJSONObject("value").getString("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void getInProgress(String medicalRecordId){
		httpUrl = http + jinruUrlString + medicalRecordId;
		Log.v(TAG, "httpUrl:"+httpUrl);
		HttpPost post = HttpUtil.getPost(httpUrl, null);
		JSONObject joRev = HttpUtil.getString(post,3);
		// = HttpUtil.getResultForHttpGet(httpUrl);

		Log.v(TAG, "jinru:"+joRev.toString());
		try {
			if(joRev.getString("result").equals("200")){
				Log.v(TAG, "进入诊室！");
				Intent intent = new Intent();
				intent.setClass(QueueActivity.this, ChatActivity.class);
				this.startActivity(intent);
				this.finish();
			}else{
				Toast.makeText(getApplicationContext(), "尚未到诊，请稍后", Toast.LENGTH_LONG);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void getInDialog(){
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.get_in_dialog);
		dialogGetInButton = (Button)dialog.findViewById(R.id.get_in_btn);
		dialogGetInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getInProgress(medicalRecordId);
				dialog.dismiss();
			}
		});

	}
	class QueueThread extends Thread{
		public void run() {
			while(true){
				//刷新间隔1*5s

				try {
					int i = 1000*5;
					Thread.sleep(i);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				getStatus(medicalRecordId);
				if(status.equals("WAITING_FOR_PATIENT")){
					Log.v(TAG, "status:"+status);
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			}
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
