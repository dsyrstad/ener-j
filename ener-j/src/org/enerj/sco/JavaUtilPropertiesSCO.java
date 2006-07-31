// Ener-J 
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/sco/JavaUtilPropertiesSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.sco;

import java.util.*;
import java.io.*;

import org.enerj.core.*;

/**
 * Second Class Object subclass for java.util.Properties.
 *
 * @version $Id: JavaUtilPropertiesSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class JavaUtilPropertiesSCO extends java.util.Properties implements SCOTracker
{
    private Persistable mOwnerFCO;
    
    //----------------------------------------------------------------------
    /**
     * Construct an empty collection using the specified initial capacity and 
     * owner FCO.
     *
     * @param anOwnerFCO the owning First Class Object.
     */
    public JavaUtilPropertiesSCO(Persistable anOwnerFCO)
    {
        super();
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
    // Overridden mutator methods from java.util.Properties.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // java.util.Map
    public Object put(Object key, Object value) 
    {
        Object o = super.put(key, value);
        setOwnerModified();
        return o;
    }
    
    //----------------------------------------------------------------------
    // java.util.Map
    public void putAll(Map t) 
    {
        super.putAll(t);
        setOwnerModified();
    }
    
    //----------------------------------------------------------------------
    // java.util.Map
    public void clear() 
    {
        super.clear();
        setOwnerModified();
    }
    
    //----------------------------------------------------------------------
    // java.util.Map
    public Object remove(Object key) 
    {
        Object o = super.remove(key);
        setOwnerModified();
        return o;
    }
    
    //----------------------------------------------------------------------
    // java.util.Properties
    public void load(InputStream inStream) throws IOException
    {
        setOwnerModified(); // Even if IOException occurs, partial load may occur
        super.load(inStream);
    }
    
    //----------------------------------------------------------------------
    // java.util.Properties
    public Object setProperty(String key, String value)
    {
        Object o = super.setProperty(key, value);
        setOwnerModified();
        return o;
    }
}
