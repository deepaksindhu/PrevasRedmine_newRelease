package com.prevas.redmine;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taskadapter.redmineapi.bean.User;

public class UserAdapter extends ArrayAdapter<User>
{
	private Context mContext;
	private List<User> mUserList;
	
	public UserAdapter(Context context, int textViewResourceId, List<User> userList)
	{
		super(context, textViewResourceId, userList);
		this.mContext = context;
		this.mUserList = userList;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) 
	{
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView userName = new TextView(mContext);
        userName.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        userName.setText(mUserList.get(position).getFullName());

        // And finally return your dynamic (or custom) view for each spinner item
        return userName;
    }

	@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setText(mUserList.get(position).getFullName());

        return label;
    }

	@Override
	public int getCount()
	{
		return mUserList.size();
	}
	
	@Override
	public User getItem(int position)
	{
		return mUserList.get(position);
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
			if (name.equals(mUserList.get(i).getFullName())) {
				resultIndex = i;
				break;
			}
		}
		return resultIndex;
	}
}
