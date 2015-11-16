package com.example.patientclient01;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class YuyueActivity extends Activity{
	private String TAG = "YuyueActivity";
	JSONObject joDoctor, joSche;
	JSONArray jaSches;
	TextView hospitalTextView, keshiTextView, zhichengTextView, zhuanchangTextView, nameTextView;
	ImageView headImageView;
	ListView scheListView;
	Button okBtn, cancelBtn;
	MyApp myApp;
	private String readFileCache;
	private JSONObject joReadFileCache;
	private SaveCache saveCache = new SaveCache(AppContext.get());
	private static JSONArray jaPros;
	private static JSONObject[] joPros;
	private static String[] namePros;
	private static int[] idJobTitles;
	private long[] scheIDs;
	private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	private static String scheIDString;
	String http, httpUrl, showStr, resultStr ;
	String postScheUrlString = "shlc/patient/medicalRecord?scheduleId=";
	String getQueueUrlString = "shlc/patient/queue/";
	JSONObject joRev;
	HashMap<String, String> selectedMap;
	String doctorId;
	String getScheUrlString = "shlc/patient/schedules?doctorId=";
	String inprogressString = "&inprogress=false";
	Button refreshBtn;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_yuyue);
		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		initCache();
		initData();
		try {
			initView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uploadSche();

	}

	private void initCache(){
		String fileName = myApp.getJoStartPath();
		doctorId = myApp.getDoctorIDString();
		//Log.v(TAG, "fileName:"+fileName);
		try {
			readFileCache = saveCache.read(fileName);
			joReadFileCache = new JSONObject(readFileCache);
			String result = joReadFileCache.getString("result");
			String resultMessage = joReadFileCache.getString("resultMessage");
			//取锟斤拷全锟斤拷value
			JSONObject joValue = joReadFileCache.getJSONObject("value");
			//取锟斤拷provinces锟斤拷锟斤拷
			/*
			jaProvinces = joValue.getJSONArray("provinces");
			joProvinces = new JSONObject[jaProvinces.length()];
			nameProvinces = new String[jaProvinces.length()];
			//nameProvinces[0] = "-锟斤拷选锟斤拷省锟斤拷-";
			for(int i=0; i<jaProvinces.length(); i++){
				joProvinces[i] = jaProvinces.getJSONObject(i);
				nameProvinces[i] = joProvinces[i].getString("name");
				Log.v(TAG, "nameProvinces:"+nameProvinces[i]);
			}
			*/
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initData(){

		Intent intent = getIntent();
		//joDoctor = new JSONObject(intent.getStringExtra("doctorInfo"));
		joDoctor = myApp.getJoDoctor();
		//joSche = new JSONObject(intent.getStringExtra("scheInfo"));
		//jaSches = joSche.getJSONArray("value");
		Log.v(TAG, "joDoctor:"+joDoctor.toString());
		//Log.v(TAG, "joSche:"+joSche.toString());

		if(doctorId!=null){
			httpUrl = http+getScheUrlString+doctorId+inprogressString;
			//httpUrl = http+getScheUrlString+doctorId;
			Log.v(TAG, "getSche:"+httpUrl);
			try {
				joRev = HttpUtil.getResultForHttpGet(httpUrl);
				Log.v(TAG, "get:"+joRev.toString());
				jaSches = joRev.getJSONArray("value");
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
	}
	private void refresh() {


		onCreate(null);
	}
	public String dateToInt(String str){
		str=str.replace(":", "");
		str=str.replace("-", "");
		str=str.replace(" ", "");
		return str;

	}
	private void initView() throws JSONException{
		//hospitalTextView = (TextView)findViewById(R.id.hospital_tv);
		//keshiTextView = (TextView)findViewById(R.id.keshi_tv);
		//zhichengTextView = (TextView)findViewById(R.id.zhicheng_tv);
		//zhuanchangTextView = (TextView)findViewById(R.id.zhuanchang_tv);
		//nameTextView = (TextView)findViewById(R.id.name_tv);
		//hospitalTextView.setText(myApp.getYiyuanString());
		//keshiTextView.setText(myApp.getKeshiString());
		//zhichengTextView.setText(namePros[joDoctor.getInt("jobTitleId")]);
		//nameTextView.setText(joDoctor.getString("name"));
		//zhuanchangTextView.setText(joDoctor.getString("skill"));

		scheListView = (ListView)findViewById(R.id.sche_listview);
		//SimpleAdapter adapter = new SimpleAdapter(this, getScheData(),R.layout.schedule_list_item,
		//	new String[]{"date","week", "time", "radio"}, new int[]{R.id.date_tv, R.id.week_tv, R.id.time_tv, R.id.sche_radio_btn});
		//scheListView.setAdapter(adapter);
		scheListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		final RadioAdapter radioAdapter = new RadioAdapter(this, getScheData());
		scheListView.setAdapter(radioAdapter);
		scheListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				Log.v(TAG, "position:"+arg2);
				map.clear();
				map.put(arg2, 100);
				radioAdapter.notifyDataSetChanged();
				scheIDString = String.valueOf(scheIDs[arg2]);
				Log.v(TAG, "scheID:"+scheIDString);

				selectedMap = (HashMap<String, String>) scheListView.getItemAtPosition(arg2);

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
			}

		});
		refreshBtn = (Button) findViewById(R.id.refresh_btn);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				refresh();
			}
		});

	}
	//璁㈠崟鎸夋椂闂存帓搴?
	Comparator<HashMap<String, String>> mapComprator = new Comparator<HashMap<String, String>>(){


		@Override
		public int compare(HashMap<String, String> map1,
						   HashMap<String, String> map2) {
			// TODO Auto-generated method stub
			String start1 = map1.get("start");
			String start2 = map2.get("start");
			long long1 = Long.parseLong(dateToInt(start1));
			long long2 = Long.parseLong(dateToInt(start2));
			int result = (int)(long1-long2);
			Log.v(TAG, "compare:"+long1+" "+long2+" "+result);
			return result;
		}
	};
	//鑾峰彇鍖荤敓鐨勬帓鐝俊鎭?
	public List<HashMap<String,String>> getScheData() {
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		scheIDs = new long[jaSches.length()];
		for(int i=0 ; i<jaSches.length(); i++){
			HashMap<String, String> itemHashMap = new HashMap<String, String>();
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
				itemHashMap.put("radio", "select");
				//itemHashMap.put("scheID", String.valueOf(scheJsonObject.getLong("id")));
				scheIDs[i] = scheJsonObject.getLong("id");
				if(count-scheJsonObject.getLong("patients")>9){
					itemHashMap.put("patients", String.valueOf(count-scheJsonObject.getLong("patients")));
				}else{
					itemHashMap.put("patients", "0"+String.valueOf(count-scheJsonObject.getLong("patients")));
				}
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
		Collections.sort(data,mapComprator);
		return data;

	}



	class RadioHolder{
		private RadioButton radioButton;
		private TextView dateTextView, weekTextView, timeTextView, queueTextView;
		public RadioHolder(View view){
			this.radioButton = (RadioButton)view.findViewById(R.id.sche_radio_btn);
			this.dateTextView = (TextView)view.findViewById(R.id.date_tv);
			this.weekTextView = (TextView)view.findViewById(R.id.week_tv);
			this.timeTextView = (TextView)view.findViewById(R.id.time_tv);
			this.queueTextView = (TextView)view.findViewById(R.id.queue_tv);

		}
	}
	class RadioAdapter extends BaseAdapter{

		private Context context;
		List<HashMap<String,String>> data;
		public RadioAdapter(Context context,  List<HashMap<String,String>> data){
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			RadioHolder rhHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.schedule_list_item, null);
				rhHolder = new RadioHolder(convertView);
				convertView.setTag(rhHolder);
			}else{
				rhHolder = (RadioHolder)convertView.getTag();
			}
			rhHolder.radioButton.setChecked(map.get(position) == null ?false:true);
			rhHolder.dateTextView.setText(data.get(position).get("date"));
			rhHolder.weekTextView.setText(data.get(position).get("week"));
			rhHolder.timeTextView.setText(data.get(position).get("time"));
			rhHolder.queueTextView.setText("尚余"+data.get(position).get("patients")+"号");
			if(data.get(position).get("patients").equals("0")){
				rhHolder.radioButton.setVisibility(View.GONE);
			}
			return convertView;
		}

	}

	private void uploadSche(){
		okBtn = (Button)findViewById(R.id.ok_btn);
		cancelBtn = (Button)findViewById(R.id.cancel_btn);

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				httpUrl = http+postScheUrlString+scheIDString;
				Log.v(TAG, "scheId:"+scheIDString);
				HttpPost post = HttpUtil.getPost(httpUrl, null);
				joRev = HttpUtil.getString(post, 3);
				Log.v(TAG,"joRev:"+joRev.toString());
				Intent intent = new Intent(YuyueActivity.this, DingDanXiangQingActivity.class);
				intent.putExtra("yuyue", joRev.toString());
				intent.putExtra("scheId", scheIDString);
				intent.putExtra("selectedMap", selectedMap);
				startActivity(intent);
				YuyueActivity.this.finish();
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*
				httpUrl = http+"shlc/patient/medicalRecords/status/NOT_PAID";
				try {
					joRev = HttpUtil.getResultForHttpGet(httpUrl);
					Log.v(TAG,"Not-Paid-joRev:"+joRev.toString());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				// TODO Auto-generated method stub
				Intent intent = new Intent(YuyueActivity.this, MainActivity.class);
				startActivity(intent);
				YuyueActivity.this.finish();
			}
		});
	}
	//鏍规嵁鏃堕棿寰楀埌鏄熸湡
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
