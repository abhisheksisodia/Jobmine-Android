package com.jobmine.Activity;

import java.net.URLEncoder;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jobmine.R;
import com.jobmine.common.Common;
import com.jobmine.common.Constants;
import com.jobmine.models.Job;
import com.jobmine.providers.JobmineProvider;


public class JobDetailsActivity extends BindingActivity {

	//Async task that will retrieve the job description
	private class GetJobDescriptionTask extends AsyncTask<String, Void, Spanned> {

		private ProgressDialog dialog = null;
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(JobDetailsActivity.this);
			dialog = ProgressDialog.show(JobDetailsActivity.this, "", "Loading...", true, true);
			dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					GetJobDescriptionTask.this.cancel(true);
					JobDetailsActivity.this.finish();
				}
			});
			dialog.show();
		}
		
		@Override
		protected Spanned doInBackground(String... params) {
			Spanned descriptionText = null;

			String jobId = getIntent().getStringExtra(Constants.idKey);
			
			try {
				//If it failed, notify
				if (!getServiceinterface().getJobDescription(jobId)) {
					Common.showNetworkErrorToast (JobDetailsActivity.this, getServiceinterface().getLastNetworkError());
				}
				
				//Always pull from Provider
				Job j = JobmineProvider.getApplication(jobId, getContentResolver());
				descriptionText = Html.fromHtml(j.description);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			return descriptionText;
		}
		
		@Override
		protected void onPostExecute(Spanned descriptionText) {
			dialog.dismiss();
			
			TextView descriptionView = (TextView)findViewById(R.id.description);
			descriptionView.setText(descriptionText);
			Linkify.addLinks(descriptionView, Linkify.WEB_URLS);
		};
		
	};

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		
		//Get job id from extra info
		String jobId = getIntent().getStringExtra(Constants.idKey);
		
		if (!Common.trim(jobId).isEmpty()) {
			//Get the job info from provider
			Job currentJob = JobmineProvider.getApplication(jobId, getContentResolver());
			
			//Get the description
			new GetJobDescriptionTask ().execute(currentJob.id);
			
			//Load job info into UI stuff
			loadUI (currentJob);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.job_details);
	}

	private void loadUI (final Job currentJob) {
		//Load job info

		
		TextView titleView = (TextView)findViewById(R.id.title);
		TextView employerView = (TextView)findViewById(R.id.employer);
		TextView jobStatusView = (TextView)findViewById(R.id.jobstatus);
		TextView appStatusView = (TextView)findViewById(R.id.appstatus);
		TextView resumeView = (TextView)findViewById(R.id.resumes);
		titleView.setText(currentJob.title);
		employerView.setText(currentJob.emplyer);
		//wow this is bad, let's find a better way of doing this...
		Linkify.addLinks(employerView, Pattern.compile(".*"), "http://google.com/search?q=");
		jobStatusView.setText(Common.getJobAppStatusString(currentJob.jobStatus));
		appStatusView.setText(Common.getJobAppStatusString(currentJob.appStatus));
		resumeView.setText(currentJob.resumes+" Applicants");

		employerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://google.com/search?q=" + URLEncoder.encode(currentJob.emplyer)));
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_details, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.refresh:
			new GetJobDescriptionTask().execute();
			break;
		case R.id.applications:
			Intent intent = new Intent(JobDetailsActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.interviews:
			Intent intent2 = new Intent(JobDetailsActivity.this, InterviewActivity.class);
			startActivity(intent2);
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	
}

