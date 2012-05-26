package com.jobmine.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class JobmineNotificationManager {
	
	public static final int UPDATE_NOTIFICATION_ID = 0;
	public static final int GENERAL_NOTIFICATION_ID = 1;
	
	public static void showNotification (Context context, int notificationId, int iconResourceId, String tickerText, String title, String message, Intent intent, boolean useSound, boolean useVibration) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Notification notification = new Notification (iconResourceId, tickerText, System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.defaults |= useSound ? Notification.DEFAULT_SOUND : 0;
		notification.defaults |= useVibration ? Notification.DEFAULT_VIBRATE : 0;
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		
		notificationManager.notify(notificationId, notification);
		
		Logger.d("Showing Notification");
	}
	
	public static void cancelNotification (Context context, int notificationId) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel(notificationId);
	}
	
	public static void showUpdatingNotification (Context context) {
		Intent intent = new Intent ();
		showNotification(context, UPDATE_NOTIFICATION_ID, android.R.drawable.ic_popup_sync, "Checking for updates", "Checking for updates", "Checking...", intent, false, false);
	}
	
	public static void cancelUpdatingNotification (Context context) {
		cancelNotification (context, UPDATE_NOTIFICATION_ID);
	}
}
