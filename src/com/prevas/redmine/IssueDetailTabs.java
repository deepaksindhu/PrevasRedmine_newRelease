package com.prevas.redmine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.taskadapter.redmineapi.RedmineManager.INCLUDE;
import com.taskadapter.redmineapi.bean.Changeset;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;

public class IssueDetailTabs extends TabActivity 
{
	private static List<Journal> mJournalList;
	private static List<Changeset> mChangeSetList;
	private static DetailsHolder mDetailsHolder;
	private ProgressDialog mProgressDialog;
	private TabHost mTabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
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
					holdIssueData();
					//addTabs();					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override  
        protected void onPreExecute()  
        {  
        	mProgressDialog = ProgressDialog.show(IssueDetailTabs.this, "", "Loading...");
        }
		
		@Override  
        protected void onPostExecute(Void result)  
		{
			setContentView(R.layout.issue_detail_tabs);			
			
			addTabs(); // once the view is set, add tabs to tab host
			
			setCustomTitlebar(); // set custom title bar with edit icon

			mProgressDialog.dismiss();
        }
	}
	
	private void setCustomTitlebar()
	{		
        Issue currentIssue = IssueList.getCurrentIssue();
        String issueId = currentIssue.getId().toString();
        String issueSubject = currentIssue.getSubject();
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.issue_edit_icon);
        TextView titleTextView = (TextView) findViewById(R.id.txt_title);
        if (null != titleTextView) {
        	titleTextView.setText("#" + issueId + " " + issueSubject);
        }
	}
	
	private void addTabs()
	{
		mTabHost = getTabHost();
				
        // Tab for Details
        TabSpec detailSpec = mTabHost.newTabSpec("Details");
        // setting Title and Icon for the Tab
        detailSpec.setIndicator("Details");//, getResources().getDrawable(R.drawable.icon_photos_tab));
        Intent detailIntent = new Intent(this, DetailsTabActivity.class);
        detailSpec.setContent(detailIntent);
 
        // Tab for Description
        TabSpec descSpec = mTabHost.newTabSpec("Description");
        descSpec.setIndicator("Description");//, getResources().getDrawable(R.drawable.icon_videos_tab));
        Intent descIntent = new Intent(this, DescriptionTabActivity.class);
        descSpec.setContent(descIntent);
 
        // Tab for History
        TabSpec historySpec = mTabHost.newTabSpec("History");
        historySpec.setIndicator("History");//, getResources().getDrawable(R.drawable.icon_videos_tab));
        Intent historyIntent = new Intent(this, HistoryTabActivity.class);
        historySpec.setContent(historyIntent);
        
        // Adding all TabSpec to TabHost
        mTabHost.addTab(detailSpec); // Adding Details tab
        mTabHost.addTab(descSpec); // Adding Description tab
        mTabHost.addTab(historySpec); // Adding History tab
	}
		
	private void holdIssueData()
	{
		Issue issue = IssueList.getCurrentIssue();
		
		// Issue Journals
		try {
			//int issueId = Integer.parseInt(mCurrentIssueId);
			Issue issueWithJournal = PrevasRedmine.m_redmineManager.getIssueById(issue.getId(), INCLUDE.journals, INCLUDE.changesets);
			if (null != issueWithJournal.getJournals()) {
				mJournalList = issueWithJournal.getJournals();
			}
			else {
				mJournalList = null;
			}
			
//			if (null != issue.getChangesets()) {
//				mChangeSetList = issue.getChangesets();
//			}
//			else {
//				mChangeSetList = null;
//			}
		} catch (Exception e) {
			e.printStackTrace();
			mJournalList = null;
		}
		
		mDetailsHolder = new DetailsHolder();
		
		mDetailsHolder.mId = issue.getId().toString();
		mDetailsHolder.mSubject = issue.getSubject();
		
		Integer parentId = issue.getParentId();
		if (null != parentId) {
			mDetailsHolder.mParentId = parentId;
		}
		
		if (null != issue.getAuthor()) {
			mDetailsHolder.mAuthor = issue.getAuthor().getFullName();
		}
		
		if (null != issue.getTracker()) {
			mDetailsHolder.mTracker = issue.getTracker().getName();
		}
		
		mDetailsHolder.mPriority = issue.getPriorityText();		
		mDetailsHolder.mStatus = issue.getStatusName();
		
		if (null != issue.getAssignee()) {
			mDetailsHolder.mAssignee = issue.getAssignee().getFullName();
		}
		
		if (null != issue.getTargetVersion()) {
			mDetailsHolder.mVersion = issue.getTargetVersion().getName();
		}
		
		if (null != issue.getStartDate()) {
        	Date date = issue.getStartDate();        	
        	SimpleDateFormat dateformatDDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");
        	StringBuilder nowDDMMYYYY = new StringBuilder(dateformatDDMMYYYY.format(date));
        	mDetailsHolder.mStartDate = nowDDMMYYYY.toString();
        }
		
		if (null != issue.getDueDate()) {
        	Date date = issue.getDueDate(); 
        	SimpleDateFormat dateformatDDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");
        	StringBuilder nowDDMMYYYY = new StringBuilder(dateformatDDMMYYYY.format(date));
        	mDetailsHolder.mDueDate = nowDDMMYYYY.toString();
        }

		mDetailsHolder.mPercentDone = issue.getDoneRatio().toString();
		
		if (null != issue.getEstimatedHours()) {
			mDetailsHolder.mEstimatedHours = issue.getEstimatedHours().toString();
		}

		if (null != issue.getSpentHours()) {
			mDetailsHolder.mSpentHours = issue.getSpentHours().toString();
		}

		if (null != issue.getCreatedOn()) {
        	Date date = issue.getCreatedOn(); 
        	SimpleDateFormat dateformatDDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");
        	StringBuilder nowDDMMYYYY = new StringBuilder(dateformatDDMMYYYY.format(date));
        	mDetailsHolder.mCreatedOn = nowDDMMYYYY.toString();
        }
		
		if (null != issue.getUpdatedOn()) {
        	Date date = issue.getUpdatedOn(); 
        	SimpleDateFormat dateformatDDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");
        	StringBuilder nowDDMMYYYY = new StringBuilder(dateformatDDMMYYYY.format(date));
        	mDetailsHolder.mUpdatedOn = nowDDMMYYYY.toString();
        }
		
		if (null != issue.getCategory()) {
			mDetailsHolder.mCategory = issue.getCategory().getName();
	    }
		
		mDetailsHolder.mDescription = issue.getDescription();
	}
	
	public static List<Journal> getIssueJournals()
	{
		return mJournalList;
	}
	
	public static List<Changeset> getIssueChangeset()
	{
		return mChangeSetList;
	}
	
	public static DetailsHolder getDetailsHolder()
	{
		return mDetailsHolder;
	}
	
	public static class DetailsHolder
	{
		public Integer mParentId = -1;
		public String mId = "--";
		public String mSubject = "--";
		public String mAuthor = "--";
		public String mTracker = "--";
		public String mPriority = "--";
		public String mStatus = "--";
		public String mAssignee = "--";
		public String mVersion = "--";
		public String mStartDate = "Empty";
		public String mDueDate = "Empty";
		public String mPercentDone = "0";
		public String mEstimatedHours = " ";
		public String mSpentHours = "--";
		public String mCreatedOn = "--";
		public String mUpdatedOn = "--";
		public String mCategory = "--";
		public String mDescription = "--";
	}
	
	public static void updateCurrentIssue(Issue updatedIssue)
	{
		IssueList.updateCurrentIssue(updatedIssue);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.project_context_menu, menu);
		menu.getItem(0).setEnabled(false);		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		boolean result = false;
	    switch (item.getItemId()) {
	    case R.id.edit_issue:
	    	result = true;
	    	Intent intent = new Intent(this, IssueEdit.class);
	    	intent.putExtras(getIntent());
	    	//startActivity(intent);
	    	startActivityForResult(intent, 1);
	    	break;
	    }
	    
	    return result;	 
	}
	
	public void onNewIconClick(View v)
	{
		Intent intent = new Intent(this, NewIssueActivity.class);
    	intent.putExtras(getIntent());
        Integer issueId = IssueList.getCurrentIssue().getId();        
        intent.putExtra("Parent", issueId);
        startActivityForResult(intent, 1);
	}
	
	public void onEditIconClick(View v)
	{
		//.setBackgroundColor(Color.BLUE);
		Intent intent = new Intent(this, IssueEdit.class);
    	intent.putExtras(getIntent());
    	//startActivity(intent);
    	startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    if (RESULT_OK == resultCode) {
	    	//Issue updatedIssue = IssueEdit.getUpdatedIssue();
	    	setResult(Activity.RESULT_OK, new Intent());
	    	finish();
	    }
	}
}
