package com.jobmine.providers;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Holds constants for the content provider
 * @author Jeremy
 *
 */
public class JobmineProviderConstants {
	
	public static final String NAME = "com.jobmine.providers.JobmineContentProvider";
	
	public static final String APPLICATIONS_PATH = "applications";
	public static final String INTERVIEWS_PATH = "interviews";
	
	public static final int APPLICATIONS_URI_CODE = 1;
	public static final int INTERVIEWS_URI_CODE = 2;
	
	public static final Uri APPLICATIONS_CONTENT_URI = Uri.parse("content://" + NAME + "/" + APPLICATIONS_PATH);
	public static final Uri INTERVIEWS_CONTENT_URI = Uri.parse("content://" + NAME + "/" + INTERVIEWS_PATH);
		
	public class ApplicationsColumns implements BaseColumns {
		public static final String JOB_TITLE = "job_title";
		public static final String JOB_ID = "job_id";
		public static final String EMPLOYER = "employer";
		public static final String JOB = "job";
		public static final String JOB_STATUS = "job_status";
		public static final String APP_STATUS = "app_staus";
		public static final String RESUMES = "resumes";
		public static final String JOB_DESCRIPTION = "job_description";
	}
	
	public class InterviewsColumns implements BaseColumns {
		public static final String JOB_ID = "job_id";
		public static final String EMPLOYER_NAME = "employer_name";
		public static final String JOB_TITLE = "job_title";
		public static final String DATE = "date";
		public static final String TYPE = "type";
		public static final String START_TIME = "start_time";
		public static final String LENGTH = "length";
		public static final String ROOM = "room";
		public static final String INSTRUCTIONS = "instructions";
		public static final String INTERVIEWER = "interviewer";
		public static final String JOB_STATUS = "job_status";
	}
	
	public static final String [] APPLICATIONS_DEFAULT_PROJECTION = new String [] {
		ApplicationsColumns._ID,
		ApplicationsColumns.JOB_TITLE,
		ApplicationsColumns.JOB_ID,
		ApplicationsColumns.EMPLOYER,
		ApplicationsColumns.JOB,
		ApplicationsColumns.JOB_STATUS,
		ApplicationsColumns.APP_STATUS,
		ApplicationsColumns.RESUMES,
		ApplicationsColumns.JOB_DESCRIPTION
	};
	
	public static final String [] INTERVIEWS_DEFAULT_PROJECTION = new String [] {
		InterviewsColumns._ID,
		InterviewsColumns.JOB_ID,
		InterviewsColumns.EMPLOYER_NAME,
		InterviewsColumns.JOB_TITLE,
		InterviewsColumns.DATE,
		InterviewsColumns.TYPE,
		InterviewsColumns.START_TIME,
		InterviewsColumns.LENGTH,
		InterviewsColumns.ROOM,
		InterviewsColumns.INSTRUCTIONS,
		InterviewsColumns.INTERVIEWER,
		InterviewsColumns.JOB_STATUS
	};

}
