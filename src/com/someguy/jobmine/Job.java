package com.someguy.jobmine;

import android.database.Cursor;

import com.jobmine.providers.JobmineProviderConstants;

public class Job {
	
	public String title = "";
	public String id = "";
	public String emplyer = "";
	public String job = "";
	public String jobStatus = "";
	public String appStatus = "";
	public String resumes = "";

	public Job() {

	}
	
	public Job(Cursor c) {
		title = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.JOB_TITLE));
		id = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.JOB_ID));
		emplyer = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.EMPLOYER));
		job = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.JOB));
		jobStatus = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.JOB_STATUS));
		appStatus = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.APP_STATUS));
		resumes = c.getString(c.getColumnIndex(JobmineProviderConstants.Columns.RESUMES));
	}
}
