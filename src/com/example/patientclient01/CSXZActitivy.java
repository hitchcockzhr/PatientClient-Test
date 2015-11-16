package com.example.patientclient01;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CSXZActitivy extends Activity{
	String TAG = "CSXZActitivy";
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
	private MyApp myApp;
	private SaveCache saveCache = new SaveCache(AppContext.get());
	ExpandableListView mView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_csxz);
		myApp = (MyApp)getApplication();
		String fileName = myApp.getJoStartPath();
		initData(fileName);
		initView();
	}


	public void initData(String fileName){


		Log.v(TAG, "fileName:"+fileName);
		try {
			readFileCache = saveCache.read(fileName);
			joReadFileCache = new JSONObject(readFileCache);
			String result = joReadFileCache.getString("result");
			String resultMessage = joReadFileCache.getString("resultMessage");

			JSONObject joValue = joReadFileCache.getJSONObject("value");

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
				for(int j=0; j<jaCities.length(); j++){
					joCities[j] = jaCities.getJSONObject(j);
					arrCities[i][j] = joCities[j].getString("name");
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

	private void initView(){
		mView = (ExpandableListView) findViewById(R.id.csxz_list);
		final MyAdapter adapter = new MyAdapter();
		mView.setAdapter(adapter);
		mView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				String cityString = (String) adapter.getChild(groupPosition, childPosition);
				Log.v(TAG, "cityString:"+cityString);
				myApp.setCityString(cityString);
				Intent intent = new Intent(CSXZActitivy.this, KsxzActivity.class);

				startActivity(intent);
				return true;
			}
		});
	}
	class MyAdapter extends BaseExpandableListAdapter {

		//设置组视图的图片
//       int[] logos = new int[] { R.drawable.wei, R.drawable.shu,R.drawable.wu};  
		//设置组视图的显示文字
		//private String[] generalsTypes = new String[] { "魏", "蜀", "吴" };
		private String[] generalsTypes = nameProvinces;

		//子视图显示文字
		/*
       private String[][] generals = new String[][] {  
               { "夏侯惇", "甄姬", "许褚", "郭嘉", "司马懿", "杨修" },  
               { "马超", "张飞", "刘备", "诸葛亮", "黄月英", "赵云" },  
               { "吕蒙", "陆逊", "孙权", "周瑜", "孙尚香" }  
               }; 
 		*/
		private String[][] generals = arrCities;


		//子视图图片
//       public int[][] generallogos = new int[][] {  
//               { R.drawable.xiahoudun, R.drawable.zhenji,  
//                       R.drawable.xuchu, R.drawable.guojia,  
//                       R.drawable.simayi, R.drawable.yangxiu },  
//               { R.drawable.machao, R.drawable.zhangfei,  
//                       R.drawable.liubei, R.drawable.zhugeliang,  
//                       R.drawable.huangyueying, R.drawable.zhaoyun },  
//               { R.drawable.lvmeng, R.drawable.luxun, R.drawable.sunquan,  
//                       R.drawable.zhouyu, R.drawable.sunshangxiang } };  

		//自己定义一个获得textview的方法
		TextView getTextView() {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 100);
			TextView textView = new TextView(CSXZActitivy.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL);
			textView.setPadding(36, 0, 0, 0);
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			return textView;
		}


		@Override
		public int getGroupCount() {
			return generalsTypes.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return generals[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return generalsTypes[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return generals[groupPosition][childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			LinearLayout ll = new LinearLayout(CSXZActitivy.this);
			ll.setOrientation(0);
//            ImageView logo = new ImageView(ExpandableList.this);  
//            logo.setImageResource(logos[groupPosition]);  
//            logo.setPadding(50, 0, 0, 0);  
//            ll.addView(logo);  
			TextView textView = getTextView();
			textView.setTextColor(Color.BLUE);
			textView.setText(getGroup(groupPosition).toString());
			ll.addView(textView);
			ll.setPadding(100, 10, 10, 10);
			return ll;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			LinearLayout ll = new LinearLayout(CSXZActitivy.this);
			ll.setOrientation(0);
//            ImageView generallogo = new ImageView(TestExpandableListView.this);  
//            generallogo.setImageResource(generallogos[groupPosition][childPosition]);  
//            ll.addView(generallogo);  
			TextView textView = getTextView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			ll.addView(textView);
			return ll;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}




	}
}
