package com.jobmine.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class JobmineProviderConstants {
	
	public static final String NAME = "com.jobmine.providers.JobmineContentProvider";
	public static final String PATH = "applications";
	public static final Uri CONTENT_URI = Uri.parse("content://" + NAME + "/" + PATH);
		
	public class Columns implements BaseColumns {
		public static final String JOB_TITLE = "job_title";
		public static final String JOB_ID = "job_id";
		public static final String EMPLOYER = "employer";
		public static final String JOB = "job";
		public static final String JOB_STATUS = "job_status";
		public static final String APP_STATUS = "app_staus";
		public static final String RESUMES = "resumes";
	}
	
	public static final String [] DEFAULT_PROJECTION = new String [] {
		Columns._ID,
		Columns.JOB_TITLE,
		Columns.JOB_ID,
		Columns.EMPLOYER,
		Columns.JOB,
		Columns.JOB_STATUS,
		Columns.APP_STATUS,
		Columns.RESUMES
	};
	
}
