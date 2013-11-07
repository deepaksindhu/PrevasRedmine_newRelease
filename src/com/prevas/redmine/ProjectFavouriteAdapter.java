package com.prevas.redmine;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.taskadapter.redmineapi.bean.Project;


public class ProjectFavouriteAdapter extends ArrayAdapter<Project> 
									 implements OnClickListener, OnCheckedChangeListener
{
	private Context mContext; 
	private List<Project> mProjectList;
	boolean [] checkedItems;
	private ArrayList<String> mFavoriteProjectsIdenfier;
	
	public ProjectFavouriteAdapter(Context context, int resource, 
			int textViewResourceId, List<Project> projectList) 
	{
		super(context, resource, textViewResourceId, projectList);
		this.mContext = context;
		this.mProjectList = projectList;
		mFavoriteProjectsIdenfier = ProjectPreferences.getSavedFavoriteProjects(context);
		Init();
	}

	public ProjectFavouriteAdapter(Context context, int textViewResourceId, 
			List<Project> projectList)
	{
		super(context, textViewResourceId, projectList);
		this.mContext = context;
		this.mProjectList = projectList;
		mFavoriteProjectsIdenfier = ProjectPreferences.getSavedFavoriteProjects(context);
		Init();
	}
	
	private void Init()
	{
		checkedItems = new boolean[getCount()];
		
		// no favorites, enable all
		if (null == mFavoriteProjectsIdenfier) {
			int itemCount = getCount();
			for (int i = 0; i < itemCount; ++i) {
				checkedItems[i] = true;
			}
			return;
		}
		
		int itemCount = mFavoriteProjectsIdenfier.size();
		if (itemCount > 0) {
			for (int i = 0; i < itemCount; ++i) {
				String identifier = mFavoriteProjectsIdenfier.get(i);
				for (int j = 0; j < mProjectList.size(); ++j) {
					if (identifier.equals(mProjectList.get(j).getIdentifier())) {
						checkedItems[j] = true;
						break;
					}
				}
			}
		}		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
		    view = inflater.inflate(R.layout.project_fav_list_row_layout, null);
		}
		view.setTag(position);
		
		TextView tv = (TextView) view.findViewById(R.id.label);
		CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
		
		tv.setText(getItem(position).toString());
		cb.setOnCheckedChangeListener(null);
		cb.setChecked(checkedItems[position]);
		cb.setOnCheckedChangeListener(this);
		cb.setTag(position);
		view.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) 
	{
		CheckBox cb = (CheckBox)v.findViewById(R.id.checkBox);
		cb.toggle();
		notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		int position = (Integer) buttonView.getTag();
		checkedItems[position] = isChecked;
	}
	
	@Override
	public int getCount()
	{
		return mProjectList.size();
	}
		
	@Override
	public Project getItem(int position)
	{
		return mProjectList.get(position);
	}
	
	public long getItemId(int position) 
	{
	    return getItem(position).getId();
	}

	public boolean isItemChecked(int position)
	{
		return checkedItems[position];
	}
	
	public String getProjectIdentifier(int position)
	{
		return mProjectList.get(position).getIdentifier();
	}
	
	public void resetCheckBoxes(boolean value)
	{
		int itemCount = getCount();
		for (int i = 0; i < itemCount; ++i) {
			checkedItems[i] = value;
		}
	}
}
