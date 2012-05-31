package com.jobmine.service;

import com.jobmine.models.Job;
import com.jobmine.models.Interview;

interface JobmineInterface {
	boolean getApplications (boolean forceUpdate);
	boolean getInterviews (boolean forceUpdate);
	boolean getJobDescription (String jobId);
	void checkForUpdates ();
	int getLastNetworkError();
}