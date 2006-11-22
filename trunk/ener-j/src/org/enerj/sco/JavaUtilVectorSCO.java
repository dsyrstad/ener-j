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
// Ener-J 
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/sco/JavaUtilVectorSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.sco;

import java.util.*;

import org.enerj.core.*;

/**
 * Second Class Object subclass for java.util.Vector.
 *
 * @version $Id: JavaUtilVectorSCO.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class JavaUtilVectorSCO extends java.util.Vector implements SCOTracker
{
    private Persistable mOwnerFCO;
    

    /**
     * Construct an empty collection using the specified initial capacity and 
     * owner FCO.
     *
     * @param anInitialCapacity an initial capacity.
     * @param anOwnerFCO the owning First Class Object.
     */
    public JavaUtilVectorSCO(int anInitialCapacity, Persistable anOwnerFCO)
    {
        super(anInitialCapacity);
        mOwnerFCO = anOwnerFCO;
    }


    // From SCOTracker...
    public Persistable getOwnerFCO()
    {
        return mOwnerFCO;
    }
    

    // From SCOTracker...
    public void setOwnerFCO(Persistable anOwner)
    {
        mOwnerFCO = anOwner;
    }
    

    // From SCOTracker...
    public void setOwnerModified() 
    {
        if (mOwnerFCO != null) {
            PersistableHelper.addModified(mOwnerFCO);
        }
    }
    

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
    

    // Overridden mutator methods from java.util.Vector.



    // java.util.Collection
    public boolean add(Object o) 
    {
        boolean b = super.add(o);
        setOwnerModified();
        return b;
    }
    

    // java.util.Collection
    public boolean addAll(Collection c) 
    {
        boolean b = super.addAll(c);
        setOwnerModified();
        return b;
    }
    

    // java.util.Collection
    public void clear() 
    {
        super.clear();
        setOwnerModified();
    }
    

    // java.util.Collection
    public boolean remove(Object o) 
    {
        boolean b = super.remove(o);
        setOwnerModified();
        return b;
    }
    

    // java.util.Collection
    public boolean removeAll(Collection c) 
    {
        boolean b = super.removeAll(c);
        setOwnerModified();
        return b;
    }
    

    // java.util.Collection
    public boolean retainAll(Collection c) 
    {
        boolean b = super.retainAll(c);
        setOwnerModified();
        return b;
    }
    

    // java.util.List
    public Object set(int index, Object element) 
    {
        Object o = super.set(index, element);
        setOwnerModified();
        return o;
    }
    

    // java.util.List
    public Object remove(int index) 
    {
        Object o = super.remove(index);
        setOwnerModified();
        return o;
    }
    

    // java.util.List
    public boolean addAll(int index, Collection c) 
    {
        boolean b = super.addAll(index, c);
        setOwnerModified();
        return b;
    }
    

    // java.util.List
    public void add(int index, Object element) 
    {
        super.add(index, element);
        setOwnerModified();
    }
    

    // java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex)
    {
        super.removeRange(fromIndex, toIndex);
        setOwnerModified();
    }


    // java.util.Vector
    public void addElement(Object obj)
    {
        super.addElement(obj);
        setOwnerModified();
    }


    // java.util.Vector
    public void insertElementAt(Object obj, int index)
    {
        super.insertElementAt(obj, index);
        setOwnerModified();
    }


    // java.util.Vector
    public void setElementAt(Object obj, int index)
    {
        super.setElementAt(obj, index);
        setOwnerModified();
    }


    // java.util.Vector
    public boolean removeElement(Object obj)
    {
        boolean b = super.removeElement(obj);
        setOwnerModified();
        return b;
    }


    // java.util.Vector
    public void removeElementAt(int index)
    {
        super.removeElementAt(index);
        setOwnerModified();
    }


    // java.util.Vector
    public void removeAllElements()
    {
        super.removeAllElements();
        setOwnerModified();
    }


    // java.util.Vector
    public void setSize(int newSize)
    {
        super.setSize(newSize);
        setOwnerModified();
    }
}
