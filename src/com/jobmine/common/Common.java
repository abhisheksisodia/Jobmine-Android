package com.jobmine.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.jobmine.providers.JobmineProvider;

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
	
	public static boolean isICS(){
		 return  Build.VERSION.SDK_INT > 11;
	}
	
	public static int getJobAppStatusCode (String status) {
		
		if (Common.trim(status).isEmpty()) {
			return Constants.STATUS_UNKNOWN;
		} else if (status.equals("Posted")) {
			return Constants.STATUS_POSTED;
		} else if (status.equals("Applied")) {
			return Constants.STATUS_APPLIED;
		} else if (status.equals("Applications Available")) {
			return Constants.STATUS_APPLICATIONS_AVAILABLE;
		} else if (status.equals("Screened")) {
			return Constants.STATUS_SCREENED;
		} else if (status.equals("Not Selected")) {
			return Constants.STATUS_NOT_SELECTED;
		} else if (status.equals("Selected")) {
			return Constants.STATUS_SELECTED;
		} else if (status.equals("Scheduled")) {
			return Constants.STATUS_SCHEDULED;
		} else if (status.equals("Alternate")) {
			return Constants.STATUS_ALTERNATE;
		} else if (status.equals("Ranking Completed")) {
			return Constants.STATUS_RANKING_COMPLETED;
		} else if (status.equals("Approved")) {
			return Constants.STATUS_APPROVED;
		} else if (status.equals("Complete")) {
			return Constants.STATUS_COMPLETE;
		} else if (status.equals("Offer")) {
			return Constants.STATUS_OFFER;
		} else if (status.equals("Cancelled")) {
			return Constants.STATUS_CANCELLED;
		} else if (status.equals("Filled")) {
			return Constants.STATUS_FILLED;
		}
			
		return Constants.STATUS_UNKNOWN;
	}
	
	public static String getJobAppStatusString (int code) {
		switch (code) {
			case Constants.STATUS_UNKNOWN:
				return "";
			case Constants.STATUS_POSTED:
				return "Posted";
			case Constants.STATUS_APPLIED:
				return "Applied";
			case Constants.STATUS_APPLICATIONS_AVAILABLE:
				return "Applications Available";
			case Constants.STATUS_SCREENED:
				return "Screened";
			case Constants.STATUS_NOT_SELECTED:
				return "Not Selected";
			case Constants.STATUS_SELECTED:
				return "Selected";
			case Constants.STATUS_SCHEDULED:
				return "Scheduled";
			case Constants.STATUS_ALTERNATE:
				return "Alternate";
			case Constants.STATUS_RANKING_COMPLETED:
				return "Ranking Completed";
			case Constants.STATUS_APPROVED:
				return "Approved";
			case Constants.STATUS_COMPLETE:
				return "Complete";
			case Constants.STATUS_OFFER:
				return "Offer";
			case Constants.STATUS_CANCELLED:
				return "Cancelled";
			case Constants.STATUS_FILLED:
				return "Filled";
			default:
				return "";
		}
	}
	
	public static void clearAllData (Context context) {
		EncryptedSharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		Editor editor = settings.edit();
		
		editor.remove(Constants.userNameKey);
		editor.remove(Constants.pwdKey);
		editor.remove(Constants.PREFERENCE_LAST_UPDATE_TIME_APPS);
		editor.remove(Constants.PREFERENCE_LAST_UPDATE_TIME_INTER);
		editor.remove(Constants.appliedKey);
		editor.remove(Constants.selectedKey);
		editor.remove(Constants.notSelectedKey);
		editor.remove(Constants.rankedKey);
		editor.commit();
		
		JobmineProvider.deleteAllApplications(context.getContentResolver());
		JobmineProvider.deleteAllInterviews(context.getContentResolver());
	}
	
	public static String trim (String s) {
		return s.replaceAll("\240", "").trim();
	}
}
