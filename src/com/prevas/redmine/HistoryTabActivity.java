package com.prevas.redmine;

import java.text.SimpleDateFormat;
import java.util.List;

import com.taskadapter.redmineapi.bean.Journal;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryTabActivity extends Activity 
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_history_tab_view);

   	 	LinearLayout mainLayout = (LinearLayout) findViewById(R.id.historyLayout);
        
        LayoutParams lparams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        try {
        	List<Journal> journals = IssueDetailTabs.getIssueJournals();
        	SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd-MM-yyyy");
        	
   		 	if (null != journals && journals.size() > 0) {   		 		
	 			int marginfromTop = 25;
	 			int journalCount = journals.size();
	 			TextView userLabel = null;
	 			TextView notesLabel = null;
	 			for (int i = 0; i < journalCount; ++i) {
	       			Journal journal = journals.get(i);	            			 
       			 	            			 
	       			StringBuilder nowMMDDYYYY = new StringBuilder(
       					 dateformatMMDDYYYY.format(journal.getCreatedOn()));
	       			 
	       			userLabel = new TextView(this);
	       			userLabel.setPadding(5, marginfromTop, 0, 0);
	       			userLabel.setText("Updated by " + journal.getUser().getFullName() 
	       								+ " " + "On "+ nowMMDDYYYY.toString());
	       			
	       			userLabel.setTextColor(Color.parseColor("#286A9A"));
	       			userLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f);
	       			userLabel.setTypeface(null, Typeface.BOLD);
	       			userLabel.setLayoutParams(lparams);
	       			mainLayout.addView(userLabel);
	       			marginfromTop = 0;
	       			
	       			String notes = " ";
	       			if (null != journal.getNotes())
	       				notes = journal.getNotes();
	       			
	       			notesLabel = new TextView(this);
     				notesLabel.setPadding(5, marginfromTop, 0, 0);
     				notesLabel.setText("Description :\n" + notes);
     				notesLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
     				notesLabel.setLayoutParams(lparams);
     				mainLayout.addView(notesLabel);
	 			}
   		 	}
   		 	
//   		 	List<Changeset> changeSetList = IssueDetailTabs.getIssueChangeset();
//   		 	if (null != changeSetList && changeSetList.size() > 0) {
//	 			int marginfromTop = 40;
//	 			int changeSetCount = changeSetList.size();
//	 			TextView userLabel = null;
//	 			TextView commentsLabel = null;
//	 			for (int i = 0; i < changeSetCount; ++i) {
//	 				Changeset changeSet = changeSetList.get(i);
//	 				
//	 				StringBuilder nowMMDDYYYY = new StringBuilder(
//      					 dateformatMMDDYYYY.format(changeSet.getCommitedOn()));
//	       			userLabel = new TextView(this);
//	       			userLabel.setPadding(5, marginfromTop, 0, 0);
//	       			userLabel.setText("Updated by " + changeSet.getUser().getFullName() 
//	       								+ " " + "On "+ nowMMDDYYYY.toString());
//	       			
//	       			userLabel.setTextColor(Color.parseColor("#286A9A"));
//	       			userLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9f);
//	       			userLabel.setTypeface(null, Typeface.BOLD);
//	       			userLabel.setLayoutParams(lparams);
//	       			mainLayout.addView(userLabel);
//	       			marginfromTop = 0;
//	       			
//	       			commentsLabel = new TextView(this);
//	       			String comments = " ";
//	       			if (null != changeSet.getComments())
//	       				comments = changeSet.getComments();
//	       			
//	       			commentsLabel = new TextView(this);
//	       			commentsLabel.setPadding(5, marginfromTop, 0, 0);
//	       			commentsLabel.setText("Description :\n" + comments);
//	       			commentsLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f);
//	       			commentsLabel.setLayoutParams(lparams);
//     				mainLayout.addView(commentsLabel);
//	 			}
//   		 	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        	mainLayout.addView(getNoHistoryTextView());
        }      
	}
	
	private TextView getNoHistoryTextView()
	{
		TextView sHistory = new TextView(this);
   	 	sHistory.setPadding(5, 30, 0, 0);
   	 	sHistory.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
   	 	sHistory.setText("No history for this issue!");
   	 	sHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
   	 	return sHistory;
	}
}
