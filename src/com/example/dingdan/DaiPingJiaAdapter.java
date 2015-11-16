package com.example.dingdan;

import java.util.HashMap;
import java.util.List;

import com.example.patientclient01.PingJiaActivity;
import com.example.patientclient01.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DaiPingJiaAdapter extends BaseAdapter{
	private Context context;
	List<HashMap<String,String>> data;
	String medicalRecordId;
	public DaiPingJiaAdapter(Context context,  List<HashMap<String,String>> data) {
		// TODO Auto-generated constructor stub
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
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.dd_dpj_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
			viewHolder.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
			viewHolder.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
			viewHolder.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
			viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
			viewHolder.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
			viewHolder.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
			viewHolder.pingjiaButton = (Button)convertView.findViewById(R.id.pingjia_btn);
			viewHolder.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
			//viewHolder.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		Log.v("dpjAdapter", data.toString());
		viewHolder.hospitalTextView.setText(data.get(position).get("hospital"));
		viewHolder.keshiTextView.setText(data.get(position).get("keshi"));
		viewHolder.zhichengTextView.setText(data.get(position).get("zhicheng"));
		viewHolder.yuyueTimeTextView.setText(data.get(position).get("time"));
		viewHolder.nameTextView.setText(data.get(position).get("name"));
		viewHolder.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
		viewHolder.createTimeTextView.setText(data.get(position).get("creatTime"));
		medicalRecordId = data.get(position).get("id");
		viewHolder.pingjiaButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, PingJiaActivity.class);
				intent.putExtra("medicalRecordId", medicalRecordId);
				context.startActivity(intent);
				//((Activity)context).finish();
			}
		});
		return convertView;
	}

	class ViewHolder{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button pingjiaButton;
		/*
		public ViewHolder(View view){
			hospitalTextView = (TextView)view.findViewById(R.id.hospital_tv);
			keshiTextView = (TextView)view.findViewById(R.id.keshi_tv);
			zhichengTextView = (TextView)view.findViewById(R.id.zhicheng_tv);
			yuyueTimeTextView = (TextView)view.findViewById(R.id.yuyue_time_tv);
			nameTextView = (TextView)view.findViewById(R.id.name_tv);
			zhuanchangTextView = (TextView)view.findViewById(R.id.zhuanchang_tv);
			headImageView = (ImageView)view.findViewById(R.id.head_imageview);
			fukuanButton = (Button)view.findViewById(R.id.fukuan_btn);
			cancelDdButton = (Button)view.findViewById(R.id.cancel_dd_btn);
		}
		*/
		
	}
}
