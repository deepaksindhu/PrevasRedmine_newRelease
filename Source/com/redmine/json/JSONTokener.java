package com.redmine.json;



// Referenced classes of package org.json:
//            JSONObject, JSONArray, JSONException

public class JSONTokener
{

    private int myIndex;
    private String mySource;

    public JSONTokener(String s)
    {
        myIndex = 0;
        mySource = s;
    }

    public void back()
    {
        if(myIndex > 0)
        {
            myIndex--;
        }
    }

    public static int dehexchar(char c)
    {
        if(c >= '0' && c <= '9')
        {
            return c - 48;
        }
        if(c >= 'A' && c <= 'F')
        {
            return c - 55;
        }
        if(c >= 'a' && c <= 'f')
        {
            return c - 87;
        } else
        {
            return -1;
        }
    }

    public boolean more()
    {
        return myIndex < mySource.length();
    }

    public char next()
    {
        if(more())
        {
            char c = mySource.charAt(myIndex);
            myIndex++;
            return c;
        } else
        {
            return '\0';
        }
    }

    public char next(char c)
        throws JSONException
    {
        char c1 = next();
        if(c1 != c)
        {
            throw syntaxError((new StringBuilder()).append("Expected '").append(c).append("' and instead saw '").append(c1).append("'").toString());
        } else
        {
            return c1;
        }
    }

    public String next(int i)
        throws JSONException
    {
        int j = myIndex;
        int k = j + i;
        if(k >= mySource.length())
        {
            throw syntaxError("Substring bounds error");
        } else
        {
            myIndex += i;
            return mySource.substring(j, k);
        }
    }

    public char nextClean()
        throws JSONException
    {
        char c;
        do
        {
label0:
            do
            {
                c = next();
                if(c == '/')
                {
                    switch(next())
                    {
                    case 47: // '/'
                        do
                        {
                            c = next();
                        } while(c != '\n' && c != '\r' && c != 0);
                        continue;

                    case 42: // '*'
                        do
                        {
                            do
                            {
                                c = next();
                                if(c == 0)
                                {
                                    throw syntaxError("Unclosed comment");
                                }
                            } while(c != '*');
                            if(next() == '/')
                            {
                                continue label0;
                            }
                            back();
                        } while(true);
                    }
                    back();
                    return '/';
                }
                if(c != '#')
                {
                    break;
                }
                do
                {
                    c = next();
                } while(c != '\n' && c != '\r' && c != 0);
            } while(true);
        } while(c != 0 && c <= ' ');
        return c;
    }

    public String nextString(char c)
        throws JSONException
    {
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            char c1 = next();
            switch(c1)
            {
            case 0: // '\0'
            case 10: // '\n'
            case 13: // '\r'
                throw syntaxError("Unterminated string");

            case 92: // '\\'
                c1 = next();
                switch(c1)
                {
                case 98: // 'b'
                    stringbuffer.append('\b');
                    break;

                case 116: // 't'
                    stringbuffer.append('\t');
                    break;

                case 110: // 'n'
                    stringbuffer.append('\n');
                    break;

                case 102: // 'f'
                    stringbuffer.append('\f');
                    break;

                case 114: // 'r'
                    stringbuffer.append('\r');
                    break;

                case 117: // 'u'
                    stringbuffer.append((char)Integer.parseInt(next(4), 16));
                    break;

                case 120: // 'x'
                    stringbuffer.append((char)Integer.parseInt(next(2), 16));
                    break;

                case 99: // 'c'
                case 100: // 'd'
                case 101: // 'e'
                case 103: // 'g'
                case 104: // 'h'
                case 105: // 'i'
                case 106: // 'j'
                case 107: // 'k'
                case 108: // 'l'
                case 109: // 'm'
                case 111: // 'o'
                case 112: // 'p'
                case 113: // 'q'
                case 115: // 's'
                case 118: // 'v'
                case 119: // 'w'
                default:
                    stringbuffer.append(c1);
                    break;
                }
                break;

            default:
                if(c1 == c)
                {
                    return stringbuffer.toString();
                }
                stringbuffer.append(c1);
                break;
            }
        } while(true);
    }

    public String nextTo(char c)
    {
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            char c1 = next();
            if(c1 == c || c1 == 0 || c1 == '\n' || c1 == '\r')
            {
                if(c1 != 0)
                {
                    back();
                }
                return stringbuffer.toString().trim();
            }
            stringbuffer.append(c1);
        } while(true);
    }

    public String nextTo(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            char c = next();
            if(s.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r')
            {
                if(c != 0)
                {
                    back();
                }
                return stringbuffer.toString().trim();
            }
            stringbuffer.append(c);
        } while(true);
    }

    public Object nextValue()
        throws JSONException
    {
        String s;
        char c = nextClean();
        switch(c)
        {
        case 34: // '"'
        case 39: // '\''
            return nextString(c);

        case 123: // '{'
            back();
            return new JSONObject(this);

        case 91: // '['
            back();
            return new JSONArray(this);
        }
        StringBuffer stringbuffer = new StringBuffer();
        char c1 = c;
        for(; c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0; c = next())
        {
            stringbuffer.append(c);
        }

        back();
        s = stringbuffer.toString().trim();
        if(s.equals(""))
        {
            throw syntaxError("Missing value");
        }
        return JSONObject.stringToValue(s);
//        if(s.equalsIgnoreCase("true"))
//        {
//            return Boolean.TRUE;
//        }
//        if(s.equalsIgnoreCase("false"))
//        {
//            return Boolean.FALSE;
//        }
//        if(s.equalsIgnoreCase("null"))
//        {
//            return JSONObject.NULL;
//        }
//        if((c1 < '0' || c1 > '9') && c1 != '.' && c1 != '-' && c1 != '+')
//        {
//            break MISSING_BLOCK_LABEL_331;
//        }
//        if(c1 != '0')
//        {
//            break MISSING_BLOCK_LABEL_296;
//        }
//        if(s.length() <= 2 || s.charAt(1) != 'x' && s.charAt(1) != 'X')
//        {
//            break MISSING_BLOCK_LABEL_280;
//        }
//        return new Integer(Integer.parseInt(s.substring(2), 16));
//        Exception exception;
//
//        break MISSING_BLOCK_LABEL_296;
//        return new Integer(Integer.parseInt(s, 8));
//
//        return new Integer(s);
//
//        return new Long(s);
//        Exception exception1;
//
//        return new Double(s);
//        Exception exception2;
//
//        return s;
    }

    public char skipTo(char c)
    {
        int i = myIndex;
        char c1;
        do
        {
            c1 = next();
            if(c1 == 0)
            {
                myIndex = i;
                return c1;
            }
        } while(c1 != c);
        back();
        return c1;
    }

    public boolean skipPast(String s)
    {
        myIndex = mySource.indexOf(s, myIndex);
        if(myIndex < 0)
        {
            myIndex = mySource.length();
            return false;
        } else
        {
            myIndex += s.length();
            return true;
        }
    }

    public JSONException syntaxError(String s)
    {
        return new JSONException((new StringBuilder()).append(s).append(toString()).toString());
    }

    public String toString()
    {
        return (new StringBuilder()).append(" at character ").append(myIndex).append(" of ").append(mySource).toString();
    }
}