package com.prevas.redmine;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taskadapter.redmineapi.bean.Version;

public class VersionAdapter extends ArrayAdapter<Version>
{
	private Context mContext;
	private List<Version> mVersionList;
	
	public VersionAdapter(Context context, int textViewResourceId, List<Version> versionList)
	{
		super(context, textViewResourceId, versionList);
		this.mContext = context;
		this.mVersionList = versionList;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
	{
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView userName = new TextView(mContext);
        userName.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        userName.setText(mVersionList.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return userName;
    }

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{
        TextView versionName = new TextView(mContext);
        versionName.setTextColor(Color.BLACK);
        versionName.setText(mVersionList.get(position).getName());

        return versionName;
    }

	@Override
	public int getCount()
	{
		return mVersionList.size();
	}
	
	@Override
	public Version getItem(int position)
	{
		return mVersionList.get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	public int getItemIndexByName(String name)
	{
		int resultIndex = 0;
		int itemCount = getCount();
		for (int i = 0; i < itemCount; ++i) {
			if (name.equals(mVersionList.get(i).getName())) {
				resultIndex = i;
				break;
			}
		}
		return resultIndex;
	}
}
