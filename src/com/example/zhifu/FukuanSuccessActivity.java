package com.example.zhifu;

import com.example.patientclient01.DingDanActivity;
import com.example.patientclient01.DingDanGuanLiActivity;
import com.example.patientclient01.R;
import com.example.patientclient01.SysApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FukuanSuccessActivity extends Activity{
	private Button wdddButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this); 
		setContentView(R.layout.activity_fukuan_success);
		myYuyue();
	}
	public void myYuyue(){
		wdddButton = (Button)findViewById(R.id.wddd_btn);
		wdddButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FukuanSuccessActivity.this, DingDanGuanLiActivity.class);
				startActivity(intent);
			}
		});
	}
}
