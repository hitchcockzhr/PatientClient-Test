package com.example.patientclient01;

public class TimeStamp {
	public static String TimeStamp2Date(String timestampString, String formats){
		Long timestamp = Long.parseLong(timestampString)*1000;
		String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
		return date;
	}
	public static String TimeStamp2Date(Long timestamp, String formats){
		//Long timestamp = Long.parseLong(timestampString)*1000;
		String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
		return date;
	}
}
