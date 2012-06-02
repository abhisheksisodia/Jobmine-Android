package com.jobmine.interview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jobmine.R;
import com.jobmine.common.Logger;
import com.jobmine.models.Interview;

public class InterviewAdapter extends BaseAdapter {
	private static final int INTERVIEW_TYPE = 0;
	private static final int HEADER_TYPE = 1;
	List<Interview> interview = new ArrayList<Interview>();
	List<Interview> groupInterview = new ArrayList<Interview>();
	Context context;

	public InterviewAdapter(Context context) {
		this.context = context;
	}

	@Override
	public Interview getItem(int position) {
		Logger.d("get possition " + position);
		if (position < interview.size()) {
			return interview.get(position);
		} else if (position > interview.size()) {
			return interview.get(position - interview.size() - 1);
		} else {
			return null;
		}
	}

	@Override
	public int getCount() {
		return interview.size() + groupInterview.size() + (groupInterview.size() == 0 ? 0 : 1);
	}

	@Override
	public int getItemViewType(int position) {
		if ( groupInterview.size() != 0 && position == interview.size()){
			return HEADER_TYPE;
		} else {
			return INTERVIEW_TYPE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (getItemViewType(position) == INTERVIEW_TYPE) {
			Interview i = getItem(position);
			if (convertView == null) {
				v = LayoutInflater.from(context).inflate(R.layout.interview_entry, null);
			}
			TextView employer = (TextView) v.findViewById(R.id.interview_employer);
			TextView title = (TextView) v.findViewById(R.id.interview_title);
			TextView time = (TextView) v.findViewById(R.id.interview_time);
			TextView date = (TextView) v.findViewById(R.id.interview_date);
			TextView length = (TextView) v.findViewById(R.id.interview_length);
			TextView room = (TextView) v.findViewById(R.id.interview_room);

			title.setText(i.title);
			time.setText(i.time);
			date.setText(i.date);
			room.setText(i.room);
			length.setText(i.length + " Minutes");

			employer.setText(i.employerName);
		} else {
			if (convertView == null){
				v = LayoutInflater.from(context).inflate(R.layout.interview_header, null);
			}
		}

		return v;

	}

	public void setContent(List<Interview> interviews) {
		for (Interview interview : interviews) {
			if (interview.type.equals("Group")) {
				groupInterview.add(interview);
			} else {
				this.interview.add(interview);
			}
		}
		notifyDataSetChanged();
	}

}
