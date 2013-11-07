package com.redmine.json;


public class JSONException extends Exception
{
	private static final long serialVersionUID = 0;
    private Throwable cause;

    public JSONException(String message)
    {
        super(message);
    }

    public JSONException(Throwable throwable)
    {
        super(throwable.getMessage());
        this.cause = throwable;
    }

    public Throwable getCause()
    {
        return this.cause;
    }
}
