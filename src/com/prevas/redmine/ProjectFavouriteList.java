package com.prevas.redmine;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taskadapter.redmineapi.bean.Project;

public class ProjectFavouriteList extends Activity 
{
	private ListView mFavListView;
	private ProjectFavouriteAdapter mProjectAdapter;
	private CheckBox mSelectAllCheckbox;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		
		setContentView(R.layout.project_fav_list_view);
		
		//this must be called AFTER setContentView
		setCustomTitlebar();
		
		List<Project> projectList = PrevasRedmine.mProjectList;
		mProjectAdapter = new ProjectFavouriteAdapter(this, 
				R.layout.project_fav_list_row_layout, R.id.label, projectList);
		
		mFavListView = (ListView) findViewById(R.id.favlistview);
		//mFavListView.setClickable(true);
		mFavListView.setAdapter(mProjectAdapter);
		
		mFavListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) 
			{
				//mProjectAdapter.checkedItems();
				Toast.makeText(ProjectFavouriteList.this, "Hello", Toast.LENGTH_LONG).show();
			}
		});		
    }
	
	private void setCustomTitlebar()
	{		
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.project_fav_custom_title_bar);        
        TextView textView = (TextView)findViewById(R.id.project_fav_custom_title_text);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        textView.setText("Prevas Redmine");
        
        mSelectAllCheckbox = (CheckBox) findViewById(R.id.selectAllcheckBox);
        //selectAllCheckBox.setChecked(false);
        mSelectAllCheckbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mProjectAdapter.resetCheckBoxes(mSelectAllCheckbox.isChecked());
				mProjectAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_prevas_redmine, menu);
        menu.getItem(0).setEnabled(false);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) {
    	case R.id.project_save:
    		saveFavouriteProject();
    		break;
    	}
    	return true;
    }
    
	@Override
	public void onBackPressed()
	{
		Intent data = new Intent();
    	data.putExtra(StringConsts.ACTIVITY_CLASS_NAME, "ProjectFavouriteList");
    	if (null == getParent()) {
    		setResult(Activity.RESULT_CANCELED, data);
    	}
    	else {
    		getParent().setResult(Activity.RESULT_CANCELED, data);
    	}
    	finish();
	}
    
    private void saveFavouriteProject()
    {
    	ArrayList<String> proj_identifiers = new ArrayList<String>();
    	
    	int itemCount = mProjectAdapter.getCount();
    	Project project = null;
    	for (int i = 0; i < itemCount; ++i) {
    		project = mProjectAdapter.getItem(i);
    		boolean value = mProjectAdapter.isItemChecked(i);
    		if (value) {    			
    			proj_identifiers.add(project.getIdentifier());
    		}
    	}
    	
    	ProjectPreferences.SaveFavoriteProjects(getApplicationContext(), proj_identifiers);
    	//finish();
    	
    	Intent data = new Intent();
    	data.putExtra(StringConsts.ACTIVITY_CLASS_NAME, "ProjectFavouriteList");
    	
    	/* finish() method only sends back the result if there is a mParent property set to null. 
    	* Otherwise the result is lost. */
    	if (null == getParent()) {
    		setResult(Activity.RESULT_OK, data);
    	}
    	else {
    		getParent().setResult(Activity.RESULT_OK, data);
    	}
    	finish();
    }
}


//private ArrayList<String> getSavedListItems()
//{
//	ArrayList<String> proj_identifiers = new ArrayList<String>();
//	Context context = getApplicationContext();
//	SharedPreferences preferences = context.getSharedPreferences(StringConsts.FAVORITE_PROJECTS,
//													Context.MODE_PRIVATE);
//	boolean wereFavSaved = preferences.getBoolean(StringConsts.PREF_SAVED, false);
//	if (!wereFavSaved) return null;
//	
//	int itemCount = preferences.getInt("FavProjectsCount", 0);
//	String key = "KEY";
//	for (int i = 0; i < itemCount; ++i) {
//		proj_identifiers.add(preferences.getString(key + "_" + i, "0"));
//	}
//	
//	return proj_identifiers;
//}