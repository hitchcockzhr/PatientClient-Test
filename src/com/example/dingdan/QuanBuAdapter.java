package com.example.dingdan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import com.example.dingdan.DaiKaiShiAdapter.ViewHolder;
import com.example.patientclient01.ChatActivity;
import com.example.patientclient01.HttpUtil;
import com.example.patientclient01.MyApp;
import com.example.patientclient01.QueueActivity;
import com.example.patientclient01.R;
import com.example.zhifu.FukuanSuccessActivity;
import com.qrcode.QRActivity;
import com.qrcode.QRMainActivity;

import android.R.integer;
import android.R.raw;
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

public class QuanBuAdapter extends BaseAdapter{
	String TAG = "QuanBuAdapter";
	private Context context;
	List<HashMap<String,String>> data;
	private MyApp myApp;
	private String http, httpUrl;
	private String cancelDdUrlString = "shlc/patient/cancel/medicalRecord/";
	private String getDaiFuKuanString = "shlc/patient/medicalRecords/status/NOT_PAID";
	String fukuanUrlString = "shlc/patient/payment/medicalRecord/";
	String jinruUrlString = "shlc/patient/medicalRecord/";
	String scheduleId;
	String medicalRecordId;
	private JSONObject joRev;

	private final int TYPE_ONE = 0, TYPE_TWO=1, TYPE_THREE=2, TYPE_FOUR=3, TYPE_FIVE=4, TYPE_SIX=5,TYPE_SEVEN=6,TYPE_EIGHT=7,TYPE_COUNT=8;
	public QuanBuAdapter(Context context,  List<HashMap<String,String>> data, String http, MyApp myApp, String medicalRecordId) {
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
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_COUNT;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	@Override
	public int getItemViewType(int position) {
		HashMap<String, String> map = data.get(position);
		int type = Integer.parseInt(map.get("type"));
		//Log.e("TYPE:", "" + type);
		return type;

	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HashMap<String, String> map = data.get(position);
		int type = getItemViewType(position);
		ViewHolderDFK viewHolderDFK = null;
		ViewHolderDPJ viewHolderDPJ = null;
		ViewHolderYWC viewHolderYWC = null;
		ViewHolderDKS viewHolderDKS = null;
		ViewHolderYKS viewHolderYKS = null;
		ViewHolderDJY viewHolderDJY = null;
		ViewHolderDFYF viewHolderDFYF = null;
		ViewHolderDQY viewHolderDQY = null;
		if(convertView == null){
			switch (type) {
				case TYPE_ONE:
					viewHolderDFK = new ViewHolderDFK();
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_dfk_list_item, null);
					viewHolderDFK.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderDFK.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderDFK.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderDFK.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderDFK.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderDFK.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDFK.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderDFK.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderDFK.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderDFK.fukuanButton = (Button)convertView.findViewById(R.id.fukuan_btn);
					viewHolderDFK.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
					Log.v("QuanBuAdapter-DFK", data.toString());
					viewHolderDFK.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDFK.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDFK.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDFK.yuyueTimeTextView.setText(data.get(position).get("time"));

					viewHolderDFK.nameTextView.setText(data.get(position).get("name"));
					viewHolderDFK.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					scheduleId = data.get(position).get("id");
					viewHolderDFK.cancelDdButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + cancelDdUrlString + scheduleId;
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

					viewHolderDFK.fukuanButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + fukuanUrlString + scheduleId;
							Log.v(TAG, "httpUrl:"+httpUrl);
							HttpPost post = HttpUtil.getPost(httpUrl, null);
							joRev = HttpUtil.getString(post, 3);
							Log.v(TAG, "fukuan");
							Intent intent = new Intent(context, FukuanSuccessActivity.class);
							context.startActivity(intent);
							((Activity)context).finish();
							//initData();
							notifyDataSetChanged();
						}
					});
					convertView.setTag(viewHolderDFK);
					break;
				case TYPE_TWO:
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_dks_list_item, null);
					viewHolderDKS = new ViewHolderDKS();
					viewHolderDKS.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderDKS.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderDKS.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderDKS.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderDKS.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderDKS.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderDKS.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderDKS.jinruButton = (Button)convertView.findViewById(R.id.jinru_btn);
					viewHolderDKS.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
					viewHolderDKS.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderDKS.createTimeTextView.setText(data.get(position).get("creatTime"));
					Log.v("dksAdapter", data.toString());
					viewHolderDKS.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDKS.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDKS.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDKS.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDKS.nameTextView.setText(data.get(position).get("name"));
					viewHolderDKS.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					scheduleId = data.get(position).get("id");
					viewHolderDKS.cancelDdButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + cancelDdUrlString + scheduleId;
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

					viewHolderDKS.jinruButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(context, QueueActivity.class);
							intent.putExtra("medicalRecordId", scheduleId);
							context.startActivity(intent);
							notifyDataSetChanged();
						}
					});
					convertView.setTag(viewHolderDKS);
					break;
				case TYPE_THREE:
					viewHolderDPJ = new ViewHolderDPJ();
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_dpj_list_item, null);
					viewHolderDPJ.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderDPJ.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderDPJ.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderDPJ.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderDPJ.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderDPJ.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderDPJ.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderDPJ.pingjiaButton = (Button)convertView.findViewById(R.id.pingjia_btn);
					viewHolderDPJ.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderDPJ.createTimeTextView.setText(data.get(position).get("creatTime"));
					Log.v("QuanBuAdapter-DPJ", data.toString());
					viewHolderDPJ.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDPJ.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDPJ.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDPJ.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDPJ.nameTextView.setText(data.get(position).get("name"));
					viewHolderDPJ.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));

					convertView.setTag(viewHolderDPJ);
					break;
				case TYPE_FOUR:
					viewHolderYWC = new ViewHolderYWC();
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_ywc_list_item, null);
					viewHolderYWC.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderYWC.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderYWC.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderYWC.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderYWC.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderYWC.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderYWC.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderYWC.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderYWC.createTimeTextView.setText(data.get(position).get("creatTime"));
					Log.v("QuanBuAdapter-YWC", data.toString());
					viewHolderYWC.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderYWC.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderYWC.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderYWC.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderYWC.nameTextView.setText(data.get(position).get("name"));
					viewHolderYWC.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));

					convertView.setTag(viewHolderYWC);
					break;
				case TYPE_FIVE:
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
					scheduleId = data.get(position).get("id");
					viewHolderYKS.cancelDdButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + cancelDdUrlString + scheduleId;
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
							intent.putExtra("medicalRecordId", scheduleId);
							myApp.setMedicalRecordId(scheduleId);
							context.startActivity(intent);
							notifyDataSetChanged();
						}
					});
					convertView.setTag(viewHolderYKS);
					break;
				case TYPE_SIX:
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_djy_list_item, null);
					viewHolderDJY = new ViewHolderDJY();
					viewHolderDJY.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderDJY.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderDJY.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderDJY.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderDJY.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderDJY.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderDJY.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderDJY.jiuyiButton = (Button)convertView.findViewById(R.id.jiuyi_btn);
					viewHolderDJY.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
					viewHolderDJY.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderDJY.createTimeTextView.setText(data.get(position).get("creatTime"));
					Log.v("djyAdapter", data.toString());
					viewHolderDJY.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDJY.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDJY.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDJY.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDJY.nameTextView.setText(data.get(position).get("name"));
					viewHolderDJY.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					scheduleId = data.get(position).get("id");
					viewHolderDJY.cancelDdButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + cancelDdUrlString + scheduleId;
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

					viewHolderDJY.jiuyiButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							//待修改--跳转到医生扫码页面
							Intent intent = new Intent();
							intent.setClass(context, QRActivity.class);
							intent.putExtra("medicalRecordId", scheduleId);
							myApp.setMedicalRecordId(scheduleId);
							context.startActivity(intent);
							notifyDataSetChanged();
						}
					});
					convertView.setTag(viewHolderDJY);
					break;
				case TYPE_SEVEN:
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_dfyf_list_item, null);
					viewHolderDFYF = new ViewHolderDFYF();
					viewHolderDFYF.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderDFYF.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderDFYF.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderDFYF.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderDFYF.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderDFYF.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderDFYF.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderDFYF.fuyaofeiButton = (Button)convertView.findViewById(R.id.fuyaofei_btn);
					viewHolderDFYF.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
					viewHolderDFYF.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderDFYF.createTimeTextView.setText(data.get(position).get("creatTime"));
					Log.v("dfyfAdapter", data.toString());
					viewHolderDFYF.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDFYF.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDFYF.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDFYF.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDFYF.nameTextView.setText(data.get(position).get("name"));
					viewHolderDFYF.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					scheduleId = data.get(position).get("id");
					viewHolderDFYF.cancelDdButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + cancelDdUrlString + scheduleId;
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

					viewHolderDFYF.fuyaofeiButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							//待修改--跳转到付药款页面
							httpUrl = http + fukuanUrlString + scheduleId;
							Log.v(TAG, "httpUrl:"+httpUrl);
							HttpPost post = HttpUtil.getPost(httpUrl, null);
							joRev = HttpUtil.getString(post, 3);
							Log.v(TAG, "fuyaofei");
							Intent intent = new Intent(context, FukuanSuccessActivity.class);
							context.startActivity(intent);
							((Activity)context).finish();
							//initData();
							notifyDataSetChanged();
						}
					});
					convertView.setTag(viewHolderDFYF);
					break;
				case TYPE_EIGHT:
					convertView = LayoutInflater.from(context).inflate(R.layout.dd_dqy_list_item, null);
					viewHolderDQY = new ViewHolderDQY();
					viewHolderDQY.hospitalTextView = (TextView) convertView.findViewById(R.id.hospital_tv);
					viewHolderDQY.keshiTextView = (TextView)convertView.findViewById(R.id.keshi_tv);
					viewHolderDQY.zhichengTextView = (TextView)convertView.findViewById(R.id.zhicheng_tv);
					viewHolderDQY.yuyueTimeTextView = (TextView)convertView.findViewById(R.id.yuyue_time_tv);
					viewHolderDQY.nameTextView = (TextView)convertView.findViewById(R.id.name_tv);
					viewHolderDQY.zhuanchangTextView = (TextView)convertView.findViewById(R.id.zhuanchang_tv);
					viewHolderDQY.headImageView = (ImageView)convertView.findViewById(R.id.head_imageview);
					viewHolderDQY.quyaoButton = (Button)convertView.findViewById(R.id.quyao_btn);
					viewHolderDQY.cancelDdButton = (Button)convertView.findViewById(R.id.cancel_dd_btn);
					viewHolderDQY.createTimeTextView = (TextView)convertView.findViewById(R.id.order_time_tv);
					viewHolderDQY.createTimeTextView.setText(data.get(position).get("creatTime"));
					Log.v("dqyAdapter", data.toString());
					viewHolderDQY.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDQY.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDQY.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDQY.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDQY.nameTextView.setText(data.get(position).get("name"));
					viewHolderDQY.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					scheduleId = data.get(position).get("id");
					viewHolderDQY.cancelDdButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub

							httpUrl = http + cancelDdUrlString + scheduleId;
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

					viewHolderDQY.quyaoButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							//待修改--跳转到取药扫码页面
							Intent intent = new Intent();
							intent.setClass(context, QRMainActivity.class);
							intent.putExtra("medicalRecordId", scheduleId);
							myApp.setMedicalRecordId(scheduleId);
							context.startActivity(intent);
							notifyDataSetChanged();
						}
					});
					convertView.setTag(viewHolderDQY);
					break;
				default:
					break;
			}
		}else{
			switch (type) {
				case TYPE_ONE:
					viewHolderDFK = (ViewHolderDFK) convertView.getTag();
					viewHolderDFK.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDFK.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDFK.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDFK.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDFK.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDFK.nameTextView.setText(data.get(position).get("name"));
					viewHolderDFK.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_TWO:
					viewHolderDKS = (ViewHolderDKS) convertView.getTag();
					viewHolderDKS.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDKS.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDKS.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDKS.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDKS.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDKS.nameTextView.setText(data.get(position).get("name"));
					viewHolderDKS.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_THREE:
					viewHolderDPJ = (ViewHolderDPJ) convertView.getTag();
					viewHolderDPJ.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDPJ.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDPJ.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDPJ.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDPJ.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDPJ.nameTextView.setText(data.get(position).get("name"));
					viewHolderDPJ.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_FOUR:
					viewHolderYWC = (ViewHolderYWC) convertView.getTag();
					viewHolderYWC.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderYWC.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderYWC.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderYWC.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderYWC.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderYWC.nameTextView.setText(data.get(position).get("name"));
					viewHolderYWC.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_FIVE:
					viewHolderYKS = (ViewHolderYKS) convertView.getTag();
					viewHolderYKS.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderYKS.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderYKS.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderYKS.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderYKS.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderYKS.nameTextView.setText(data.get(position).get("name"));
					viewHolderYKS.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_SIX:
					viewHolderDJY = (ViewHolderDJY) convertView.getTag();
					viewHolderDJY.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDJY.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDJY.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDJY.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDJY.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDJY.nameTextView.setText(data.get(position).get("name"));
					viewHolderDJY.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_SEVEN:
					viewHolderDFYF = (ViewHolderDFYF) convertView.getTag();
					viewHolderDFYF.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDFYF.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDFYF.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDFYF.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDFYF.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDFYF.nameTextView.setText(data.get(position).get("name"));
					viewHolderDFYF.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				case TYPE_EIGHT:
					viewHolderDQY = (ViewHolderDQY) convertView.getTag();
					viewHolderDQY.hospitalTextView.setText(data.get(position).get("hospital"));
					viewHolderDQY.keshiTextView.setText(data.get(position).get("keshi"));
					viewHolderDQY.zhichengTextView.setText(data.get(position).get("zhicheng"));
					viewHolderDQY.yuyueTimeTextView.setText(data.get(position).get("time"));
					viewHolderDQY.createTimeTextView.setText(data.get(position).get("creatTime"));
					viewHolderDQY.nameTextView.setText(data.get(position).get("name"));
					viewHolderDQY.zhuanchangTextView.setText(data.get(position).get("zhuanchang"));
					break;
				default:
					break;
			}
		}



		return convertView;
	}

	class ViewHolderDFK{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		TextView createTimeTextView;
		ImageView headImageView;
		Button fukuanButton, cancelDdButton;
	}
	class ViewHolderDPJ{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button pingjiaButton;
	}
	class ViewHolderYWC{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		TextView createTimeTextView;
		ImageView headImageView;
	}
	class ViewHolderDKS{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button jinruButton, cancelDdButton;
	}
	class ViewHolderYKS{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button jinruButton, cancelDdButton;
	}
	class ViewHolderDJY{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button jiuyiButton, cancelDdButton;
	}
	class ViewHolderDFYF{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button fuyaofeiButton, cancelDdButton;
	}
	class ViewHolderDQY{
		TextView hospitalTextView, keshiTextView, zhichengTextView;
		TextView yuyueTimeTextView, nameTextView, zhuanchangTextView;
		ImageView headImageView;
		TextView createTimeTextView;
		Button quyaoButton, cancelDdButton;
	}

}
