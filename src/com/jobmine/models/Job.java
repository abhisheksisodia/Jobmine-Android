package com.jobmine.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jobmine.providers.JobmineProviderConstants;

public class Job implements Parcelable {
	
	public String title = "";
	public String id = "";
	public String emplyer = "";
	public String job = "";
	public int jobStatus = -1;
	public int appStatus = -1;
	public String resumes = "";
	public String description = "";
			
	public Job() {

	}
	
	public Job(Parcel in) {
		readFromParcel(in);
	}
	
	public Job(Cursor c) {
		title = c.getString(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.JOB_TITLE));
		id = c.getString(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.JOB_ID));
		emplyer = c.getString(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.EMPLOYER));
		job = c.getString(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.JOB));
		jobStatus = c.getInt(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.JOB_STATUS));
		appStatus = c.getInt(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.APP_STATUS));
		resumes = c.getString(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.RESUMES));
		description = c.getString(c.getColumnIndex(JobmineProviderConstants.ApplicationsColumns.JOB_DESCRIPTION));
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(id);
		dest.writeString(emplyer);
		dest.writeString(job);
		dest.writeInt(jobStatus);
		dest.writeInt(appStatus);
		dest.writeString(resumes);
		dest.writeString(description);
	}

	public void readFromParcel(Parcel in) {
		title = in.readString();
		id = in.readString();
		emplyer = in.readString();
		job = in.readString();
		jobStatus = in.readInt();
		appStatus = in.readInt();
		resumes = in.readString();
		description = in.readString();
	}
	
	public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>() {
		public Job createFromParcel(Parcel in) {
			return new Job(in);
		}

		public Job[] newArray(int size) {
			return new Job[size];
		}
	};


	@Override
	public int describeContents() {
		return 0;
	}
}
