package com.redmine.json;

import java.util.Iterator;

// Referenced classes of package org.json:
//            JSONObject, HTTPTokener, JSONException

public class HTTP
{

    public static final String CRLF = "\r\n";

    public HTTP()
    {
    }

    public static JSONObject toJSONObject(String s)
        throws JSONException
    {
        JSONObject jsonobject = new JSONObject();
        HTTPTokener httptokener = new HTTPTokener(s);
        String s1 = httptokener.nextToken();
        if(s1.toUpperCase().startsWith("HTTP"))
        {
            jsonobject.put("HTTP-Version", s1);
            jsonobject.put("Status-Code", httptokener.nextToken());
            jsonobject.put("Reason-Phrase", httptokener.nextTo('\0'));
            httptokener.next();
        } else
        {
            jsonobject.put("Method", s1);
            jsonobject.put("Request-URI", httptokener.nextToken());
            jsonobject.put("HTTP-Version", httptokener.nextToken());
        }
        for(; httptokener.more(); httptokener.next())
        {
            String s2 = httptokener.nextTo(':');
            httptokener.next(':');
            jsonobject.put(s2, httptokener.nextTo('\0'));
        }

        return jsonobject;
    }

    public static String toString(JSONObject jsonobject)
        throws JSONException
    {
        Iterator iterator = jsonobject.keys();
        StringBuffer stringbuffer = new StringBuffer();
        if(jsonobject.has("Status-Code") && jsonobject.has("Reason-Phrase"))
        {
            stringbuffer.append(jsonobject.getString("HTTP-Version"));
            stringbuffer.append(' ');
            stringbuffer.append(jsonobject.getString("Status-Code"));
            stringbuffer.append(' ');
            stringbuffer.append(jsonobject.getString("Reason-Phrase"));
        } else
        if(jsonobject.has("Method") && jsonobject.has("Request-URI"))
        {
            stringbuffer.append(jsonobject.getString("Method"));
            stringbuffer.append(' ');
            stringbuffer.append('"');
            stringbuffer.append(jsonobject.getString("Request-URI"));
            stringbuffer.append('"');
            stringbuffer.append(' ');
            stringbuffer.append(jsonobject.getString("HTTP-Version"));
        } else
        {
            throw new JSONException("Not enough material for an HTTP header.");
        }
        stringbuffer.append("\r\n");
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            String s = iterator.next().toString();
            if(!s.equals("HTTP-Version") && !s.equals("Status-Code") && !s.equals("Reason-Phrase") && !s.equals("Method") && !s.equals("Request-URI") && !jsonobject.isNull(s))
            {
                stringbuffer.append(s);
                stringbuffer.append(": ");
                stringbuffer.append(jsonobject.getString(s));
                stringbuffer.append("\r\n");
            }
        } while(true);
        stringbuffer.append("\r\n");
        return stringbuffer.toString();
    }
}
