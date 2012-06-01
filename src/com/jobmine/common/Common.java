package com.jobmine.common;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Common {
	
	public static void showNetworkErrorToast (final Activity context, final int errorCode) {
		
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				switch (errorCode) {
					case JobmineNetworkRequest.INVALID_ID_PASS:
						Toast.makeText(context, "Invalid UserID/Password", Toast.LENGTH_SHORT).show();
						break;
					case JobmineNetworkRequest.INVALID_TIME:
						Toast.makeText(context, "Invalid login time...", Toast.LENGTH_SHORT).show();
						break;
					case JobmineNetworkRequest.UNKNOWN_ERROR:
						Toast.makeText(context, "Oops! Unknown Error", Toast.LENGTH_SHORT).show();
						break;
					case JobmineNetworkRequest.NO_NETWORK_CONNECTION:
						Toast.makeText(context, "No network connection detected", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});
	}
	
	public static boolean hasNetworkConnection (Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		return activeNetworkInfo != null;
	}
	
}
