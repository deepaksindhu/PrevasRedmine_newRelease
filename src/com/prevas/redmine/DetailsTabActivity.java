package com.prevas.redmine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.prevas.redmine.IssueDetailTabs.DetailsHolder;

public class DetailsTabActivity extends Activity 
{	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_details_tab_view);

        DetailsHolder holder = IssueDetailTabs.getDetailsHolder();
        
        try {
	        TextView authorTextView = (TextView) findViewById(R.id.value_author);
	        authorTextView.setText(holder.mAuthor);
	        
	        TextView trackerTextView = (TextView) findViewById(R.id.value_tracker);
	        trackerTextView.setText(holder.mTracker);
	        
	        TextView priorityTextView = (TextView) findViewById(R.id.value_priority);
	        priorityTextView.setText(holder.mPriority);
	        
	        TextView assigneeTextView = (TextView) findViewById(R.id.value_assignee);
	        assigneeTextView.setText(holder.mAssignee);
	        
	        TextView categoryTextView = (TextView) findViewById(R.id.value_category);
	        categoryTextView.setText(holder.mCategory);
	        
	        TextView versionTextView = (TextView) findViewById(R.id.value_version);
	        versionTextView.setText(holder.mVersion);
	        
	        TextView startDateTextView = (TextView) findViewById(R.id.value_startDate);
	        startDateTextView.setText(holder.mStartDate);
	        
	        TextView dueDateTextView = (TextView) findViewById(R.id.value_dueDate);
	        dueDateTextView.setText(holder.mDueDate);
	        
	        TextView percentDoneTextView = (TextView) findViewById(R.id.value_percentDone);
	        percentDoneTextView.setText(holder.mPercentDone);
	        
	        TextView estimatedTimeTextView = (TextView) findViewById(R.id.value_estimatedTime);
	        estimatedTimeTextView.setText(holder.mEstimatedHours);
	        
	        TextView remainingTimeTextView = (TextView) findViewById(R.id.value_remainingTime);
	        remainingTimeTextView.setText(" ");
	        
	        TextView spentTimeTextView = (TextView) findViewById(R.id.value_spentTime);
	        spentTimeTextView.setText(holder.mSpentHours);
	        
	        TextView createdOnTextView = (TextView) findViewById(R.id.value_createdOn);
	        createdOnTextView.setText(holder.mCreatedOn);
	        
	        TextView updatedOnTextView = (TextView) findViewById(R.id.value_updatedOn);
	        updatedOnTextView.setText(holder.mUpdatedOn);
	        
        } catch (Exception e) {
        	e.printStackTrace();
        }     
	}
}
