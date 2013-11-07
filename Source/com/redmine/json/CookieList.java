package com.redmine.json;

import java.util.Iterator;

// Referenced classes of package org.json:
//            JSONObject, JSONTokener, JSONException, Cookie
public class CookieList
{

    public CookieList()
    {
    }

    public static JSONObject toJSONObject(String s)
        throws JSONException
    {
        JSONObject jsonobject = new JSONObject();
        for(JSONTokener jsontokener = new JSONTokener(s); jsontokener.more(); jsontokener.next())
        {
            String s1 = Cookie.unescape(jsontokener.nextTo('='));
            jsontokener.next('=');
            jsonobject.put(s1, Cookie.unescape(jsontokener.nextTo(';')));
        }

        return jsonobject;
    }

    public static String toString(JSONObject jsonobject)
        throws JSONException
    {
        boolean flag = false;
        Iterator iterator = jsonobject.keys();
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            String s = iterator.next().toString();
            if(!jsonobject.isNull(s))
            {
                if(flag)
                {
                    stringbuffer.append(';');
                }
                stringbuffer.append(Cookie.escape(s));
                stringbuffer.append("=");
                stringbuffer.append(Cookie.escape(jsonobject.getString(s)));
                flag = true;
            }
        } while(true);
        return stringbuffer.toString();
    }
}
