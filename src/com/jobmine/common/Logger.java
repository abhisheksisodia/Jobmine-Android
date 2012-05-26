package com.jobmine.common;

import android.util.Log;

public class Logger {
	
	private static final String DEBUG_TAG = "jobmine";
	
	public static void d (String message) {
		Log.d(DEBUG_TAG, message);
	}
	
	public static void w (String message) {
		Log.w(DEBUG_TAG, message);
	}
	
	public static void e (String message) {
		Log.e(DEBUG_TAG, message);
	}
	
	public static void v (String message) {
		Log.v(DEBUG_TAG, message);
	}
	
	public static void i (String message) {
		Log.i(DEBUG_TAG, message);
	}
	
	public static void wtf (String message) {
		Log.wtf(DEBUG_TAG, message);
	}
}
