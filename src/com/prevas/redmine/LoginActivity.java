package com.prevas.redmine;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.prevas.redmine.ProjectPreferences.LoginHolder;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;

public class LoginActivity extends Activity
{
	private static RedmineManager mRedmineManager;
	private static RedmineManager mAdmin;
	public ProgressDialog mProgressDialog;
	private EditText mUrlEditText;
	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private Button mLoginBtn;
	private CheckBox mSaveCredentialsCheckbox;
	private TextView mErrorTextView;
	private boolean mLoginSuccess;
	private LoginHolder mHolder;
	private static List<Project> mProjectList;
	private static boolean mLoginFromSavedPassword;
	private static String mAdminkey = "0fdfa5934da5e49171ed6ec74bede41ef236b57a";
	
	public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		
		mLoginFromSavedPassword = false;
		
		mHolder = new LoginHolder();
		
		Intent intent = getIntent();
		
		mLoginSuccess = false;
		mSaveCredentialsCheckbox = (CheckBox) findViewById(R.id.chk_save);
		
		if (ProjectPreferences.hasUserCredentialSaved(getApplicationContext())) {
			mLoginFromSavedPassword = true;
			mHolder = ProjectPreferences.getSavedLoginCredentials(getApplicationContext());
			new LoadViewTask().execute();
		}
		else {
			mUrlEditText = (EditText) findViewById(R.id.edit_hosturl);
			//mUrlEditText.setHint("http://project.prevas.in");
			//mUrlEditText.setText("http://project.prevas.in");
			
			mUsernameEditText = (EditText) findViewById(R.id.edit_username);
			mPasswordEditText = (EditText) findViewById(R.id.edit_password);
			
			mErrorTextView = (TextView) findViewById(R.id.txt_errormsg);
			
			String errormsg = intent.getStringExtra("ERROR_MSG");
			if (null != errormsg) {
				mErrorTextView.setText(errormsg);
			}
			
			mLoginBtn = (Button) findViewById(R.id.btn_login);
			mLoginBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					int urlsize = mUrlEditText.getText().length();
					int usersize = mUsernameEditText.getText().length();					 
	                int passsize = mPasswordEditText.getText().length();
	                
	                if (urlsize > 0 && usersize > 0 && passsize > 0) {	                	
	                	mHolder.mUrl = mUrlEditText.getText().toString();	                	 
	                	mHolder.mUserName = mUsernameEditText.getText().toString();
	                	mHolder.mPassword = mPasswordEditText.getText().toString();
	                	new LoadViewTask().execute();
	                }
	                else {
	                	mErrorTextView.setText("Please provide input for all the fields");
	                }	                
				}
			});

		}
	}
	
	private class LoadViewTask extends AsyncTask<Void, Integer, Void>
	{
		@Override  
        protected void onPreExecute()  
        {  
        	mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Login...");
        }

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			try {
				synchronized (this) {
					mLoginSuccess = tryLoginFromSavedCredentials(mHolder);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		 @Override  
        protected void onPostExecute(Void result)  
        {  
            //close the progress dialog  
            mProgressDialog.dismiss();
            finishLogin(mSaveCredentialsCheckbox.isChecked());
            finish();
        }  
	}
	
	private void finishLogin(boolean saveLogin)
	{
		/* finish() method only sends back the result if there is a mParent property set to null. 
    	* Otherwise the result is lost. */
		Intent data = new Intent();
		data.putExtra(StringConsts.ACTIVITY_CLASS_NAME, "LoginActivity");
		
		boolean hasNetwork = isDeviceConnectedToNetwork();
		if (!hasNetwork) {
			data.putExtra(StringConsts.EXIT_APP, "NoNetwork");
		}
		
    	if (null == getParent()) {
    		if (!mLoginSuccess) {
    			if (true == mLoginFromSavedPassword) {
    				// probably password/url has been changed, clear saved login credentials
    				clearSavedLoginCrdent();
    			}  				
    			setResult(Activity.RESULT_CANCELED, data);
    		}
    		else {
            	if (saveLogin && mSaveCredentialsCheckbox.isChecked()) {
            		ProjectPreferences.SaveLoginCredentials(getApplicationContext(), mHolder);
            	}
    			setResult(Activity.RESULT_OK, data);
    		}
    	}
    	else {
    		if (!mLoginSuccess) {
    			if (true == mLoginFromSavedPassword) {
    				// probably password/url has been changed, clear saved login credentials
    				clearSavedLoginCrdent();
    			}
    			getParent().setResult(Activity.RESULT_CANCELED, data);
    		}
    		else {
            	if (saveLogin && mSaveCredentialsCheckbox.isChecked()) {
            		ProjectPreferences.SaveLoginCredentials(getApplicationContext(), mHolder);
            	}
    			getParent().setResult(Activity.RESULT_OK, data);
    		}
    	}
	}
	
	private void clearSavedLoginCrdent()
	{
		ProjectPreferences.clearLoginDetails(getApplicationContext());
	}
	
	private boolean tryLoginFromSavedCredentials(LoginHolder holder)
	{
		boolean success = true; 
		try {
			tryLogin(holder);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}
	
	private void tryLogin(LoginHolder holder) throws RedmineException
	{
		mRedmineManager = new RedmineManager(holder.mUrl, holder.mUserName, holder.mPassword);
		mProjectList = mRedmineManager.getProjects();
		mAdmin = new RedmineManager(holder.mUrl, mAdminkey);
	}

	public static RedmineManager getRedmineManager()
	{
		return mRedmineManager;
	}
	
	public static RedmineManager getAdmin()
	{
		return mAdmin;
	}
	
	public static List<Project> getProjectList()
	{
		return mProjectList;
	}
	
	// check if android device is connected to internet or not
	private boolean isDeviceConnectedToNetwork() 
	{
		boolean connected = false;
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
	            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
	        //we are connected to a network
	        connected = true;
	    }
	    return connected;
	}
}
