package com.example.dingdan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.patientclient01.DingDanGuanLiActivity;
import com.example.patientclient01.HttpUtil;
import com.example.patientclient01.MainActivity;
import com.example.patientclient01.MyApp;
import com.example.patientclient01.R;
import com.example.patientclient01.TimeStamp;

public class DingDanDaiFuYaoFei extends Activity{
	private String TAG = "DingDanDaiFuYaoFei";
	MyApp myApp;
	String getDaiFuYaoFeiString = "shlc/patient/medicalRecords/status/P_NOT_PAID";
	String http, httpUrl;
	JSONObject joRev;
	JSONArray scheduleJsonArray;
	private String formats = "yyyy-MM-dd HH:mm:ss";
	ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
	private ListView listView;
	String medicalRecordId;
	private List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	private Button backButton;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dd_dfyf);
		myApp = (MyApp)getApplication();
		jsonList = myApp.getJsonList();
		http = myApp.getHttp();
		medicalRecordId = myApp.getMedicalRecordId();
		initData();
		sortDataByCreateTime();
		listView = (ListView)findViewById(R.id.dfyf_listview);
		listView.setAdapter(new DaiFuYaoFeiAdapter(this, data, http, myApp,medicalRecordId));
		backButton = (Button)findViewById(R.id.back_btn);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DingDanDaiFuYaoFei.this, MainActivity.class);
				startActivity(intent);
				//YuYueGuanLiActivity.this.finish();
			}
		});
	}
	private void initData(){
		httpUrl = http+getDaiFuYaoFeiString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"DaiFuYaoFei-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			for(int i = 0; i < scheduleJsonArray.length(); i++){
				JSONObject jObject = scheduleJsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(jObject.get("id")));
				long createTime = jObject.getLong("createTime");
				String createTimeString = TimeStamp.TimeStamp2Date(createTime, formats);
				Log.v(TAG,"createTimeString："+createTimeString);
				map.put("creatTime", createTimeString);
				JSONObject scheduleJsonObject = jObject.getJSONObject("schedule");
				map.put("time", scheduleJsonObject.getString("startTime"));
				JSONObject doctorJsonObject = jObject.getJSONObject("doctor");
				long departmentId = doctorJsonObject.getLong("departmentId");
				for (int j = 0; j < jsonList.size(); j++) {
					if(departmentId == jsonList.get(j).getLong("department_id")){
						map.put("hospital", jsonList.get(j).getString("hospital_name"));
						map.put("keshi", jsonList.get(j).getString("department_name"));
						map.put("zhicheng", jsonList.get(j).getString("jobTitle_name"));
					}
				}
				map.put("name", doctorJsonObject.getString("name"));
				map.put("zhuanchang",  doctorJsonObject.getString("skill"));
				map.put("type", "6");
				Log.v(TAG, "DaiFuYaoFei-map:"+map.toString());
				data.add(map);
			}
			myApp.setDataDFYF(data);
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
}
