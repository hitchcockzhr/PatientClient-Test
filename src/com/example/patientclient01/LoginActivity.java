package com.example.patientclient01;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//登录
public class LoginActivity extends Activity {
	TextView registerTextView;
	MyApp myApp;
	String http, httpUrl;
	String loginUrl = "shlc/patient/login";
	String startUrl = "shlc/initData";
	EditText nameEditText, psdEditText;
	Button loginButton, upBtn;
	JSONObject params = new JSONObject();
	String TAG = "LoginActivity";
	String showString;
	SaveCache saveCache;
	private Handler mHandler;
	CheckBox rememberCheckBox;
	ProgressDialog pDialog;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_login);
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		//从服务器初始化医疗信息
		saveCache = new SaveCache(AppContext.get());
		getStart();

		RelativeLayout rLayout = (RelativeLayout)findViewById(R.id.login_layout);
		rLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		//上传error log
		upBtn = (Button)findViewById(R.id.up_btn);
		upBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendEmail();
			}
		});
		//打开注册页面
		registerTextView = (TextView)findViewById(R.id.register_tv);
		registerTextView.setClickable(true);
		registerTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
				startActivity(intent);
				//LoginActivity.this.finish();
			}
		});

		nameEditText = (EditText)findViewById(R.id.name_et);
		psdEditText = (EditText)findViewById(R.id.psd_et);
		rememberCheckBox = (CheckBox)findViewById(R.id.remember_psd_cb);
		//监听记住密码多选框按钮事件  
		rememberCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (rememberCheckBox.isChecked()) {

					System.out.println("记住密码已选中");
					sp.edit().putBoolean("ISCHECK", true).commit();

				}else {

					System.out.println("记住密码没有选中");
					sp.edit().putBoolean("ISCHECK", false).commit();

				}

			}
		});
		if(sp.getBoolean("ISCHECK", false))
		{
			//设置默认是记录密码状态  
			rememberCheckBox.setChecked(true);
			nameEditText.setText(sp.getString("USER_NAME", ""));
			psdEditText.setText(sp.getString("PASSWORD", ""));
			//判断自动登陆多选框状态

		}
		loginButton = (Button)findViewById(R.id.login_btn);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pDialog = new ProgressDialog(LoginActivity.this);
				pDialog.setTitle("登录中");
				pDialog.setMessage("请稍等......");
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();
				new LoginThread().run();
			}
		});
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 1){
					showToast("登录失败，请检查用户名和密码");
					return;
				}
