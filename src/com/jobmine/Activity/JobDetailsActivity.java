package com.jobmine.Activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jobmine.R;
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
			dialog = ProgressDialog.show(JobDetailsActivity.this, "", "Loading...", true, false);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					dialog.cancel();
				}
			});
			dialog.show();
		}
		
		@Override
		protected Spanned doInBackground(String... params) {
			Spanned descriptionText = null;
			
			try {
				descriptionText = Html.fromHtml(getServiceinterface().getJobDescription(params [0]));
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
		
		if (!jobId.isEmpty()) {
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
		jobStatusView.setText(currentJob.jobStatus);
		appStatusView.setText(currentJob.appStatus);
		resumeView.setText(currentJob.resumes+" Applicants");
		
		employerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
				intent.putExtra(SearchManager.QUERY, currentJob.emplyer);
				startActivity(intent);
			}
		});
	}
}

