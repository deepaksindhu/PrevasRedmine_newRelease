package com.redmine.json;

import java.io.StringWriter;

// Referenced classes of package org.json:
//            JSONWriter

public class JSONStringer extends JSONWriter
{

    public JSONStringer()
    {
        super(new StringWriter());
    }

    public String toString()
    {
        return this.mode != 'd' ? null : this.writer.toString();
    }
}
