package com.example.patientclient01;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RegisterActivity extends Activity {
	EditText nameEditText, psdEditText, psdAgainEditText, xingmingEditText, idEditText,
				mailEditText, phoneEditText, addressEditText;
	Spinner sexSpinner, yearSpinner, monthSpinner, daySpinner;
	Button registerButton;
	JSONObject params = new JSONObject();
	String sexStr, dayStr, monthStr, yearStr, birthdayStr;
	private String TAG = "RegisterActivity";
	static String http ;//="http://192.168.1.54:8080/";
	static String regUrl = "shlc/patient/user";
	static String httpUrl;
	private String showStr = "";
	private String resultStr = "";
	private MyApp myApp;
	ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this); 
		setContentView(R.layout.activity_register);
		myApp = (MyApp)getApplication();
        http = myApp.getHttp();
		LinearLayout rLayout = (LinearLayout)findViewById(R.id.register_layout);
		rLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))   
	            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
	            		InputMethodManager.HIDE_NOT_ALWAYS);   
			}
		});
		
		nameEditText = (EditText)findViewById(R.id.name_et);
		psdEditText = (EditText)findViewById(R.id.psd_et);
		psdAgainEditText = (EditText)findViewById(R.id.psd_again_et);
		xingmingEditText = (EditText)findViewById(R.id.xingming_et);
		idEditText = (EditText)findViewById(R.id.id_et);
		mailEditText = (EditText)findViewById(R.id.mail_et);
		phoneEditText = (EditText)findViewById(R.id.phone_et);
		addressEditText = (EditText)findViewById(R.id.address_et);
		sexSpinner = (Spinner)findViewById(R.id.sex_spinner);
		yearSpinner = (Spinner)findViewById(R.id.year_spinner);
		monthSpinner = (Spinner)findViewById(R.id.month_spinner);
		daySpinner = (Spinner)findViewById(R.id.day_spinner);
		registerButton = (Button)findViewById(R.id.ok_btn);
		
		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>
        (RegisterActivity.this, android.R.layout.simple_spinner_item,getYearData());
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>
        (RegisterActivity.this, android.R.layout.simple_spinner_item,getMonthData());
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>
        (RegisterActivity.this, android.R.layout.simple_spinner_item,getDayData());
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>
        (RegisterActivity.this, android.R.layout.simple_spinner_item,getSexData());
        
        sexSpinner.setAdapter(sexAdapter);
        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);
        daySpinner.setAdapter(dayAdapter);
        setSpinner();
        registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pDialog = new ProgressDialog(RegisterActivity.this);
				pDialog.setTitle("注册中");
				pDialog.setMessage("请稍等......");
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();
				new RegisterThread().run();
			}
		});
	}
	
	class RegisterThread extends Thread{
		@SuppressLint("NewApi")
		public void run() {
			if (android.os.Build.VERSION.SDK_INT > 9) {
			    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			    StrictMode.setThreadPolicy(policy);
			}
			birthdayStr = yearStr+"-"+monthStr+"-"+dayStr;
			try {
				params.put("userId", nameEditText.getText().toString());
				params.put("password", psdEditText.getText().toString());
				params.put("name", xingmingEditText.getText().toString());
				params.put("sex", sexStr);
				params.put("birthday", birthdayStr);
				params.put("identity", idEditText.getText().toString());
				params.put("email", mailEditText.getText().toString());
				params.put("phone", phoneEditText.getText().toString());
				params.put("address", addressEditText.getText().toString());
				
				Log.d(TAG, "httpUrl:"+http+regUrl);
				Log.d(TAG, "params:"+params.toString());
				
				httpUrl = http + regUrl;
				HttpPost post = HttpUtil.getPost(httpUrl, params);
				JSONObject joRev = HttpUtil.getString(post, 3);
				showStr = joRev.getString("result");
				resultStr = joRev.getString("resultMessage");
				pDialog.dismiss();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (showStr.equals("200")) {
				Log.d(TAG,  "resultStr:"+resultStr);
				Toast.makeText(RegisterActivity.this, "注册成功！",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
			}
		}
	}
	
	public void setSpinner() {
		yearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				yearStr = parent.getItemAtPosition(position).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
	    });
	    monthSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				monthStr = parent.getItemAtPosition(position).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
	    });
	    daySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				dayStr = parent.getItemAtPosition(position).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
	    });
	    sexSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				sexStr = parent.getItemAtPosition(position).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub	
			}
	    });
	}
	
	
	private List<String> getYearData(){
		List<String> dataList = new ArrayList<String>();
		for(int i=1940; i<2015; i++){
			dataList.add(String.valueOf(i));
		}
		return dataList;	
	}
	private List<String> getMonthData(){
		List<String> dataList = new ArrayList<String>();
		for(int i=1; i<13; i++){
			dataList.add(String.valueOf(i));
		}
		return dataList;	
	}
	private List<String> getDayData(){
		List<String> dataList = new ArrayList<String>();
		for(int i=1; i<32; i++){
			dataList.add(String.valueOf(i));
		}
		return dataList;	
	}
	private List<String> getSexData(){
		List<String> dataList = new ArrayList<String>();
		
		dataList.add("男");
		dataList.add("女");
		
		return dataList;	
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
