package com.jobmine.service;

import com.jobmine.models.Job;
import com.jobmine.models.Interview;

interface JobmineInterface {
	List<Job> getApplications ();
	List<Interview> getInterviews ();
	String getJobDescription (String jobId);
	void checkForUpdates (in List<Job> data);
}