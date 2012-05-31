package com.jobmine.common;

public class Constants {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String appliedKey = "displayApplied";
	public static final String selectedKey = "displaySelected";
	public static final String notSelectedKey = "displayNotSelected";
	public static final String rankedKey = "displayRanked";
	public static final String userNameKey = "USERNAMEKEY";
	public static final String pwdKey = "PWDKEY";
	public static final String idKey = "idkey";
	public static final String titleKey = "titlekey";
	public static final String employerKey = "employerkey";
	public static final String jobStatusKey = "jobstatuskey";
	public static final String appStatusKey = "appstatuskey";
	public static final String resumeKey = "resumekey";
	
	//Time interval for checking for new interviews
	public static final int SERVICE_UPDATE_TIME_INTERVAL_SECONDS = 60*30;
	
	//Refresh time for apps/interviews
	public static final int LAST_UPDATE_TIME_TRESHOLD_SECONDS = 5 * 60;
	
	public static final String PREFERENCE_LAST_UPDATE_TIME_APPS = "last_update_time_apps";
	public static final String PREFERENCE_LAST_UPDATE_TIME_INTER = "last_update_time_inter";
}
