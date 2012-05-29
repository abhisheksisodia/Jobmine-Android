package com.jobmine.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.jobmine.common.Logger;
import com.jobmine.service.JobmineInterface;
import com.jobmine.service.JobmineService;

/**
 * Class used for activities to have access to the service's functions
 * Call getServiceinterface() to get an interface to the service
 * Make sure you call super() for onResume() and onPause()
 * @author Jeremy
 *
 */
public class BindingActivity extends Activity {

	private JobmineInterface serviceInterface = null;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceInterface = JobmineInterface.Stub.asInterface(service);
			BindingActivity.this.onServiceConnected ();
			Logger.d("Client onServiceConnected() was called");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Logger.d("Client onServiceDisconnected() was called");
		}
	};

	private void bindToJobmineService () {
		Intent i = new Intent (JobmineService.class.getName());
		bindService(i, serviceConnection, Activity.BIND_AUTO_CREATE);
	}
	
	private void unbindFromJobmineService () {
		unbindService(serviceConnection);
	}
	
	@Override
	protected void onResume() {
		bindToJobmineService();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unbindFromJobmineService();
		super.onPause();
	}
	
	protected void onServiceConnected () {
		
	}
	
	public JobmineInterface getServiceinterface () {
		return serviceInterface;
	}
	
	
}
