package com.jobmine.service;

import com.jobmine.common.JobmineNotificationManager;

public class JobmineUpdaterTask implements Runnable {

	private JobmineService context = null;
	
	public JobmineUpdaterTask(JobmineService context) {
		this.context = context;
	}
	
	@Override
	public void run() {
		JobmineNotificationManager.showUpdatingNotification(context);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		JobmineNotificationManager.cancelUpdatingNotification(context);
		context.stopSelf();
	}

}
