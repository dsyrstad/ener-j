/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
//$Header: $

package org.enerj.core;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Tracks a list of modified Persistables. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ModifiedPersistableList
{
    /** List of Persistable objects created or modified during this transaction. Key is OID, value is the Persistable. */
    private LinkedHashMap<Long,Persistable> mModifiedObjects = new LinkedHashMap<Long,Persistable>(1024, .75F, false);

    /**
     * Construct a ModifiedPersistables. 
     */
    public ModifiedPersistableList()
    {
    }
    
    /**
     * Add a Persistable to the list of modified objects.  
     *
     * @param aPersistable the object to be added. {@link Persistable#enerj_GetPrivateOID()} must
     *  be set. 
     */
    public void addToModifiedList(Persistable aPersistable)
    {
        mModifiedObjects.put(aPersistable.enerj_GetPrivateOID(), aPersistable);
    }

    /**
     * Gets the list of modified objects.
     *
     * @return the list of modified objects.
     */
    public Iterator<Persistable> getIterator()
    {
        return mModifiedObjects.values().iterator();
    }
    
    /**
     * Clears the list of modified objects.
     */
    public void clearModifiedList()
    {
        mModifiedObjects.clear();
    }
    
    
    /**
     * Gets a modified object based on its OID.
     *
     * @param anOID the OID.
     * 
     * @return the modified object, or null if the object does not exist.
     */
    public Persistable getModifiedObjectByOID(long anOID)
    {
        return mModifiedObjects.get(anOID);
    }

}
