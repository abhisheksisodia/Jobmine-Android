package com.jobmine.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.jobmine.common.JobmineNetworkRequest;
import com.jobmine.common.Logger;
import com.jobmine.models.Interview;
import com.jobmine.models.Job;
import com.jobmine.providers.JobmineProvider;

/**
 * Service that will perform updates and service client requests
 * @author Jeremy
 *
 */
public class JobmineService extends Service {

	private int startReason = -1;
	
	//Implementation of the interface
	private JobmineInterface.Stub serviceInterface = new JobmineInterface.Stub() {

		@Override
		public List<Job> getApplications () throws RemoteException {
			ArrayList<Job> jobs = JobmineNetworkRequest.getJobmine(JobmineService.this);
			
			if (jobs != null && jobs.size() > 0) {
				JobmineProvider.updateOrInsertApplications(jobs, getContentResolver());
			}
			
			return jobs;
		}
		
		@Override
		public List<Interview> getInterviews () throws RemoteException {
			ArrayList<Interview> interviews = JobmineNetworkRequest.getInterviews (JobmineService.this);
			
			return interviews;
		}

		@Override
		public String getJobDescription (String jobId) throws RemoteException {
			Job j = JobmineProvider.getApplication(jobId, getContentResolver());
			
			if (j == null) {
				return "";	
				
			} else if (j.description.isEmpty()) {
				//Make network request
				String description = JobmineNetworkRequest.getJobDescription (JobmineService.this, jobId);
	
				//Update database and return 
				JobmineProvider.updateJobDescription(jobId, description, getContentResolver());
				return description;
				
			} else {
				return j.description;
			}
		}

		@Override
		public void checkForUpdates(List<Job> data) throws RemoteException {
			beginUpdate ((ArrayList<Job>) data);
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
		}

		// Perform necessary action for updates
		if (startReason == JobmineAlarmManager.START_SERVICE_FOR_UPDATES) {
			beginUpdate (null);
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
		return serviceInterface;
	}
	
	private void beginUpdate (ArrayList<Job> newData) {
		Thread thread = new Thread(new JobmineUpdaterTask(this, newData));
		thread.start();
	}

}
