package com.example.dingdan;

import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import com.example.patientclient01.HttpUtil;
import com.example.patientclient01.MyApp;
import com.example.patientclient01.R;
import com.qrcode.QRActivity;
import com.qrcode.QRMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DaiFuYaoFeiAdapter extends BaseAdapter{
	String TAG = "DaiFuYaoFeiAdapter";
	private String cancelDdUrlString = "shlc/patient/cancel/medicalRecord/";
	String fukuanUrlString = "shlc/patient/payment/medicalRecord/";
	private Context context;
	private String http, httpUrl;
	List<HashMap<String,String>> data;
	String scheduleId;
	String medicalRecordId;
	private JSONObject joRev;
	private MyApp myApp;
	public DaiFuYaoFeiAdapter(Context context,  List<HashMap<String,String>> data, String http, MyApp myApp, String medicalRecordId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.data = data;
		this.http = http;
		this.myApp = myApp;
		this.medicalRecordId = medicalRecordId;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.dd_dfyf_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
			viewHolder.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
			viewHolder.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
			viewHolder.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
			viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
			viewHolder.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
			viewHolder.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
			viewHolder.fuyaofeiButton = (Button)convertView.findViewById(R.id.fuyaofei_btn);
			viewHolder.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
			viewHolder.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
			convertView.setTag(viewHolder);

		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		Log.v("djyAdapter", data.toString());
		viewHolder.hospitalTextView.setText(data.get(position).get("hospital"));
		viewHolder.keshiTextView.setText(data.get(position).get("keshi"));
		viewHolder.zhichengTextView.setText(data.get(position).get("zhicheng"));
		viewHolder.yuyueTimeTextView.setText(data.get(position).get("time"));
		viewHolder.nameTextView.setText(data.get(position).get("name"));
		viewHolder.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
		viewHolder.createTimeTextView.setText(data.get(position).get("creatTime"));
		medicalRecordId = data.get(position).get("id");
		viewHolder.cancelDdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				httpUrl = http + cancelDdUrlString + medicalRecordId;
				Log.v(TAG, "httpUrl:"+httpUrl);
				HttpPost post = HttpUtil.getPost(httpUrl, null);
				joRev = HttpUtil.getString(post, 3);
				Log.v(TAG, "cancelDd");
				Intent intent = new Intent(context, DingDanQuXiao.class);
				context.startActivity(intent);
				((Activity)context).finish();
				notifyDataSetChanged();
			}
		});

		viewHolder.fuyaofeiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//待修改--跳转到医生扫码页面
				httpUrl = http + fukuanUrlString + medicalRecordId;
				Log.v(TAG, "httpUrl:"+httpUrl);
				HttpPost post = HttpUtil.getPost(httpUrl, null);
				joRev = HttpUtil.getString(post, 3);
				Log.v(TAG, "fuyaofei");
				Intent intent = new Intent();
				intent.setClass(context, QRActivity.class);
				intent.putExtra("medicalRecordId", medicalRecordId);
				myApp.setMedicalRecordId(medicalRecordId);
				context.startActivity(intent);
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	class ViewHolder{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button fuyaofeiButton, cancelDdButton;
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
