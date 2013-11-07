package com.prevas.redmine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.Version;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class NewIssueActivity extends Activity 
{
	static final int STARTDATE_DIALOG_ID = 0;
	static final int DUEDATE_DIALOG_ID = 1;
	
	Integer 					mParentId;
	ProgressDialog 				mProgressDialog;
	private IssueTrackerAdapter mTrackerAdapter;
	private IssueStatusAdapter 	mStatusAdapter;
	private ArrayAdapter<String> mPrioritiesAdapter;
	private VersionAdapter		mVersionAdapter;
	private ArrayAdapter<String> mAssigneeAdapter;
	private HashMap<String, Integer> mPriorityMap;
	
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
	
	private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    
	private int mDueYear;
    private int mDueMonth;
    private int mDueDay;
	
    private Date mStartDate;
    private Date mDueDate;
    
    Integer	mPercentDone = -1;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{	
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        mPriorityMap = new HashMap<String, Integer>();
        mPriorityMap.put("Low", 3);
        mPriorityMap.put("Normal", 4);
        mPriorityMap.put("High", 5);
        mPriorityMap.put("Urgent", 6);
        mPriorityMap.put("Immediate", 7);
        
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
					addDefaultDataToNewIssue();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override  
        protected void onPreExecute()  
        {  
        	mProgressDialog = ProgressDialog.show(NewIssueActivity.this, "", "Loading...");
        }
		
		@Override  
        protected void onPostExecute(Void result)  
        {
			setContentView(R.layout.issue_edit);
			loadContents();
			setCustomTitlebar();
			mProgressDialog.dismiss();
        }
	}
	
	private void setCustomTitlebar()
	{
        Intent intent = getIntent();
        String projTitle = intent.getStringExtra(StringConsts.PROJECT_NAME);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.issue_save_title);
        TextView titleTextView = (TextView) findViewById(R.id.txt_title);
        if (null != titleTextView) {
        	titleTextView.setText(projTitle);
        }
	}
	
	public void onSaveBtnClick(View v)
	{
		new LoadSaveViewTask().execute();
		//finish();
	}
	
	private void addDefaultDataToNewIssue()
	{		
		// Trackers
		try {
			List<Tracker> trackersList = PrevasRedmine.m_redmineManager.getTrackers();
			mTrackerAdapter = new IssueTrackerAdapter(this, android.R.layout.simple_spinner_item, trackersList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Status
		try {
			List<IssueStatus> statusList = PrevasRedmine.m_redmineManager.getStatuses();
			mStatusAdapter = new IssueStatusAdapter(this, android.R.layout.simple_spinner_item, statusList);
		} catch (RedmineException e) {
			e.printStackTrace();
		}
		
		// Priority
		mPrioritiesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
		mPrioritiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (String s : mPriorityMap.keySet()) {
			mPrioritiesAdapter.add(s);
		}
		
		// Version
		try {
			int projectId = PrevasRedmine.mCurrentProjectId;
			List<Version> versionList = PrevasRedmine.m_redmineManager.getVersions(projectId);
			mVersionAdapter = new VersionAdapter(this, android.R.layout.simple_spinner_item, versionList);
		} catch (RedmineException e) {
			e.printStackTrace();
		}
		
		// Assignee
		try {
			List<Membership> mMemberList = PrevasRedmine.getSelectedProjectMembers();
			mAssigneeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
			mAssigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);			
			int count = mMemberList.size();
			for (int i = 0; i < count; ++i) {
				if (null != mMemberList.get(i).getUser()) {
					mAssigneeAdapter.add(mMemberList.get(i).getUser().getFullName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadContents()
	{
		mParentId = getIntent().getIntExtra("Parent", -1);
		
		// Parent id if it is a subtask
		CheckBox parentIdCheckBox = (CheckBox) findViewById(R.id.check_parentId);
		parentIdCheckBox.setClickable(false);
		if (-1 != mParentId) {
			parentIdCheckBox.setChecked(true);
			parentIdCheckBox.setText("Parent #" + Integer.toString(mParentId));
		}
		else {
			parentIdCheckBox.setChecked(false);
			//parentIdCheckBox.setClickable(false);
			parentIdCheckBox.setText("No parent");
		}
		
		mSubjectEditText = (EditText) findViewById(R.id.edit_Subject);
		mSubjectEditText.setHint("Enter Subject");
		
		mDescriptionEditText = (EditText) findViewById(R.id.edit_Description);
		//mDescriptionEditText.setHint("Enter Description");		
		mNotesEditText = (EditText) findViewById(R.id.edit_Notes);
		
		mEstimatedHoursEditText = (EditText) findViewById(R.id.edit_EstimatedTime);
		
		mStartDateTextView = (TextView) findViewById(R.id.txt_StartDateValue);
		
		// Trackers - Spinner
        Spinner spinTracker = (Spinner) findViewById(R.id.spin_Tracker);
        try {
			spinTracker.setAdapter(mTrackerAdapter);			
		} catch (Exception e) {			
			e.printStackTrace();
		}
        
        // Statuses - Spinner
        Spinner spinStatus = (Spinner) findViewById(R.id.spin_Status);
        try {
        	spinStatus.setAdapter(mStatusAdapter);        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        // Priority - Spinner
        Spinner spinPriority = (Spinner) findViewById(R.id.spin_Priority);
        try {       	
        	spinPriority.setAdapter(mPrioritiesAdapter);
        } catch (Exception e) {
        	e.printStackTrace();
        }
       
        // Version - Spinner
        Spinner spinVersion = (Spinner) findViewById(R.id.spin_Version);
    	try {
    		spinVersion.setAdapter(mVersionAdapter);
    	} catch (Exception e) {
        	e.printStackTrace();
        }
    	
    	// Assignee
    	Spinner spinMember = (Spinner) findViewById(R.id.spin_Assignee);
        try {
        	spinMember.setAdapter(mAssigneeAdapter);
        	String currentUser = PrevasRedmine.m_redmineManager.getCurrentUser().toString();
        	spinMember.setSelection(mAssigneeAdapter.getPosition(currentUser));
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
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
        
     // Progress Contents
    	mPercentDoneTextView = (TextView) findViewById(R.id.txt_Progress);
    	mPercentDoneSeekbar = (SeekBar) findViewById(R.id.seekbar_PercentDone);    	
    	try {

			mPercentDoneTextView.setText("0%");
    		mPercentDoneSeekbar.setMax(0);
    		mPercentDoneSeekbar.setMax(100);
    		// setting to 0, there is a bug in android progress bar
			mPercentDoneSeekbar.setProgress(0);			
    		mPercentDoneSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekbar, int progress, boolean fromTouch) 
				{					
					mPercentDoneTextView.setText(Integer.toString(progress) + "%");
					mPercentDone = progress;
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
	}
	
	private User getUserFromMemberList(String name)
	{
		List<Membership> memberList = PrevasRedmine.getSelectedProjectMembers();
		User user = null;
		int count = memberList.size();
		Membership mem = null;
		for (int i = 0; i < count; ++i) {
			mem = memberList.get(i);
			if (null != mem.getUser() && name.equals(mem.getUser().getFullName())) {
				user = mem.getUser();
				break;
			}
		}
		return user;
	}
	
	private boolean isEmpty(EditText editText)
	{
		if (null != editText && editText.getText().toString().trim().length() > 0) {
			return false;
		}
		else {
			return true;
		}
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
                    mStartDate = new Date(selectedYear - 1900, selectedMonth, selectedDay);
                    //mIssue.setStartDate(d);
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
	                mDueDate = new Date(selectedYear - 1900, selectedMonth, selectedDay);	                
	                //mIssue.setDueDate(d);
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
	
	private void createIssue()
	{
		if (isEmpty(mSubjectEditText)) {
			Toast.makeText(this, "Subject can't be empty", Toast.LENGTH_LONG).show();
			return;
		}
		
		Issue issue = new Issue();
		if (-1 != mParentId) {
			issue.setParentId(mParentId);
		}
		
		// Subject
		issue.setSubject(mSubjectEditText.getText().toString());
		
		// Description
		if (!isEmpty(mDescriptionEditText)) {
			issue.setDescription(mDescriptionEditText.getText().toString());
		}
		
		// Notes
		if (!isEmpty(mNotesEditText)) {
			issue.setNotes(mNotesEditText.getText().toString());
		}
		
		// Estimated hours
		String hoursString = mEstimatedHoursEditText.getText().toString();
		if (null != hoursString && hoursString.length() > 0) {
			try {
				float estimatedHours = Float.parseFloat(hoursString);
				issue.setEstimatedHours(estimatedHours);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Start date
		if (null != mStartDate) {
			issue.setStartDate(mStartDate);
		}
		
		// Due date
		if (null != mDueDate) {
			issue.setStartDate(mDueDate);
		}
		
		// Percent done
		if (-1 != mPercentDone) {
			issue.setDoneRatio(mPercentDone);
		}
		
		// Tracker
		Spinner trackerSpinner = (Spinner) findViewById(R.id.spin_Tracker);
		Tracker tracker = mTrackerAdapter.getItem(trackerSpinner.getSelectedItemPosition());
		issue.setTracker(tracker);
				
		// Status
		Spinner statusSpinner = (Spinner) findViewById(R.id.spin_Status);
		Integer statusId = (int) mStatusAdapter.getItemId(statusSpinner.getSelectedItemPosition());
		issue.setStatusId(statusId);
		
		// Priority
		Spinner prioritySpinner = (Spinner) findViewById(R.id.spin_Priority);
		String priorityString = mPrioritiesAdapter.getItem(prioritySpinner.getSelectedItemPosition());
		Integer priorityId = mPriorityMap.get(priorityString);
		issue.setPriorityId(priorityId);
		
		// Version
		Spinner versionSpinner = (Spinner) findViewById(R.id.spin_Version);
		Version version = mVersionAdapter.getItem(versionSpinner.getSelectedItemPosition());
		issue.setTargetVersion(version);
		
		// Assignee
		Spinner assigneeSpinner = (Spinner) findViewById(R.id.spin_Assignee);
		String userString = mAssigneeAdapter.getItem(assigneeSpinner.getSelectedItemPosition());
		issue.setAssignee(getUserFromMemberList(userString));
		
		// Create issue
		try {
			String projectKey = PrevasRedmine.getCurrentProjectIdentifier();
			Issue createdIssue = PrevasRedmine.m_redmineManager.createIssue(projectKey, issue);
			if (null != createdIssue) {
				PrevasRedmine.addNewIssueToMap(createdIssue);
				//Toast.makeText(this, "Issue Created !", Toast.LENGTH_LONG).show();
				//setResult(Activity.RESULT_OK);
			}
		} catch (RedmineException e) {
			e.printStackTrace();
		}
		
	}
	
	private class LoadSaveViewTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) 
		{
			synchronized (this)  
       	 	{
				try {					
					createIssue();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override  
        protected void onPreExecute()  
        {  
        	mProgressDialog = ProgressDialog.show(NewIssueActivity.this, "", "Creating...");
        }
		
		@Override  
        protected void onPostExecute(Void result)  
        {			
			mProgressDialog.dismiss();
			//Toast.makeText(NewIssueActivity.this, "Issue Created !", Toast.LENGTH_LONG).show();
			setResult(Activity.RESULT_OK);
			finish();
        }
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent event) 
    {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View v = getCurrentFocus();
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + v.getLeft() - scrcoords[0];
            float y = event.getRawY() + v.getTop() - scrcoords[1];
            
            if (event.getAction() == MotionEvent.ACTION_UP 
				&& (x < v.getLeft() || x >= v.getRight() 
				|| y < v.getTop() || y > v.getBottom()) ) { 
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }
}
