package com.prevas.redmine;

///import com.prevas.redmine.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DescriptionTabActivity extends Activity 
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_desc_tab_view); 
        TextView descTextView = (TextView) findViewById(R.id.txt_desc);
        descTextView.setPadding(5, 25, 0, 0);
        
   	 	try {
   	 		descTextView.setText(IssueDetailTabs.getDetailsHolder().mDescription);
   	 	} catch (Exception e) {
   	 		e.printStackTrace();
   	 		descTextView.setText("No Description found");
   	 	}
	}
}
