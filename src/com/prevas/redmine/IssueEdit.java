package com.prevas.redmine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.prevas.redmine.IssueDetailTabs.DetailsHolder;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;

public class IssueEdit extends Activity 
{
	static final int STARTDATE_DIALOG_ID = 0;
	static final int DUEDATE_DIALOG_ID = 1;
	
	ProgressDialog mProgressDialog;
	
	private IssueTrackerAdapter 	mTrackerAdapter;
	private IssueStatusAdapter 		mStatusAdapter;
	private UserAdapter				mUserAdapter;
	private VersionAdapter			mVersionAdapter;
	private ArrayAdapter<String> 	mPrioritiesAdapter;
	private ArrayAdapter<String> 	mAssigneeAdapter;
	private List<Membership> 		mMemberList;
	
	private EditText mSubjectEditText;
	private EditText mDescriptionEditText;
	private EditText mNotesEditText;
	private EditText mEstimatedHoursEditText;
	private TextView mPercentDoneTextView;
	private SeekBar mPercentDoneSeekbar;
	private TextView mStartDateTextView;
	private TextView mDueDateTextView;
	private Button mStartDateBtn;
	private Button mDueDateBtn;
	
	private static Issue mIssue;
		
	private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    
	private int mDueYear;
    private int mDueMonth;
    private int mDueDay;
    
    
	private HashMap<String, Integer> mPriorityMap;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{	
        super.onCreate(savedInstanceState);
        // Prevent soft keyboard to come up as soon as activity starts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        mPriorityMap = new HashMap<String, Integer>();
        mPriorityMap.put("Low", 3);
        mPriorityMap.put("Normal", 4);
        mPriorityMap.put("High", 5);
        mPriorityMap.put("Urgent", 6);
        mPriorityMap.put("Immediate", 7);
        
        mIssue = IssueList.getCurrentIssue();
        
        new LoadViewTask().execute();
	}	

	private class LoadViewTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			synchronized (this)  
       	 	{
				try {
					getDataFromIssue();					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override  
        protected void onPreExecute()  
        {  
        	mProgressDialog = ProgressDialog.show(IssueEdit.this, "", "Loading...");
        }
		
		@Override  
        protected void onPostExecute(Void result)  
        {
			setContentView(R.layout.issue_edit);
			loadContents();
			//setTitle("#" + mIssue.getId().toString() + " " + mIssue.getSubject());
			setCustomTitlebar();
			mProgressDialog.dismiss();
        }
		
		private void setCustomTitlebar()
		{	        
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.issue_save_title);
	        TextView titleTextView = (TextView) findViewById(R.id.txt_title);
	        if (null != titleTextView) {
	        	titleTextView.setText("#" + mIssue.getId().toString() + " " + mIssue.getSubject());
	        }
		}
	}
		
