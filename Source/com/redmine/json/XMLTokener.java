package com.redmine.json;

import java.util.HashMap;

// Referenced classes of package org.json:
//            JSONTokener, JSONException, XML

public class XMLTokener extends JSONTokener
{

    public static final HashMap entity;

    public XMLTokener(String s)
    {
        super(s);
    }

    public String nextCDATA()
        throws JSONException
    {
        StringBuffer stringbuffer = new StringBuffer();
        int i;
        do
        {
            char c = next();
            if(c == 0)
            {
                throw syntaxError("Unclosed CDATA");
            }
            stringbuffer.append(c);
            i = stringbuffer.length() - 3;
        } while(i < 0 || stringbuffer.charAt(i) != ']' || stringbuffer.charAt(i + 1) != ']' || stringbuffer.charAt(i + 2) != '>');
        stringbuffer.setLength(i);
        return stringbuffer.toString();
    }

    public Object nextContent()
        throws JSONException
    {
        char c;
        do
        {
            c = next();
        } while(Character.isWhitespace(c));
        if(c == 0)
        {
            return null;
        }
        if(c == '<')
        {
            return XML.LT;
        }
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            if(c == '<' || c == 0)
            {
                back();
                return stringbuffer.toString().trim();
            }
            if(c == '&')
            {
                stringbuffer.append(nextEntity(c));
            } else
            {
                stringbuffer.append(c);
            }
            c = next();
        } while(true);
    }

    public Object nextEntity(char c)
        throws JSONException
    {
        StringBuffer stringbuffer = new StringBuffer();
        char c1;
        do
        {
            c1 = next();
            if(!Character.isLetterOrDigit(c1) && c1 != '#')
            {
                break;
            }
            stringbuffer.append(Character.toLowerCase(c1));
        } while(true);
        if(c1 != ';')
        {
            throw syntaxError((new StringBuilder()).append("Missing ';' in XML entity: &").append(stringbuffer).toString());
        } else
        {
            String s = stringbuffer.toString();
            Object obj = entity.get(s);
            return obj == null ? (new StringBuilder()).append(c).append(s).append(";").toString() : obj;
        }
    }

    public Object nextMeta()
        throws JSONException
    {
        char c;
        do
        {
            c = next();
        } while(Character.isWhitespace(c));
        switch(c)
        {
        case 0: // '\0'
            throw syntaxError("Misshaped meta tag");

        case 60: // '<'
            return XML.LT;

        case 62: // '>'
            return XML.GT;

        case 47: // '/'
            return XML.SLASH;

        case 61: // '='
            return XML.EQ;

        case 33: // '!'
            return XML.BANG;

        case 63: // '?'
            return XML.QUEST;

        case 34: // '"'
        case 39: // '\''
            char c2 = c;
            do
            {
                c = next();
                if(c == 0)
                {
                    throw syntaxError("Unterminated string");
                }
            } while(c != c2);
            return Boolean.TRUE;
        }
        do
        {
            char c1 = next();
            if(Character.isWhitespace(c1))
            {
                return Boolean.TRUE;
            }
            switch(c1)
            {
            case 0: // '\0'
            case 33: // '!'
            case 34: // '"'
            case 39: // '\''
            case 47: // '/'
            case 60: // '<'
            case 61: // '='
            case 62: // '>'
            case 63: // '?'
                back();
                return Boolean.TRUE;
            }
        } while(true);
    }

    public Object nextToken()
        throws JSONException
    {
        char c;
        do
        {
            c = next();
        } while(Character.isWhitespace(c));
        StringBuffer stringbuffer1;
        switch(c)
        {
        case 0: // '\0'
            throw syntaxError("Misshaped element");

        case 60: // '<'
            throw syntaxError("Misplaced '<'");

        case 62: // '>'
            return XML.GT;

        case 47: // '/'
            return XML.SLASH;

        case 61: // '='
            return XML.EQ;

        case 33: // '!'
            return XML.BANG;

        case 63: // '?'
            return XML.QUEST;

        case 34: // '"'
        case 39: // '\''
            char c1 = c;
            StringBuffer stringbuffer = new StringBuffer();
            do
            {
                c = next();
                if(c == 0)
                {
                    throw syntaxError("Unterminated string");
                }
                if(c == c1)
                {
                    return stringbuffer.toString();
                }
                if(c == '&')
                {
                    stringbuffer.append(nextEntity(c));
                } else
                {
                    stringbuffer.append(c);
                }
            } while(true);

        default:
            stringbuffer1 = new StringBuffer();
            break;
        }
        do
        {
            stringbuffer1.append(c);
            c = next();
            if(Character.isWhitespace(c))
            {
                return stringbuffer1.toString();
            }
            switch(c)
            {
            case 0: // '\0'
            case 33: // '!'
            case 47: // '/'
            case 61: // '='
            case 62: // '>'
            case 63: // '?'
            case 91: // '['
            case 93: // ']'
                back();
                return stringbuffer1.toString();

            case 34: // '"'
            case 39: // '\''
            case 60: // '<'
                throw syntaxError("Bad character in a name");
            }
        } while(true);
    }

    static 
    {
        entity = new HashMap(8);
        entity.put("amp", XML.AMP);
        entity.put("apos", XML.APOS);
        entity.put("gt", XML.GT);
        entity.put("lt", XML.LT);
        entity.put("quot", XML.QUOT);
    }
}
