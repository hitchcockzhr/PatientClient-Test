package com.example.patientclient01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;



import android.app.Application;
import android.content.res.Configuration;


public class MyApp extends Application
{
	private String joStartPath, http, userId, medicalRecordId;
	private JSONObject LoginRev, joDoctor;
	private int departmentId;
	private boolean isJinRu;
	public boolean isJinRu() {
		return isJinRu;
	}

	public void setJinRu(boolean isJinRu) {
		this.isJinRu = isJinRu;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public JSONObject getJoDoctor() {
		return joDoctor;
	}

	public void setJoDoctor(JSONObject joDoctor) {
		this.joDoctor = joDoctor;
	}

	public JSONObject getLoginRev() {
		return LoginRev;
	}

	public void setLoginRev(JSONObject loginRev) {
		LoginRev = loginRev;
	}
	public String getMedicalRecordId() {
		return medicalRecordId;
	}

	public void setMedicalRecordId(String medicalRecordId) {
		this.medicalRecordId = medicalRecordId;
	}

	private String diquString, yiyuanString, keshiString, yishengString, cityString, doctorIDString;
	private ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
	private List<HashMap<String,String>> dataDFK = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataDPJ = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataDKS = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataDJY = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataDFYF = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataDQY = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataYPJ = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String,String>> dataYJS = new ArrayList<HashMap<String,String>>();
	public List<HashMap<String, String>> getDataYPJ() {
		return dataYPJ;
	}

	public void setDataYPJ(List<HashMap<String, String>> dataYPJ) {
		this.dataYPJ = dataYPJ;
	}

	public List<HashMap<String, String>> getDataYJS() {
		return dataYJS;
	}

	public void setDataYJS(List<HashMap<String, String>> dataYJS) {
		this.dataYJS = dataYJS;
	}

	public List<HashMap<String, String>> getDataDQY() {
		return dataDQY;
	}

	public void setDataDQY(List<HashMap<String, String>> dataDQY) {
		this.dataDQY = dataDQY;
	}

	public List<HashMap<String, String>> getDataDFYF() {
		return dataDFYF;
	}

	public void setDataDFYF(List<HashMap<String, String>> dataDFYF) {
		this.dataDFYF = dataDFYF;
	}

	public List<HashMap<String, String>> getDataDJY() {
		return dataDJY;
	}

	public void setDataDJY(List<HashMap<String, String>> dataDJY) {
		this.dataDJY = dataDJY;
	}

	public String getDoctorIDString() {
		return doctorIDString;
	}
	
	public void setDoctorIDString(String doctorIDString) {
		this.doctorIDString = doctorIDString;
	}

	private HttpClient httpClient;
	private static MyApp instance;
	public static MyApp getInstance(){
		return instance;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getHttp() {
		return http;
	}
	public void setHttp(String http) {
		this.http = http;
	}

	private int shengposition, shiposition;
	public int getShengposition() {
		return shengposition;
	}
	public void setShengposition(int shengposition) {
		this.shengposition = shengposition;
	}
	public int getShiposition() {
		return shiposition;
	}
	public void setShiposition(int shiposition) {
		this.shiposition = shiposition;
	}
	public String getJoStartPath() {
		return joStartPath;
	}
	public void setJoStartPath(String joStartPath) {
		this.joStartPath = joStartPath;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		//setHttp("http://192.168.1.100:8080/");
		//setHttp("http://192.168.1.102:8080/");
		//setHttp("http://172.27.35.1:8080/");
		setHttp("http://123.56.155.17:8080/");
		
		
		AppContext.init(this);
		instance = this;
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());
	}
	@Override
	public void onTerminate()
	{
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	}

	public String getDiquString() {
		return diquString;
	}

	public void setDiquString(String diquString) {
		this.diquString = diquString;
	}
	
	public String getYiyuanString() {
		return yiyuanString;
	}

	public void setYiyuanString(String yiyuanString) {
		this.yiyuanString = yiyuanString;
	}

	public String getKeshiString() {
		return keshiString;
	}

	public void setKeshiString(String keshiString) {
		this.keshiString = keshiString;
	}

	public String getYishengString() {
		return yishengString;
	}

	public void setYishengString(String yishengString) {
		this.yishengString = yishengString;
	}

	public String getCityString() {
		return cityString;
	}

	public void setCityString(String cityString) {
		this.cityString = cityString;
	}

	public ArrayList<JSONObject> getJsonList() {
		return jsonList;
	}

	public void setJsonList(ArrayList<JSONObject> jsonList) {
		this.jsonList = jsonList;
	}

	public List<HashMap<String,String>> getDataDFK() {
		return dataDFK;
	}

	public void setDataDFK(List<HashMap<String,String>> dataDFK) {
		this.dataDFK = dataDFK;
	}

	public List<HashMap<String,String>> getDataDPJ() {
		return dataDPJ;
	}

	public void setDataDPJ(List<HashMap<String,String>> dataDPJ) {
		this.dataDPJ = dataDPJ;
	}

	public List<HashMap<String,String>> getDataDKS() {
		return dataDKS;
	}

	public void setDataDKS(List<HashMap<String,String>> dataDKS) {
		this.dataDKS = dataDKS;
	}

	
}
