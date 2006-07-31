// Ener-J 
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/sco/JavaUtilDateSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.sco;

import java.util.*;

import org.enerj.core.*;

/**
 * Second Class Object subclass for java.util.Date.
 *
 * @version $Id: JavaUtilDateSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class JavaUtilDateSCO extends java.util.Date implements SCOTracker
{
    private Persistable mOwnerFCO;
    
    //----------------------------------------------------------------------
    /**
     * Construct an empty collection using the specified date/time and 
     * owner FCO.
     *
     * @param aMillisTime the date in milliseconds since Jan 1, 1970.
     * @param anOwnerFCO the owning First Class Object.
     */
    public JavaUtilDateSCO(long aMillisTime, Persistable anOwnerFCO)
    {
        super(aMillisTime);
        mOwnerFCO = anOwnerFCO;
    }

    //----------------------------------------------------------------------
    // From SCOTracker...
    public Persistable getOwnerFCO()
    {
        return mOwnerFCO;
    }
    
    //----------------------------------------------------------------------
    // From SCOTracker...
    public void setOwnerFCO(Persistable anOwner)
    {
        mOwnerFCO = anOwner;
    }
    
    //----------------------------------------------------------------------
    // From SCOTracker...
    public void setOwnerModified() 
    {
        if (mOwnerFCO != null) {
            PersistableHelper.addModified(mOwnerFCO);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Returns a clone without the owner set.
     *
     * @return an un-owned clone.
     */
    public Object clone()
    {
        SCOTracker clone = (SCOTracker)super.clone();
        clone.setOwnerFCO(null);
        return clone;
    }
    
    //----------------------------------------------------------------------
    // Overridden mutator methods from java.util.Date.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // java.util.Date
    public void setTime(long time) 
    {
        super.setTime(time);
        setOwnerModified();
    }
}
