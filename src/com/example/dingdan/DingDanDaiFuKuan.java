package com.example.dingdan;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.patientclient01.*;


import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DingDanDaiFuKuan extends Activity{
	private String TAG = "DingDanDaiFuKuan";
	MyApp myApp;
	//订单状态为未付款url
	String getDaiFuKuanString = "shlc/patient/medicalRecords/status/NOT_PAID";
	String http, httpUrl;
	JSONObject joRev;
	JSONArray scheduleJsonArray;
	ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
	private String formats = "yyyy-MM-dd HH:mm:ss";
	private ListView listView;
	private List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	DaiFuKuanAdapter daiFuKuanAdapter;
	private Button backButton;
	//RefreshableView refreshableView; 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dd_dfk);
		//refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);  
		myApp = (MyApp)getApplication();
		//从myApp中获取初始化信息
		jsonList = myApp.getJsonList();
		http = myApp.getHttp();
		initData();
		sortDataByCreateTime();
		listView = (ListView)findViewById(R.id.dfk_listview);
		daiFuKuanAdapter = new DaiFuKuanAdapter(this, data, http, myApp);
		listView.setAdapter(daiFuKuanAdapter);
		daiFuKuanAdapter.notifyDataSetChanged();
		backButton = (Button)findViewById(R.id.back_btn);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanDaiFuKuan.this, MainActivity.class);
				startActivity(intent);
				//YuYueGuanLiActivity.this.finish();
			}
		});
		/*
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {  
            @Override  
            public void onRefresh() {  
                try {  
                    Thread.sleep(3000);  
                    //myApp.setDataDFK(null);
                   // data =  new ArrayList<HashMap<String,String>>();
                    //initData();
                  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                refreshableView.finishRefreshing();  
            }  
        }, 0); 
		*/
	}
	private void initData(){
		httpUrl = http+getDaiFuKuanString;
		try {
			//从服务器获取未获款的订单信息 json格式
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Not-Paid-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			for(int i = 0; i < scheduleJsonArray.length(); i++){
				JSONObject jObject = scheduleJsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(jObject.get("id")));
				JSONObject scheduleJsonObject = jObject.getJSONObject("schedule");
				map.put("time", scheduleJsonObject.getString("startTime"));
				long createTime = jObject.getLong("createTime");
				String createTimeString = TimeStamp.TimeStamp2Date(createTime, formats);
				Log.v(TAG,"createTimeString："+createTimeString);
				map.put("creatTime", createTimeString);
				JSONObject doctorJsonObject = jObject.getJSONObject("doctor");
				long departmentId = doctorJsonObject.getLong("departmentId");
				//通过departmentID来获取到医院、科室、职称等信息
				for (int j = 0; j < jsonList.size(); j++) {
					if(departmentId == jsonList.get(j).getLong("department_id")){
						map.put("hospital", jsonList.get(j).getString("hospital_name"));
						map.put("keshi", jsonList.get(j).getString("department_name"));
						map.put("zhicheng", jsonList.get(j).getString("jobTitle_name"));
					}
				}
				map.put("name", doctorJsonObject.getString("name"));
				map.put("zhuanchang",  doctorJsonObject.getString("skill"));
				map.put("type", "0");
				Log.v(TAG, "daifukuan-map:"+map.toString());
				data.add(map);
			}
			//将未付款信息存到myApp中
			myApp.setDataDFK(data);
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
	public List<HashMap<String, String>> getData() {
		return data;
	}
	public void setData(List<HashMap<String, String>> data) {
		this.data = data;
	}
	//================================================================================================
	//按订单创建时间排序
	Comparator<HashMap<String, String>> mapComprator = new Comparator<HashMap<String, String>>(){


		@Override
		public int compare(HashMap<String, String> map1,
						   HashMap<String, String> map2) {
			// TODO Auto-generated method stub
			String start1 = map1.get("creatTime");
			String start2 = map2.get("creatTime");
			long long1 = Long.parseLong(dateToInt(start1));
			long long2 = Long.parseLong(dateToInt(start2));
			int result = (int)(long1-long2);
			Log.v(TAG, "compare:"+long1+" "+long2+" "+result);
			return result;
		}
	};
	public String dateToInt(String str){
		str=str.replace(":", "");
		str=str.replace("-", "");
		str=str.replace(" ", "");
		return str;

	}
	public void sortDataByCreateTime(){
		Collections.sort(data,mapComprator);
		Log.v(TAG, data.toString());
	}
	//================================================================================================
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