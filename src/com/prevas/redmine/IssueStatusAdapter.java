package com.prevas.redmine;

import java.util.List;

import com.taskadapter.redmineapi.bean.IssueStatus;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IssueStatusAdapter extends ArrayAdapter<IssueStatus> 
{
	private Context mContext;
	List<IssueStatus> mStatusList;
	
	public IssueStatusAdapter(Context context, int textViewResourceId, List<IssueStatus> statusList) 
	{
		super(context, textViewResourceId, statusList);
		this.mContext = context;
		this.mStatusList = statusList;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
	{        
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setText(mStatusList.get(position).getName());
        return label;
    }

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setText(mStatusList.get(position).getName());
        return label;
    }

	@Override
	public int getCount()
	{
		return mStatusList.size();
	}
	
	@Override
	public IssueStatus getItem(int position)
	{
		return mStatusList.get(position);
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
			if (name.equals(mStatusList.get(i).getName())) {
				resultIndex = i;
				break;
			}
		}
		return resultIndex;
	}
}
