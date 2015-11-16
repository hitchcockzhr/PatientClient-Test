package com.example.patientclient01;

import android.app.Activity;

public class ActivityContext {
	
	private static Activity c;

	public static void init(Activity ctx) {
		c = ctx;
	}

	public static Activity get() {
		return c;
	}

}