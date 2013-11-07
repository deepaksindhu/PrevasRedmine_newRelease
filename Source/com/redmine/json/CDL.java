package com.redmine.json;


// Referenced classes of package org.json:
//            JSONArray, JSONTokener, JSONException, JSONObject

public class CDL
{

    public CDL()
    {
    }

    private static String getValue(JSONTokener jsontokener)
        throws JSONException
    {
        char c;
        do
        {
            c = jsontokener.next();
        } while(c <= ' ' && c != 0);
        switch(c)
        {
        case 0: // '\0'
            return null;

        case 34: // '"'
        case 39: // '\''
            return jsontokener.nextString(c);

        case 44: // ','
            jsontokener.back();
            return "";
        }
        jsontokener.back();
        return jsontokener.nextTo(',');
    }

    public static JSONArray rowToJSONArray(JSONTokener x) throws JSONException 
    {
        JSONArray ja = new JSONArray();
        for (;;) {
            String value = getValue(x);
            char c = x.next();
            if (value == null ||
                    (ja.length() == 0 && value.length() == 0 && c != ',')) {
                return null;
            }
            ja.put(value);
            for (;;) {
                if (c == ',') {
                    break;
                }
                if (c != ' ') {
                    if (c == '\n' || c == '\r' || c == 0) {
                        return ja;
                    }
                    throw x.syntaxError("Bad character '" + c + "' (" +
                            (int)c + ").");
                }
                c = x.next();
            }
        }
    }

    public static JSONObject rowToJSONObject(JSONArray jsonarray, JSONTokener jsontokener)
        throws JSONException
    {
        JSONArray jsonarray1 = rowToJSONArray(jsontokener);
        return jsonarray1 == null ? null : jsonarray1.toJSONObject(jsonarray);
    }

    public static JSONArray toJSONArray(String s)
        throws JSONException
    {
        return toJSONArray(new JSONTokener(s));
    }

    public static JSONArray toJSONArray(JSONTokener jsontokener)
        throws JSONException
    {
        return toJSONArray(rowToJSONArray(jsontokener), jsontokener);
    }

    public static JSONArray toJSONArray(JSONArray jsonarray, String s)
        throws JSONException
    {
        return toJSONArray(jsonarray, new JSONTokener(s));
    }

    public static JSONArray toJSONArray(JSONArray jsonarray, JSONTokener jsontokener)
        throws JSONException
    {
        if(jsonarray == null || jsonarray.length() == 0)
        {
            return null;
        }
        JSONArray jsonarray1 = new JSONArray();
        do
        {
            JSONObject jsonobject = rowToJSONObject(jsonarray, jsontokener);
            if(jsonobject == null)
            {
                break;
            }
            jsonarray1.put(jsonobject);
        } while(true);
        if(jsonarray1.length() == 0)
        {
            return null;
        } else
        {
            return jsonarray1;
        }
    }

    public static String rowToString(JSONArray jsonarray)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < jsonarray.length(); i++)
        {
            if(i > 0)
            {
                stringbuffer.append(',');
            }
            Object obj = jsonarray.opt(i);
            if(obj == null)
            {
                continue;
            }
            String s = obj.toString();
            if(s.indexOf(',') >= 0)
            {
                if(s.indexOf('"') >= 0)
                {
                    stringbuffer.append('\'');
                    stringbuffer.append(s);
                    stringbuffer.append('\'');
                } else
                {
                    stringbuffer.append('"');
                    stringbuffer.append(s);
                    stringbuffer.append('"');
                }
            } else
            {
                stringbuffer.append(s);
            }
        }

        stringbuffer.append('\n');
        return stringbuffer.toString();
    }

    public static String toString(JSONArray jsonarray)
        throws JSONException
    {
        JSONObject jsonobject = jsonarray.optJSONObject(0);
        if(jsonobject != null)
        {
            JSONArray jsonarray1 = jsonobject.names();
            if(jsonarray1 != null)
            {
                return (new StringBuilder()).append(rowToString(jsonarray1)).append(toString(jsonarray1, jsonarray)).toString();
            }
        }
        return null;
    }

    public static String toString(JSONArray jsonarray, JSONArray jsonarray1)
        throws JSONException
    {
        if(jsonarray == null || jsonarray.length() == 0)
        {
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < jsonarray1.length(); i++)
        {
            JSONObject jsonobject = jsonarray1.optJSONObject(i);
            if(jsonobject != null)
            {
                stringbuffer.append(rowToString(jsonobject.toJSONArray(jsonarray)));
            }
        }

        return stringbuffer.toString();
    }
}
