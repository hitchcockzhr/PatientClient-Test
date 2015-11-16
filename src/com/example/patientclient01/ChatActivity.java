package com.example.patientclient01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;







import com.example.patientclient01.RecordButton.OnFinishedRecordListener;
import com.example.picture.PhotoUtils;
import com.example.picture.Picture;
import com.example.picture.PictureActivity;
import com.example.picture.SelectPictureActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity{
	String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private String TAG = "ChatActivity";
	ArrayList<HashMap<String,Object>> chatList=null;
	String[] from={"image","text","filepath"};
	int[][] to={{R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_imageview_me, R.id.chatlist_voice_me},
			{R.id.chatlist_image_other,R.id.chatlist_text_other,R.id.chatlist_imageview_other, R.id.chatlist_voice_other}};
	int[][] layout={{R.layout.chat_listitem_me,R.layout.chat_listpic_me,R.layout.chat_listvoice_me},
			{R.layout.chat_listitem_other,R.layout.chat_listpic_other, R.layout.chat_listvoice_other}};
	String userQQ=null;
	//对方聊天
	public final static int OTHER=1;
	//我方聊天
	public final static int ME=0;
	//文本聊天
	public final static int TEXT = 0;
	//图片聊天
	public final static int IMAGE = 1;
	//语音聊天
	public final static int VOICE = 2;
	//语音按钮
	RecordButton voiceButton;
	boolean voiceFlag = false;
	//protected ListView chatListView=null;
	//下拉刷新
	protected PullToRefreshListView chatPullToRefreshListView;
	//发送文本     聊天界面更多功能键
	protected Button chatSendButton=null,chatFunctionBtn;
	//发送图片
	protected ImageButton chatSendImageButton = null;
	//添加图片
	protected ImageButton chatAddImageButton = null;
	//看化验 包括：患处、处方、化验 按钮
	protected Button huanchuButton, chufangButton, huayanButton;
	String TYPE_ONE = "?type=SORE", TYPE_TWO="?type=INSPECTION", TYPE_THREE = "?type=PRESCRIPTION";
	//聊天文本编辑框
	protected EditText editText=null;
	protected MyChatAdapter adapter=null;
	Handler mHandler;
	private Bitmap bitmap;
	private int id;
	//最后一次更新时间
	private long lastModified;
	private MyApp myApp;
	private String http;
	//消息url 语音和图片都从此url取得
	private String messageUrl = "shlc/patient/messages/medicalRecord/";
	//文本消息url
	private String textMessageUrl = "shlc/patient/message/content/";
	//发送消息url
	private String postMessageUrl = "shlc/patient/message/medicalRecord/";
	//取得最新的20条消息url
	private String getLast20MessagesUrl = "shlc/patient/messages/medicalRecord/";
	//指定messageId 
	private String messageIdUrl = "?messageId=";
	//获取病情描述
	private String getMedicalRecordDescriptionString = "shlc/patient/medicalRecord/";
	private static String httpUrl;
	private String formats = "yyyy-MM-dd HH:mm:ss";
	private JSONArray jaMessages;
	private JSONObject[] joMessages;
	private static boolean messageGet = false;
	private String name="医生", patientId="ming";
	private int medicalRecordId;
	private View chatLayout;
	private TextView chatname_tv;
	//聊天界面更多功能layout
	private LinearLayout addLayout;
	int messageId;
	private ArrayList<String>selectedPicture = new ArrayList<String>();
	private static final int REQUEST_PICK = 0;
	private int picKind;
	private static final String IMAGE_FILE_NAME = "tempImage.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_chat);
		myApp = (MyApp)getApplication();
		http = myApp.getHttp();
		//语音路径
		path += "/mmmm.amr";
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(100 * 1024 * 1024)
				.diskCacheFileCount(300).tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
		//获取当前聊天的医疗id
		medicalRecordId = Integer.parseInt(myApp.getMedicalRecordId());
		addLayout = (LinearLayout) findViewById(R.id.chat_function_linear);
		loadChat( name, medicalRecordId);
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.chat_layout);
		rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				addLayout.setVisibility(View.GONE);
			}
		});
		/*
		try {
			getLast20Messages(1);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	//获取最近的20条消息
	private JSONObject[] getLast20Messages(int messageId) throws ClientProtocolException, IOException{


		JSONObject joRev = new JSONObject();
		joRev = HttpUtil.getResultForHttpGet(http+getLast20MessagesUrl+medicalRecordId+messageIdUrl+messageId);
		Log.v(TAG, "last20:"+joRev.toString());
		try {
			String resultStr = joRev.getString("result");
			if(resultStr.equals("200")){
				jaMessages = joRev.getJSONArray("value");
				joMessages = new JSONObject[jaMessages.length()];
				for(int i=0; i<jaMessages.length(); i++){
					joMessages[i] = jaMessages.getJSONObject(i);
					String timeStr = TimeStamp2Date(joMessages[i].getLong("lastModified"), formats);
					Log.v(TAG, "timeStr:"+timeStr);

				}
				//return joMessages;
				Log.v(TAG, "joMessages:"+String.valueOf(joMessages.length));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return joMessages;
	}
	//获取消息
	private JSONObject[] getMessage(int medicalRecordId){


		JSONObject joRev = new JSONObject();
		try {
			Log.v(TAG, "url="+ http+messageUrl+medicalRecordId);
			joRev = HttpUtil.getResultForHttpGet(http+messageUrl+medicalRecordId);

			Log.v(TAG, "joRevMessage:"+joRev.toString());
			String resultStr = joRev.getString("result");
			if(resultStr.equals("200")){
				jaMessages = joRev.getJSONArray("value");
				joMessages = new JSONObject[jaMessages.length()];
				for(int i=0; i<jaMessages.length(); i++){
					joMessages[i] = jaMessages.getJSONObject(i);
					String timeStr = TimeStamp2Date(joMessages[i].getLong("lastModified"), formats);
					Log.v(TAG, "timeStr:"+timeStr);

				}
				//return joMessages;
				Log.v(TAG, "joMessages:"+String.valueOf(joMessages.length));
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
		return joMessages;
	}
	private String getTextMessage(int id){


		String revStr = null;
		try {
			//JSONObject joRev = HttpUtil.getResultForHttpGet(http+messageUrl+"?patientId=ming&messageId=3");
			//String revStr = HttpUtil.getTextMessageForHttpGet(http+textMessageUrl+"3");
			revStr = HttpUtil.getTextMessageForHttpGet(http+textMessageUrl+id);
			Log.v(TAG, "joRevTextMessage:"+revStr);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return revStr;

	}
	private Bitmap getImageMessage(int id){

		http = myApp.getHttp();
		Bitmap revBitmap = null;
		try {
			//JSONObject joRev = HttpUtil.getResultForHttpGet(http+messageUrl+"?patientId=ming&messageId=3");
			//String revStr = HttpUtil.getTextMessageForHttpGet(http+textMessageUrl+"3");
			revBitmap = HttpUtil.getImageMessageForHttpGet(http+textMessageUrl+id);
			Log.v(TAG, "joRevImageMessage:"+revBitmap.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return revBitmap;
	}
	//显示聊天图片
	private void showBitmap(String filepath){
		Bitmap bitmap = BitmapFactory.decodeFile(filepath);
		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.show_bitmap,
				(ViewGroup) findViewById(R.id.layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.imageView);
		image.setImageBitmap(bitmap);
		image.setAdjustViewBounds(true);
		imageDialog.setView(layout);
		imageDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});


		imageDialog.create();
		imageDialog.show();
	}
	//将新的消息加入到聊天界面中
	protected void addTextToList(String text,int type, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("contentType", type);
		map.put("image", who==ME?R.drawable.contact_1:R.drawable.contact_0);
		map.put("text", text);
		chatList.add(map);
		//chatPullToRefreshListView.getRefreshableView().setSelection(chatList.size()-1);

	}
	//将新的消息加入到聊天界面中
	protected void addImageToList(String filepath, int type, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("contentType", type);
		map.put("image", who==ME?R.drawable.contact_1:R.drawable.contact_0);
		map.put("text", filepath);
		chatList.add(map);
		//chatPullToRefreshListView.getRefreshableView().setSelection(chatList.size()-1);
	}
	//将新的消息加入到聊天界面中
	protected void addVoiceToList(String filepath, int type, int who) throws IOException{
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("contentType", type);
		map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
		map.put("text", getAmrDuration(new File(filepath)));
		map.put("filepath", filepath);
		chatList.add(map);
	}
	//将旧的消息加入到聊天界面中
	protected void addOldTextToList(String text,int type, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("contentType", type);
		map.put("image", who==ME?R.drawable.contact_1:R.drawable.contact_0);
		map.put("text", text);
		chatList.add(0,map);
		//chatPullToRefreshListView.getRefreshableView().setSelection(chatList.size()-1);

	}
	//将旧的消息加入到聊天界面中
	protected void addOldImageToList(String filepath, int type, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("contentType", type);
		map.put("image", who==ME?R.drawable.contact_1:R.drawable.contact_0);
		map.put("text", filepath);
		chatList.add(0,map);
		//chatPullToRefreshListView.getRefreshableView().setSelection(chatList.size()-1);
	}
	//将旧的消息加入到聊天界面中
	protected void addOldVoiceToList(String filepath, int type, int who) throws IOException{
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("contentType", type);
		map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
		map.put("text", getAmrDuration(new File(filepath)));
		map.put("filepath", filepath);
		chatList.add(0,map);
	}
	//将旧的消息加入到聊天界面中
	private void addOldContentToList(JSONObject[] joMessages){
		try {
			if(messageId == 0 && joMessages.length>0){
				messageId = joMessages[0].getInt("id");
			}else if(joMessages.length>0){
				messageId = joMessages[0].getInt("id")<messageId?joMessages[0].getInt("id"):messageId;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=joMessages.length-1; i>-1; i--){
			try {
				int id = joMessages[i].getInt("id");
				String srcType = joMessages[i].getString("srcType");
				String destType = joMessages[i].getString("destType");
				String src = joMessages[i].getString("src");
				String dest = joMessages[i].getString("dest");
				String lastModified = TimeStamp2Date(joMessages[i].getLong("lastModified"), formats);
				String contentType = joMessages[i].getString("contentType");
				int who = 0;
				if(srcType.equals("PATIENT_TYPE")){
					who = ME;
				}else if(srcType.equals("DOCTOR_TYPE")){
					who = OTHER;
				}
				int type = 0;
				if(contentType.equals("text/plain")){
					String textMessage = getTextMessage(id);
					type = TEXT;
					addOldTextToList(textMessage, type,who);
				}else if(contentType.equals("image/jpg")){
					Bitmap imageMessage = getImageMessage(id);
					String filepath = String.valueOf(id)+".jpg";
					if(HttpUtil.saveBitmap2file(imageMessage, filepath)){
						type = IMAGE;
						addOldImageToList(filepath, type, who);
					}
				}else if(contentType.equals("audio/amr")){
					String fileName = Environment.getExternalStorageDirectory()
							.getAbsolutePath()+"/"+id+".amr";
					HttpUtil.getAudioMessageForHttpGet(http+textMessageUrl+id, fileName);
					type = VOICE;
					addOldVoiceToList(fileName, type, who);
				}

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

		}
	}
	//将新的消息加入到聊天界面中
	private void addContentToList(JSONObject[] joMessages){
		try {
			if(messageId == 0){
				messageId = joMessages[0].getInt("id");
			}else if(joMessages.length>0){
				messageId = joMessages[0].getInt("id")<messageId?joMessages[0].getInt("id"):messageId;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=0; i<joMessages.length; i++){
			try {
				int id = joMessages[i].getInt("id");
				String srcType = joMessages[i].getString("srcType");
				String destType = joMessages[i].getString("destType");
				String src = joMessages[i].getString("src");
				String dest = joMessages[i].getString("dest");
				String lastModified = TimeStamp2Date(joMessages[i].getLong("lastModified"), formats);
				String contentType = joMessages[i].getString("contentType");
				int who = 0;
				if(srcType.equals("PATIENT_TYPE")){
					who = ME;
				}else if(srcType.equals("DOCTOR_TYPE")){
					who = OTHER;
				}
				int type = 0;
				if(contentType.equals("text/plain")){
					String textMessage = getTextMessage(id);
					type = TEXT;
					addTextToList(textMessage, type,who);
				}else if(contentType.equals("image/jpg")){
					Bitmap imageMessage = getImageMessage(id);
					String filepath = String.valueOf(id)+".jpg";
					if(HttpUtil.saveBitmap2file(imageMessage, filepath)){
						type = IMAGE;
						addImageToList(filepath, type, who);
					}
				}else if(contentType.equals("audio/amr")){
					String fileName = Environment.getExternalStorageDirectory()
							.getAbsolutePath()+"/"+id+".amr";
					HttpUtil.getAudioMessageForHttpGet(http+textMessageUrl+id, fileName);
					type = VOICE;
					addVoiceToList(fileName, type, who);
				}

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

		}
	}

	private Bitmap zoomBitmap(String filepath){
		Bitmap bitMap = BitmapFactory.decodeFile(filepath);
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		// 设置想要的大小
		int newWidth = 60;
		int newHeight = 80;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix,true);
		return bitMap;
	}

	private class MyChatAdapter extends BaseAdapter{

		Context context=null;
		ArrayList<HashMap<String,Object>> chatList=null;
		int[][] layout;
		String[] from;
		int[][] to;
		ViewHolder holder=null;


		public MyChatAdapter(Context context,
							 ArrayList<HashMap<String, Object>> chatList, int[][] layout,
							 String[] from, int[][] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			public ImageView imageViewHead=null;
			public TextView textView=null;
			public ImageView imageViewContent=null;

		}
		public void refresh( ArrayList<HashMap<String, Object>> chatList)   {
			this.chatList = chatList;
			notifyDataSetChanged();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			//ViewHolder holder=null;
			int who=(Integer)chatList.get(position).get("person");
			int type = (Integer)chatList.get(position).get("contentType");
			if(type != 2){
				convertView= LayoutInflater.from(context).inflate(layout[who==ME?0:1][type==TEXT?0:1], null);
			}else if(type == 2){
				convertView= LayoutInflater.from(context).inflate(layout[who==ME?0:1][2],null);
			}
			holder=new ViewHolder();
			holder.imageViewHead=(ImageView)convertView.findViewById(to[who][0]);

			if(type == 0){
				holder.textView=(TextView)convertView.findViewById(to[who][1]);
			}else if(type == 1){
				holder.imageViewContent=(ImageView)convertView.findViewById(to[who][2]);
			}else if(type == 2){
				holder.textView=(TextView)convertView.findViewById(to[who][3]);
			}
			if(holder.textView!=null && type == 0){

				holder.textView.setText(chatList.get(position).get("text").toString());
			}else if(holder.textView != null && type == 2){
				int time = Integer.parseInt(chatList.get(position).get("text").toString())/1000;
				holder.textView.setText(time +"s 语音");
				final String filepath = chatList.get(position).get("filepath").toString();
				holder.textView.setClickable(true);
				holder.textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						playMusic(filepath);
					}
				});
			}
			if(holder.imageViewContent!=null){
				//holder.imageViewContent.setImageBitmap((Bitmap) chatList.get(position).get(from[1]));
				//holder.imageViewContent.setImageBitmap(getImageMessage(1));
				String filename = chatList.get(position).get("text").toString();
				final String filepath = Environment.getExternalStorageDirectory()+"/" +filename;
				Log.v(TAG, "filepath:"+filepath);
				Bitmap smallBitmap = zoomBitmap(filepath);
				holder.imageViewContent.setImageBitmap(smallBitmap);

				holder.imageViewContent.setOnClickListener(new ImageViewListenser(position));

			}
			return convertView;
		}

		class ImageViewListenser implements OnClickListener{
			private int position;
			public ImageViewListenser(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				//int vid = view.getId();
				//Log.v(TAG, "click id1:"+String.valueOf(vid));
				//Log.v(TAG, "click id2:"+String.valueOf(holder.imageViewContent.getId()));
				//if(vid == holder.imageViewContent.getId()){
				String filename = chatList.get(position).get("text").toString();

				final String filepath = Environment.getExternalStorageDirectory()+"/" +filename;
				Log.v(TAG, "filepath:"+filepath);
				showBitmap(filepath);
				//}
			}

		}

	}
	public void initChatList(int medicalRecordId){
		httpUrl = http+getMedicalRecordDescriptionString+medicalRecordId;
		JSONObject joRev = new JSONObject();
		try {
			joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG, "description:"+joRev.toString());
			String description = joRev.getJSONObject("value").getString("description");
			addTextToList(description, TEXT, ME);
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

		//addTextToList(text, type, who)
	}

	public void loadChat( String name,  final int medicalRecordId){
		chatLayout = (RelativeLayout)findViewById(R.id.chat_layout);
		chatList=new ArrayList<HashMap<String,Object>>();
		initChatList(medicalRecordId);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 1){
					adapter.refresh(chatList);
					//chatListView.setSelection(chatList.size()-1);
				}
//	              mAdapter.notifyDataSetChanged();  
			}
		};
		chatSendButton=(Button)chatLayout.findViewById(R.id.chat_bottom_sendbutton);
		chatSendImageButton = (ImageButton)chatLayout.findViewById(R.id.chat_bottom_look);
		chatAddImageButton = (ImageButton)chatLayout.findViewById(R.id.chat_bottom_add);
		editText=(EditText)chatLayout.findViewById(R.id.chat_bottom_edittext);
		chatPullToRefreshListView=(PullToRefreshListView)chatLayout.findViewById(R.id.chat_list);
		chatname_tv = (TextView)chatLayout.findViewById(R.id.chat_contact_name);
		chatname_tv.setText("与"+name+"诊疗中");
		voiceButton = (RecordButton)chatLayout.findViewById(R.id.chat_voice_btn);
		chatFunctionBtn = (Button)chatLayout.findViewById(R.id.chat_bottom_function_btn);
		//聊天功能显示
		chatFunctionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!voiceFlag){
					voiceButton.setVisibility(View.VISIBLE);
					editText.setVisibility(View.GONE);
					chatSendButton.setVisibility(View.GONE);
					voiceFlag = true;
				}else{
					voiceButton.setVisibility(View.GONE);
					editText.setVisibility(View.VISIBLE);
					chatSendButton.setVisibility(View.VISIBLE);
					voiceFlag = false;
				}
			}
		});

		adapter=new MyChatAdapter(this,chatList,layout,from,to);
		final String PostId = patientId;
		new MessageThread().start();

		chatSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String myWord=null;

				/**
				 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
				 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
				 * ，并且不能发送空消息。
				 */

				myWord=(editText.getText()+"").toString();
				if(myWord.length()==0)
					return;

				JSONObject joRev = HttpUtil.postString(myWord, http+postMessageUrl+medicalRecordId);
				Log.v(TAG, "post text:" +http+postMessageUrl+medicalRecordId);
				Log.v(TAG, "POST joRev:"+joRev.toString());
				try {
					messageId = joRev.getJSONObject("value").getInt("id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				editText.setText("");
				addTextToList(myWord,TEXT, ME);
				/**
				 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
				 */
				adapter.notifyDataSetChanged();
				//chatPullToRefreshListView.getRefreshableView().setSelection(chatList.size()-1);

			}
		});
		chatSendImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				picKind = 0;
				startActivityForResult(new Intent(ChatActivity.this, SelectPictureActivity.class), REQUEST_PICK);
					/*
					int subActivity = 10;
					Intent intent = new Intent(ChatActivity.this, PictureActivity.class);
					intent.putExtra("id", "5");
					intent.putExtra("medicalRecordId", medicalRecordId);
					startActivityForResult(intent, subActivity);
					//startActivity(intent);
					 * */

			}
		});
		voiceButton.setSavePath(path);
		voiceButton.setOnFinishedRecordListener(new OnFinishedRecordListener() {

			@Override
			public void onFinishedRecord(String audioPath) {
				// TODO Auto-generated method stub
				Log.i("RECORD!!!", "finished!!!!!!!!!! save to "
						+ audioPath);
				File file = new File(path);
				httpUrl = http + postMessageUrl + medicalRecordId;
				Log.v(TAG, "httpUrl:"+httpUrl);
				//file = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
				JSONObject joRev = HttpUtil.postAudio(file, httpUrl);
				Log.v(TAG, "audio rev:"+joRev.toString());
				try {
					if(joRev.getString("result").equals("200")){
						JSONObject[] joAudioRevs = new JSONObject[1];
						joAudioRevs[0]= joRev.getJSONObject("value");
						addContentToList(joAudioRevs);
						Toast.makeText(ChatActivity.this, "语音已上传成功！",Toast.LENGTH_LONG).show();
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		chatAddImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addLayout.setVisibility(View.VISIBLE);
			}
		});
		chatPullToRefreshListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				addLayout.setVisibility(View.GONE);
				return false;
			}
		});
		huanchuButton = (Button)chatLayout.findViewById(R.id.huanchu_sendbutton);
		chufangButton = (Button)chatLayout.findViewById(R.id.chufang_sendbutton);
		huayanButton = (Button)chatLayout.findViewById(R.id.huayan_sendbutton);
		huanchuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				picKind = 1;
				startActivityForResult(new Intent(ChatActivity.this, SelectPictureActivity.class), REQUEST_PICK);
			}
		});
		chufangButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				picKind = 3;
				startActivityForResult(new Intent(ChatActivity.this, SelectPictureActivity.class), REQUEST_PICK);
			}
		});
		huayanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				picKind = 2;
				startActivityForResult(new Intent(ChatActivity.this, SelectPictureActivity.class), REQUEST_PICK);
			}
		});

		chatPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(ChatActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new GetDataTask().execute();
			}
		});
		chatPullToRefreshListView.setAdapter(adapter);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String> {

		//后台处理部分
		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			//String str="Added after refresh...I add";
			JSONObject[] joLast20 = null;
			try {
				joLast20 = getLast20Messages(messageId);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray jaLast20 = new JSONArray();
			for(int i=0; i<joLast20.length; i++){
				jaLast20.put(joLast20[i]);
			}
			return jaLast20.toString();
		}

		//这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
		//根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
		@Override
		protected void onPostExecute(String result) {
			//在头部增加新添内容
			try {
				JSONArray jaLast20 = new JSONArray(result);
				JSONObject[] jObjects = new JSONObject[jaLast20.length()];
				for(int i=0; i<jaLast20.length(); i++){
					jObjects[i] = jaLast20.getJSONObject(i);
				}
				addOldContentToList(jObjects);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//mListItems.addFirst(result);

			//通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
			adapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			chatPullToRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}






	//时间戳和日期转换

	public String TimeStamp2Date(String timestampString, String formats){
		Long timestamp = Long.parseLong(timestampString)*1000;
		String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
		return date;
	}
	public String TimeStamp2Date(Long timestamp, String formats){
		//Long timestamp = Long.parseLong(timestampString)*1000;
		String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
		return date;
	}
	//刷新消息线程
	private class MessageThread extends Thread{
		public void run(){
			while(true){
				//刷新间隔3000ms
				try {
					int i = 3000;
					Thread.sleep(i);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//功能实现

				joMessages = getMessage(medicalRecordId);
				Message message = new Message();
				message.what = 1;
				if(joMessages!=null&&joMessages.length != 0){
					Log.v(TAG, "MT1:"+messageGet);
					addContentToList(joMessages);
					messageGet = true;
					message.what = 1;
					Log.v(TAG, "MT2:"+messageGet);
					mHandler.sendMessage(message);
				}


			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			selectedPicture = (ArrayList<String>) data
					.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
			Log.v(TAG, "huanchuSelectedPicture:"+selectedPicture.toString());
			if(selectedPicture.size()>0){
				for(int i=0; i<selectedPicture.size(); i++){
					Bitmap bitmap = PhotoUtils.getimage(selectedPicture.get(i));
					File file = null;
					JSONObject joRev = new JSONObject();
					if(PhotoUtils.saveBitmap2file(bitmap)){
						switch (picKind) {
							case 0:
								httpUrl = http + postMessageUrl + medicalRecordId;
								Log.v(TAG, "httpUrl:"+httpUrl);
								file = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
								joRev = HttpUtil.postImage(file, httpUrl);
								Log.v(TAG, "image rev:"+joRev.toString());
								try {
									if(joRev.getString("result").equals("200")){
										JSONObject[] joImageRevs = new JSONObject[1];
										joImageRevs[0]= joRev.getJSONObject("value");
										addContentToList(joImageRevs);
										Toast.makeText(this, "聊天图片已上传成功！",Toast.LENGTH_LONG).show();
										adapter.notifyDataSetChanged();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 1:
								httpUrl = http + postMessageUrl + medicalRecordId+TYPE_ONE;
								Log.v(TAG, "httpUrl:"+httpUrl);
								file = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
								joRev = HttpUtil.postImage(file, httpUrl);
								Log.v(TAG, "image rev:"+joRev.toString());
								try {
									if(joRev.getString("result").equals("200")){
										Toast.makeText(this, "患处图片已上传成功！",Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 2:
								httpUrl = http + postMessageUrl + medicalRecordId+TYPE_TWO;
								Log.v(TAG, "httpUrl:"+httpUrl);
								file = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
								joRev = HttpUtil.postImage(file, httpUrl);
								Log.v(TAG, "image rev:"+joRev.toString());
								try {
									if(joRev.getString("result").equals("200")){
										Toast.makeText(this, "化验图片已上传成功！",Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 3:
								httpUrl = http + postMessageUrl + medicalRecordId+TYPE_THREE;
								Log.v(TAG, "httpUrl:"+httpUrl);
								file = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
								joRev = HttpUtil.postImage(file, httpUrl);
								Log.v(TAG, "image rev:"+joRev.toString());
								try {
									if(joRev.getString("result").equals("200")){
										Toast.makeText(this, "诊断图片已上传成功！",Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							default:
								break;
						}
					}
				}
			}
		}
	}
	/**
	 * 得到amr的时长
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getAmrDuration(File file) throws IOException {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();//文件的长度
			int pos = 6;//设置初始位置
			int frameCount = 0;//初始帧数
			int packedPos = -1;
			/////////////////////////////////////////////////////
			byte[] datas = new byte[1];//初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			/////////////////////////////////////////////////////
			duration += frameCount * 20;//帧数*20
		} finally {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		}
		return duration;
	}
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

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
