//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/tools/enerjbrowser/model/QueryDef.java,v 1.1 2006/02/03 19:41:01 dsyrstad Exp $

package org.enerj.tools.browser.model;

/**
 * Query definition and result used by the model. <p>
 * 
 * @version $Id: QueryDef.java,v 1.1 2006/02/03 19:41:01 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QueryDef
{
    private String mQuery;
    private Object mResult;
    
    //--------------------------------------------------------------------------------
    QueryDef(String aQuery, Object aResult)
    {
        mQuery = aQuery;
        mResult = aResult;
    }

    //--------------------------------------------------------------------------------
    public String getQuery()
    {
        return mQuery;
    }

    //--------------------------------------------------------------------------------
    public Object getResult()
    {
        return mResult;
    }
    
    //--------------------------------------------------------------------------------
    public String toString() 
    {
        return mQuery;
    }
}
