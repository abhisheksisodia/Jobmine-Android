package com.jobmine.providers;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.jobmine.common.Logger;
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
		private static final int DATABASE_VERSION = 2;
		
		private static final String APPLICATIONS_TABLE_NAME = "Applications";
		private static final String APPLICATIONS_CREATE_DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS " + APPLICATIONS_TABLE_NAME + "("
													+ JobmineProviderConstants.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
													+ JobmineProviderConstants.Columns.JOB_TITLE + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB_ID + " TEXT UNIQUE,"
													+ JobmineProviderConstants.Columns.EMPLOYER + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB_STATUS + " TEXT,"
													+ JobmineProviderConstants.Columns.APP_STATUS + " TEXT,"
													+ JobmineProviderConstants.Columns.RESUMES + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB_DESCRIPTION + " TEXT"
													+ ")";

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(APPLICATIONS_CREATE_DATABASE_QUERY);
			Logger.d("Created new database");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Logger.d("Upgrading database");
			db.execSQL("DROP TABLE IF EXISTS " + APPLICATIONS_TABLE_NAME);
			onCreate(db);
		}

	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext(), DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.dir/vnd.jobmine.applications";
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count = db.delete(DatabaseHelper.APPLICATIONS_TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int inserted = 0;
		
		for (ContentValues v : values) {
			inserted += db.insert(DatabaseHelper.APPLICATIONS_TABLE_NAME, null, v) != -1 ? 1 : 0;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return inserted;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		long rowId = db.insert(DatabaseHelper.APPLICATIONS_TABLE_NAME, null, values);
		
		if (rowId != -1) {
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor c = db.query (DatabaseHelper.APPLICATIONS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count = db.update (DatabaseHelper.APPLICATIONS_TABLE_NAME, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	public static void addApplication (Job job, ContentResolver resolver) {
		if (job != null) {
			ContentValues values = new ContentValues ();
			
			values.put(JobmineProviderConstants.Columns.JOB_TITLE, job.title);
			values.put(JobmineProviderConstants.Columns.JOB_ID, job.id);
			values.put(JobmineProviderConstants.Columns.EMPLOYER, job.emplyer);
			values.put(JobmineProviderConstants.Columns.JOB, job.job);
			values.put(JobmineProviderConstants.Columns.JOB_STATUS, job.jobStatus);
			values.put(JobmineProviderConstants.Columns.APP_STATUS, job.appStatus);
			values.put(JobmineProviderConstants.Columns.RESUMES, job.resumes);
			values.put(JobmineProviderConstants.Columns.JOB_DESCRIPTION, job.description);
			
			resolver.insert(JobmineProviderConstants.CONTENT_URI, values);
		}
	}
	
	public static int updateApplication (Job job, ContentResolver resolver) {
		if (job != null) {
			ContentValues values = new ContentValues ();
			
			values.put(JobmineProviderConstants.Columns.JOB_TITLE, job.title);
			values.put(JobmineProviderConstants.Columns.JOB_ID, job.id);
			values.put(JobmineProviderConstants.Columns.EMPLOYER, job.emplyer);
			values.put(JobmineProviderConstants.Columns.JOB, job.job);
			values.put(JobmineProviderConstants.Columns.JOB_STATUS, job.jobStatus);
			values.put(JobmineProviderConstants.Columns.APP_STATUS, job.appStatus);
			values.put(JobmineProviderConstants.Columns.RESUMES, job.resumes);
			
			return resolver.update(JobmineProviderConstants.CONTENT_URI, values, JobmineProviderConstants.Columns.JOB_ID + " = ?", new String [] { job.id });
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

	public static HashMap<Integer, Job> getApplications (ContentResolver resolver) {
		HashMap<Integer, Job> jobs = new HashMap<Integer, Job>();
    	
    	Cursor c = resolver.query(JobmineProviderConstants.CONTENT_URI, JobmineProviderConstants.DEFAULT_PROJECTION, null, null, null);
    	
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
	
	public static Job getApplication (String jobId, ContentResolver resolver) {

		String selection = JobmineProviderConstants.Columns.JOB_ID + " = ?";
		String args [] = new String [] { jobId };
		
    	Cursor c = resolver.query(JobmineProviderConstants.CONTENT_URI, JobmineProviderConstants.DEFAULT_PROJECTION, selection, args, null);
    	
    	Job j = null;
    	
    	if (c.moveToFirst()) {
    		j = new Job (c);
    	}
    	
    	c.close();
    	
    	return j;
	}

	public static void updateJobDescription (String jobId, String description, ContentResolver resolver) {
		
		ContentValues values = new ContentValues();
		values.put(JobmineProviderConstants.Columns.JOB_DESCRIPTION, description);
		
		String selection = JobmineProviderConstants.Columns.JOB_ID + " = ?";
		String args [] = new String [] { jobId };
		
		resolver.update(JobmineProviderConstants.CONTENT_URI, values, selection, args);
	}
	
	public static void deleteAll (ContentResolver resolver) {
		resolver.delete(JobmineProviderConstants.CONTENT_URI, null, null);
	}
}
