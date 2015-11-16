package com.example.patientclient01;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.dingdan.DingDanDaiFuKuan;
import com.example.dingdan.DingDanDaiFuYaoFei;
import com.example.dingdan.DingDanDaiJiuYi;
import com.example.dingdan.DingDanDaiKaiShi;
import com.example.dingdan.DingDanDaiPingJia;
import com.example.dingdan.DingDanDaiQuYao;
import com.example.dingdan.DingDanQuanBu;
import com.example.dingdan.DingDanYiJieShu;
import com.example.dingdan.DingDanYiKaiShi;
import com.example.dingdan.DingDanYiPingJia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//订单管理
public class DingDanGuanLiActivity extends Activity{
	String TAG = "DingDanGuanLiActivity";
	MyApp myApp;
	String http, httpUrl;
	JSONArray scheduleJsonArray;
	JSONObject joRev;
	TextView quanbuTextView, daifukuanTextView, daikaishiTextView,daijiuyiTextView, yikaishiTextView;
	TextView daifuyaofeiTextView, daiquyaoTextView, daipingjiaTextView, yipingjiaTextView, yijieshuTextView;
	//已完成
	String getYiWanChengString = "shlc/patient/medicalRecords/status/EVALUATED";
	//待付款
	String getDaiFuKuanString = "shlc/patient/medicalRecords/status/NOT_PAID";
	//待评价
	String getDaiPingJiaString = "shlc/patient/medicalRecords/status/CLOSED";
	//待开始
	String getDaiKaiShiString = "shlc/patient/medicalRecords/status/NOT_STARTED";
	//待进入
	String getJinRuString = "shlc/patient/medicalRecords/status/WAITING_FOR_PATIENT";
	//进行中
	String getInProgressString = "shlc/patient/medicalRecords/status/IN_PROGRESS";
	//待面诊就医订单
	String getDaiJiuYiString = "shlc/patient/medicalRecords/status/WAITING_FOR_MEET";
	//待付药费订单
	String getDaiFuYaoFeiString = "shlc/patient/medicalRecords/status/P_NOT_PAID";
	//待取药订单
	String getDaiQuYaoString = "shlc/patient/medicalRecords/status/WAITING_FOR_MED";
	//统计各类订单数量
	int dksLength, dfghfLength, djyLength, dfyfLength, dqyLength, dpjLenth, ypjLength, jinxingLength, quanbuLength;
	private Button backButton;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ddgl_new);
		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		quanbuTextView = (TextView)findViewById(R.id.quanbu_tv);
		daifukuanTextView = (TextView)findViewById(R.id.daifukuan_tv);
		daikaishiTextView = (TextView)findViewById(R.id.daikaishi_tv);
		daijiuyiTextView = (TextView)findViewById(R.id.daijiuyi_tv);
		daifuyaofeiTextView = (TextView)findViewById(R.id.daifuyaofei_tv);
		daiquyaoTextView = (TextView)findViewById(R.id.daiquyao_tv);
		daipingjiaTextView = (TextView)findViewById(R.id.daipingjia_tv);
		yipingjiaTextView = (TextView)findViewById(R.id.yipingjia_tv);
		yikaishiTextView = (TextView)findViewById(R.id.jinxing_tv);
		//yijieshuTextView = (TextView)findViewById(R.id.yijieshu_tv);
		yipingjiaTextView.setText("已评价订单 ("+"0"+")");
		daifukuanTextView.setText("待付咨询费订单 ("+"0"+")");
		daipingjiaTextView.setText("待评价订单 ("+"0"+")");
		daikaishiTextView.setText("待开始咨询订单 ("+"0"+")");
		yikaishiTextView.setText("正在进行咨询订单 ("+"0"+")");
		daifuyaofeiTextView.setText("待付药费订单 ("+"0"+")");
		daijiuyiTextView.setText("待面诊就医订单 ("+"0"+")");
		daiquyaoTextView.setText("待取药订单 ("+"0"+")");
		quanbuTextView.setText("全部订单 ("+"0"+")");
		initData();
		initView();
		backButton = (Button)findViewById(R.id.back_btn);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, MainActivity.class);
				startActivity(intent);
				//YuYueGuanLiActivity.this.finish();
			}
		});
	}
	//获取各类订单内容
	public void initData(){
		httpUrl = http+getYiWanChengString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"YiWanCheng-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			ypjLength = scheduleJsonArray.length();
			yipingjiaTextView.setText("已评价订单 ("+ypjLength+")");
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

		httpUrl = http+getDaiFuKuanString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Not-Paid-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			dfghfLength = scheduleJsonArray.length();
			daifukuanTextView.setText("待付咨询费订单 ("+dfghfLength+")");
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

		httpUrl = http+getDaiPingJiaString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"DaiPingJia-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			dpjLenth = scheduleJsonArray.length();
			daipingjiaTextView.setText("待评价订单 ("+dpjLenth+")");
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
		httpUrl = http+getDaiKaiShiString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Not-Started-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			dksLength = scheduleJsonArray.length();
			daikaishiTextView.setText("待开始咨询订单 ("+dksLength+")");
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
		httpUrl = http+getInProgressString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"In_Progress-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			jinxingLength = scheduleJsonArray.length();
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
		httpUrl = http+getJinRuString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"In_Progress-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			jinxingLength += scheduleJsonArray.length();
			yikaishiTextView.setText("正在进行咨询订单 ("+jinxingLength+")");
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
		httpUrl = http+getDaiJiuYiString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Waiting-for-meet-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			djyLength = scheduleJsonArray.length();
			daijiuyiTextView.setText("待面诊就医订单 ("+djyLength+")");
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
		httpUrl = http+getDaiFuYaoFeiString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"P-NOT-PAID-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			dfyfLength = scheduleJsonArray.length();
			daifuyaofeiTextView.setText("待付药费订单 ("+dfyfLength+")");
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
		httpUrl = http+getDaiQuYaoString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Waiting-for-med-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			dqyLength = scheduleJsonArray.length();
			daiquyaoTextView.setText("待取药订单 ("+dqyLength+")");
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
		quanbuLength = dfghfLength+dksLength+jinxingLength+djyLength+dfyfLength+dqyLength+dpjLenth+ypjLength;
		quanbuTextView.setText("全部订单 ("+quanbuLength+")");
	}

	public void initView(){
		quanbuTextView.setClickable(true);
		quanbuTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanQuanBu.class);
				startActivity(intent);

			}
		});
		daifukuanTextView.setClickable(true);
		daifukuanTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanDaiFuKuan.class);
				startActivity(intent);

			}
		});
		daikaishiTextView.setClickable(true);
		daikaishiTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanDaiKaiShi.class);
				startActivity(intent);

			}
		});
		daijiuyiTextView.setClickable(true);
		daijiuyiTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanDaiJiuYi.class);
				startActivity(intent);

			}
		});
		daifuyaofeiTextView.setClickable(true);
		daifuyaofeiTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanDaiFuYaoFei.class);
				startActivity(intent);

			}
		});
		daiquyaoTextView.setClickable(true);
		daiquyaoTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanDaiQuYao.class);
				startActivity(intent);

			}
		});
		daipingjiaTextView.setClickable(true);
		daipingjiaTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanDaiPingJia.class);
				startActivity(intent);

			}
		});
		yipingjiaTextView.setClickable(true);
		yipingjiaTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanYiPingJia.class);
				startActivity(intent);

			}
		});
		yikaishiTextView.setClickable(true);
		yikaishiTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanYiKaiShi.class);
				startActivity(intent);

			}
		});
		
		/*
		yijieshuTextView.setClickable(true);
		yijieshuTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanGuanLiActivity.this, DingDanYiJieShu.class);
				startActivity(intent);
				
			}
		});
		*/

	}
}
