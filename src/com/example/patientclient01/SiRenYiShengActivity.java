package com.example.patientclient01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;







import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class SiRenYiShengActivity extends Activity{
	String TAG = "SiRenYiShengActivity";
	ListView jiageListView, fuwuListView;
	private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_syrs);
		initView();
	}
	private void initView(){
		jiageListView = (ListView)findViewById(R.id.srys_jg_listview);
		jiageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		final JiageRadioAdapter jiageRadioAdapter = new JiageRadioAdapter(this , getJiageData());
		jiageListView.setAdapter(jiageRadioAdapter);

	}
	public List<HashMap<String,String>> getJiageData(){
		List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
		for(int i=0 ; i<2; i++){
			HashMap<String, String> itemHashMap = new HashMap<String, String>();
			if(i==0){
				itemHashMap.put("zhouqi", "一周");
				itemHashMap.put("jiage", "50元");
				itemHashMap.put("radio", "select");
			}else if(i==1){
				itemHashMap.put("zhouqi", "一月");
				itemHashMap.put("jiage", "150元");
				itemHashMap.put("radio", "select");
			}
			data.add(itemHashMap);
		}
		Log.v(TAG, "jiage List:"+data.toString());
		return data;
	}
	class JiageRadioAdapter extends BaseAdapter{

		private Context context;
		List<HashMap<String,String>> data;
		public JiageRadioAdapter(Context context,  List<HashMap<String,String>> data){
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
			JiageRadioHolder rhHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.srys_jg_item, null);
				rhHolder = new JiageRadioHolder(convertView);
				convertView.setTag(rhHolder);
			}else{
				rhHolder = (JiageRadioHolder)convertView.getTag();
			}
			rhHolder.radioButton.setChecked(map.get(position) == null ?false:true);
			rhHolder.zhouqiTextView.setText(data.get(position).get("zhouqi"));
			rhHolder.jiageTextView.setText(data.get(position).get("jiage"));

			return convertView;
		}

	}
	class JiageRadioHolder{
		private RadioButton radioButton;
		private TextView zhouqiTextView, jiageTextView;
		public JiageRadioHolder(View view){
			this.radioButton = (RadioButton)view.findViewById(R.id.srys_jg_radio);
			this.zhouqiTextView = (TextView)view.findViewById(R.id.srys_zq_tv);
			this.jiageTextView = (TextView)view.findViewById(R.id.srys_jg_tv);


		}
	}

}
