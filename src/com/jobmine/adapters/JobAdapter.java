package com.jobmine.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jobmine.R;
import com.jobmine.Activity.JobDetailsActivity;
import com.jobmine.common.Constants;
import com.jobmine.models.Job;

public class JobAdapter extends BaseAdapter {
	Context context;
	List<Job> jobies = new ArrayList<Job>();

	public JobAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return jobies.size();
	}

	@Override
	public Job getItem(int arg0) {
		return jobies.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.jobentry, null);
		}
		final Job job = jobies.get(position);

		TextView jobTitle = (TextView) v.findViewById(R.id.textView1);
		TextView jobEmployer = (TextView) v.findViewById(R.id.textView5);
		TextView jobStatusText = (TextView) v.findViewById(R.id.textView2);
		TextView appStatusText = (TextView) v.findViewById(R.id.textView3);
		TextView resumesText = (TextView) v.findViewById(R.id.textView4);
		View sideColour = v.findViewById(R.id.side_tab);

		jobTitle.setText(job.title);
		jobEmployer.setText(job.emplyer);
		jobStatusText.setText(job.jobStatus);
		appStatusText.setText(job.appStatus);

		// Set background colour based on job and app status
		if (job.appStatus.contains("Not Selected") || job.jobStatus.contains("Cancelled")) {
			sideColour.setBackgroundColor(0xFFF4BABA); // red
		} else if (job.appStatus.contains("Selected") || job.appStatus.contains("Scheduled")) {
			sideColour.setBackgroundColor(0xFFA3F57F); // green
		} else if (job.appStatus.contains("Offer") || job.jobStatus.contains("Offer")) {
			sideColour.setBackgroundColor(0xFFDAA520); // amber
		} else if (job.jobStatus.contains("Ranking Completed") || job.jobStatus.contains("Filled")) {
			sideColour.setBackgroundColor(Color.GRAY); // gray
		} else {
			sideColour.setBackgroundColor(Color.TRANSPARENT); // gray
		}

		resumesText.setText(job.resumes + " Applicants");
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, JobDetailsActivity.class);
				intent.putExtra(Constants.idKey, job.id);
				context.startActivity(intent);

			}
		});
		//
		// if ((displayApplied && job.appStatus.contains("Applied") ||
		// (displaySelected && job.appStatus.contains("Selected") &&
		// !job.appStatus.contains("Not"))
		// || (displaySelected && job.appStatus.contains("Alternate")) ||
		// (displaySelected && job.appStatus.contains("Scheduled")) ||
		// (displayNotSelected
		// && job.appStatus.contains("Not Selected") || (displayNotSelected &&
		// job.jobStatus.contains("Cancelled")) || (displayRanked &&
		// job.jobStatus.contains("Ranking Completed"))))) {
		// list.addView(v);
		// }

		return v;
	}

//	public void setContent(List<Job> list) {
//		jobies = new ArrayList<Job>(list);
//		notifyDataSetChanged();
//	}

	public void setContentFiltered(List<Job> list, boolean applied, boolean selected, boolean notSelected, boolean ranking) {
		jobies.clear();
		for (Job job : list) {
			String status = job.appStatus;
			if (applied && status.contains("Applied")) {
				jobies.add(job);
			} else if (selected && (status.contains("Selected") || status.contains("Scheduled"))) {
				jobies.add(job);
			} else if (notSelected && status.contains("Not Selected")) {
				jobies.add(job);
			} else if (ranking && job.jobStatus.contains("Ranking Completed")) {
				jobies.add(job);
			}
		}
		notifyDataSetChanged();
	}
}
