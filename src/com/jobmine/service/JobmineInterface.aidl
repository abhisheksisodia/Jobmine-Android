package com.jobmine.service;

import com.jobmine.models.Job;

interface JobmineInterface {
	List<Job> getApplications ();
	String getJobDescription (String jobId);
}