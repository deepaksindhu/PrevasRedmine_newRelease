package com.prevas.redmine;

import java.util.List;

import com.taskadapter.redmineapi.bean.Tracker;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IssueTrackerAdapter extends ArrayAdapter<Tracker>
{
	private Context mContext;
	private List<Tracker> mTrackerList;
	
	public IssueTrackerAdapter(Context context, int textViewResourceId, List<Tracker> trackerList)
	{
		super(context, textViewResourceId, trackerList);
		this.mContext = context;
		this.mTrackerList = trackerList;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
	{
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(mTrackerList.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setText(mTrackerList.get(position).getName());

        return label;
    }

	@Override
	public int getCount()
	{
		return mTrackerList.size();
	}
	
	@Override
	public Tracker getItem(int position)
	{
		return mTrackerList.get(position);
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
			if (name.equals(mTrackerList.get(i).getName())) {
				resultIndex = i;
				break;
			}
		}
		return resultIndex;
	}
}
