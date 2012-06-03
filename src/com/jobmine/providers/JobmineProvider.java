package com.jobmine.providers;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.jobmine.common.Logger;
import com.jobmine.models.Interview;
import com.jobmine.models.Job;


/**
 * Stores application and job information
 * @author Jeremy
 *
 */
public class JobmineProvider extends ContentProvider {

	private DatabaseHelper databaseHelper = null;

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "Jobmine.db";
		private static final int DATABASE_VERSION = 4;
		
		private static final String APPLICATIONS_TABLE_NAME = "Applications";
		private static final String INTERVIEWS_TABLE_NAME = "Interviews";
		
		private static final String APPLICATIONS_CREATE_DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS " + APPLICATIONS_TABLE_NAME + "("
													+ JobmineProviderConstants.ApplicationsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
													+ JobmineProviderConstants.ApplicationsColumns.JOB_TITLE + " TEXT,"
													+ JobmineProviderConstants.ApplicationsColumns.JOB_ID + " TEXT UNIQUE,"
													+ JobmineProviderConstants.ApplicationsColumns.EMPLOYER + " TEXT,"
													+ JobmineProviderConstants.ApplicationsColumns.JOB + " TEXT,"
													+ JobmineProviderConstants.ApplicationsColumns.JOB_STATUS + " TEXT,"
													+ JobmineProviderConstants.ApplicationsColumns.APP_STATUS + " TEXT,"
													+ JobmineProviderConstants.ApplicationsColumns.RESUMES + " TEXT,"
													+ JobmineProviderConstants.ApplicationsColumns.JOB_DESCRIPTION + " TEXT"
													+ ")";
		
		private static final String INTERVIEWS_CREATE_DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS " + INTERVIEWS_TABLE_NAME + "("
				+ JobmineProviderConstants.InterviewsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ JobmineProviderConstants.InterviewsColumns.JOB_ID + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.EMPLOYER_NAME + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.JOB_TITLE + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.DATE + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.TYPE + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.START_TIME + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.LENGTH + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.ROOM + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.INSTRUCTIONS + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.INTERVIEWER + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.JOB_STATUS + " TEXT,"
				+ JobmineProviderConstants.InterviewsColumns.START_TIME_UNIX + " LONG"
				+ ")";

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(APPLICATIONS_CREATE_DATABASE_QUERY);
			db.execSQL(INTERVIEWS_CREATE_DATABASE_QUERY);
			Logger.d("Created new database");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Logger.d("Upgrading database");
			db.execSQL("DROP TABLE IF EXISTS " + APPLICATIONS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + INTERVIEWS_TABLE_NAME);
			onCreate(db);
		}

	}
	
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH) {
		{
			addURI(JobmineProviderConstants.NAME, JobmineProviderConstants.APPLICATIONS_PATH, JobmineProviderConstants.APPLICATIONS_URI_CODE);
			addURI(JobmineProviderConstants.NAME, JobmineProviderConstants.INTERVIEWS_PATH, JobmineProviderConstants.INTERVIEWS_URI_CODE);
		}
	};

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext(), DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
		return true;
	}

	@Override
	public String getType(Uri uri) {
		int match = uriMatcher.match(uri);
        switch (match)
        {
	        case JobmineProviderConstants.APPLICATIONS_URI_CODE:
	        	return "vnd.android.cursor.dir/vnd.jobmine.applications";
	        	
	        case JobmineProviderConstants.INTERVIEWS_URI_CODE:
	        	return "vnd.android.cursor.dir/vnd.jobmine.interviews";
	        	
	        default:
	        	return null;
        }
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		int match = uriMatcher.match(uri);
		int count = 0;
		
        switch (match)
        {
	        case JobmineProviderConstants.APPLICATIONS_URI_CODE:
	        	count = db.delete(DatabaseHelper.APPLICATIONS_TABLE_NAME, selection, selectionArgs);
	        	break;
	        	
	        case JobmineProviderConstants.INTERVIEWS_URI_CODE:
	        	count = db.delete(DatabaseHelper.INTERVIEWS_TABLE_NAME, selection, selectionArgs);
	        	break;
	        	
	        default:
	        	return 0;
        }

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		int match = uriMatcher.match(uri);
		long rowId = 0;
		
        switch (match)
        {
	        case JobmineProviderConstants.APPLICATIONS_URI_CODE:
	        	rowId = db.insert(DatabaseHelper.APPLICATIONS_TABLE_NAME, null, values);
	        	break;
	        	
	        case JobmineProviderConstants.INTERVIEWS_URI_CODE:
	        	rowId = db.insert(DatabaseHelper.INTERVIEWS_TABLE_NAME, null, values);
	        	break;
	        	
	        default:
	        	rowId = -1;
        }
	
		if (rowId != -1) {
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		
		int match = uriMatcher.match(uri);
		Cursor c;
		
        switch (match)
        {
	        case JobmineProviderConstants.APPLICATIONS_URI_CODE:
	        	c = db.query (DatabaseHelper.APPLICATIONS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
	        	break;
	        	
	        case JobmineProviderConstants.INTERVIEWS_URI_CODE:
	        	c = db.query (DatabaseHelper.INTERVIEWS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
	        	break;
	        	
	        default:
	        	c = null;
        }

        if(c != null) {
        	c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		int match = uriMatcher.match(uri);
		int count = 0;
		
        switch (match)
        {
	        case JobmineProviderConstants.APPLICATIONS_URI_CODE:
	        	count = db.update (DatabaseHelper.APPLICATIONS_TABLE_NAME, values, selection, selectionArgs);
	        	break;
	        	
	        case JobmineProviderConstants.INTERVIEWS_URI_CODE:
	        	count = db.update (DatabaseHelper.INTERVIEWS_TABLE_NAME, values, selection, selectionArgs);
	        	break;
	        	
	        default:
	        	return 0;
        }

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	public static void addApplication (Job job, ContentResolver resolver) {
		if (job != null) {
			ContentValues values = new ContentValues ();
			
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_TITLE, job.title);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_ID, job.id);
			values.put(JobmineProviderConstants.ApplicationsColumns.EMPLOYER, job.emplyer);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB, job.job);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_STATUS, job.jobStatus);
			values.put(JobmineProviderConstants.ApplicationsColumns.APP_STATUS, job.appStatus);
			values.put(JobmineProviderConstants.ApplicationsColumns.RESUMES, job.resumes);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_DESCRIPTION, job.description);
			
			resolver.insert(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, values);
		}
	}
	
	public static int updateApplication (Job job, ContentResolver resolver) {
		if (job != null) {
			ContentValues values = new ContentValues ();
			
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_TITLE, job.title);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_ID, job.id);
			values.put(JobmineProviderConstants.ApplicationsColumns.EMPLOYER, job.emplyer);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB, job.job);
			values.put(JobmineProviderConstants.ApplicationsColumns.JOB_STATUS, job.jobStatus);
			values.put(JobmineProviderConstants.ApplicationsColumns.APP_STATUS, job.appStatus);
			values.put(JobmineProviderConstants.ApplicationsColumns.RESUMES, job.resumes);
			
			return resolver.update(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, values, JobmineProviderConstants.ApplicationsColumns.JOB_ID + " = ?", new String [] { job.id });
		}
		
		return 0;
	}
	
	public static void addApplications (ArrayList<Job> jobs, ContentResolver resolver) {
		if (jobs != null && jobs.size() > 0) {
			for (int i = 0; i < jobs.size(); i++) {
				addApplication (jobs.get(i), resolver);
			}
		}
	}
	
	public static void updateApplications (ArrayList<Job> jobs, ContentResolver resolver) {
		if (jobs != null && jobs.size() > 0) {
			for (int i = 0; i < jobs.size(); i++) {
				updateApplication (jobs.get(i), resolver);
			}
		}
	}
	
	public static void updateOrInsertApplications (ArrayList<Job> jobs, ContentResolver resolver) {
		if (jobs != null && jobs.size() > 0) {
			for (int i = 0; i < jobs.size(); i++) {
				if (updateApplication (jobs.get(i), resolver) == 0) { //If none were updated, insert it instead
					addApplication(jobs.get(i), resolver);
				}
			}
		}
	}

	public static HashMap<Integer, Job> getApplicationsMap (ContentResolver resolver) {
		HashMap<Integer, Job> jobs = new HashMap<Integer, Job>();
    	
    	Cursor c = resolver.query(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, JobmineProviderConstants.APPLICATIONS_DEFAULT_PROJECTION, null, null, null);
    	
    	if (c.moveToFirst()) {
    		do {
    			Job j = new Job(c);
    			try {
    				jobs.put(Integer.parseInt(j.id), j);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		} while (c.moveToNext());
    	}
    	
    	c.close();
    	
    	return jobs;
	}
	
	public static ArrayList<Job> getApplications (ContentResolver resolver) {
		ArrayList<Job> jobs = new ArrayList<Job>();
    	
    	Cursor c = resolver.query(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, JobmineProviderConstants.APPLICATIONS_DEFAULT_PROJECTION, null, null, null);
    	
    	if (c.moveToFirst()) {
    		do {
    			jobs.add(new Job(c));
    		} while (c.moveToNext());
    	}
    	
    	c.close();
    	
    	return jobs;
	}
	
	public static Job getApplication (String jobId, ContentResolver resolver) {

		String selection = JobmineProviderConstants.ApplicationsColumns.JOB_ID + " = ?";
		String args [] = new String [] { jobId };
		
    	Cursor c = resolver.query(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, JobmineProviderConstants.APPLICATIONS_DEFAULT_PROJECTION, selection, args, null);
    	
    	Job j = null;
    	
    	if (c.moveToFirst()) {
    		j = new Job (c);
    	}
    	
    	c.close();
    	
    	return j;
	}

	public static void updateJobDescription (String jobId, String description, ContentResolver resolver) {
		
		ContentValues values = new ContentValues();
		values.put(JobmineProviderConstants.ApplicationsColumns.JOB_DESCRIPTION, description);
		
		String selection = JobmineProviderConstants.ApplicationsColumns.JOB_ID + " = ?";
		String args [] = new String [] { jobId };
		
		resolver.update(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, values, selection, args);
	}
	
	public static void deleteAllApplications (ContentResolver resolver) {
		resolver.delete(JobmineProviderConstants.APPLICATIONS_CONTENT_URI, null, null);
	}
	
	
	public static void addInterview (Interview interview, ContentResolver resolver) {
		if (interview != null) {
			ContentValues values = new ContentValues ();
			
			values.put(JobmineProviderConstants.InterviewsColumns.JOB_ID, interview.id);
			values.put(JobmineProviderConstants.InterviewsColumns.EMPLOYER_NAME, interview.employerName);
			values.put(JobmineProviderConstants.InterviewsColumns.JOB_TITLE, interview.title);
			values.put(JobmineProviderConstants.InterviewsColumns.DATE, interview.date);
			values.put(JobmineProviderConstants.InterviewsColumns.TYPE, interview.type);
			values.put(JobmineProviderConstants.InterviewsColumns.START_TIME, interview.time);
			values.put(JobmineProviderConstants.InterviewsColumns.LENGTH, interview.length);
			values.put(JobmineProviderConstants.InterviewsColumns.ROOM, interview.room);
			values.put(JobmineProviderConstants.InterviewsColumns.INSTRUCTIONS, interview.instructions);
			values.put(JobmineProviderConstants.InterviewsColumns.INTERVIEWER, interview.interviewer);
			values.put(JobmineProviderConstants.InterviewsColumns.JOB_STATUS, interview.status);
			values.put(JobmineProviderConstants.InterviewsColumns.START_TIME_UNIX, interview.unixTime);
			
			resolver.insert(JobmineProviderConstants.INTERVIEWS_CONTENT_URI, values);
		}
	}
	
	public static void addInterviews (ArrayList<Interview> interviews, ContentResolver resolver) {
		if (interviews != null && interviews.size() > 0) {
			for (int i = 0; i < interviews.size(); i++) {
				addInterview (interviews.get(i), resolver);
			}
		}
	}
	
	public static ArrayList<Interview> getInterviews (ContentResolver resolver) {
		ArrayList<Interview> interviews = new ArrayList<Interview>();
    	
    	Cursor c = resolver.query(JobmineProviderConstants.INTERVIEWS_CONTENT_URI, JobmineProviderConstants.INTERVIEWS_DEFAULT_PROJECTION, null, null, null);
    	
    	if (c.moveToFirst()) {
    		do {
    			interviews.add(new Interview(c));
    		} while (c.moveToNext());
    	}
    	
    	c.close();
    	
    	return interviews;
	}
	
	public static void deleteAllInterviews (ContentResolver resolver) {
		resolver.delete(JobmineProviderConstants.INTERVIEWS_CONTENT_URI, null, null);
	}
}
