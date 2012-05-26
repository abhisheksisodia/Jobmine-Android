package com.jobmine.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.jobmine.service.JobmineService;

public class JobmineAlarmManager {
	public static final String START_SERVICE_REASON = "START_REASON";
	public static final int START_SERVICE_FOR_REQUESTS = 0;
	public static final int START_SERVICE_FOR_UPDATES = 1;
	
	public static void setAlarm (Context context, int intervalInSeconds) {
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent (context, JobmineService.class);
		intent.putExtra(START_SERVICE_REASON, START_SERVICE_FOR_UPDATES);
		
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), intervalInSeconds * 1000, pendingIntent);
		
		Logger.d("Setting alarm");
	}
	
	public static void cancelAlarm (Context context) {
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent (context, JobmineService.class);
		intent.putExtra(START_SERVICE_REASON, START_SERVICE_FOR_UPDATES);
		
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		alarmManager.cancel(pendingIntent);
	}

}
