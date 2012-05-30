package com.jobmine.service;

import com.jobmine.models.Job;
import com.jobmine.models.Interview;

interface JobmineInterface {
	void getApplications (boolean forceUpdate);
	void getInterviews (boolean forceUpdate);
	void getJobDescription (String jobId);
	void checkForUpdates ();
	int getLastNetworkError();
}