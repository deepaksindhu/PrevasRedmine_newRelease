package com.prevas.redmine;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taskadapter.redmineapi.bean.Issue;

public class IssueAdapter extends ArrayAdapter<Issue>
//public class IssueAdapter extends BaseAdapter
{
	private final List<Issue> mIssueList;
	//private final Activity mContext;
	
	// new
	private Context mContext;
	int mResourceId;
	//

	public IssueAdapter(Context context, int textViewResourceId, List<Issue> issueList) 
	{
		super(context, textViewResourceId, issueList);
		mContext = context;
		mIssueList = issueList;
		mResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
	    View row = convertView;
	    ViewHolder viewHolder = null;
	    
	    if (row == null) {
	    	//LayoutInflater inflator = mContext.getLayoutInflater();
	    	LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
	    	//row = inflater.inflate(R.layout.issue_list_item_view, null);
	    	row = inflater.inflate(mResourceId, parent, false);
	    	
	    	viewHolder = new ViewHolder();
	    	
	    	viewHolder.sIssueTracker = (TextView) row.findViewById(R.id.issue_tracker);
	    	viewHolder.sIssueId = (TextView) row.findViewById(R.id.issue_no);
	    	viewHolder.sIssueAssgnedto = (TextView) row.findViewById(R.id.issue_assignee);
	    	viewHolder.sIssuePriority = (TextView) row.findViewById(R.id.issue_priority);
	    	viewHolder.sIssueStatus = (TextView) row.findViewById(R.id.issue_status);
	    	viewHolder.sIssueSubject = (TextView) row.findViewById(R.id.issue_subject);
	    	
	    	row.setTag(viewHolder);
	    	
	    } else {
	    	viewHolder = (ViewHolder) row.getTag();
	    }
	    
	    Issue issue = mIssueList.get(position);
	    if (issue != null) {    	
	    	try {
			    viewHolder.sIssueTracker.setText(issue.getTracker().getName());
			    viewHolder.sIssueId.setText("#" + issue.getId().toString());
			    if (null != issue.getAssignee()) {
			    	viewHolder.sIssueAssgnedto.setText(issue.getAssignee().getFullName());
			    }
			    else {
			    	viewHolder.sIssueAssgnedto.setText("--");
			    }
			    
			    viewHolder.sIssuePriority.setText(issue.getPriorityText());
			    viewHolder.sIssueStatus.setText(issue.getStatusName());
			    viewHolder.sIssueSubject.setText(issue.getSubject());			   
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
    	
	    return row;
	}
	
	public static class ViewHolder 
	{
	    public TextView sIssueTracker;
	    public TextView sIssueId;
	    public TextView sIssueAssgnedto;
	    public TextView sIssuePriority;
	    public TextView sIssueStatus;
	    public TextView sIssueSubject;
	}

	@Override
	public int getCount() 
	{
		return mIssueList.size();
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

}
