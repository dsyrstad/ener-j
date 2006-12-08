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
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/PersistentArray.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.enerj.annotations.Persist;
import org.odmg.QueryInvalidException;

/**
 * Ener-J implementation of org.odmg.DArray which supports persistable dynamic arrays
 * as first-class objects (FCOs). This type of array is useful when the array itself can
 * fit entirely in memory at one time.  If you have an array that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use VeryLargeDArray.
 * <p>
 * The array is implemented as a container of java.util.ArrayList. However,
 * if you reference this type of collection in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the collection to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.ArrayList directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 *
 * @version $Id: PersistentArray.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DArray
 * @see LargePersistentArrayList
 */
@Persist
public class PersistentArrayList<E> implements org.odmg.DArray<E>, Cloneable
{
    /** The delegate array. This is treated as an SCO when this FCO is persisted. */
    private ArrayList<E> mArrayList;
    

    /**
     * Constructs a new PersistentArray with the specified initial capacity. 
     * 
     * @param anInitialCapacity the initially allocated capacity of the array.
     *  This does not affect the size of the array.
     */
    public PersistentArrayList(int anInitialCapacity)
    {
        mArrayList = new ArrayList<E>(anInitialCapacity);
    }
    

    /**
     * Constructs a new PersistentArray with an initial capacity of 10. 
     */
    public PersistentArrayList()
    {
        this(10);
    }
    

    // Start of Interfaces: org.odmg.DArray, java.util.Collection, java.util.List...



    public boolean add(E o)
    {
        return mArrayList.add(o);
    }
    

    public void add(int index, E element)
    {
        mArrayList.add(index, element);
    }
    

    public boolean addAll(Collection<? extends E> c)
    {
        return mArrayList.addAll(c);
    }
    

    public boolean addAll(int index, Collection<? extends E> c)
    {
        return mArrayList.addAll(index, c);
    }
    

    public void clear()
    {
        mArrayList.clear();
    }
    

    public boolean contains(Object o)
    {
        return mArrayList.contains(o);
    }
    

    public boolean containsAll(Collection c)
    {
        return mArrayList.containsAll(c);
    }
    

    public E get(int index) 
    {
        return mArrayList.get(index);
    }
    

    public int indexOf(Object o) 
    {
        return mArrayList.indexOf(o);
    }
    

    public boolean isEmpty() 
    {
        return mArrayList.isEmpty();
    }
    

    public Iterator<E> iterator() 
    {
        return mArrayList.iterator();
    }
    

    public int lastIndexOf(Object o) 
    {
        return mArrayList.lastIndexOf(o);
    }
    

    public ListIterator<E> listIterator() 
    {
        return mArrayList.listIterator();
    }
    

    public ListIterator<E> listIterator(int index) 
    {
        return mArrayList.listIterator(index);
    }
    

    public boolean remove(Object o) 
    {
        return mArrayList.remove(o);
    }
    

    public E remove(int index) 
    {
        return mArrayList.remove(index);
    }
    

    public boolean removeAll(Collection c) 
    {
        return mArrayList.removeAll(c);
    }
    

    public void resize(int aNewSize) 
    {
        // ODMG v3.0 2.3.6.4 says resize changes the maximum number of elements
        // the array can contain. It also says if aNewSize is smaller than
        // the actual number of elements, an exception is thrown. The
        // org.odmg javadoc says nothing about the behavior. Poet allows shrinking
        // or growing. IIOSS conforms to the spec.
        if (aNewSize < mArrayList.size()) {
            throw new IllegalArgumentException("resize(" + aNewSize + ") is smaller than current size: " + mArrayList.size());
        }
        
        int count = aNewSize - mArrayList.size();
        for (; count > 0; --count) {
            mArrayList.add(null);
        }
    }
    

    public boolean retainAll(Collection c) 
    {
        return mArrayList.retainAll(c);
    }
    

    public E set(int index, E element) 
    {
        return mArrayList.set(index, element);
    }
    

    public int size() 
    {
        return mArrayList.size();
    }
    

    public List<E> subList(int fromIndex, int toIndex) 
    {
        return mArrayList.subList(fromIndex, toIndex);
    }
    

    public Object[] toArray() 
    {
        return mArrayList.toArray();
    }
    

    public <T> T[] toArray(T[] a) 
    {
        return mArrayList.toArray(a);
    }
    

    public java.util.Iterator<E> select(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public boolean existsElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public org.odmg.DCollection<E> query(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public E selectElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    /**
     * {@inheritDoc}
     */
    public int hashCode() 
    {
        return mArrayList.hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mArrayList.equals(anObject);
    }


    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mArrayList.toString();
    }


    /**
     * {@inheritDoc}
     */
    public PersistentArrayList<E> clone() throws CloneNotSupportedException
    {
        PersistentArrayList<E> clone = (PersistentArrayList<E>)super.clone();
        clone.mArrayList = (ArrayList<E>)mArrayList.clone();
        return clone;
    }
    

    // ...End of Interfaces: org.odmg.DArray, java.util.Collection, java.util.List.

}

