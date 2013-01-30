package com.jobmine.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.jobmine.R;
import com.jobmine.adapters.InterviewAdapter;
import com.jobmine.common.Common;
import com.jobmine.models.Interview;
import com.jobmine.providers.JobmineProvider;
import com.jobmine.service.JobmineInterface;

public class InterviewActivity extends BindingActivity {
	InterviewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobinterview);
		adapter = new InterviewAdapter(this);
		ListView lv = (ListView) this.findViewById(R.id.interview_list);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		new InterviewTask().execute(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_interview, menu);
		return true;
	}
	
	
	private class InterviewTask extends AsyncTask<Boolean, Void, List<Interview>>{
			private ProgressDialog dialog = null;
			
			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(InterviewActivity.this);
				dialog = ProgressDialog.show(InterviewActivity.this, "", "Loading...", true, true);
				dialog.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						InterviewTask.this.cancel(true);
						InterviewActivity.this.finish();
					}
				});
				dialog.show();
			}
			
			@Override
			protected List<Interview> doInBackground(Boolean... params) {
				JobmineInterface jobmineInterface = getServiceinterface();
				List<Interview> interviews = new ArrayList<Interview>();
				
				try {
					//If it failed, notify
					if (!jobmineInterface.getInterviews(params[0])) {
						Common.showNetworkErrorToast (InterviewActivity.this, getServiceinterface().getLastNetworkError());
					}
					
					//Always pull from Provider
					interviews = JobmineProvider.getInterviews(getContentResolver());
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				return interviews;
			}
			
			@Override
			protected void onPostExecute(List<Interview> interviews) {
				dialog.dismiss();
				if (interviews != null) {
					adapter.setContent(interviews);
				} else {
					Toast.makeText(InterviewActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
				}
				
			}


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.refresh:
			new InterviewTask().execute(true);
			break;
		case R.id.applications:
			Intent intent = new Intent(InterviewActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}
