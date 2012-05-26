package com.jobmine.service;

import com.jobmine.common.JobmineNotificationManager;
import com.jobmine.common.Logger;
import com.someguy.jobmine.MainActivity;

import android.R;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class JobmineService extends Service {

	private JobmineInterface.Stub serviceInterface = new JobmineInterface.Stub() {

		@Override
		public void go() throws RemoteException {
			JobmineNotificationManager.getInstance().setNotification(JobmineService.this, R.drawable.arrow_up_float, "Ticker", "Title", "Message", new Intent (JobmineService.this, MainActivity.class));
		}
		
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d ("Service onCreate() was called");
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.d ("Service onDestory() was called");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return serviceInterface;
	}

}
