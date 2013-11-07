package com.prevas.redmine;

import java.util.ArrayList;
import java.util.List;

import com.taskadapter.redmineapi.bean.Project;

import android.content.Context;
import android.content.SharedPreferences;

public class ProjectPreferences 
{
	public static class LoginHolder
	{
		public String mUrl;
		public String mUserName;
		public String mPassword;
	}
	
	public ProjectPreferences()
	{
		
	}
	
	public static boolean hasUserCredentialSaved(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(StringConsts.LOGIN_DETAILS,
																	 Context.MODE_PRIVATE);
		return preferences.getBoolean(StringConsts.LOGIN_SAVED, false);
	}
	
	public static void SaveLoginCredentials(Context context, LoginHolder holder)
	{
		if (null != holder) {
			SharedPreferences preferences = context.getSharedPreferences(StringConsts.LOGIN_DETAILS,
																		 Context.MODE_PRIVATE);			
			SharedPreferences.Editor prefEditor = preferences.edit();
			prefEditor.clear(); // clear old favorites
			
			prefEditor.putString(StringConsts.HOST_URL, holder.mUrl);
			prefEditor.putString(StringConsts.USER_NAME, holder.mUserName);
			prefEditor.putString(StringConsts.PASSWORD, holder.mPassword);
			prefEditor.putBoolean(StringConsts.LOGIN_SAVED, true);
			prefEditor.commit();
		}
	}
	
	public static void clearLoginDetails(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(StringConsts.LOGIN_DETAILS, Context.MODE_PRIVATE);		
		SharedPreferences.Editor prefEditor = preferences.edit();
		prefEditor.clear();		
		prefEditor.commit();
	}
	
	public static LoginHolder getSavedLoginCredentials(Context context)
	{
		LoginHolder holder = new LoginHolder();
		SharedPreferences preferences = context.getSharedPreferences(StringConsts.LOGIN_DETAILS,
																	 Context.MODE_PRIVATE);
		boolean wereCredentialsSaved = preferences.getBoolean(StringConsts.LOGIN_SAVED, false);
		if (!wereCredentialsSaved) return null;
		
		String empty = " ";
		holder.mUrl = preferences.getString(StringConsts.HOST_URL, empty);
		holder.mUserName = preferences.getString(StringConsts.USER_NAME, empty);
		holder.mPassword = preferences.getString(StringConsts.PASSWORD, empty);
		
		return holder;
	}
	
	public static boolean hasFavoriteProjects(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(StringConsts.FAVORITE_PROJECTS,
																	 Context.MODE_PRIVATE);
		return preferences.getBoolean(StringConsts.PREF_SAVED, false);
	}
	
	public static void SaveFavoriteProjects(Context context, ArrayList<String> list)
	{
		int itemCount = list.size();
		if (itemCount > 0) {
			SharedPreferences Preferences = context.getSharedPreferences(StringConsts.FAVORITE_PROJECTS,
														Context.MODE_PRIVATE);			
			SharedPreferences.Editor prefEditor = Preferences.edit();
			prefEditor.clear(); // clear old favorites
			prefEditor.putInt(StringConsts.FAV_PROJECT_COUNT, itemCount);
			String key = "KEY";
    		for (int i = 0 ; i < itemCount ;++i) {
    			prefEditor.putString(key + "_" + i, list.get(i));
    		}
    		prefEditor.putBoolean(StringConsts.PREF_SAVED, true);
    		prefEditor.commit();
		}
	}
	
	public static ArrayList<String> getSavedFavoriteProjects(Context context)
	{
		ArrayList<String> proj_identifiers = new ArrayList<String>();
		SharedPreferences preferences = context.getSharedPreferences(StringConsts.FAVORITE_PROJECTS,
														Context.MODE_PRIVATE);
		boolean wereFavSaved = preferences.getBoolean(StringConsts.PREF_SAVED, false);
		if (!wereFavSaved) return null;
		
		int itemCount = preferences.getInt(StringConsts.FAV_PROJECT_COUNT, 0);
		String key = "KEY";
		for (int i = 0; i < itemCount; ++i) {
			proj_identifiers.add(preferences.getString(key + "_" + i, "0"));
		}
		
		return proj_identifiers;
	}
	
	public static List<Project> getSavedFavoriteProjectList(Context context, final List<Project> fullProjectList)
	{
		List<Project> favProjectList = new ArrayList<Project>();
		
		ArrayList<String> savedProjectIdentifiers = getSavedFavoriteProjects(context);
		int itemCount = savedProjectIdentifiers.size();
		
		if (itemCount > 0) {
			int allProjectsCount = fullProjectList.size();
			Project project = null;
			for (int i = 0; i < itemCount; ++i) {
				for (int j = 0; j < allProjectsCount; ++j) {
					project = fullProjectList.get(j);
					if (savedProjectIdentifiers.get(i).equals(project.getIdentifier())) {
						favProjectList.add(project);
						break;
					}
				}
			}
		}
		else {
			favProjectList = fullProjectList;
		}
		
		return favProjectList;
	}
}
