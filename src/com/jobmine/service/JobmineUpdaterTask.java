package com.jobmine.service;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

import com.jobmine.common.Constants;
import com.jobmine.common.JobmineNotificationManager;
import com.jobmine.providers.JobmineContentProvider;
import com.someguy.jobmine.Common;
import com.someguy.jobmine.EncryptedSharedPreferences;
import com.someguy.jobmine.Job;

public class JobmineUpdaterTask implements Runnable {

	private JobmineService context = null;
	
	public JobmineUpdaterTask(JobmineService context) {
		this.context = context;
	}
	
	@Override
	public void run() {
		JobmineNotificationManager.showUpdatingNotification(context);
		
		//Set username/password
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		Common.setUserName(settings.getString(Constants.userNameKey, ""));
		Common.setPassword(settings.getString(Constants.pwdKey, ""));
		
		//Get current jobs and new jobs
		ArrayList<Job> lastJobs = JobmineContentProvider.getApplications(context.getContentResolver());
		ArrayList<Job> newJobs = Common.getJobmine();
		
		if (newJobs != null && newJobs.size() > 0) {
		
			//Place old jobs in a hashmap
			HashMap<Integer, Job> oldJobsMap = new HashMap<Integer, Job>();
			for (Job j : lastJobs) {
				try {
					oldJobsMap.put(Integer.parseInt(j.id), j);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			int newJobCount = 0;
			
			//Compare all jobs
			for (Job j : newJobs) {
				//Logger.d ("Got job: " + j.job + ", Employer: " + j.emplyer + ", ID: " + j.id);
				
				/*if (j.emplyer.contains("Bloomberg")) { //unit test
					if (lastJobs.size() > 0) {
						j.appStatus = "Selected";
					}
				}*/
				
				try {
					if (oldJobsMap.containsKey(Integer.parseInt(j.id))) {
						Job old = oldJobsMap.get(Integer.parseInt(j.id));
						
						if (old.appStatus.equals("Applied") && 
								(j.appStatus.equals("Selected") || j.appStatus.equals("Scheduled"))) {
							newJobCount++;
							
							if (newJobCount == 1) {
								JobmineNotificationManager.showSingleInterviewNotification(context, j.emplyer, j.title);
							} else {
								JobmineNotificationManager.showMultipleInterviewNotification(context, newJobCount);
							}
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			//Replace provider with new data
			JobmineContentProvider.deleteAll(context.getContentResolver());
			JobmineContentProvider.addApplications(newJobs, context.getContentResolver());
		}
		
		//Cancel the current notification and stop the service
		JobmineNotificationManager.cancelUpdatingNotification(context);
		context.stopSelf();
	}

}
