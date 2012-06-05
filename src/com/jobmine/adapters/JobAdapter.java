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
import com.jobmine.common.Common;
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
		ViewHolder holder = null;
		View v = convertView;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.jobentry, null);
			holder = new ViewHolder();
			holder.jobTitle = (TextView) v.findViewById(R.id.textView1);
			holder.jobEmployer = (TextView) v.findViewById(R.id.textView5);
			holder.jobStatusText = (TextView) v.findViewById(R.id.textView2);
			holder.appStatusText = (TextView) v.findViewById(R.id.textView3);
			holder.resumesText = (TextView) v.findViewById(R.id.textView4);
			holder.sideColour = v.findViewById(R.id.side_tab);
			v.setTag(holder);

		} else {
			holder = (ViewHolder) v.getTag();
		}
		final Job job = jobies.get(position);

		holder.jobTitle.setText(job.title);
		holder.jobEmployer.setText(job.emplyer);
		holder.jobStatusText.setText(Common.getJobAppStatusString(job.jobStatus));
		holder.appStatusText.setText(Common.getJobAppStatusString(job.appStatus));

		// Set background colour based on job and app status
		if (job.appStatus == Constants.STATUS_NOT_SELECTED || job.jobStatus == Constants.STATUS_CANCELLED) {
			holder.sideColour.setBackgroundColor(0xFFF4BABA); // red
		} else if (job.appStatus == Constants.STATUS_SELECTED || job.appStatus == Constants.STATUS_SCHEDULED) {
			holder.sideColour.setBackgroundColor(0xFFA3F57F); // green
		} else if (job.appStatus == Constants.STATUS_OFFER || job.jobStatus == Constants.STATUS_OFFER) {
			holder.sideColour.setBackgroundColor(0xFFDAA520); // amber
		} else if (job.jobStatus == Constants.STATUS_RANKING_COMPLETED || job.jobStatus == Constants.STATUS_FILLED) {
			holder.sideColour.setBackgroundColor(Color.GRAY); // gray
		} else {
			holder.sideColour.setBackgroundColor(Color.TRANSPARENT); // nothing
		}

		holder.resumesText.setText(job.resumes + " Applicants");
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, JobDetailsActivity.class);
				intent.putExtra(Constants.idKey, job.id);
				context.startActivity(intent);
			}
		});

		return v;
	}

	// public void setContent(List<Job> list) {
	// jobies = new ArrayList<Job>(list);
	// notifyDataSetChanged();
	// }

	public void setContentFiltered(List<Job> list, boolean applied, boolean selected, boolean notSelected, boolean ranking) {
		jobies.clear();
		for (Job job : list) {
			int status = job.appStatus;
			if (applied && status == Constants.STATUS_APPLIED) {
				jobies.add(job);
			} else if (selected && (status == Constants.STATUS_SELECTED || status == Constants.STATUS_SCHEDULED)) {
				jobies.add(job);
			} else if (notSelected && status == Constants.STATUS_NOT_SELECTED) {
				jobies.add(job);
			} else if (ranking && job.jobStatus == Constants.STATUS_RANKING_COMPLETED) {
				jobies.add(job);
			}
		}
		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView jobTitle;
		TextView jobEmployer;
		TextView jobStatusText;
		TextView appStatusText;
		TextView resumesText;
		View sideColour;
	}
}
