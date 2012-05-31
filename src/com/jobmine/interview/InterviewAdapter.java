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
import com.jobmine.models.Interview;

public class InterviewAdapter extends BaseAdapter{
	List<Interview> interview = new ArrayList<Interview>();
	Context context;
	
	public InterviewAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public Interview getItem(int position) {
		return interview.get(position);
	}


	@Override
	public int getCount() {
		return interview.size();
	}


	@Override
	public long getItemId(int arg0) {
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null){
			v = LayoutInflater.from(context).inflate(R.layout.interview_entry, null);
		}
		Interview i = interview.get(position);
		
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
		
		return v;
		
	}
	
	public void setContent(List<Interview> interviews){
		this.interview = interviews;
		notifyDataSetChanged();
	}
	
	
	


	

}
