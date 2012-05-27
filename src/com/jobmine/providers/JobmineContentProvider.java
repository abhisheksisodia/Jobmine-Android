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
import com.someguy.jobmine.Job;

public class JobmineContentProvider extends ContentProvider {

	private DatabaseHelper databaseHelper = null;

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "Jobmine.db";
		private static final int DATABASE_VERSION = 1;
		
		private static final String APPLICATIONS_TABLE_NAME = "Applications";
		private static final String APPLICATIONS_CREATE_DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS " + APPLICATIONS_TABLE_NAME + "("
													+ JobmineProviderConstants.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
													+ JobmineProviderConstants.Columns.JOB_TITLE + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB_ID + " TEXT,"
													+ JobmineProviderConstants.Columns.EMPLOYER + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB + " TEXT,"
													+ JobmineProviderConstants.Columns.JOB_STATUS + " TEXT,"
													+ JobmineProviderConstants.Columns.APP_STATUS + " TEXT,"
													+ JobmineProviderConstants.Columns.RESUMES + " TEXT"
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
			db.execSQL("DROP TABLE IF EXISTS " + APPLICATIONS_CREATE_DATABASE_QUERY);
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
		int count = db.delete(DatabaseHelper.APPLICATIONS_TABLE_NAME, selection, selectionArgs);;
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
	
	public static void addApplications (ArrayList<Job> jobs, ContentResolver resolver) {
		
		if (jobs != null && jobs.size() > 0) {
			ContentValues values[] = new ContentValues [jobs.size()];
	
			for (int i = 0; i < jobs.size(); i++) {
				values[i] = new ContentValues();
				values[i].put(JobmineProviderConstants.Columns.JOB_TITLE, jobs.get(i).title);
				values[i].put(JobmineProviderConstants.Columns.JOB_ID, jobs.get(i).id);
				values[i].put(JobmineProviderConstants.Columns.EMPLOYER, jobs.get(i).emplyer);
				values[i].put(JobmineProviderConstants.Columns.JOB, jobs.get(i).job);
				values[i].put(JobmineProviderConstants.Columns.JOB_STATUS, jobs.get(i).jobStatus);
				values[i].put(JobmineProviderConstants.Columns.APP_STATUS, jobs.get(i).appStatus);
				values[i].put(JobmineProviderConstants.Columns.RESUMES, jobs.get(i).resumes);
			}
			
			resolver.bulkInsert(JobmineProviderConstants.CONTENT_URI, values);
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
	
	public static void deleteAll (ContentResolver resolver) {
		resolver.delete(JobmineProviderConstants.CONTENT_URI, null, null);
	}
}
