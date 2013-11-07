package com.redmine.json;


// Referenced classes of package org.json:
//            JSONTokener, JSONException

public class HTTPTokener extends JSONTokener
{

    public HTTPTokener(String s)
    {
        super(s);
    }

    public String nextToken()
        throws JSONException
    {
        StringBuffer stringbuffer = new StringBuffer();
        char c;
        do
        {
            c = next();
        } while(Character.isWhitespace(c));
        if(c == '"' || c == '\'')
        {
            char c1 = c;
            do
            {
                c = next();
                if(c < ' ')
                {
                    throw syntaxError("Unterminated string.");
                }
                if(c == c1)
                {
                    return stringbuffer.toString();
                }
                stringbuffer.append(c);
            } while(true);
        }
        do
        {
            if(c == 0 || Character.isWhitespace(c))
            {
                return stringbuffer.toString();
            }
            stringbuffer.append(c);
            c = next();
        } while(true);
    }
}
