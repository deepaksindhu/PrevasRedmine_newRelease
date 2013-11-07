package com.prevas.redmine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;

public class PrevasRedmine extends Activity 
{
	ArrayAdapter<Project> mProjectListAdapter;
	public static RedmineManager m_redmineManager;
	private String m_sRedmineHost = "https://ban.pdprojects.prevas.com";	
	private String m_sAccessKey = "";//785221130be2620e84fe72057bbdb3d1ee8c8b36";
	public static List<Project> mProjectList;
	private static HashMap<String, List<Issue>> mProjectIssuesMap;
	private static HashMap<String, List<Issue>> mProjectIssuesWithJournalsMap;
	public static int mCurrentProjectId = 0;
	public static String mCurrentProjectStringId = " ";
	private static Project mCurrentProject = null;
	private static List<Project> mFavoriteProjectList;
	private int mResultCodeFromFavProject = 0;
	private ListView mProjectListView;
	private static HashMap<String, List<Membership>> mProjectMembershipMap;
	private static String m_sUsername;
	private static String m_sPassword;
	
		
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
	    }

        super.onCreate(savedInstanceState);
        //this must be called BEFORE setContentView
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_prevas_redmine);

        mProjectListView = (ListView) findViewById(R.id.projectListView); 
        
        startActivityForResult(new Intent(PrevasRedmine.this, LoginActivity.class), 1); 
        //startActivity(loginIntent);

        //mProgressDialog = ProgressDialog.show(PrevasRedmine.this, "", "Loading...");
        mProjectList = new ArrayList<Project>();
        mProjectListAdapter = new ArrayAdapter<Project>(this, R.layout.project_row_layout, mProjectList);
        
