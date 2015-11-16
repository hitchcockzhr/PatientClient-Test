package com.example.patientclient01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class KsxzActivity extends Activity{
	String TAG = "KsxzActivity";
	MyApp myApp;
	private String readFileCache;
	private JSONObject joReadFileCache;
	private SaveCache saveCache = new SaveCache(AppContext.get());
	private JSONArray jaDepartmentTypes;
	private JSONObject[] joDepartmentTypes;
	private int[] departmentTypeIdInts;
	private String[] nameDepartmentTypes;
	ListView departmentTypeListView;
	int type;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_ksjy_list);
		Intent intent = getIntent();
		type = intent.getIntExtra("type", 0);
		Log.v(TAG, "ksxz type:"+type);
		myApp = (MyApp)getApplication();
		initData();
		initView();
	}
	public void initData(){
		String fileName = myApp.getJoStartPath();

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
			*/
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

	private void initView(){
		departmentTypeListView = (ListView)findViewById(R.id.keshi_listview);
		SimpleAdapter adapter = new SimpleAdapter(this, getKeshiData(),R.layout.department_type_list_item,
				new String[]{"departmentTypeName"}, new int[]{R.id.department_type_tv});
		departmentTypeListView.setAdapter(adapter);
		departmentTypeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> keshiParent, View keshiView, int keshiPosition,
									long keshiId) {
				// TODO Auto-generated method stub
				String departmentString = keshiParent.getItemAtPosition(keshiPosition).toString();
				departmentString = departmentString.substring(departmentString.length()-3,departmentString.length()-1);
				Log.v(TAG, "departmentString:"+departmentString);
				if(departmentString.equals(nameDepartmentTypes[keshiPosition])){
					int id = departmentTypeIdInts[keshiPosition];
					Log.v(TAG, "departmentID:"+String.valueOf(id));
					if(type == 1){
						Intent intent = new Intent(KsxzActivity.this, YsxzActivity.class);
						intent.putExtra("departmentID", id);
						startActivity(intent);
					}else if(type == 0){
						myApp.setDepartmentId(id);
						Intent intent = new Intent(KsxzActivity.this, KSJYActivity.class);
						intent.putExtra("departmentID", id);
						startActivity(intent);
					}
				}
			}
		});

	}
	public List<HashMap<String,String>> getKeshiData() {
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		for(int i=0; i<departmentTypeIdInts.length;i++){
			HashMap<String, String> itemHashMap = new HashMap<String, String>();
			itemHashMap.put("departmentTypeName", nameDepartmentTypes[i]);
			data.add(itemHashMap);
		}
		return data;

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
