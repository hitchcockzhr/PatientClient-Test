package com.qrcode;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.patientclient01.HttpUtil;
import com.example.patientclient01.MyApp;
import com.example.patientclient01.R;
import com.example.patientclient01.SysApplication;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QRActivity extends Activity{
	String TAG = "QRActivity";
	TextView doctorQRTextView, doctorResultTextView, medicineQRTextView, medicineResultTextView;
	Button doctorQRButton, medicineQRButton, refreshButton;
	ImageView doctorImageView, medicineImageView;
	MyApp myApp;
	String medicalRecordId;
	boolean medicineFlag;
	String http, httpUrl;
	String getMedicineString = "shlc/patient/medicalRecord/";
	String getDoctorString = "shlc/doctor/medicalRecord/";
	String medicineStatus = "aaa" , doctorStatus;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.qr_main_new);
		myApp = (MyApp) getApplication();
		http = myApp.getHttp();
		medicalRecordId = myApp.getMedicalRecordId();
		doctorQRTextView = (TextView) findViewById(R.id.doctor_qr_string);
		doctorResultTextView = (TextView)findViewById(R.id.doctor_tv);
		medicineQRTextView = (TextView)findViewById(R.id.medicine_qr_string);
		medicineResultTextView = (TextView)findViewById(R.id.medicine_tv);
		doctorQRButton = (Button)findViewById(R.id.doctor_add_qrcode);
		medicineQRButton = (Button)findViewById(R.id.medicine_add_qrcode);
		doctorImageView = (ImageView)findViewById(R.id.doctor_qr_image);
		medicineImageView = (ImageView)findViewById(R.id.medicine_qr_image);
		doctorQRTextView.setText("CONFIRM:"+medicalRecordId);
		refreshButton = (Button)findViewById(R.id.refresh_btn);
		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getMedicalRecordInfo(medicalRecordId);
				// TODO Auto-generated method stub
				doctorResultTextView.setText(medicineStatus);
				medicineResultTextView.setText(medicineStatus);
			}
		});
		doctorQRButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					// TODO Auto-generated method stub
					String contentString = doctorQRTextView.getText().toString();
					if (contentString.contains("CONFIRM")) {

						Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
						doctorImageView.setImageBitmap(qrCodeBitmap);
					}else {
						Toast.makeText(QRActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
					}

				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		medicineQRButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//medicineFlag = getMedicalRecordInfo(medicalRecordId);
				if (medicineStatus.equals("WAITING_FOR_MED")) {
					medicineQRTextView.setText("FETCH:"+medicalRecordId);
					Bitmap qrCodeBitmap;
					try {
						qrCodeBitmap = EncodingHandler.createQRCode(medicineQRTextView.getText().toString(), 350);
						medicineImageView.setImageBitmap(qrCodeBitmap);
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}else {
					Toast.makeText(QRActivity.this, "尚未就医扫码,请就医后刷新状态", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});
	}
	public void getMedicalRecordInfo(String medicalRecordId){
		httpUrl = http+getMedicineString+medicalRecordId;
		try {
			JSONObject joRev = HttpUtil.getResultForHttpGet(httpUrl);
			Log.v(TAG, "joRev:"+joRev.toString());
			medicineStatus = joRev.getJSONObject("value").getString("status");


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
