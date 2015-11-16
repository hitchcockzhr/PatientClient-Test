package com.example.personalcenter;

import android.app.Activity;
import android.os.Bundle;

import com.example.patientclient01.R;
import com.example.patientclient01.SysApplication;

public class PersonalCenterActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this); 
		setContentView(R.layout.activity_main);
	}
}