//        m_redmineManager = new RedmineManager(m_sRedmineHost, m_sAccessKey);
//        
//        try {
//        	mProjectList = m_redmineManager.getProjects();
//        	if (ProjectPreferences.hasFavoriteProjects(getApplicationContext())) {
//        		mFavoriteProjectList = ProjectPreferences.getSavedFavoriteProjectList(
//        				getApplicationContext(), mProjectList);
//        		mProjectListAdapter = new ArrayAdapter<Project>(this, R.layout.project_row_layout, mFavoriteProjectList);
//        	}
//        	else {
//        		mProjectListAdapter = new ArrayAdapter<Project>(this, R.layout.project_row_layout, mProjectList);
//        	}
//        	mProjectIssuesMap = new HashMap<String, List<Issue>>();
//        	mProjectIssuesWithJournalsMap = new HashMap<String, List<Issue>>();
//        	//refreshProjectList();
//        	//PrepareIssuesFromProjects();
//		} catch (RedmineException e) {
//			e.printStackTrace();
//		}
        
        //this.setListAdapter(mProjectListAdapter);

        
        //this must be called AFTER setContentView
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.project_custom_title_bar);
 
        //set the title - custom title in the middle of the title bar
        TextView textView = (TextView)findViewById(R.id.project_custom_title_text);
        textView.setText("Prevas Redmine");
       
        mProjectListView.setAdapter(mProjectListAdapter);
        mProjectListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
			{
				Intent intent = new Intent(PrevasRedmine.this, IssueList.class);
		        //String projId = mProjectListAdapter.getItem(position).getId().toString();
		        //intent.putExtra(StringConsts.PROJECT_ID_INT, projId);
		        mCurrentProject = mProjectListAdapter.getItem(position);
		        mCurrentProjectId = mProjectListAdapter.getItem(position).getId();
		        //String projIdInt = intent.getStringExtra(StringConsts.PROJECT_ID_INT);
		        
		        String projectId = mProjectListAdapter.getItem(position).getIdentifier();
//		        Project p = mProjectListAdapter.getItem(position);
//		        try {
//		        	List<Membership> memship = PrevasRedmine.m_redmineManager.getMemberships(p);
//		        	for (int i = 0; i < memship.size(); ++i)
//		        		System.out.println(memship.get(i).getUser().getFullName());
//		        } catch (Exception e) {
//		        	e.printStackTrace();
//		        }
		        
		        mCurrentProjectStringId = projectId;
		        
		        intent.putExtra(StringConsts.PROJECT_ID, projectId);
		        intent.putExtra(StringConsts.PROJECT_NAME, mProjectListAdapter.getItem(position).getName());
		        startActivity(intent);
			}
        }); 
	}
    	
	private void testUserProjects(Integer userId)
	{
		List<Project> newProjects = new ArrayList<Project>();
		int count = mProjectList.size();
		for (int i = 0; i < count; ++i) {
			Project project = mProjectList.get(i);
			try {
				List<Membership> memberships = m_redmineManager.getMemberships(project);
				int memCount = memberships.size();
				for (int j = 0; j < memCount; ++j) {
					if (userId == memberships.get(j).getUser().getId()) {
						// add project to the list view
						newProjects.add(project);
					}
				}
			} catch (RedmineException e) {				
				e.printStackTrace();
			}
		}
	}
	
	private void startup()
	{
		try {			 
			//mProjectList = m_redmineManager.getProjects();
			//LoginHolder holder = ProjectPreferences.getSavedLoginCredentials(PrevasRedmine.this);
			//m_redmineManager = new RedmineManager(holder.mUrl, holder.mUserName, holder.mPassword);
			if (ProjectPreferences.hasFavoriteProjects(getApplicationContext())) {
				mFavoriteProjectList = ProjectPreferences.getSavedFavoriteProjectList(
						getApplicationContext(), mProjectList);
				mProjectListAdapter = new ArrayAdapter<Project>(this, R.layout.project_row_layout, mFavoriteProjectList);
			}
		 	else {
		 		mProjectListAdapter = new ArrayAdapter<Project>(this, R.layout.project_row_layout, mProjectList);
		 	}
			mProjectIssuesMap = new HashMap<String, List<Issue>>();
			mProjectIssuesWithJournalsMap = new HashMap<String, List<Issue>>();
			mProjectMembershipMap = new HashMap<String, List<Membership>>();
			
//			m_sUsername = holder.mUserName;
//			m_sPassword = holder.mPassword;
		} catch (Exception e) {
			e.printStackTrace();
		}
	        	       
		mProjectListView.setAdapter(mProjectListAdapter);
	}
		
	public static List<Issue> getIssues(String projectIdentifier)
	{		
		return mProjectIssuesMap.get(projectIdentifier);
	}

	public static List<Issue> getIssuesWithJournals(String projectIdentifier)
	{
		List<Issue> issueList = mProjectIssuesWithJournalsMap.get(projectIdentifier);
		if (null == issueList) {
			try {
				issueList = m_redmineManager.getIssues(mCurrentProject.getId().toString(), null); 
						//INCLUDE.journals, INCLUDE.relations, INCLUDE.changesets, INCLUDE.attachments);
				mProjectIssuesWithJournalsMap.put(projectIdentifier, issueList);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return issueList;
	}
	
	public static void addNewIssueToMap(Issue issue)
	{		
		List<Issue> issueList = mProjectIssuesWithJournalsMap.get(mCurrentProjectStringId);
		issueList.add(issue);
		mProjectIssuesWithJournalsMap.remove(mCurrentProjectStringId);
		mProjectIssuesWithJournalsMap.put(mCurrentProjectStringId, issueList);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_prevas_redmine, menu);
		menu.getItem(1).setVisible(false);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) {
	    case R.id.refresh:
//	    	try {
//				refreshProjectList();
//			} catch (RedmineException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    	break;
	    	
	    case R.id.project_favourite:
	    	setFavouriteProjects();
	    	break;
	    	
	    case R.id.clear_Login:
	    	clearLogin();
	    	break;
	    }
	    
	    return true;	 
	}

	private void clearLogin()
	{
		ProjectPreferences.clearLoginDetails(getApplicationContext());
		Toast.makeText(this, "Removed saved login credentials !", Toast.LENGTH_LONG).show();
	}
	
	private void setFavouriteProjects()
	{
		Intent intent = new Intent(this, ProjectFavouriteList.class);
		startActivityForResult(intent, mResultCodeFromFavProject);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    String className = data.getStringExtra(StringConsts.ACTIVITY_CLASS_NAME);
	    
	    if ("ProjectFavouriteList".equals(className) && RESULT_OK == resultCode) {
	    	mFavoriteProjectList = ProjectPreferences.getSavedFavoriteProjectList(
												getApplicationContext(), mProjectList);
	    	mProjectListAdapter = new ArrayAdapter<Project>(this, R.layout.project_row_layout, mFavoriteProjectList);
	    	mProjectListView.setAdapter(mProjectListAdapter);
	    	mProjectListAdapter.notifyDataSetChanged();
	    }
	    else {
	    	if ("LoginActivity".equals(className)) {
	    		if (RESULT_OK == resultCode) {
	    			m_redmineManager = LoginActivity.getRedmineManager();
	    			mProjectList = LoginActivity.getProjectList();
	    			startup();
	    		}
	    		else {
	    			String noNetwork = data.getStringExtra(StringConsts.EXIT_APP);
	    			if (null != noNetwork && noNetwork.equals("NoNetwork")) {
	    				AlertDialog appCloseAlert = new AlertDialog.Builder(this).create();
	    				appCloseAlert.setTitle(StringConsts.LOGIN_ERROR);
	    				appCloseAlert.setMessage(StringConsts.NETWORK_ERROR_MSG);
	    				appCloseAlert.setCancelable(false);
	    				appCloseAlert.setButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								finish();
							}
	    				});
	    				// show it
	    				appCloseAlert.show();
	    			}
	    			else {
		    			Intent intent = new Intent(PrevasRedmine.this, LoginActivity.class);
		    			intent.putExtra("ERROR_MSG", "Failed to login");
		    			startActivityForResult(intent, 1);
	    			}
	    		}
	    	}
	    }
	}

	public static boolean updateIssue(Issue issue)
	{
		boolean updated = false;
		try {
			m_redmineManager.update(issue);
			// Also, update the issue map which is holding all the issues;
			List<Issue> issueList = mProjectIssuesWithJournalsMap.get(getCurrentProjectIdentifier());
			if (null != issueList && issueList.size() > 0) {
				//issueList.remove(issue);
				mProjectIssuesWithJournalsMap.remove(issueList);
			}
			updated = true;
		} catch (RedmineException e) {
			e.printStackTrace();
		}
		
		return updated;
	}
		
	@Override
	public void onBackPressed()
	{
		finish();
	}
	
	public static List<Membership> getSelectedProjectMembers()
	{
		String projectIdentifier = mCurrentProject.getIdentifier();
		List<Membership> membership = mProjectMembershipMap.get(projectIdentifier);
		
		if (null == membership) {
			try {
				//membership = LoginActivity.getAdmin().getMemberships(projectIdentifier);
				membership = m_redmineManager.getMemberships(projectIdentifier);
				mProjectMembershipMap.put(projectIdentifier, membership);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return membership;
	}	
	
	public static int getCurrentProjectId()
	{
		if (null != mCurrentProject) {
			return mCurrentProject.getId();
		}
		
		return 0;
	}
	
	public static Project getCurrentProject()
	{
		if (null != mCurrentProject) {
			return mCurrentProject;
		}
		
		return null;
	}
	
	public static String getCurrentProjectIdentifier()
	{
		if (null != mCurrentProject) {
			return mCurrentProject.getIdentifier();
		}
		
		return null;
	}
}
