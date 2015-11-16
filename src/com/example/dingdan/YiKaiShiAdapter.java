package com.example.dingdan;

import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

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

import com.example.dingdan.QuanBuAdapter.ViewHolderDFK;
import com.example.dingdan.QuanBuAdapter.ViewHolderDFYF;
import com.example.dingdan.QuanBuAdapter.ViewHolderDJY;
import com.example.dingdan.QuanBuAdapter.ViewHolderDKS;
import com.example.dingdan.QuanBuAdapter.ViewHolderDPJ;
import com.example.dingdan.QuanBuAdapter.ViewHolderDQY;
import com.example.dingdan.QuanBuAdapter.ViewHolderYKS;
import com.example.dingdan.QuanBuAdapter.ViewHolderYWC;
import com.example.patientclient01.ChatActivity;
import com.example.patientclient01.HttpUtil;
import com.example.patientclient01.MyApp;
import com.example.patientclient01.QueueActivity;
import com.example.patientclient01.R;
import com.example.zhifu.FukuanSuccessActivity;
import com.qrcode.QRActivity;
import com.qrcode.QRMainActivity;

public class YiKaiShiAdapter extends BaseAdapter{
	String TAG = "YiKaiShiAdapter";
	private Context context;
	List<HashMap<String,String>> data;
	private MyApp myApp;
	private String http, httpUrl;;
	String jinruUrlString = "shlc/patient/medicalRecord/";
	private String cancelDdUrlString = "shlc/patient/cancel/medicalRecord/";
	
	String medicalRecordId;
	private JSONObject joRev;
	public YiKaiShiAdapter(Context context,  List<HashMap<String,String>> data, String http, MyApp myApp) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.data = data;
		this.http = http;
		this.myApp = myApp;
		
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
		HashMap<String, String> map = data.get(position);
		
		ViewHolderYKS viewHolderYKS = null;
		
		if(convertView == null){
			
			convertView = LayoutInflater.from(context).inflate(R.layout.dd_yks_list_item, null);
			viewHolderYKS = new ViewHolderYKS();
			viewHolderYKS.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
			viewHolderYKS.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
			viewHolderYKS.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
			viewHolderYKS.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
			viewHolderYKS.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
			viewHolderYKS.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
			viewHolderYKS.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
			viewHolderYKS.jinruButton = (Button)convertView.findViewById(R.id.jinru_btn);
			viewHolderYKS.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
			viewHolderYKS.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
			viewHolderYKS.createTimeTextView.setText(data.get(position).get("creatTime"));
			Log.v("dksAdapter", data.toString());
			viewHolderYKS.hospitalTextView.setText(data.get(position).get("hospital"));
			viewHolderYKS.keshiTextView.setText(data.get(position).get("keshi"));
			viewHolderYKS.zhichengTextView.setText(data.get(position).get("zhicheng"));
			viewHolderYKS.yuyueTimeTextView.setText(data.get(position).get("time"));
			viewHolderYKS.nameTextView.setText(data.get(position).get("name"));
			viewHolderYKS.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
			medicalRecordId = data.get(position).get("id");
			viewHolderYKS.cancelDdButton.setOnClickListener(new OnClickListener() {
				
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
			
			viewHolderYKS.jinruButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(context, ChatActivity.class);
					intent.putExtra("medicalRecordId", medicalRecordId);
					myApp.setMedicalRecordId(medicalRecordId);
					myApp.setJinRu(true);
					context.startActivity(intent);
					notifyDataSetChanged();
				}
			});
			convertView.setTag(viewHolderYKS);
				
		}else{
			
			viewHolderYKS = (ViewHolderYKS) convertView.getTag();
			viewHolderYKS.hospitalTextView.setText(data.get(position).get("hospital"));
			viewHolderYKS.keshiTextView.setText(data.get(position).get("keshi"));
			viewHolderYKS.zhichengTextView.setText(data.get(position).get("zhicheng"));
			viewHolderYKS.yuyueTimeTextView.setText(data.get(position).get("time"));
			viewHolderYKS.createTimeTextView.setText(data.get(position).get("creatTime"));
			viewHolderYKS.nameTextView.setText(data.get(position).get("name"));
			viewHolderYKS.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
				
			
		}
		
		return convertView;
	}

	
	class ViewHolderYKS{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button jinruButton, cancelDdButton;
	}
	
	
}

