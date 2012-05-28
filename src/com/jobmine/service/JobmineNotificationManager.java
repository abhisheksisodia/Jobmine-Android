package com.jobmine.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jobmine.common.Constants;
import com.someguy.jobmine.JobDetailsActivity;
import com.someguy.jobmine.MainActivity;



/**
 * Class that will handle all notifications
 * @author Jeremy
 *
 */
public class JobmineNotificationManager {
	
	public static final int UPDATE_NOTIFICATION_ID = 0;
	public static final int GENERAL_NOTIFICATION_ID = 1;
	public static final int INTERVIEW_NOTIFICATION_ID = 2;
	
	public static void showUpdatingNotification (Context context) {
		Intent intent = new Intent (context, MainActivity.class);
		showNotification(context, UPDATE_NOTIFICATION_ID, android.R.drawable.ic_popup_sync, 
				"Checking for updates", "Checking for interviews", "Checking...", intent, false, false, false, true);
	}
	
	public static void cancelUpdatingNotification (Context context) {
		cancelNotification (context, UPDATE_NOTIFICATION_ID);
	}
	
	public static void showSingleInterviewNotification (Context context, String jobId, String employer, String job) {
		Intent intent = new Intent (context, JobDetailsActivity.class);
		intent.putExtra(Constants.idKey, jobId);
		showNotification(context, INTERVIEW_NOTIFICATION_ID, android.R.drawable.btn_star, 
				"New Interview with " + employer, "Interview with " + employer, job, intent, true, true, true, false);
	}
	
	public static void showMultipleInterviewNotification (Context context, int count) {
		Intent intent = new Intent (context, MainActivity.class);
		showNotification(context, INTERVIEW_NOTIFICATION_ID, android.R.drawable.btn_star, 
				count + " new interviews", count + " new interviews", "Multiple Interviews", intent, true, true, true, false);
	}
	
	public static void cancelInterviewNotification (Context context) {
		cancelNotification (context, INTERVIEW_NOTIFICATION_ID);
	}
	
	private static void showNotification (Context context, int notificationId, int iconResourceId, String tickerText, String title, String message, Intent intent, boolean useSound, boolean useVibration, boolean autoCancel, boolean noClear) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Notification notification = new Notification (iconResourceId, tickerText, System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		
		notification.defaults |= useSound ? Notification.DEFAULT_SOUND : 0;
		notification.defaults |= useVibration ? Notification.DEFAULT_VIBRATE : 0;
		notification.flags |= autoCancel ? Notification.FLAG_AUTO_CANCEL : 0;
		notification.flags |= noClear ? Notification.FLAG_NO_CLEAR : 0;
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		
		notificationManager.notify(notificationId, notification);
	}
	
	private static void cancelNotification (Context context, int notificationId) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel(notificationId);
	}
}
