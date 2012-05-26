package com.jobmine.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class JobmineNotificationManager {
	private static JobmineNotificationManager instance;
	
	private boolean includeSound = false;
	private boolean includeVibration = false;
	
	public static JobmineNotificationManager getInstance () {
		
		if (instance == null) {
			instance = new JobmineNotificationManager();
		}
		
		return instance;
	}
	
	public void setUseSound (boolean useSound) {
		includeSound = useSound;
	}
	
	public void setUseVibration (boolean useVibration) {
		includeVibration = useVibration;
	}
	
	public void setNotification (Context context, int iconResourceId, String tickerText, String title, String message, Intent intent) {
		Logger.d("sending notification");
		
		Notification notification = new Notification (iconResourceId, tickerText, System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notification.defaults |= includeSound ? Notification.DEFAULT_SOUND : 0;
		notification.defaults |= includeVibration ? Notification.DEFAULT_VIBRATE : 0;
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		
		notificationManager.notify(0, notification);
	}
}