//              mAdapter.notifyDataSetChanged();  
			}
		};
	}
	//注册线程
	class LoginThread extends Thread{
		public void run(){
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			try {
				Thread.sleep(1000);
				params.put("userId", nameEditText.getText().toString());
				params.put("password", psdEditText.getText().toString());
				httpUrl = http+loginUrl;
				HttpPost post = HttpUtil.getPost(httpUrl, params);
				JSONObject joRev = HttpUtil.getLoginString(post, 3);
				showString = joRev.getString("result");
				Log.v(TAG, "登录："+showString);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(showString.equals("200")){
				if(rememberCheckBox.isChecked())
				{
					//记住用户名、密码、
					Editor editor = sp.edit();
					editor.putString("USER_NAME", nameEditText.getText().toString());
					editor.putString("PASSWORD",psdEditText.getText().toString());
					editor.commit();
				}
				pDialog.cancel();
				Intent intent = new Intent(LoginActivity.this , MainActivity.class);
				startActivity(intent);
				//showToast("锟斤拷录锟缴癸拷锟斤拷");
				LoginActivity.this.finish();
			}else{
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);

				return;
			}
		}
	}
	public void showToast(String msg){
		//Looper.prepare();
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		// Looper.loop();
	}
	//初始化
	private void getStart(){
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		JSONObject joRev = new JSONObject();
		try {
			joRev = HttpUtil.getResultForHttpGet(http+startUrl);
			Log.v(TAG, "getStarted:"+joRev.toString());
			queryJson(joRev.getJSONObject("value"));
			//锟侥硷拷锟斤拷锟斤拷
			String fileName = saveCache.saveFileCachePath("JSONStart", http+startUrl);
			myApp.setJoStartPath(fileName);
			Log.v(TAG,"JoStartPath:"+myApp.getJoStartPath());
			saveCache.save(fileName, joRev.toString());
			Log.v(TAG, "fileName:"+fileName.toString());
			//String readFileCache = saveCache.read(fileName);
			//Log.v(TAG, "readFileCache:"+readFileCache);
			//JSONObject joReadFileCache = new JSONObject(readFileCache);
			//Log.v(TAG, joRev.toString());
			/*
			String result = joReadFileCache.getString("result");
			String resultMessage = joReadFileCache.getString("resultMessage");
			JSONObject joValue = joReadFileCache.getJSONObject("value");
			JSONArray jaProvinces = joValue.getJSONArray("provinces");
			JSONArray jaJobTitiles = joValue.getJSONArray("jobTitles");
			for(int i=0; i<jaProvinces.length(); i++){
				JSONObject joProvince = jaProvinces.getJSONObject(i);
				//Log.v(TAG, joProvince.toString());
				if(joProvince.get("name").equals("锟斤拷锟斤拷")){
					Log.v(TAG, "锟斤拷锟斤拷");
					
				}
			}
			//Log.v(TAG, result);
			//Log.v(TAG, resultMessage);
			//Log.v(TAG, joValue.toString());
			//Log.v(TAG, jaProvinces.toString());
			//Log.v(TAG, jaJobTitiles.toString());
			//锟斤拷锟斤拷Start锟斤拷锟截碉拷JSON//shlc/initData
			 */

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//解析初始化数据-解析json
	private void queryJson(JSONObject jsonObject){
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		try {
			JSONArray provincesJsonArray = jsonObject.getJSONArray("provinces");
			for (int i = 0; i < provincesJsonArray.length(); i++) {
				JSONObject provinceJsonObject = provincesJsonArray.getJSONObject(i);
				JSONObject jo = new JSONObject();
				jo.put("province_name", provinceJsonObject.getString("name"));
				jo.put("province_id", provinceJsonObject.getLong("id"));
				JSONArray citiesJsonArray = provinceJsonObject.getJSONArray("cities");
				for (int j = 0; j < citiesJsonArray.length(); j++) {
					JSONObject cityJsonObject = citiesJsonArray.getJSONObject(j);
					jo.put("city_name", cityJsonObject.getString("name"));
					jo.put("city_id", cityJsonObject.getLong("id"));
					JSONArray hospitalsJsonArray = cityJsonObject.getJSONArray("hospitals");
					for(int k = 0; k < hospitalsJsonArray.length(); k++){
						JSONObject hospitalJsonObject = hospitalsJsonArray.getJSONObject(k);
						jo.put("hospital_name", hospitalJsonObject.getString("name"));
						jo.put("hospital_id", hospitalJsonObject.getLong("id"));
						JSONArray departmentsJsonArray = hospitalJsonObject.getJSONArray("departments");
						for (int l = 0; l < departmentsJsonArray.length(); l++) {
							JSONObject departmentJsonObject = departmentsJsonArray.getJSONObject(l);
							JSONObject departmentTypeJsonObject = departmentJsonObject.getJSONObject("departmentType");
							jo.put("department_name", departmentJsonObject.getString("name"));
							jo.put("department_id", departmentJsonObject.getLong("id"));
							jo.put("departmentType_name", departmentTypeJsonObject.getString("name"));
							jo.put("departmentType_id", departmentTypeJsonObject.getLong("id"));
							//Log.v(TAG, "department_jo:"+jo.toString());
							JSONArray feesJsonArray = hospitalJsonObject.getJSONArray("fees");
							for (int m = 0; m < feesJsonArray.length(); m++) {
								JSONObject feeJsonObject = feesJsonArray.getJSONObject(m);
								JSONObject jobTitleJsonObject = feeJsonObject.getJSONObject("jobTitle");
								jo.put("fee_name", feeJsonObject.getString("name"));
								jo.put("fee_id", feeJsonObject.getLong("id"));
								jo.put("fee_price", feeJsonObject.getLong("price"));
								jo.put("jobTitle_name", jobTitleJsonObject.getString("name"));
								jo.put("jobTitle_id", jobTitleJsonObject.getLong("id"));
								Log.v(TAG, "fee_jo:"+jo.toString());
								jsonList.add(jo);
							}
						}

					}
				}
			}
			myApp.setJsonList(jsonList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 发送邮件的方法
	 * @return
	 */
	@SuppressWarnings("static-access")
	private boolean sendEmail(){
		Properties props = new Properties();
		props.put("mail.smtp.protocol", "smtp");
		props.put("mail.smtp.auth", "true");//设置要验证
		props.put("mail.smtp.host", "smtp.sina.com");//设置host
		props.put("mail.smtp.port", "25");  //设置端口
		PassAuthenticator pass = new PassAuthenticator();   //获取帐号密码
		Session session = Session.getInstance(props, pass); //获取验证会话
		try
		{
			//配置发送及接收邮箱
			InternetAddress fromAddress, toAddress;
			/**
			 * 这个地方需要改成自己的邮箱
			 */
			fromAddress = new InternetAddress("zhangruitest0113@sina.com");
			toAddress   = new InternetAddress("zhangruitest0113@sina.com");
			/**
			 * 一下内容是：发送邮件时添加附件
			 */
			MimeBodyPart attachPart = new MimeBodyPart();
			FileDataSource fds = new FileDataSource(Environment.getExternalStorageDirectory()+"/crash"+"/crash.log"); //打开要发送的文件
			Log.v(TAG, "已打开crash文件");
			attachPart.setDataHandler(new DataHandler(fds));
			attachPart.setFileName(fds.getName());
			MimeMultipart allMultipart = new MimeMultipart("mixed"); //附件
			allMultipart.addBodyPart(attachPart);//添加
			Log.v(TAG, "已添加附件");
			//配置发送信息
			MimeMessage message = new MimeMessage(session);
//                message.setContent("test", "text/plain"); 
			message.setContent(allMultipart); //发邮件时添加附件
			message.setSubject("Patient Crash Log");
			message.setFrom(fromAddress);
			message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
			message.saveChanges();
			//连接邮箱并发送
			Transport transport = session.getTransport("smtp");
			/**
			 * 这个地方需要改称自己的账号和密码
			 */
			transport.connect("smtp.sina.com", "zhangruitest0113@sina.com", "123456789Abc");
			transport.send(message);
			transport.close();
		} catch (Exception e) {
			//Log.e("sendmail", e.printStackTrace());
			e.printStackTrace();
			//throw new RuntimeException();//将此异常向上抛出，此时CrashHandler就能够接收这里抛出的异常并最终将其存放到txt文件中

		}
		return false;
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
