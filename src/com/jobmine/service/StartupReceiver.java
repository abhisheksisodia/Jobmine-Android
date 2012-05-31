package com.jobmine.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jobmine.common.Constants;
import com.jobmine.common.Logger;

/**
 * This receiver will be called when the device is booted.
 * It will setup the 'alarm' to start the service updater
 * @author Jeremy
 *
 */
public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.d ("Device was just booted!");
		
		JobmineAlarmManager.setUpdateAlarm(context, Constants.SERVICE_UPDATE_TIME_INTERVAL_SECONDS);
	}

}
