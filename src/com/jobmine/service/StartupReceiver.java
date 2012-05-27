package com.jobmine.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jobmine.common.Constants;
import com.jobmine.common.Logger;

public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.d ("Device was just booted!");
		
		JobmineAlarmManager.setUpdateAlarm(context, Constants.SERVICE_UPDATE_TIME_INTERVAL);
	}

}
