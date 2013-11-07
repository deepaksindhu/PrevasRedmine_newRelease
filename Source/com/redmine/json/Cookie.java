package com.redmine.json;


// Referenced classes of package org.json:
//            JSONObject, JSONTokener, JSONException

public class Cookie
{

    public Cookie()
    {
    }

    public static String escape(String s)
    {
        String s1 = s.trim();
        StringBuffer stringbuffer = new StringBuffer();
        int i = s1.length();
        for(int j = 0; j < i; j++)
        {
            char c = s1.charAt(j);
            if(c < ' ' || c == '+' || c == '%' || c == '=' || c == ';')
            {
                stringbuffer.append('%');
                stringbuffer.append(Character.forDigit((char)(c >>> 4 & 0xf), 16));
                stringbuffer.append(Character.forDigit((char)(c & 0xf), 16));
            } else
            {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    public static JSONObject toJSONObject(String s)
        throws JSONException
    {
        JSONObject jsonobject = new JSONObject();
        JSONTokener jsontokener = new JSONTokener(s);
        jsonobject.put("name", jsontokener.nextTo('='));
        jsontokener.next('=');
        jsonobject.put("value", jsontokener.nextTo(';'));
        jsontokener.next();
        String s1;
        Object obj;
        for(; jsontokener.more(); jsonobject.put(s1, obj))
        {
            s1 = unescape(jsontokener.nextTo("=;"));
            if(jsontokener.next() != '=')
            {
                if(s1.equals("secure"))
                {
                    obj = Boolean.TRUE;
                } else
                {
                    throw jsontokener.syntaxError("Missing '=' in cookie parameter.");
                }
            } else
            {
                obj = unescape(jsontokener.nextTo(';'));
                jsontokener.next();
            }
        }

        return jsonobject;
    }

    public static String toString(JSONObject jsonobject)
        throws JSONException
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(escape(jsonobject.getString("name")));
        stringbuffer.append("=");
        stringbuffer.append(escape(jsonobject.getString("value")));
        if(jsonobject.has("expires"))
        {
            stringbuffer.append(";expires=");
            stringbuffer.append(jsonobject.getString("expires"));
        }
        if(jsonobject.has("domain"))
        {
            stringbuffer.append(";domain=");
            stringbuffer.append(escape(jsonobject.getString("domain")));
        }
        if(jsonobject.has("path"))
        {
            stringbuffer.append(";path=");
            stringbuffer.append(escape(jsonobject.getString("path")));
        }
        if(jsonobject.optBoolean("secure"))
        {
            stringbuffer.append(";secure");
        }
        return stringbuffer.toString();
    }

    public static String unescape(String s)
    {
        int i = s.length();
        StringBuffer stringbuffer = new StringBuffer();
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            if(c == '+')
            {
                c = ' ';
            } else
            if(c == '%' && j + 2 < i)
            {
                int k = JSONTokener.dehexchar(s.charAt(j + 1));
                int l = JSONTokener.dehexchar(s.charAt(j + 2));
                if(k >= 0 && l >= 0)
                {
                    c = (char)(k * 16 + l);
                    j += 2;
                }
            }
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }
}
