// Ener-J 
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/sco/JavaSqlTimestampSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.sco;

import java.util.*;

import org.enerj.core.*;

/**
 * Second Class Object subclass for java.sql.Timestamp.
 *
 * @version $Id: JavaSqlTimestampSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class JavaSqlTimestampSCO extends java.sql.Timestamp implements SCOTracker
{
    private Persistable mOwnerFCO;
    
    //----------------------------------------------------------------------
    /**
     * Construct an empty collection using the specified initial capacity and 
     * owner FCO.
     *
     * @param aMillisTime the date in milliseconds since Jan 1, 1970.
     * @param aNanos the nanosecond part of the time value.
     * @param anOwnerFCO the owning First Class Object.
     */
    public JavaSqlTimestampSCO(long aMillisTime, int aNanos, Persistable anOwnerFCO)
    {
        super(aMillisTime);
        // Call the super method to avoid setting the owner modified.
        super.setNanos(aNanos);
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
    // Overridden mutator methods from java.sql.Timestamp.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // java.util.Timestamp
    public void setTime(long time) 
    {
        super.setTime(time);
        setOwnerModified();
    }

    //----------------------------------------------------------------------
    // java.sql.Timestamp
    public void setNanos(int n)
    {
        super.setNanos(n);
        setOwnerModified();
    }
}
