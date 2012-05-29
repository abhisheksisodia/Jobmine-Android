package com.jobmine.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.jobmine.common.JobmineNetworkRequest;
import com.jobmine.models.Job;
import com.jobmine.providers.JobmineProvider;

/**
 * Runnable that will perform the update check and notify of any new interviews.
 * @author Jeremy
 *
 */
public class JobmineUpdaterTask implements Runnable {

	private JobmineService service = null;
	private ArrayList<Job> newData = null;
	
	public JobmineUpdaterTask(JobmineService context, ArrayList<Job> newData) {
		this.service = context;
		this.newData = newData;
	}
	
	@Override
	public void run() {
		JobmineNotificationManager.showUpdatingNotification(service);
		
		//Get current jobs and new jobs
		HashMap<Integer, Job> oldJobsMap = JobmineProvider.getApplications(service.getContentResolver());
		ArrayList<Job> newJobs = new ArrayList<Job>();
		
		//If we are passed the jobs manually, use it instead of fetching new data
		if (newData != null && newData.size() > 0) {
			newJobs = newData;
		} else {
			newJobs = JobmineNetworkRequest.getJobmine(service);
		}
		
		if (newJobs != null && oldJobsMap != null && oldJobsMap.size() > 0 && newJobs.size() > 0) {
			
			int newJobCount = 0;

			//Compare all jobs
			for (Job j : newJobs) {
				
				try {
					
					//Only check if the job existed in the old data as well
					if (oldJobsMap.containsKey(Integer.parseInt(j.id))) {
						Job old = oldJobsMap.get(Integer.parseInt(j.id));
	
						//We went from applied to selected or scheduled
						if (old.appStatus.equals("Applied") && (j.appStatus.equals("Selected") || j.appStatus.equals("Scheduled"))) {
							newJobCount++;
							
							//Show different notifications for different counts
							if (newJobCount == 1) {
								JobmineNotificationManager.showSingleInterviewNotification(service, j.id, j.emplyer, j.title);
							} else {
								JobmineNotificationManager.showMultipleInterviewNotification(service, newJobCount);
							}
							
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		//Replace provider with new data
		if (newJobs != null && newJobs.size() > 0) {
			JobmineProvider.updateOrInsertApplications(newJobs, service.getContentResolver());
		}
		
		//Cancel the current notification and stop the service
		JobmineNotificationManager.cancelUpdatingNotification(service);
		service.stopSelf();
	}

}
