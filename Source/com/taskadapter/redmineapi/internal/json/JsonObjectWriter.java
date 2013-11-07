package com.taskadapter.redmineapi.internal.json;

//import org.json.JSONException;
//import org.json.JSONWriter;
import com.redmine.json.JSONWriter;
import com.redmine.json.JSONException;
/**
 * Json object writer.
 * 
 * @author maxkar
 * 
 */
public interface JsonObjectWriter<T> {
	public void write(JSONWriter writer, T object) throws JSONException;
}
