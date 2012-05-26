package com.someguy.jobmine;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobmine.common.Logger;
import com.jobmine.service.JobmineInterface;
import com.jobmine.service.JobmineService;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	ListView mListView;
	boolean displayApplied, displaySelected, displayNotSelected, displayRanked;
	ArrayList<Job> jobies;
	SharedPreferences settings;
	Editor editor;

	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String appliedKey = "displayApplied";
	public static final String selectedKey = "displaySelected";
	public static final String notSelectedKey = "displayNotSelected";
	public static final String rankedKey = "displayRanked";
	public static final String userNameKey = "USERNAMEKEY";
	public static final String pwdKey = "PWDKEY";
	public static final String idKey = "idkey";
	public static final String titleKey = "titlekey";
	public static final String employerKey = "employerkey";
	public static final String jobStatusKey = "jobstatuskey";
	public static final String appStatusKey = "appstatuskey";
	public static final String resumeKey = "resumekey";

	private JobmineInterface serverInterface = null;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serverInterface = JobmineInterface.Stub.asInterface(service);

			try {
				serverInterface.go();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Logger.d("Client onServiceConnected() was called");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Logger.d("Client onServiceDisconnected() was called");
		}
	};

	private void startJobmineService() {
		Intent i = new Intent(JobmineService.class.getName());

		startService(i);
		bindService(i, serviceConnection, Activity.BIND_AUTO_CREATE);
	}

	private void unbindFromService() {
		Intent i = new Intent(JobmineService.class.getName());

		unbindService(serviceConnection);
	}

	

	public class getData extends AsyncTask<Void, Void, ArrayList<Job>> {

		ProgressDialog dialog;
		private getData selfReference;
		Activity activity;

		public getData(Activity activity) {
			selfReference = this;
			this.activity = activity;
			dialog = new ProgressDialog(activity);
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(activity, "", "Loading...", true, true);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (selfReference != null) {
						selfReference.cancel(true);
					}
				}
			});
			dialog.show();
		}

		@Override
		protected ArrayList<Job> doInBackground(Void... arg0) {
			return Common.getJobmine();
		}

		@Override
		protected void onPostExecute(ArrayList<Job> param) {
			dialog.dismiss();
			if (param != null) {
				jobies = param;
				setContent(param);
			} else {
				Toast.makeText(MainActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
			}

		}

	}

	private void setContent(ArrayList<Job> jobies) {
		LinearLayout list = (LinearLayout) findViewById(R.id.linearlayout1);
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		list.removeAllViews();
		for (final Job job : jobies) {
			if (!job.title.equals("")) {

				View v = li.inflate(R.layout.jobentry, null);

				TextView jobTitle = (TextView) v.findViewById(R.id.textView1);
				TextView jobEmployer = (TextView) v.findViewById(R.id.textView5);
				TextView jobStatusText = (TextView) v.findViewById(R.id.textView2);
				TextView appStatusText = (TextView) v.findViewById(R.id.textView3);
				TextView resumesText = (TextView) v.findViewById(R.id.textView4);

				jobTitle.setText(job.title);
				jobEmployer.setText(job.emplyer);
				jobStatusText.setText(job.jobStatus);
				appStatusText.setText(job.appStatus);
				if (job.appStatus.contains("Not Selected")) {
					appStatusText.setBackgroundResource(R.color.red);
				} else if (job.appStatus.contains("Selected") || job.appStatus.contains("Scheduled")) {
					appStatusText.setBackgroundResource(R.color.green);
				} else if (job.appStatus.contains("Alternate")) {
					appStatusText.setBackgroundColor(R.color.amber);
				}
				if (job.jobStatus.contains("Cancelled")) {
					jobStatusText.setBackgroundColor(R.color.red);
				} else if (job.jobStatus.contains("Ranking Completed") || job.jobStatus.contains("Offer")) {
					jobStatusText.setBackgroundColor(R.color.green);
				}
				resumesText.setText(job.resumes + " Applicants");
				v.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							Intent intent = new Intent(MainActivity.this, JobDetails.class);
							intent.putExtra(titleKey, job.title);
							intent.putExtra(idKey, job.id);
							intent.putExtra(employerKey, job.emplyer);
							intent.putExtra(jobStatusKey, job.jobStatus);
							intent.putExtra(appStatusKey, job.appStatus);
							intent.putExtra(resumeKey, job.resumes);
							startActivity(intent);
						}
						return true;
					}
				});

				if ((displayApplied && job.appStatus.contains("Applied") || (displaySelected && job.appStatus.contains("Selected") && !job.appStatus.contains("Not"))
						|| (displaySelected && job.appStatus.contains("Alternate")) || (displaySelected && job.appStatus.contains("Scheduled")) || (displayNotSelected
						&& job.appStatus.contains("Not Selected") || (displayNotSelected && job.jobStatus.contains("Cancelled")) || (displayRanked && job.jobStatus.contains("Ranking Completed"))))) {
					list.addView(v);
				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		startJobmineService();

		// getSupportActionBar().setDisplayShowHomeEnabled(false);
		// getSupportActionBar().setDisplayShowTitleEnabled(true);
		settings = new EncryptedSharedPreferences(this, this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
		editor = settings.edit();
		jobies = new ArrayList<Job>();
		displayApplied = settings.getBoolean(appliedKey, true);
		displaySelected = settings.getBoolean(selectedKey, true);
		displayNotSelected = settings.getBoolean(notSelectedKey, true);
		displayRanked = settings.getBoolean(rankedKey, true);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindFromService();
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

				Common.setUserName(usernameFieldContent);
				Common.setPassword(passwordFieldContent);
				try {
					editor.putString(userNameKey, usernameFieldContent);
					editor.putString(pwdKey, passwordFieldContent);
					editor.commit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new getData(MainActivity.this).execute(new Void[3]);
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
		if (jobies.size() > 0) {
			setContent(jobies);
		} else {
			if (settings.contains(userNameKey) && settings.contains(pwdKey)) {
				try {
					Common.setUserName(settings.getString(userNameKey, ""));
					Common.setPassword(settings.getString(pwdKey, ""));
					new getData(MainActivity.this).execute(new Void[3]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.refresh:
			new getData(this).execute(new Void[3]);
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
					editor.putBoolean(appliedKey, displayApplied);
					editor.putBoolean(selectedKey, displaySelected);
					editor.putBoolean(notSelectedKey, displayNotSelected);
					editor.putBoolean(rankedKey, displayRanked);
					editor.commit();
					setContent(jobies);
				}
			});
			builder.show();
			break;
		case R.id.logout:
			editor.remove(userNameKey);
			editor.remove(pwdKey);
			editor.commit();
			LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearlayout1);
			linearLayout1.removeAllViews();
			createDialog();
			break;
		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}