package com.jobmine.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class JobmineProviderConstants {
	
	public static final String NAME = "com.jobmine.providers.JobmineContentProvider";
	public static final String PATH = "applications";
	public static final Uri CONTENT_URI = Uri.parse("content://" + NAME + "/" + PATH);
		
	public class Columns implements BaseColumns {
		public static final String JOB_ID = "job_id";
		public static final String JOB_TITLE = "job_title";
		public static final String TERM = "term";
		public static final String JOB_STATUS = "job_status";
		public static final String APP_STATUS = "app_staus";
		public static final String LAST_DAY_APPLY = "last_day_apply";
		public static final String NUMBER_APPS = "number_apps";
	}
	
	public static final String [] DEFAULT_PROJECTION = new String [] {
		Columns._ID,
		Columns.JOB_ID,
		Columns.JOB_TITLE,
		Columns.TERM,
		Columns.JOB_STATUS,
		Columns.APP_STATUS,
		Columns.LAST_DAY_APPLY,
		Columns.NUMBER_APPS
	};
	
}