	private void getDataFromIssue()
	{
		try {
			List<Tracker> trackersList = PrevasRedmine.m_redmineManager.getTrackers();
			mTrackerAdapter = new IssueTrackerAdapter(this, android.R.layout.simple_spinner_item, trackersList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<IssueStatus> statusList = PrevasRedmine.m_redmineManager.getStatuses();
			mStatusAdapter = new IssueStatusAdapter(this, android.R.layout.simple_spinner_item, statusList);
		} catch (RedmineException e) {
			e.printStackTrace();
		}
    	
		try {
			int projectId = PrevasRedmine.mCurrentProjectId;
			List<Version> versionList = PrevasRedmine.m_redmineManager.getVersions(projectId);
			mVersionAdapter = new VersionAdapter(this, android.R.layout.simple_spinner_item, versionList);
		} catch (RedmineException e) {
			e.printStackTrace();
		}
		
		try {
			mMemberList = PrevasRedmine.getSelectedProjectMembers();
			mAssigneeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
			mAssigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mAssigneeAdapter.add(" ");
			int count = mMemberList.size();
			for (int i = 0; i < count; ++i) {
				if (null != mMemberList.get(i).getUser()) {
					mAssigneeAdapter.add(mMemberList.get(i).getUser().getFullName());
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	mPrioritiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
		mPrioritiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (String s : mPriorityMap.keySet()) {
			mPrioritiesAdapter.add(s);
		}
	}
	
	private void loadContents()
	{
		DetailsHolder holder = IssueDetailTabs.getDetailsHolder();
		
		// Parent id if it is a subtask
		CheckBox parentIdCheckBox = (CheckBox) findViewById(R.id.check_parentId);
		if (-1 != holder.mParentId) {
			parentIdCheckBox.setEnabled(false);
			parentIdCheckBox.setChecked(true);
			parentIdCheckBox.setText("Parent #" + Integer.toString(holder.mParentId));
		}
		else {
			parentIdCheckBox.setVisibility(0);
		}
		
//		// Parent id if it is a subtask
//		mParentIdCheckBox = (CheckBox) findViewById(R.id.check_parentId);
//		mParentIdCheckBox.setEnabled(false);
//		if (-1 != holder.mParentId) {
//			mParentIdCheckBox.setChecked(true);
//			mParentIdCheckBox.setText("Parent #" + holder.mParentId.toString());
//		} 
//		else {
//			mParentIdCheckBox.setChecked(false);
//			mParentIdCheckBox.setText("No parent");
//		}
		
		
		// Subject
		mSubjectEditText = (EditText) findViewById(R.id.edit_Subject);
		mSubjectEditText.setText(holder.mSubject);

        
        // Trackers - Spinner
        Spinner spinTracker = (Spinner) findViewById(R.id.spin_Tracker);
        try {
			spinTracker.setAdapter(mTrackerAdapter);
			spinTracker.setSelection(mTrackerAdapter.getItemIndexByName(holder.mTracker), true);
			spinTracker.setOnItemSelectedListener(new OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) 
	            {
	                Tracker tracker = mTrackerAdapter.getItem(position);
	                mIssue.setTracker(tracker);
	            }

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) 
				{
					// nothing to do
				}
	        });

		} catch (Exception e) {			
			e.printStackTrace();
		}
        
        // Statuses - Spinner
        Spinner spinStatus = (Spinner) findViewById(R.id.spin_Status);
        try {
        	spinStatus.setAdapter(mStatusAdapter);		
    		spinStatus.setSelection(mStatusAdapter.getItemIndexByName(holder.mStatus), true);
			spinStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) 
	            {
	                IssueStatus status = mStatusAdapter.getItem(position);
	                mIssue.setStatusId(status.getId());
	                mIssue.setStatusName(status.getName());
	            }

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) 
				{
					// nothing to do
				}
	        });
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        // Members
        Spinner spinMember = (Spinner) findViewById(R.id.spin_Assignee);
        try {
        	spinMember.setAdapter(mAssigneeAdapter);
        	if (!("--").equals(holder.mAssignee)) {
        		spinMember.setSelection(mAssigneeAdapter.getPosition(holder.mAssignee));
        	}
        	spinMember.setOnItemSelectedListener(new OnItemSelectedListener() {
        	    private int memberIndex = 0;
	            @Override
	            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) 
	            {
	            	if (memberIndex <= 0) {
	            		memberIndex++;
	            	}
	            	else {
	            		String userString = mAssigneeAdapter.getItem(position);
		            	User user = getUserFromMemberList(userString);
		            	if (null != user) {
		            		mIssue.setAssignee(user);
		            	}
	            	}
	            }
            
				@Override
				public void onNothingSelected(AdapterView<?> adapterView) 
				{
					// nothing to do
				}
	        });
        	
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
                
        Spinner spinPriority = (Spinner) findViewById(R.id.spin_Priority);
        try {       	
        	spinPriority.setAdapter(mPrioritiesAdapter);
    		spinPriority.setSelection(mPrioritiesAdapter.getPosition(holder.mPriority));
        	spinPriority.setOnItemSelectedListener(new OnItemSelectedListener() {
        	     public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) 
        	     {
        	    	 String priorityString = mPrioritiesAdapter.getItem(position);
        	    	 Integer priorityId = mPriorityMap.get(priorityString);
        	    	 mIssue.setPriorityText(priorityString);
        	    	 mIssue.setPriorityId(priorityId);
        	     }
        	     public void onNothingSelected(AdapterView<?> adapterView) 
        	     { 
        	    	 // do nothing
        	     }
        	});

        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        mStartDateTextView = (TextView) findViewById(R.id.txt_StartDateValue);       	
    	mStartDateTextView.setText(holder.mStartDate);
        
    	mStartDateBtn = (Button) findViewById(R.id.btn_StartDateChange);
        mStartDateBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) 
			{
				showDialog(STARTDATE_DIALOG_ID);
			}
		});
        /* get the current date */
        final Calendar startDateCal = Calendar.getInstance();
        mStartYear = startDateCal.get(Calendar.YEAR);
        mStartMonth = startDateCal.get(Calendar.MONTH);
        mStartDay = startDateCal.get(Calendar.DAY_OF_MONTH);
   
        mDueDateTextView = (TextView) findViewById(R.id.txt_DueDateValue);
    	mDueDateTextView.setText(holder.mDueDate);
        
        mDueDateBtn = (Button) findViewById(R.id.btn_DueDateChange);
        mDueDateBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) 
			{
				showDialog(DUEDATE_DIALOG_ID);
			}
		});
        
        /* get the current date */
        final Calendar dueDateCal = Calendar.getInstance();
        mDueYear = dueDateCal.get(Calendar.YEAR);
        mDueMonth = dueDateCal.get(Calendar.MONTH);
        mDueDay = dueDateCal.get(Calendar.DAY_OF_MONTH);
       
        // Estimated Hours
        mEstimatedHoursEditText = (EditText) findViewById(R.id.edit_EstimatedTime);
    	mEstimatedHoursEditText.setText(holder.mEstimatedHours);
        
        
        // Progress Contents
    	mPercentDoneTextView = (TextView) findViewById(R.id.txt_Progress);
    	mPercentDoneSeekbar = (SeekBar) findViewById(R.id.seekbar_PercentDone);    	
    	try {

			mPercentDoneTextView.setText(holder.mPercentDone + "%");
    		mPercentDoneSeekbar.setMax(0);
    		mPercentDoneSeekbar.setMax(100);
    		// setting to 0, there is a bug in android progress bar
			mPercentDoneSeekbar.setProgress(0);			
			mPercentDoneSeekbar.setProgress(Integer.parseInt(holder.mPercentDone));
    		mPercentDoneSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekbar, int progress, boolean fromTouch) 
				{					
					mPercentDoneTextView.setText(Integer.toString(progress) + "%");
					mIssue.setDoneRatio(progress);
				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) 
				{
				}

				@Override
				public void onStopTrackingTouch(SeekBar arg0) 
				{
					
				}
    			
    		});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	Spinner spinVersion = (Spinner) findViewById(R.id.spin_Version);
    	try {
    		spinVersion.setAdapter(mVersionAdapter);
			spinVersion.setSelection(mVersionAdapter.getItemIndexByName(holder.mVersion));
        	spinVersion.setOnItemSelectedListener(new OnItemSelectedListener() {
	       	    @Override 
        		public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) 
	       	    {
	       	    	Version version = mVersionAdapter.getItem(position);
					mIssue.setTargetVersion(version);
	       	    }
	       	    @Override
	       	    public void onNothingSelected(AdapterView<?> adapterView) 
	       	    { 
	       	    	// do nothing
	       	    }
        	});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	// Description
    	mDescriptionEditText = (EditText) findViewById(R.id.edit_Description);    	
		mDescriptionEditText.append(holder.mDescription);
    	    	
    	mNotesEditText = (EditText) findViewById(R.id.edit_Notes);
	}
	
	private void updateStartDateDisplay() 
	{
		mStartDateTextView.setText( new StringBuilder()
	                // Month is 0 based so add 1
	                .append(mStartDay).append("-")
	                .append(mStartMonth + 1).append("-")
	                .append(mStartYear).append(" "));					
	}

	private void updateDueDateDisplay() 
	{
		mDueDateTextView.setText( new StringBuilder()
	                // Month is 0 based so add 1
	                .append(mDueDay).append("-")
	                .append(mDueMonth + 1).append("-")
	                .append(mDueYear).append(" "));
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.set(mDueYear, mDueMonth, mDueDay);
//		Date d = cal.getTime();
//		mIssue.setDueDate(cal.getTime());
	}
	
   	// START Date - the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
    			@Override
                public void onDateSet(DatePicker view, int selectedYear, 
                                      int selectedMonth, int selectedDay) 
                {
                    mStartYear = selectedYear;
                    mStartMonth = selectedMonth;
                    mStartDay = selectedDay;                     
                    Date d = new Date(selectedYear - 1900, selectedMonth, selectedDay);
                    mIssue.setStartDate(d);
                    updateStartDateDisplay();
                }
            };
            
	// DUE Date - the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDueDateSetListener =
	        new DatePickerDialog.OnDateSetListener() {	
				@Override
	            public void onDateSet(DatePicker view, int selectedYear, 
	                                  int selectedMonth, int selectedDay) 
	            {
	                mDueYear = selectedYear;
	                mDueMonth = selectedMonth;
	                mDueDay = selectedDay;
	                Date d = new Date(selectedYear - 1900, selectedMonth, selectedDay);	                
	                mIssue.setDueDate(d);
	                updateDueDateDisplay();
	            }
	        };            
            
	//@Override
	protected Dialog onCreateDialog(int id) 
	{
	    switch (id) {
	    case STARTDATE_DIALOG_ID:
	        return new DatePickerDialog(this, mStartDateSetListener, mStartYear, mStartMonth, mStartDay);	    
	    case DUEDATE_DIALOG_ID:
	        return new DatePickerDialog(this, mDueDateSetListener, mDueYear, mDueMonth, mDueDay);
	    }
	    return null;
	}
	
	public static Issue getUpdatedIssue() 
	{
		return mIssue;
	}
	
	private void saveIssue()
	{
		mIssue.setSubject(mSubjectEditText.getText().toString());
		mIssue.setDescription(mDescriptionEditText.getText().toString());
		mIssue.setNotes(mNotesEditText.getText().toString());
		String hoursString = mEstimatedHoursEditText.getText().toString();
		if (null != hoursString && hoursString.length() > 0) {
			try {
				float estimatedHours = Float.parseFloat(hoursString);
				mIssue.setEstimatedHours(estimatedHours);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		IssueDetailTabs.updateCurrentIssue(mIssue);
		setResult(Activity.RESULT_OK, new Intent());
		finish();
	}
	
	private User getUserFromMemberList(String name)
	{
		User user = null;
		int count = mMemberList.size();
		Membership mem = null;
		for (int i = 0; i < count; ++i) {
			mem = mMemberList.get(i);
			if (null != mem.getUser() && name.equals(mem.getUser().getFullName())) {
				user = mem.getUser();
				break;
			}
		}
		return user;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_prevas_redmine, menu);
		menu.getItem(0).setVisible(false);
		menu.getItem(2).setVisible(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) {
	    case R.id.project_save:
	    	saveIssue();
	    	break;
	    }
		return true;
	}
	
	public void onSaveBtnClick(View v)
	{
		saveIssue();
	}
}
