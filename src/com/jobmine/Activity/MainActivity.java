package com.jobmine.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.jobmine.R;
import com.jobmine.adapters.JobAdapter;
import com.jobmine.common.Common;
import com.jobmine.common.Constants;
import com.jobmine.common.EncryptedSharedPreferences;
import com.jobmine.common.JobmineNetworkRequest;
import com.jobmine.models.Job;
import com.jobmine.providers.JobmineProvider;
import com.jobmine.service.JobmineAlarmManager;

public class MainActivity extends BindingActivity {
	/** Called when the activity is first created. */
	ListView mListView;
	boolean displayApplied, displaySelected, displayNotSelected, displayRanked;
	List<Job> jobies;
	JobAdapter jobAdapter;
	SharedPreferences settings;
	Editor editor;
	int lastNetworkError = -1;

	public class getData extends AsyncTask<Boolean, Void, List<Job>> {

		ProgressDialog dialog = null;
		Activity activity;

		public getData(Activity activity) {
			this.activity = activity;
			dialog = new ProgressDialog(activity);
		}
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog = ProgressDialog.show(MainActivity.this, "", "Loading...", true, true);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					dialog.cancel();
				}
			});
			dialog.show();
		}

		@Override
		protected List<Job> doInBackground(Boolean... arg0) {
			List<Job> jobs = new ArrayList<Job>();
			
			JobmineAlarmManager.setUpdateAlarm(MainActivity.this, Constants.SERVICE_UPDATE_TIME_INTERVAL_SECONDS);
			boolean forceUpdate = arg0 [0];
			
			try {
				//If it failed, notify
				if (!getServiceinterface().getApplications(forceUpdate)) {
					lastNetworkError = getServiceinterface().getLastNetworkError();
					Common.showNetworkErrorToast (MainActivity.this, lastNetworkError);
					
					if (lastNetworkError == JobmineNetworkRequest.INVALID_ID_PASS) {
						editor.remove(Constants.userNameKey);
						editor.remove(Constants.pwdKey);
						editor.commit();
					}
				} else {
					lastNetworkError = -1;
				}
				
				//Always pull from Provider
				jobs = JobmineProvider.getApplications(getContentResolver());
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			return jobs;
		}

		@Override
		protected void onPostExecute(List<Job> param) {
			
			dialog.dismiss();
			
			if (!settings.contains(Constants.userNameKey)) {
				createDialog();
			} else {
				if (param != null) {
					jobies = param;
					jobAdapter.setContentFiltered(param, displayApplied, displaySelected, displayNotSelected, displayRanked);
				} 
			}
		}

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		// getSupportActionBar().setDisplayShowHomeEnabled(false);
		// getSupportActionBar().setDisplayShowTitleEnabled(true);
		settings = new EncryptedSharedPreferences(this, this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		editor = settings.edit();
		jobies = new ArrayList<Job>();
		displayApplied = settings.getBoolean(Constants.appliedKey, true);
		displaySelected = settings.getBoolean(Constants.selectedKey, true);
		displayNotSelected = settings.getBoolean(Constants.notSelectedKey, true);
		displayRanked = settings.getBoolean(Constants.rankedKey, true);
		
		jobAdapter = new JobAdapter(this);
		
		((ListView)findViewById(R.id.JobList)).setAdapter(jobAdapter);
	}

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = vi.inflate(R.layout.dialog, null);
		builder.setView(layout);
		builder.setMessage("Please enter your username and password here:").setCancelable(false).setPositiveButton("Log In", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// if yes, then get the password from the
				// dialog, and begin the authemailaccoutn
				// asynctask
				EditText usernameField = (EditText) layout.findViewById(R.id.username1);
				EditText passwordField = (EditText) layout.findViewById(R.id.password1);
				String passwordFieldContent = passwordField.getEditableText().toString();
				String usernameFieldContent = usernameField.getEditableText().toString();
				try {
					editor.putString(Constants.userNameKey, usernameFieldContent);
					editor.putString(Constants.pwdKey, passwordFieldContent);
					editor.commit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new getData(MainActivity.this).execute(false);
			}
		})
		// otherwise,just cancel the dialog
				.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {
			// if the dialog is cancelled, then reset the pressed and email
			// enabled states back to false
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		// only show the dialog if there's no password (TODO: have a way for the
		// user to reset the password)
		builder.show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		
		if (jobies.size() > 0) {
			jobAdapter.setContentFiltered(jobies,displayApplied,displaySelected,displayNotSelected,displayRanked);
		} else {
			if (settings.contains(Constants.userNameKey) && settings.contains(Constants.pwdKey)) {
				try {
					new getData(MainActivity.this).execute(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				createDialog();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.refresh:
			new getData(this).execute(true);
			break;
		case R.id.filter:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout = vi.inflate(R.layout.filter_dialog, null);
			builder.setView(layout);
			final CheckBox applCheckBox = (CheckBox) layout.findViewById(R.id.checkBox1);
			final CheckBox selCheckBox = (CheckBox) layout.findViewById(R.id.checkBox2);
			final CheckBox notSelCheckBox = (CheckBox) layout.findViewById(R.id.checkBox4);
			final CheckBox rankCmpltCheckBox = (CheckBox) layout.findViewById(R.id.checkBox3);

			rankCmpltCheckBox.setChecked(displayRanked);
			applCheckBox.setChecked(displayApplied);
			selCheckBox.setChecked(displaySelected);
			notSelCheckBox.setChecked(displayNotSelected);
			builder.setMessage("Select application type to display:");
			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					displayApplied = applCheckBox.isChecked();
					displaySelected = selCheckBox.isChecked();
					displayNotSelected = notSelCheckBox.isChecked();
					displayRanked = rankCmpltCheckBox.isChecked();
					editor.putBoolean(Constants.appliedKey, displayApplied);
					editor.putBoolean(Constants.selectedKey, displaySelected);
					editor.putBoolean(Constants.notSelectedKey, displayNotSelected);
					editor.putBoolean(Constants.rankedKey, displayRanked);
					editor.commit();
					jobAdapter.setContentFiltered(jobies, displayApplied, displaySelected, displayNotSelected, displayRanked);
				}
			});
			builder.show();
			break;
		case R.id.logout:
			Common.clearAllData(MainActivity.this);
			createDialog();
			break;
		case R.id.interviews:
			Intent intent = new Intent(MainActivity.this, InterviewActivity.class);
			startActivity(intent);
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}