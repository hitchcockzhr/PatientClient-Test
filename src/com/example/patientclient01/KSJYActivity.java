package com.example.patientclient01;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
//快速就医
public class KSJYActivity extends Activity{
	int departmentTypeId;
	String TAG = "KSJYActivity";
	//http://localhost:8080/shlc/patient/doctors/departmentType/1?active=true
	String http, httpUrl, showStr, resultStr ;
	String getDoctorOfDepartmentString = "shlc/patient/doctors/departmentType/";
	String activeString = "?active=true";
	String getScheUrlString = "shlc/patient/schedules?doctorId=";
	String inprogressString = "&inprogress=true";
	String postScheUrlString = "shlc/patient/medicalRecord?scheduleId=";
	TextView titleTextView;
	ListView ksjyListView;

	private String readFileCache;
	private static String scheIDString;
	private JSONObject joReadFileCache;
	private SaveCache saveCache = new SaveCache(AppContext.get());
	private String[] arrProvinces, citiesSpinner, hospitalSpinner, nameProvinces, nameCities,
			nameHospitals, nameDepartments, namePros, nameDoctors, nameDoctorIDs,nameDepartmentTypes;
	private int[] idDepartments, idJobTitles,departmentTypeIdInts;
	private int idDepartmentInt, idJobTitlesInt,departmentTypeIdInt;
	private static String[][] arrCities;
	private static String[][][] arrHospitals;
	private static JSONObject[] joProvinces;
	private static JSONObject[] joCities;
	private ArrayList<JSONObject> joCitiesArrayList = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> joHospitalsArrayList = new ArrayList<JSONObject>();
	private static JSONObject[] joHospitals, joDepartments, joPros, joDoctors,joDepartmentTypes;
	private static JSONObject joDepartmentType, joDoctor, joCity, joHospital;
	private static JSONArray jaProvinces, jaCities, jaHospitals, jaDepartments, jaPros, jaDoctors, jaDoctorIDs, jaDepartmentTypes ;
	private String cityString;
	private MyApp myApp;
	private long[] scheIDs;
	ArrayList<HashMap<String, Object>> doctorArrayList = new ArrayList<HashMap<String,Object>>();
	private JSONObject joDepartment;
	private HashMap<String, String> selectedMap = new HashMap<String, String>();

