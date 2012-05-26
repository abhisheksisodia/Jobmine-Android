package com.jobmine.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.jobmine.common.JobmineAlarmManager;
import com.jobmine.common.Logger;

public class JobmineService extends Service {

	private int startReason = -1;
	
	private JobmineInterface.Stub serviceInterface = new JobmineInterface.Stub() {

		@Override
		public void go() throws RemoteException {
			//JobmineNotificationManager.showNotification(JobmineService.this, JobmineNotificationManager.GENERAL_NOTIFICATION_ID, R.drawable.arrow_up_float, "Ticker", "Title", "Message", new Intent (JobmineService.this, MainActivity.class), false, false);
		}
		
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d ("Service onCreate() was called");
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Logger.d("Service onStartCommand() was called");
		
		//Get the start reason if it exists
		if (intent.hasExtra(JobmineAlarmManager.START_SERVICE_REASON)) {
			startReason = intent.getExtras().getInt(JobmineAlarmManager.START_SERVICE_REASON);
			//Logger.d("Start reason was found to be: " + startReason);
		}

		// Perform necessary action for updates
		if (startReason == JobmineAlarmManager.START_SERVICE_FOR_UPDATES) {
			Thread thread = new Thread(new JobmineUpdaterTask(this));
			thread.start();
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.d ("Service onDestory() was called");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Logger.d ("Service onBind() was called!");
		return serviceInterface;
	}

}
