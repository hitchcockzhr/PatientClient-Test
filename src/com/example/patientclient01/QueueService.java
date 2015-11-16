package com.example.patientclient01;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
//后台服务
public class QueueService extends Service{

	public static final String TAG = "QueueService";
	MyApp myApp;
	String http,httpUrl;
	private String formats = "yyyy-MM-dd HH:mm:ss";
	String getStatusUrl = "shlc/patient/medicalRecord/";
	String getDaiKaiShiString = "shlc/patient/medicalRecords/status/NOT_STARTED";
	String getWaitString = "shlc/patient/medicalRecords/status/WAITING_FOR_PATIENT";
	String getInProgressString = "shlc/patient/medicalRecords/status/IN_PROGRESS";
	String getDocStatus = "shlc/patient/inactiveTime/";
	static String[] medicalRecordIds;
	static String medicalRecordId;
	String intervalMedcalRecordId;
	private MyBinder mBinder = new MyBinder();
	Handler mHandler;
	Dialog dialog;
	Button dialogGetInButton;
	String jinruUrlString = "shlc/patient/enter/medicalRecord/";
	Boolean flag = true;
	Context context;
	ArrayList<String> dingdanList;
	JSONObject joRev;
	ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
	private List<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> data2 = new ArrayList<HashMap<String,String>>();
	JSONArray scheduleJsonArray;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate() executed");
		myApp = (MyApp) getApplication();
		http = myApp.getHttp();
		context = this;
		dingdanList = new ArrayList<String>();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		loadMedicalRecordIds();
		selectJinRi();
	}
	//获取医疗订单id
	public void loadMedicalRecordIds(){
		//待开始
		httpUrl = http+getDaiKaiShiString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Not-Started-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			for(int i = 0; i < scheduleJsonArray.length(); i++){
				JSONObject jObject = scheduleJsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(jObject.get("id")));
				long createTime = jObject.getLong("createTime");
				String createTimeString = TimeStamp.TimeStamp2Date(createTime, formats);
				Log.v(TAG,"createTimeString："+createTimeString);
				map.put("creatTime", createTimeString);
				JSONObject scheduleJsonObject = jObject.getJSONObject("schedule");
				map.put("time", scheduleJsonObject.getString("startTime"));
				map.put("type", "1");
				Log.v(TAG, "daikaishi-map:"+map.toString());
				data.add(map);
			}
			//myApp.setDataDKS(data);
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
		//进行中
		httpUrl = http+getInProgressString;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"In_Progress-joRev:"+joRev.toString());
			scheduleJsonArray = joRev.getJSONArray("value");
			for(int i = 0; i < scheduleJsonArray.length(); i++){
				JSONObject jObject = scheduleJsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(jObject.get("id")));
				long createTime = jObject.getLong("createTime");
				String createTimeString = TimeStamp.TimeStamp2Date(createTime, formats);
				Log.v(TAG,"createTimeString："+createTimeString);
				map.put("creatTime", createTimeString);
				JSONObject scheduleJsonObject = jObject.getJSONObject("schedule");
				map.put("time", scheduleJsonObject.getString("startTime"));
				map.put("type", "4");
				Log.v(TAG, "In-Progress-map:"+map.toString());
				data.add(map);
			}
			//myApp.setDataDKS(data);
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
	//今日订单
	public void selectJinRi(){
		Log.v(TAG, "JinRi data:"+data.toString());
		for(int i=0; i<data.size(); i++){
			HashMap<String, String> map = new HashMap<String, String>();
			map = data.get(i);
			String id = map.get("id");
			String scheduleTime = map.get("time").substring(0, 10);
			String nowTime = getSystemTime();
			Log.v(TAG, "time:"+scheduleTime+" "+nowTime);
			if(scheduleTime.equals(nowTime)){
				data2.add(map);
				dingdanList.add(id);
			}
		}
	}
	public String getSystemTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = formatter.format(curDate);
		return str;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() executed");

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开始执行后台任务
				while(flag){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(dingdanList.size()>0){
						getDoctorStatus(dingdanList);
					}
					initData();
					//遍历今日订单，获取订单状态
					if(medicalRecordIds!=null && medicalRecordIds.length>0){
						for(int i=0; i<medicalRecordIds.length; i++){
							Log.v(TAG, "medicalRecordIds[i]"+medicalRecordIds[i]);
							getStatus(medicalRecordIds[i]);
						}
					}else{continue;}
				}
			}
		}).start();
		mHandler = new Handler() {
			@SuppressWarnings("deprecation")
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						httpUrl = http + jinruUrlString + medicalRecordId;
						Log.v(TAG, "httpUrl:"+httpUrl);
						HttpPost post = HttpUtil.getPost(httpUrl, null);
						JSONObject joRev = HttpUtil.getString(post,3);
						// = HttpUtil.getResultForHttpGet(httpUrl);

						Log.v(TAG, "jinru:"+joRev.toString());
						try {
							if(joRev.getString("result").equals("200")){
								Log.v(TAG, "进入诊室！");
							/*
							Notification notification = new Notification(R.drawable.ic_launcher,  
					                "你的诊室已开，请进入就诊", System.currentTimeMillis());  
					        Intent notificationIntent = new Intent(getApplication(), ChatActivity.class);  
					        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0,  
					                notificationIntent, 0);  
					        notification.setLatestEventInfo(getApplication(), "诊室已开", "请进入就诊",  
					                pendingIntent);  
					        startForeground(1, notification); 
					        */
								NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(getApplication());
								notifyBuilder.setContentTitle("诊室已开");
								notifyBuilder.setContentText("你的诊室已开，请进入就诊");
								notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
								notifyBuilder.setAutoCancel(true);
								notifyBuilder.setOngoing(false);
								Intent notificationIntent = new Intent(getApplication(), ChatActivity.class);
								PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0,
										notificationIntent, 0);
								notifyBuilder.setContentIntent(pendingIntent);
								NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								mNotificationManager.notify(0, notifyBuilder.build());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//Toast.makeText(getActivity(), "患者"+patient_name+"已进入诊室!",
						//		Toast.LENGTH_LONG).show();
						//getInDialog();
						//dialog.show();

						break;
					case 2:
						flag = false;
						final AlertDialog.Builder builder = new AlertDialog.Builder(context);
						// 设置显示信息
						builder.setMessage("您的帐号已在其他设备上登录，如此操作不是您本人进行的，请修改您的密码!").
								// 设置确定按钮
										setPositiveButton("确定", new DialogInterface.OnClickListener() {
									// 单击事件
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent();
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.setClass(QueueService.this, LoginActivity.class);
										startActivity(intent);


									}
								});
						// 创建对话框
						AlertDialog ad = builder.create();
						// 显示对话框
						ad.setCanceledOnTouchOutside(false);
						ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						ad.show();
						break;

				}
				super.handleMessage(msg);
			}
		};
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() executed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	class MyBinder extends Binder {

		public void startDownload() {
			Log.d("TAG", "startDownload() executed");
			// 执行具体的下载任务
		}

	}
	//初始化数据
	private void initData(){
		httpUrl = http+getWaitString;

		JSONObject joRev;
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG,"Wait-joRev:"+joRev.toString());
			if(joRev.getString("result").equals("401")){
				Message message = new Message();
				message.what = 2;
				mHandler.sendMessage(message);
				return;
			}


			JSONArray scheduleJsonArray = joRev.getJSONArray("value");
			if(scheduleJsonArray.length()>0){
				medicalRecordIds = new String[scheduleJsonArray.length()];
				for(int i = 0; i < scheduleJsonArray.length(); i++){
					JSONObject jObject = scheduleJsonArray.getJSONObject(i);
					medicalRecordIds[i] = String.valueOf(jObject.get("id"));
				}
			}else {
				return ;
			}
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
	//获取订单状态
	void getStatus(String medicalRecordId){
		httpUrl = http+getStatusUrl+medicalRecordId;
		Log.v(TAG, "getStatusUrl:"+httpUrl);

		JSONObject joGet = new JSONObject();
		try {
			joGet = HttpUtil.getResultForHttpGet(httpUrl);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v(TAG, "joGet:"+joGet.toString());
		try {
			String status = joGet.getJSONObject("value").getString("status");
			if(status.equals("WAITING_FOR_PATIENT")){
				Log.v(TAG, "status:"+status);
				Message message = new Message();
				message.what = 1;
				this.medicalRecordId = medicalRecordId;
				myApp.setMedicalRecordId(medicalRecordId);
				flag = false;
				mHandler.sendMessage(message);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//进入诊室
	void getInDialog(){
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.get_in_dialog);
		dialogGetInButton = (Button)dialog.findViewById(R.id.get_in_btn);
		dialogGetInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getInProgress(medicalRecordId);
				dialog.dismiss();
			}
		});

	}
	//进入诊室
	void getInProgress(String medicalRecordId){
		httpUrl = http + jinruUrlString + medicalRecordId;
		Log.v(TAG, "httpUrl:"+httpUrl);
		HttpPost post = HttpUtil.getPost(httpUrl, null);
		JSONObject joRev = HttpUtil.getString(post,3);
		// = HttpUtil.getResultForHttpGet(httpUrl);

		Log.v(TAG, "jinru:"+joRev.toString());
		try {
			if(joRev.getString("result").equals("200")){
				Log.v(TAG, "进入诊室！");
				Intent intent = new Intent();
				intent.setClass(QueueService.this, ChatActivity.class);
				this.startActivity(intent);
				//this.finish();

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//获取医生状态
	void getDoctorStatus(ArrayList<String> dingdanArrayList){
		for(int i=0; i<dingdanArrayList.size(); i++){
			httpUrl = http+getDocStatus+dingdanArrayList.get(i);
			long intervalTime = 0;
			try {
				JSONObject joGet = HttpUtil.getResultForHttpGet(httpUrl);
				Log.v(TAG, "joGetDocStatus:"+joGet.toString());
				intervalTime = joGet.getLong("value");
				if(intervalTime > 10*1000){
					intervalMedcalRecordId = dingdanArrayList.get(i);
					flag = false;
				}
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
}
