package com.jobmine.interview;

import java.util.List;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.ListView;

import com.jobmine.R;
import com.jobmine.Activity.BindingActivity;
import com.jobmine.models.Interview;
import com.jobmine.service.JobmineInterface;

public class InterviewActivity extends BindingActivity {
	InterviewAdapter adapter;
	List<Interview> interviews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobinterview);
		adapter = new InterviewAdapter(getApplicationContext());
		ListView lv = (ListView) this.findViewById(R.id.interview_list);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				JobmineInterface jobmineInterface = getServiceinterface();
				try {
					interviews = jobmineInterface.getInterviews();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.setContent(interviews);
			}
		}).run();
	}

}
