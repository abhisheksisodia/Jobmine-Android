package com.jobmine.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Interview implements Parcelable {
	
	public String employerName = "";
	public String title = "";
	public String date = "";
	public String length = "";
	public String time = "";
	public String interviewer = "";
	public String id = "";
			
	public Interview() {

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
	}

	public void readFromParcel(Parcel in) {
		employerName = in.readString();
		title = in.readString();
		date = in.readString();
		length = in.readString();
		time = in.readString();
		interviewer = in.readString();
		id = in.readString();
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
	public int describeContents() {
		return 0;
	}
}
