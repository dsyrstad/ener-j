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

import gnu.trove.TLongObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Tracks a list of modified Persistables. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ModifiedPersistableList
{
    // Note that we can't use a LinkedHashMap here because we need to return a ListIterator in order to 
    // add objects while flushing.
    
    /** List of Persistable objects created or modified during this transaction. */
    private List<Persistable> mModifiedObjectList = new ArrayList<Persistable>(1024);
    
    /** Map of the objects so that we can look them up by ID. Key is OID, value is the Persistable. */
    private TLongObjectHashMap mModifiedObjectMap = new TLongObjectHashMap(1024, .75F);

    /**
     * Construct a ModifiedPersistables. 
     */
    public ModifiedPersistableList()
    {
    }
    
    /**
     * Add a Persistable to the list of modified objects. Do not call this while iterating over the 
     * iterator returned by {@link #getIterator()}. Instead, use {@link ListIterator#add(Object)} to 
     * add the object to the iterator.
     *
     * @param aPersistable the object to be added. {@link Persistable#enerj_GetPrivateOID()} must
     *  be set. 
     */
    public void addToModifiedList(Persistable aPersistable)
    {
        mModifiedObjectList.add(aPersistable);
        addToModifiedMap(aPersistable);
    }

    /**
     * Adds the Persistable to the modified map, but not the list. 
     *
     * @param aPersistable the object to be added.
     */
    private void addToModifiedMap(Persistable aPersistable)
    {
        mModifiedObjectMap.put(aPersistable.enerj_GetPrivateOID(), aPersistable);
    }

    /**
     * Gets an iterator on the list of modified objects.
     *
     * @return an iterator on the list of modified objects.
     */
    public ListIterator<Persistable> getIterator()
    {
        return new MOListIterator();
    }
    
    /**
     * Clears the list of modified objects.
     */
    public void clearModifiedList()
    {
        mModifiedObjectList.clear();
        mModifiedObjectMap.clear();
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
        return (Persistable)mModifiedObjectMap.get(anOID);
    }
    
    /**
     * Our version of ListIterator that delegates to the list's iterator. When a
     * new object is being added, it is also placed in the map. <p>
     */
    private final class MOListIterator implements ListIterator<Persistable>
    {
        ListIterator<Persistable> delegate = mModifiedObjectList.listIterator();
        
        MOListIterator()
        {
        }

        public void add(Persistable o)
        {
            delegate.add(o);
            addToModifiedMap(o);
        }

        public boolean hasNext()
        {
            return delegate.hasNext();
        }

        public boolean hasPrevious()
        {
            return delegate.hasPrevious();
        }

        public Persistable next()
        {
            return delegate.next();
        }

        public int nextIndex()
        {
            return delegate.nextIndex();
        }

        public Persistable previous()
        {
            return delegate.previous();
        }

        public int previousIndex()
        {
            return delegate.previousIndex();
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Remove not allowed on ModifiedPersistableList");
        }

        public void set(Persistable o)
        {
            throw new UnsupportedOperationException("Set not allowed on ModifiedPersistableList");
        }
    }
}
