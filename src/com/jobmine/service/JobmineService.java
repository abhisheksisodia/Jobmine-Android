package com.jobmine.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.jobmine.common.Common;
import com.jobmine.common.Constants;
import com.jobmine.common.EncryptedSharedPreferences;
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
		public boolean getApplications (boolean forceUpdate) throws RemoteException {
			ArrayList<Job> jobs = JobmineNetworkRequest.getApplications(JobmineService.this, forceUpdate);
			
			if (jobs != null && jobs.size() > 0) {
				JobmineProvider.updateOrInsertApplications(jobs, getContentResolver());
			}
			
			return JobmineNetworkRequest.getLastNetworkError() == JobmineNetworkRequest.SUCCESS || 
					JobmineNetworkRequest.getLastNetworkError() == JobmineNetworkRequest.SUCCESS_NO_UPDATE;
		}
		
		@Override
		public boolean getInterviews (boolean forceUpdate) throws RemoteException {
			ArrayList<Interview> interviews = JobmineNetworkRequest.getInterviews (JobmineService.this, forceUpdate);
			
			if (interviews != null && interviews.size() > 0) {
				JobmineProvider.deleteAllInterviews(getContentResolver());
				JobmineProvider.addInterviews(interviews, getContentResolver());
			}
			
			return JobmineNetworkRequest.getLastNetworkError() == JobmineNetworkRequest.SUCCESS || 
					JobmineNetworkRequest.getLastNetworkError() == JobmineNetworkRequest.SUCCESS_NO_UPDATE;
		}

		@Override
		public boolean getJobDescription (String jobId) throws RemoteException {
			Job j = JobmineProvider.getApplication(jobId, getContentResolver());
			String description = j.description;
			
			if (Common.trim(description).isEmpty()) {
				//Make network request
				description = JobmineNetworkRequest.getJobDescription(JobmineService.this, jobId);
	
				//Update database and return 
				if (description != null && !description.isEmpty()) {
					JobmineProvider.updateJobDescription(jobId, description, getContentResolver());	
				}
				
				return JobmineNetworkRequest.getLastNetworkError() == JobmineNetworkRequest.SUCCESS || 
						JobmineNetworkRequest.getLastNetworkError() == JobmineNetworkRequest.SUCCESS_NO_UPDATE;
			}
			
			return true;
		}

		@Override
		public void checkForUpdates() throws RemoteException {
			beginUpdate ();
		}

		@Override
		public int getLastNetworkError() throws RemoteException {
			return JobmineNetworkRequest.getLastNetworkError();
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
		
		if(intent == null){
			return super.onStartCommand(intent, flags, startId);
		}
		//Get the start reason if it exists
		if (intent.hasExtra(JobmineAlarmManager.START_SERVICE_REASON)) {
			startReason = intent.getExtras().getInt(JobmineAlarmManager.START_SERVICE_REASON);
		}

		// Perform necessary action for updates
		if (startReason == JobmineAlarmManager.START_SERVICE_FOR_UPDATES) {
			
			//Don't pull if you don't even have an account to pull from.
			EncryptedSharedPreferences settings = new EncryptedSharedPreferences(this, this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
			if(settings.contains(Constants.pwdKey) && settings.contains(Constants.userNameKey)){
				beginUpdate ();
			}	
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
	
	private void beginUpdate () {
		Thread thread = new Thread(new JobmineUpdaterTask(this));
		thread.start();
	}

}
