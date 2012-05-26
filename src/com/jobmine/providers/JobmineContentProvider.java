package com.jobmine.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class JobmineContentProvider extends ContentProvider {

	private DatabaseHelper databaseHelper = null;

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "Jobmine.db";
		private static final int DATABASE_VERSION = 1;
		
		private static final String APPLICATIONS_TABLE_NAME = "Applications";
		private static final String APPLICATIONS_CREATE_DATABASE_QUERY = "CREATE TABLE IF NOT EXISTS " + APPLICATIONS_TABLE_NAME + "("
													+ JobmineProviderConstants.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT)";

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(APPLICATIONS_CREATE_DATABASE_QUERY);
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
	public Uri insert(Uri uri, ContentValues values) {

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		long rowId = db.insert(DatabaseHelper.APPLICATIONS_TABLE_NAME, null, values);
		
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(JobmineProviderConstants.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
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

}