	private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_ksjy);
		myApp = (MyApp) getApplication();
		http = myApp.getHttp();
		cityString = myApp.getCityString();
		initCache();
		Intent intent = getIntent();
		departmentTypeId = intent.getIntExtra("departmentID", 0);
		departmentTypeId = myApp.getDepartmentId();
		Log.v(TAG, "ksjy departmentId:"+departmentTypeId);
		if(departmentTypeId != 0){
			getDoctor(departmentTypeId);
		}
		titleTextView = (TextView)findViewById(R.id.title_tv);
		titleTextView.setText("请选择医生");
		ksjyListView = (ListView)findViewById(R.id.keshi_listview);
		final KsjyAdapter ksjyAdapter = new KsjyAdapter(this, doctorArrayList);
		ksjyListView.setAdapter(ksjyAdapter);
		/*
		ksjyListView.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//selectedMap = (HashMap<String, Object>) ksjyListView.getItemAtPosition(arg2);
				Log.v(TAG, "selected:"+arg2);
				map.clear();
				map.put(arg2, 100);
				ksjyAdapter.notifyDataSetChanged();
				scheIDString = String.valueOf(scheIDs[arg2]);
				Log.v(TAG, "scheID:"+scheIDString);
				selectedMap = (HashMap<String, String>) ksjyListView.getItemAtPosition(arg2);
				
				selectedMap.put("hospital", myApp.getYiyuanString());
				selectedMap.put("keshi", myApp.getKeshiString());
				try {
					selectedMap.put("zhicheng", namePros[joDoctor.getInt("jobTitleId")-1]);
					selectedMap.put("name", joDoctor.getString("name"));
					selectedMap.put("zhuanchang", joDoctor.getString("skill"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				selectedMap.put("scheid", scheIDString);
				Log.v(TAG, "ITEM:"+selectedMap.toString());
				//JSONObject selectedDoctor = joDoctors[arg2];
				//Log.v(TAG, "selectedDoctor:"+selectedDoctor.toString());
			}
		});
		*/
		//uploadSche();
		/*
		SimpleAdapter saAdapter = new SimpleAdapter(this, doctorArrayList, 
				R.layout.ksjy_doctor_item, 
				new String[]{"head","name","hospital","keshi","zhicheng","zhuanchang","pingjia"},
				new int[]{R.id.doctor_head_iv, R.id.name_tv, R.id.hospital_tv, R.id.keshi_tv, R.id.zhicheng_tv,R.id.discription_tv,R.id.zs_rating_bar});
		ksjyListView.setAdapter(saAdapter);
		*/
	}
	private void initCache(){
		String fileName = myApp.getJoStartPath();
		//doctorId = myApp.getDoctorIDString();
		//Log.v(TAG, "fileName:"+fileName);
		try {
			readFileCache = saveCache.read(fileName);
			joReadFileCache = new JSONObject(readFileCache);
			String result = joReadFileCache.getString("result");
			String resultMessage = joReadFileCache.getString("resultMessage");
			//取锟斤拷全锟斤拷value
			JSONObject joValue = joReadFileCache.getJSONObject("value");
			//取锟斤拷provinces锟斤拷锟斤拷
			Log.v(TAG, "joValue:"+joValue.toString());
			jaProvinces = joValue.getJSONArray("provinces");
			joProvinces = new JSONObject[jaProvinces.length()];
			nameProvinces = new String[jaProvinces.length()];
			arrCities = new String[jaProvinces.length()][];
			for(int i=0; i<jaProvinces.length(); i++){
				joProvinces[i] = jaProvinces.getJSONObject(i);
				nameProvinces[i] = joProvinces[i].getString("name");
				Log.v(TAG, "nameProvinces:"+nameProvinces[i]);
				jaCities = joProvinces[i].getJSONArray("cities");
				joCities = new JSONObject[jaCities.length()];
				arrCities[i] = new String[jaCities.length()];
				Log.v(TAG, "jaCities length:"+jaCities.length());
				for(int j=0; j<jaCities.length(); j++){
					joCities[j] = jaCities.getJSONObject(j);
					joCitiesArrayList.add(jaCities.getJSONObject(j));
					arrCities[i][j] = joCities[j].getString("name");
					Log.v(TAG, "city name:"+joCities[j].getString("name"));
				}
			}

			jaPros = joValue.getJSONArray("jobTitles");
			joPros = new JSONObject[jaPros.length()];
			namePros = new String[jaPros.length()];
			idJobTitles = new int[namePros.length];
			for(int i=0; i<jaPros.length(); i++){
				joPros[i] = jaPros.getJSONObject(i);
				namePros[i] = joPros[i].getString("name");
				idJobTitles[i] = joPros[i].getInt("id");
				Log.v(TAG, "namePros:"+namePros[i]);
				Log.v(TAG, "idJobTitles:"+idJobTitles[i]);
			}

			jaDepartmentTypes = joValue.getJSONArray("departmentTypes");
			joDepartmentTypes = new JSONObject[jaDepartmentTypes.length()];
			departmentTypeIdInts = new int[jaDepartmentTypes.length()];
			nameDepartmentTypes = new String[jaDepartmentTypes.length()];
			for(int i=0; i<jaDepartmentTypes.length(); i++){
				joDepartmentTypes[i] = jaDepartmentTypes.getJSONObject(i);
				departmentTypeIdInts[i] = joDepartmentTypes[i].getInt("id");
				nameDepartmentTypes[i] = joDepartmentTypes[i].getString("name");

			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getHospital(int departmentId) throws JSONException{
		Log.v(TAG, "city length:"+joCitiesArrayList.size());
		for(int i=0; i<joCitiesArrayList.size(); i++){

			Log.v(TAG, "city:"+cityString+" "+joCitiesArrayList.get(i).getString("name"));
			if(cityString.equals(joCitiesArrayList.get(i).getString("name"))){
				joCity = joCitiesArrayList.get(i);
				break;
			}else{
				continue;
			}
		}
		jaHospitals = joCity.getJSONArray("hospitals");
		joHospitals = new JSONObject[jaHospitals.length()];
		for(int i=0; i<jaHospitals.length(); i++){
			joHospitals[i] = jaHospitals.getJSONObject(i);
		}

		for(int i=0; i<joHospitals.length; i++){
			jaDepartments = joHospitals[i].getJSONArray("departments");
			joDepartments = new JSONObject[jaDepartments.length()];
			for(int j=0; j<jaDepartments.length();j++){
				joDepartments[j] = jaDepartments.getJSONObject(j);
				if(joDepartments[j].getInt("id") == departmentId){
					joHospital = joHospitals[i];
					joDepartment = joDepartments[j];
				}
			}


		}
		Log.v(TAG, "joHospital name:"+joHospital.getString("name"));
		return joHospital.getString("name");
	}

	public void getDoctor(int departmentTypeId){
		httpUrl = http + getDoctorOfDepartmentString + departmentTypeId+activeString;
		Log.v(TAG, "httpUrl:"+httpUrl);
		//HttpPost post = HttpUtil.getPost(httpUrl, null);

		try {
			JSONObject joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG, "ksjy doctor:"+joRev.toString());
			showStr = joRev.getString("result");
			resultStr = joRev.getString("resultMessage");
			jaDoctors = joRev.getJSONArray("value");
			joDoctors = new JSONObject[jaDoctors.length()];
			for(int i=0; i<joDoctors.length; i++){
				joDoctors[i] = jaDoctors.getJSONObject(i);
				Log.v(TAG, "joDoctor:"+joDoctors[i].toString());
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("head", joDoctors[i].getString("photoPic"));
				map.put("name", joDoctors[i].getString("name"));
				map.put("hospital", getHospital(joDoctors[i].getInt("departmentId")));
				map.put("keshi", joDepartment.getString("name"));
				map.put("zhicheng", namePros[joDoctors[i].getInt("jobTitleId")-1]);
				map.put("zhuanchang", joDoctors[i].getString("skill"));
				map.put("pingjia", new Float(4));
				List<HashMap<String,Object>> scheList = getSchedule(joDoctors[i].getString("userId"));
				Log.v(TAG, "scheList:"+scheList.toString());
				if(scheList.size()==0){

					return;
				}
				map.putAll(scheList.get(0));
				doctorArrayList.add(map);
			}
			Log.v(TAG, doctorArrayList.toString());
			if(doctorArrayList.size()==0){
				Toast.makeText(this, "当前科室无诊室", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<HashMap<String,Object>> getSchedule(String userId){
		List<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		if(userId!=null){
			httpUrl = http+getScheUrlString+userId+inprogressString;

			Log.v(TAG, "getSche:"+httpUrl);
			try {
				JSONObject joRev = HttpUtil.getResultForHttpGet(httpUrl);
				Log.v(TAG, "get:"+joRev.toString());
				JSONArray jaSches = joRev.getJSONArray("value");
				//List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
				scheIDs = new long[jaSches.length()];
				for(int i=0 ; i<jaSches.length(); i++){
					HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
					JSONObject scheJsonObject;
					try {
						scheJsonObject = jaSches.getJSONObject(i);
						long count = scheJsonObject.getLong("count");
						String startString = scheJsonObject.getString("startTime");
						String dateString = scheJsonObject.getString("startTime").substring(0, 10);
						String startTimeString = scheJsonObject.getString("startTime").substring(10, scheJsonObject.getString("startTime").length());
						String endTimeString = scheJsonObject.getString("endTime").substring(10, scheJsonObject.getString("endTime").length());
						itemHashMap.put("start", startString);
						itemHashMap.put("date", dateString);
						itemHashMap.put("time", startTimeString+"-"+endTimeString);
						itemHashMap.put("week", getWeek(dateString));
						Log.v(TAG, "time:"+startTimeString+"-"+endTimeString);
						Log.v(TAG, "week:"+getWeek(dateString));
						//itemHashMap.put("radio", "select");
						//itemHashMap.put("scheID", String.valueOf(scheJsonObject.getLong("id")));
						scheIDs[i] = scheJsonObject.getLong("id");
						itemHashMap.put("scheId", scheIDs[0]);
						itemHashMap.put("patients", String.valueOf(count-scheJsonObject.getLong("patients")));
						/*
						httpUrl = http+getQueueUrlString+scheJsonObject.getLong("id");
						JSONObject joQueue = HttpUtil.getResultForHttpGet(httpUrl);
						if(joQueue.getString("result").equals("200")){
							int queueLength = joQueue.getJSONArray("value").length();
							Log.v(TAG, "joQueue.length:"+queueLength);
							int length = (int) (count - queueLength);
							itemHashMap.put("count", String.valueOf(length));
							
						}
						
						*/
						//itemHashMap.put("endTime", endTimeString);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					data.add(itemHashMap);
				}
				//Collections.sort(data,mapComprator);

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
		return data;
	}
	private String getWeek(String pTime) {
		String weekString = "星期";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(pTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1){
			weekString += "天";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			weekString += "一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			weekString += "二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			weekString += "三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			weekString += "四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			weekString += "五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			weekString += "六";
		}
		return weekString;
	}
	class KsjyHolder{
		private TextView nameTextView, hospitalTextView, keshiTextView;
		private TextView zhichengTextView, zhuanchangTextView;
		//private RadioButton radioButton;
		private Button selectedButton;
		private TextView dateTextView, weekTextView, timeTextView, queueTextView;
		private ImageView headImageView;
		private RatingBar ratingBar;
		public KsjyHolder(View view){
			this.nameTextView = (TextView)view.findViewById(R.id.name_tv);
			this.hospitalTextView = (TextView)view.findViewById(R.id.hospital_tv);
			this.keshiTextView = (TextView)view.findViewById(R.id.keshi_tv);
			this.zhichengTextView = (TextView)view.findViewById(R.id.zhicheng_tv);
			this.zhuanchangTextView = (TextView)view.findViewById(R.id.discription_tv);
			this.headImageView = (ImageView)view.findViewById(R.id.doctor_head_iv);
			this.ratingBar = (RatingBar)view.findViewById(R.id.zs_rating_bar);
			//this.radioButton = (RadioButton)view.findViewById(R.id.sche_radio_btn);
			this.selectedButton = (Button)view.findViewById(R.id.selected_btn);
			this.dateTextView = (TextView)view.findViewById(R.id.date_tv);
			this.weekTextView = (TextView)view.findViewById(R.id.week_tv);
			this.timeTextView = (TextView)view.findViewById(R.id.time_tv);
			this.queueTextView = (TextView)view.findViewById(R.id.queue_tv);
		}
	}
	class KsjyAdapter extends BaseAdapter{
		private Context context;
		List<HashMap<String,Object>> data;
		public KsjyAdapter(Context context, List<HashMap<String,Object>> data){
			this.context = context;
			this.data = data;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			KsjyHolder ksjyHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.ksjy_doctor_item, null);
				ksjyHolder = new KsjyHolder(convertView);
				convertView.setTag(ksjyHolder);
			}else{
				ksjyHolder = (KsjyHolder)convertView.getTag();
			}
			ksjyHolder.nameTextView.setText(data.get(position).get("name").toString());
			ksjyHolder.hospitalTextView.setText(data.get(position).get("hospital").toString());
			ksjyHolder.keshiTextView.setText(data.get(position).get("keshi").toString());
			ksjyHolder.zhuanchangTextView.setText(data.get(position).get("zhuanchang").toString());
			ksjyHolder.zhichengTextView.setText(data.get(position).get("zhicheng").toString());
			ksjyHolder.headImageView.setBackgroundResource(R.drawable.ic_launcher);
			ksjyHolder.ratingBar.setRating(((Float)data.get(position).get("pingjia")).floatValue());
			//ksjyHolder.radioButton.setChecked(map.get(position) == null ?false:true);
			ksjyHolder.dateTextView.setText(data.get(position).get("date").toString());
			ksjyHolder.weekTextView.setText(data.get(position).get("week").toString());
			ksjyHolder.timeTextView.setText(data.get(position).get("time").toString());
			ksjyHolder.queueTextView.setText("尚余"+data.get(position).get("patients")+"号");
			//if(data.get(position).get("patients").equals("0")){
			//	ksjyHolder.radioButton.setVisibility(View.GONE);
			//}

			ksjyHolder.selectedButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					uploadSche(position);
				}
			});
			return convertView;
		}
		private void uploadSche(int position){
			scheIDString = String.valueOf(data.get(position).get("scheId"));
			httpUrl = http+postScheUrlString+scheIDString;
			HttpPost post = HttpUtil.getPost(httpUrl, null);
			JSONObject joRev = HttpUtil.getString(post, 3);
			Log.v(TAG,"joRev:"+joRev.toString());
			selectedMap.put("hospital", data.get(position).get("hospital").toString());
			selectedMap.put("keshi", data.get(position).get("keshi").toString());
			selectedMap.put("zhicheng", data.get(position).get("zhicheng").toString());
			selectedMap.put("name", data.get(position).get("name").toString());
			selectedMap.put("zhuanchang",data.get(position).get("zhuanchang").toString());


			selectedMap.put("scheid", scheIDString);
			Log.v(TAG, "selectedMap:"+selectedMap.toString());
			Intent intent = new Intent(KSJYActivity.this, DingDanXiangQingActivity.class);
			intent.putExtra("yuyue", joRev.toString());
			intent.putExtra("scheId", scheIDString);
			intent.putExtra("selectedMap", selectedMap);
			startActivity(intent);
			KSJYActivity.this.finish();
		}

	}


}
