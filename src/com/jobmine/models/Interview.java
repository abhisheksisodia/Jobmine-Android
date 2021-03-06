package com.jobmine.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.jobmine.providers.JobmineProviderConstants;

public class Interview implements Parcelable {
	
	public String employerName = "";
	public String title = "";
	public String date = "";
	public String length = "";
	public String time = "";
	public String interviewer = "";
	public String id = "";
	public String room = "";
	public String type = "";
	public String instructions = "";
	public int status = -1;
	public long unixTime;

	public Interview() {

	}
	
	public Interview(Cursor c) {
		employerName = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.EMPLOYER_NAME));
		title = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.JOB_TITLE));
		date = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.DATE));
		length = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.LENGTH));
		time = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.START_TIME));
		interviewer = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.INTERVIEWER));
		id = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.JOB_ID));
		room = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.ROOM));
		type = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.TYPE));
		instructions = c.getString(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.INSTRUCTIONS));
		status = c.getInt(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.JOB_STATUS));
		unixTime = c.getLong(c.getColumnIndex(JobmineProviderConstants.InterviewsColumns.START_TIME_UNIX));
	}
	
	public Interview(Parcel in) {
		readFromParcel(in);
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(employerName);
		dest.writeString(title);
		dest.writeString(date);
		dest.writeString(length);
		dest.writeString(time);
		dest.writeString(interviewer);
		dest.writeString(id);
		dest.writeString(room);
		dest.writeString(type);
		dest.writeString(instructions);
		dest.writeInt(status);
		dest.writeLong(unixTime);
	}

	public void readFromParcel(Parcel in) {
		employerName = in.readString();
		title = in.readString();
		date = in.readString();
		length = in.readString();
		time = in.readString();
		interviewer = in.readString();
		id = in.readString();
		room = in.readString();
		type = in.readString();
		instructions = in.readString();
		status = in.readInt();
		unixTime = in.readLong();
	}
	
	public static final Parcelable.Creator<Interview> CREATOR = new Parcelable.Creator<Interview>() {
		public Interview createFromParcel(Parcel in) {
			return new Interview(in);
		}

		public Interview[] newArray(int size) {
			return new Interview[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		
		if (o instanceof Interview) {
			Interview another = (Interview)o;
			boolean rslt = true;
			
			rslt &= employerName.equals(another.employerName);
			rslt &= title.equals(another.title);
			rslt &= date.equals(another.date);
			rslt &= interviewer.equals(another.interviewer);
			rslt &= type.equals(another.type);
			rslt &= instructions.equals(another.instructions);
			rslt &= id.equals(another.id);
			
			return rslt;
		} else {
			return super.equals(o);
		}
		
	}

	@Override
	public int describeContents() {
		return 0;
	};
}
