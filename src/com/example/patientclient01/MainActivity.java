package com.example.patientclient01;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.dingdan.DingDanJinRi;

import android.os.Bundle;
import android.os.IBinder;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	private ActionBarDrawerToggle drawerbar;
	//实现右侧抽屉显示效果
	public DrawerLayout drawerLayout;
	private SlideFragment slideFragment ;
	private RelativeLayout rightRelativeLayout;

	private String readFileCache;
	private JSONObject joReadFileCache;
	private String[] arrProvinces, citiesSpinner, hospitalSpinner, nameProvinces, nameCities,
			nameHospitals, nameDepartments, namePros, nameDoctors, nameDoctorIDs,nameDepartmentTypes;
	private int[] idDepartments, idJobTitles,departmentTypeIdInts;
	private int idDepartmentInt, idJobTitlesInt,departmentTypeIdInt;
	private static String[][] arrCities;
	private static String[][][] arrHospitals;
	private static ArrayAdapter<String> shiAdapter, hospitalAdapter, departmentAdapter;
	private static JSONObject[] joProvinces;
	private static JSONObject[] joCities;
	private static JSONObject[] joHospitals, joDepartments, joPros, joDoctors,joDepartmentTypes;
	private static JSONObject joDepartmentType, joDoctor;
	private static JSONArray jaProvinces, jaCities, jaHospitals, jaDepartments, jaPros, jaDoctors, jaDoctorIDs, jaDepartmentTypes ;
	ProgressDialog pDialog;
	private Button yuyueButton, ksjyButton, wdddButton, grzxButton, jysmButton;

	private MyApp myApp;
	private SaveCache saveCache = new SaveCache(AppContext.get());
	String http, httpUrl, showStr, resultStr ;
	String getDoctorsUrl = "shlc/patient/doctors/department/";
	String getScheUrlString = "shlc/patient/schedules?doctorId=";
	String inprogressString = "&inprogress=false";
	String TAG = "MainActivity";
	JSONObject joRev;
	private QueueService.MyBinder myBinder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_main);

		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		String fileName = myApp.getJoStartPath();
		initData(fileName);



		initView("请点击医院进行选择");
		drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

		drawerLayout.setScrimColor(0x00000000);
		initEvent();
		//yuyue();
		ksjy();
		myYuyue();
		myGrzx();
		Intent startIntent = new Intent(this, QueueService.class);
		startService(startIntent);
	}
	public void showToast(String msg){
		//Looper.prepare();
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		// Looper.loop();
	}
	public void initData(String fileName){


		Log.v(TAG, "fileName:"+fileName);
		try {
			readFileCache = saveCache.read(fileName);
			joReadFileCache = new JSONObject(readFileCache);
			String result = joReadFileCache.getString("result");
			String resultMessage = joReadFileCache.getString("resultMessage");

			JSONObject joValue = joReadFileCache.getJSONObject("value");
			Log.v(TAG, "initData:"+joValue);
			jaProvinces = joValue.getJSONArray("provinces");
			joProvinces = new JSONObject[jaProvinces.length()];
			nameProvinces = new String[jaProvinces.length()];

			for(int i=0; i<jaProvinces.length(); i++){
				joProvinces[i] = jaProvinces.getJSONObject(i);
				nameProvinces[i] = joProvinces[i].getString("name");
				Log.v(TAG, "nameProvinces:"+nameProvinces[i]);
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
	//初始化视图--中间选择部分
	public void initView(String str){
		slideFragment = new SlideFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.main_content_frame_parent, slideFragment);
		fragmentTransaction.commitAllowingStateLoss();
		initRightLayout(str);
	}
	//打开右侧抽屉效果
	public void initRightLayout(String str){

		rightRelativeLayout = (RelativeLayout)findViewById(R.id.main_right_drawer_layout);
		rightRelativeLayout.removeAllViews();
		View view = getLayoutInflater().inflate(R.layout.right_drawer_layout, null);
		TextView testTextView = (TextView)view.findViewById(R.id.right_drawer_textView);

		ListView rightDrawerListView = (ListView)view.findViewById(R.id.right_drawer_listView);
		if(str.equals("地区")){
			Log.v(TAG, "地区");
			testTextView.setText("省份");
			/*
			rightDrawerListView.setAdapter(
					new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getShengData()));
					*/
			SimpleAdapter adapter = new SimpleAdapter(this, getShengData(),
					R.layout.right_drawer_list_item, new String[]{"name"},
					new int[]{R.id.item_tv});
			rightDrawerListView.setAdapter(adapter);
			rightDrawerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> shengParent, View shengView,
										int shengPosition, long shengId) {
					// TODO Auto-generated method stub
					myApp.setShengposition(shengPosition);
					String shengStr = shengParent.getItemAtPosition(shengPosition).toString();
					shengStr = shengStr.substring(6, shengStr.length()-1);
					Log.v(TAG, "shengStr:"+shengStr);
					Log.v(TAG, "name:"+nameProvinces[shengPosition]);
					if(shengStr.equals(nameProvinces[shengPosition])){
						if(shengStr.equals(myApp.getDiquString())){
							//return;
							myApp.setDiquString(shengStr);
						}else{
							myApp.setDiquString(shengStr);
							myApp.setKeshiString("科室");
							myApp.setYiyuanString("医院");
							myApp.setYishengString("医生");
							myApp.setCityString(null);
						}
						slideFragment.keshiTextView.setText(myApp.getKeshiString());
						slideFragment.yiyuanTextView.setText(myApp.getYiyuanString());
						slideFragment.yishengTextView.setText(myApp.getYishengString());
						slideFragment.diquTextView.setText(shengStr);

						Log.v(TAG, "position:"+shengPosition);
						for(int i=0; i<joProvinces.length; i++){
							try {

								if(shengStr.equals(joProvinces[i].getString("name"))){
									jaCities = joProvinces[i].getJSONArray("cities");
									joCities = new JSONObject[jaCities.length()];
									nameCities = new String[joCities.length];

									for(int j=0; j<jaCities.length(); j++){
										joCities[j] = jaCities.getJSONObject(j);
										nameCities[j] = joCities[j].getString("name");
										Log.v(TAG, "nameCity:"+nameCities[j]);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					initRightLayout("City");
					//openRightLayout();
				}
			});
		}else if(str.equals("City")){
			Log.v(TAG, "CITY");
			testTextView.setText("城市");
			SimpleAdapter adapter = new SimpleAdapter(this, getShiData(),
					R.layout.right_drawer_list_item, new String[]{"name"},
					new int[]{R.id.item_tv});
			rightDrawerListView.setAdapter(adapter);
			rightDrawerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> shiParent, View shiView,
										int shiPosition, long shiId) {
					// TODO Auto-generated method stub
					String shiStr = shiParent.getItemAtPosition(shiPosition).toString();
					Log.v(TAG, "shiStr:"+shiStr);
					shiStr = shiStr.substring(6, shiStr.length()-1);
					if(shiStr.equals(nameCities[shiPosition])){
						if(shiStr.equals(myApp.getCityString())){
							//	return;
							myApp.setCityString(shiStr);
						}else{
							myApp.setCityString(shiStr);
							myApp.setKeshiString("科室");
							myApp.setYiyuanString("医院");
							myApp.setYishengString("医生");
						}
						//myApp.setCityString(null);
						slideFragment.keshiTextView.setText(myApp.getKeshiString());
						slideFragment.yiyuanTextView.setText(myApp.getYiyuanString());
						slideFragment.yishengTextView.setText(myApp.getYishengString());
						//slideFragment.diquTextView.setText(shengStr);
						for(int i=0; i<joCities.length; i++){
							try {
								if(shiStr.equals(joCities[i].getString("name"))){
									jaHospitals = joCities[i].getJSONArray("hospitals");
									joHospitals = new JSONObject[jaHospitals.length()];
									nameHospitals = new String[joHospitals.length];
									for (int j=0; j<jaHospitals.length(); j++){
										joHospitals[j] = jaHospitals.getJSONObject(j);
										nameHospitals[j] = joHospitals[j].getString("name");
										Log.v(TAG, "nameHospitals:"+nameHospitals[j]);
									}

								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					initRightLayout("Yiyuan");
				}

			});
		}else if(str.equals("Yiyuan")){
			Log.v(TAG, "Yiyuan");
			testTextView.setText("医院");
			SimpleAdapter adapter = new SimpleAdapter(this, getYiyuanData(),
					R.layout.right_drawer_list_item, new String[]{"name"},
					new int[]{R.id.item_tv});
			rightDrawerListView.setAdapter(adapter);
			rightDrawerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> yiyuanParent, View yiyuanView,
										int yiyuanPosition, long yiyuanId) {
					// TODO Auto-generated method stub
					String yyStr = yiyuanParent.getItemAtPosition(yiyuanPosition).toString();
					yyStr = yyStr.substring(6, yyStr.length()-1);
					if(yyStr.equals(nameHospitals[yiyuanPosition])){
						if(yyStr.equals(myApp.getYiyuanString())){
							//	return;
							myApp.setYiyuanString(yyStr);
						}else{
							myApp.setYiyuanString(yyStr);
							myApp.setKeshiString("科室");
							myApp.setYishengString("医生");
						}


						slideFragment.keshiTextView.setText(myApp.getKeshiString());
						slideFragment.yishengTextView.setText(myApp.getYishengString());
						myApp.setYiyuanString(yyStr);
						slideFragment.yiyuanTextView.setText(yyStr);
						for(int i=0; i<joHospitals.length; i++){
							try {
								if(yyStr.equals(joHospitals[i].getString("name"))){
									jaDepartments = joHospitals[i].getJSONArray("departments");
									joDepartments = new JSONObject[jaDepartments.length()];
									nameDepartments = new String[joDepartments.length];
									idDepartments = new int[joDepartments.length];
									for(int j=0; j<jaDepartments.length(); j++){
										joDepartments[j] = jaDepartments.getJSONObject(j);
										nameDepartments[j] = joDepartments[j].getString("name");
										idDepartments[j] = joDepartments[j].getInt("id");
										Log.v(TAG, "nameDepartments:"+nameDepartments[j]);
									}

								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					initRightLayout("KeShi");
				}

			});
		}else if(str.equals("KeShi")){
			Log.v(TAG, "KeShi");
			testTextView.setText("科室");
			SimpleAdapter adapter = new SimpleAdapter(this, getKeShiData(),
					R.layout.right_drawer_list_item, new String[]{"name"},
					new int[]{R.id.item_tv});
			rightDrawerListView.setAdapter(adapter);
			rightDrawerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> keshiParent, View keshiView,
										int keshiPosition, long keshiId) {
					// TODO Auto-generated method stub
					String keshiStr = keshiParent.getItemAtPosition(keshiPosition).toString();
					Log.v(TAG, "keshiStr:"+keshiStr);
					keshiStr = keshiStr.substring(6, keshiStr.length()-1);
					Log.v(TAG, "keshiStr:"+keshiStr);
					if(keshiStr.equals(nameDepartments[keshiPosition])){
						if(keshiStr.equals(myApp.getKeshiString())){
							//	return;
							myApp.setKeshiString(keshiStr);
						}else{
							myApp.setKeshiString(keshiStr);
							myApp.setYishengString("医生");
						}
						Log.v(TAG, "keshiStr:"+keshiStr);
						myApp.setKeshiString(keshiStr);
						slideFragment.keshiTextView.setText(keshiStr);
						slideFragment.yishengTextView.setText(myApp.getYishengString());
						String departmentId = String.valueOf(idDepartments[keshiPosition]);
						httpUrl = http + getDoctorsUrl + departmentId;
						Log.v(TAG, "httpUrl:"+httpUrl);
						//HttpPost post = HttpUtil.getPost(httpUrl, null);

						try {
							joRev = HttpUtil.getResultForHttpGet(httpUrl);
							showStr = joRev.getString("result");
							resultStr = joRev.getString("resultMessage");
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
						if (showStr.equals("200")) {
							Log.d(TAG,  "resultStr:"+resultStr);
							Log.d(TAG, "joRev:"+joRev.toString());
							try {
								jaDoctors = joRev.getJSONArray("value");
								joDoctors = new JSONObject[jaDoctors.length()];
								nameDoctors = new String[jaDoctors.length()];
								nameDoctorIDs = new String[jaDoctors.length()];
								for(int i=0; i<jaDoctors.length(); i++){
									joDoctors[i] = jaDoctors.getJSONObject(i);
									nameDoctors[i] = joDoctors[i].getString("name");
									nameDoctorIDs[i] = joDoctors[i].getString("userId");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					initRightLayout("YiSheng");
				}

			});
		}else if(str.equals("YiSheng")){
			Log.v(TAG, "YiSheng");
			testTextView.setText("医生");
			SimpleAdapter adapter = new SimpleAdapter(this, getYiShengData(),
					R.layout.right_drawer_list_item, new String[]{"name"},
					new int[]{R.id.item_tv});
			rightDrawerListView.setAdapter(adapter);
			rightDrawerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> yishengParent, View yishengView,
										int yishengPosition, long yishengId) {
					// TODO Auto-generated method stub
					String yishengStr = yishengParent.getItemAtPosition(yishengPosition).toString();
					Log.v(TAG, "yishengStr:"+yishengStr);
					yishengStr = yishengStr.substring(6, yishengStr.length()-1);
					Log.v(TAG, "yishengStr:"+yishengStr);
					if(yishengStr.equals(nameDoctors[yishengPosition])){
						Log.v(TAG, "yishengStr:"+yishengStr);
						myApp.setYishengString(yishengStr);
						myApp.setDoctorIDString(nameDoctorIDs[yishengPosition]);
						joDoctor = joDoctors[yishengPosition];
						Log.v(TAG,"joDoctor:"+joDoctor);
						slideFragment.yishengTextView.setText(yishengStr);
						rightRelativeLayout.setVisibility(View.GONE);
						drawerLayout.closeDrawer(rightRelativeLayout);
					}
				}

			});
		}
		rightRelativeLayout.addView(view);
	}
	//切换右侧抽屉的显示/隐藏
	private void initEvent() {
		drawerbar = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_launcher, R.string.open, R.string.close) {
			@Override
			public void onDrawerOpened(View drawerView) {

				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {

				super.onDrawerClosed(drawerView);
			}
		};
		drawerLayout.setDrawerListener(drawerbar);
	}


	public void openRightLayout(String str) {
		initView(str);
		if (drawerLayout.isDrawerOpen(rightRelativeLayout)) {
			drawerLayout.closeDrawer(rightRelativeLayout);
		} else {
			drawerLayout.openDrawer(rightRelativeLayout);
		}
	}

	public void openRightLayout() {
		//initView(str);
		if (drawerLayout.isDrawerOpen(rightRelativeLayout)) {
			drawerLayout.closeDrawer(rightRelativeLayout);
		} else {
			drawerLayout.openDrawer(rightRelativeLayout);
		}
	}

	public List<HashMap<String,String>> getShengData(){
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		for(int i=0 ; i<nameProvinces.length; i++){
			HashMap<String, String> itemHashMap = new HashMap<String, String>();
			itemHashMap.put("name", nameProvinces[i]);
			data.add(itemHashMap);
		}
		return data;

	}

	public List<HashMap<String,String>> getShiData(){
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		for(int i=0 ; i<nameCities.length; i++){
			HashMap<String, String> itemHashMap = new HashMap<String, String>();
			itemHashMap.put("name", nameCities[i]);
			data.add(itemHashMap);
		}
		return data;

	}

	public List<HashMap<String,String>> getYiyuanData(){
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		if(nameHospitals == null){
			showToast("请按顺序选择后确定医生");

		}else {
			for(int i=0 ; i<nameHospitals.length; i++){
				HashMap<String, String> itemHashMap = new HashMap<String, String>();
				itemHashMap.put("name", nameHospitals[i]);
				data.add(itemHashMap);
			}
		}

		return data;

	}

	public List<HashMap<String,String>> getKeShiData(){
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		if(nameDepartments == null){
			showToast("请按顺序选择");
		}else{
			for(int i=0 ; i<nameDepartments.length; i++){
				HashMap<String, String> itemHashMap = new HashMap<String, String>();
				itemHashMap.put("name", nameDepartments[i]);
				//itemHashMap.put("id", String.valueOf(idDepartments[i]));
				data.add(itemHashMap);
			}
		}
		return data;

	}

	public List<HashMap<String,String>> getYiShengData(){
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		if(nameDoctors == null){
			showToast("请按顺序选择");
		}else{
			for(int i=0 ; i<nameDoctors.length; i++){
				HashMap<String, String> itemHashMap = new HashMap<String, String>();
				itemHashMap.put("name", nameDoctors[i]);
				data.add(itemHashMap);
			}
		}
		return data;

	}
	//预约医生
	public void yuyue(){
		//yuyueButton = (Button)findViewById(R.id.yuyue_btn);
		//slideFragment.yuyueButton.setOnClickListener(new OnClickListener() {

		//	@Override
		//	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String doctorId = myApp.getDoctorIDString();
		if(doctorId!=null){
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setTitle("查找中");
			pDialog.setMessage("请稍等......");
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.show();
			httpUrl = http+getScheUrlString+doctorId+inprogressString;
			//httpUrl = http+getScheUrlString+doctorId;
			Log.v(TAG, "getSche:"+httpUrl);
			try {
				joRev = HttpUtil.getResultForHttpGet(httpUrl);
				Log.v(TAG, "get doctor:"+joRev.toString());
				pDialog.dismiss();

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent(MainActivity.this, YuYueGuanLiActivity.class);
			myApp.setJoDoctor(joDoctor);
			//intent.putExtra("doctorInfo", joDoctor.toString());
			//intent.putExtra("scheInfo", joRev.toString());
			startActivity(intent);
			//MainActivity.this.finish();
		}else{
			//pDialog.dismiss();
			Toast.makeText(getApplicationContext(), "请先确定要挂号的医生", Toast.LENGTH_SHORT).show();

		}

		//	}
		//});
	}
	//快速就医
	public void ksjy(){
		ksjyButton = (Button)findViewById(R.id.ksjy_btn);
		ksjyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setTitle("查找中");
				pDialog.setMessage("请稍等......");
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();
				/*
				Intent intent = new Intent(MainActivity.this, KsxzActivity.class);
				intent.putExtra("type", 1);
				startActivity(intent);
				*/
				Intent intent = new Intent(MainActivity.this, CSXZActitivy.class);
				pDialog.dismiss();
				startActivity(intent);

				//MainActivity.this.finish();
			}
		});
	}
	//我的订单
	public void myYuyue(){
		wdddButton = (Button)findViewById(R.id.wddd_btn);
		wdddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stubg
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setTitle("查找中");
				pDialog.setMessage("请稍等......");
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();
				Intent intent = new Intent(MainActivity.this, DingDanGuanLiActivity.class);
				pDialog.dismiss();
				startActivity(intent);

			}
		});
	}
	//个人中心
	public void myGrzx(){
		grzxButton = (Button)findViewById(R.id.grzx_btn);
		grzxButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setTitle("打开个人中心中");
				pDialog.setMessage("请稍等......");
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();
				Intent  intent = new Intent(MainActivity.this, GeRenZhongXinActivity.class);
				pDialog.dismiss();
				startActivity(intent);

			}
		});
	}
	//就医扫码
	public void myJysm(){
		Intent intent = new Intent(MainActivity.this, SaoMaGuanLiActivity.class);
		startActivity(intent);
	}
	//今日预约
	public void myJinRi(){
		Intent intent = new Intent(MainActivity.this, DingDanJinRi.class);
		startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (QueueService.MyBinder) service;
			myBinder.startDownload();
		}
	};
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
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.stopService(new Intent(this, QueueService.class));
	}
}
