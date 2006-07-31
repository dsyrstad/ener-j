//Ener-J
//Copyright 2000-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/tools/enerjbrowser/model/NamedObject.java,v 1.2 2006/01/17 02:41:09 dsyrstad Exp $

package org.enerj.tools.browser.model;

/**
 * A object that has a name associated with it. <p>
 * 
 * @version $Id: NamedObject.java,v 1.2 2006/01/17 02:41:09 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class NamedObject
{
    private String mName;
    private Object mObject;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a NamedObject. 
     *
     * @param aName
     * @param anObject
     */
    public NamedObject(String aName, Object anObject) 
    {
        mName = aName;
        mObject = anObject;
    }
    
    //--------------------------------------------------------------------------------
    public String getName()
    {
        return mName;
    }

    //--------------------------------------------------------------------------------
    public Object getObject()
    {
        return mObject;
    }
    
    //--------------------------------------------------------------------------------
    public String toString()
    {
        return mName;
    }

    //--------------------------------------------------------------------------------
    public boolean equals(Object anObj)
    {
        if (mObject == null) {
            return anObj == null;
        }
        
        if (anObj == null) {
            return false;
        }
        
        return mObject.equals(anObj);
    }

    //--------------------------------------------------------------------------------
    public int hashCode()
    {
        if (mObject != null) {
            return mObject.hashCode();
        }
        
        return 37;
    }
}
