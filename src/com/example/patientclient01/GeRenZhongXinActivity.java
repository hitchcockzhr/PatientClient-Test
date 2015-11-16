package com.example.patientclient01;

import com.qrcode.QRMainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//个人中心
public class GeRenZhongXinActivity extends Activity{
	private Button chakanButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this); 
		setContentView(R.layout.activity_grzx);
		chakanButton = (Button)findViewById(R.id.chakan_btn);
		chakanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GeRenZhongXinActivity.this , QRMainActivity.class);
				startActivity(intent);
			}
		});
	}
}
