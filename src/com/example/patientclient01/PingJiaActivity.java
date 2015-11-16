package com.example.patientclient01;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
//对本次诊疗的评价
public class PingJiaActivity extends Activity{
	String TAG = "PingJiaActivity";
	EditText pingjiaEditText;
	//准时、准确、经济、有效、耐心
	RatingBar zhunshiRatingBar, zhunqueRatingBar, jingjiRatingBar, youxiaoRatingBar, naixinRatingBar;
	Button okButton, cancelButton;
	long zhunshi, zhunque, jingji, youxiao, naixin;
	String medicalRecordId;
	MyApp myApp;
	String http, httpUrl;
	String pingjiaUrlString = "shlc/patient/evaluate/medicalRecord/";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_pingjia);
		pingjiaEditText = (EditText)findViewById(R.id.pingjia_et);
		zhunshiRatingBar = (RatingBar)findViewById(R.id.zs_rating_bar);
		zhunqueRatingBar = (RatingBar)findViewById(R.id.zq_rating_bar);
		jingjiRatingBar = (RatingBar)findViewById(R.id.jj_rating_bar);
		youxiaoRatingBar = (RatingBar)findViewById(R.id.yx_rating_bar);
		naixinRatingBar = (RatingBar)findViewById(R.id.nx_rating_bar);
		okButton = (Button)findViewById(R.id.ok_btn);
		cancelButton = (Button)findViewById(R.id.cancel_btn);

		Intent intent = getIntent();
		medicalRecordId = intent.getStringExtra("medicalRecordId");
		Log.v(TAG, "medicalRecordId:"+ medicalRecordId);

		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		getRating();
	}
	//上传评价
	public void getRating(){
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				zhunshi = (long) zhunshiRatingBar.getRating();
				zhunque = (long) zhunqueRatingBar.getRating();
				jingji = (long) jingjiRatingBar.getRating();
				youxiao = (long) youxiaoRatingBar.getRating();
				naixin = (long) naixinRatingBar.getRating();
				String pingjiaString = pingjiaEditText.getText().toString();
				httpUrl = http+pingjiaUrlString+medicalRecordId;
				Log.v(TAG, "httpUrl:"+httpUrl);
				JSONObject joSend = new JSONObject();
				try {
					joSend.put("description", pingjiaString);
					joSend.put("zs", zhunshi);
					joSend.put("zq", zhunque);
					joSend.put("jj", jingji);
					joSend.put("yx", youxiao);
					joSend.put("nx", naixin);
					HttpPost post = HttpUtil.getPost(httpUrl, joSend);
					JSONObject joRev = HttpUtil.getString(post, 3);
					Log.v(TAG, "joRev:"+joRev.toString());
					if(joRev.getString("result").equals("200")){
						System.out.println("评价成功！");
						Intent intent = new Intent(PingJiaActivity.this, MainActivity.class);
						startActivity(intent);
						PingJiaActivity.this.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});



	}
}
