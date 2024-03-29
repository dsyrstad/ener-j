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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/PersistentBag.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DBag which supports persistable bags
 * as first-class objects (FCOs). This type of bag is useful when the bag itself can
 * fit entirely in memory at one time.  If you have an bag that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use VeryLargeDArray.
 * <p>
 * The bag is implemented as a container of java.util.Collection. However,
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
 * @version $Id: PersistentBag.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DBag
 * @see LargePersistentArrayList
 */
@Persist
public class PersistentBag<E> implements org.odmg.DBag<E>, Cloneable
{
    /** The delegate bag. This is treated as an SCO when this FCO is persisted. */
    private Collection<E> mDelegateBag;
    

    /**
     * Constructs a PersistentBag backed by the given Collection. Changes to this DBag
     * are reflected in the delegate, and vice versa.
     *
     * @param aCollection the delegate "bag".
     */
    public PersistentBag(Collection<E> aCollection)
    {
        mDelegateBag = aCollection;
    }


    /**
     * Constructs an empty PersistentBag with the specified initial capacity. 
     *
     * @param anInitialCapacity the initial capacity of the bag. This does not 
     *  affect the bag size().
     */
    public PersistentBag(int anInitialCapacity)
    {
        mDelegateBag = new ArrayList<E>(anInitialCapacity);
    }


    /**
     * Constructs an empty PersistentBag with an initial capacity of 30. 
     */
    public PersistentBag()
    {
        this(30);
    }


    // Start of Interfaces: org.odmg.DBag, java.util.Collection



    public boolean add(E o)
    {
        return mDelegateBag.add(o);
    }
    

    public boolean addAll(Collection<? extends E> c)
    {
        return mDelegateBag.addAll(c);
    }
    

    public void clear()
    {
        mDelegateBag.clear();
    }
    

    public boolean contains(Object o)
    {
        return mDelegateBag.contains(o);
    }
    

    public boolean containsAll(Collection c)
    {
        return mDelegateBag.containsAll(c);
    }
    

    public boolean isEmpty() 
    {
        return mDelegateBag.isEmpty();
    }
    

    public Iterator<E> iterator() 
    {
        return mDelegateBag.iterator();
    }
    

    public boolean remove(Object o) 
    {
        return mDelegateBag.remove(o);
    }
    

    public boolean removeAll(Collection c) 
    {
        return mDelegateBag.removeAll(c);
    }
    

    public boolean retainAll(Collection c) 
    {
        return mDelegateBag.retainAll(c);
    }
    

    public int size() 
    {
        return mDelegateBag.size();
    }
    

    public Object[] toArray() 
    {
        return mDelegateBag.toArray();
    }
    

    public <T> T[] toArray(T[] a) 
    {
        return mDelegateBag.toArray(a);
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
        return mDelegateBag.hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mDelegateBag.equals(anObject);
    }


    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mDelegateBag.toString();
    }


    /**
     * {@inheritDoc}
     */
    public PersistentBag<E> clone() throws CloneNotSupportedException
    {
        PersistentBag<E> clone = (PersistentBag<E>)super.clone();
        Collection<E> bag = new ArrayList<E>( mDelegateBag.size() );
        bag.addAll(mDelegateBag);
        clone.mDelegateBag = bag;
        return clone;
    }
    

    /** 
     * A new <code>DBag</code> instance is created that contains the difference of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>removeAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>removeAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other bag to use in creating the difference.
     *
     * @return a <code>DBag</code> instance that contains the elements of this object
     * minus the elements in <code>anOtherBag</code>.
     */
    public DBag<E> difference(DBag anOtherBag)
    {
        return difference((Collection)anOtherBag);
    }
    

    /** 
     * A new <code>DBag</code> instance is created that contains the difference of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>removeAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>removeAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other Collection ("bag") to use in creating the difference.
     *
     * @return a <code>DBag</code> instance that contains the elements of this object
     *  minus the elements in <code>otherBag</code>.
     */
    public DBag<E> difference(Collection anOtherBag) 
    {
        int bag1Size = size();
        int bag2Size = anOtherBag.size();
        // Worst case: all items from both bags will be in the result.
        DBag<E> result = new PersistentBag<E>(bag1Size + bag2Size);
        
        Iterator<E> iterator1 = this.iterator();
        while (iterator1.hasNext()) {
            E obj = iterator1.next();
            if ( !anOtherBag.contains(obj) ) {
                result.add(obj);
            }
        }
        
        return result;
    }
    

    /** 
     * A new <code>DBag</code> instance is created that contains the intersection of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>retainAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>retainAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other bag to use in creating the intersection.
     *
     * @return a <code>DBag</code> instance that contains the intersection of this
     * object and <code>anOtherBag</code>.
     */
    public DBag<E> intersection(DBag<E> anOtherBag)
    {
        return intersection((Collection<E>)anOtherBag);
    }
    

    /** 
     * A new <code>DBag</code> instance is created that contains the intersection of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>retainAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>retainAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other Collection ("bag") to use in creating the intersection.
     *
     * @return a <code>DBag</code> instance that contains the intersection of this
     * object and <code>otherBag</code>.
     */
    public DBag<E> intersection(Collection<E> anOtherBag)
    {
        int bag1Size = size();
        int bag2Size = anOtherBag.size();
        // Worst case: all items from both bags will be in the result.
        DBag<E> result = new PersistentBag<E>(bag1Size + bag2Size);
        
        Iterator<E> iterator1;
        // Iterate over the smaller bag
        if (bag1Size > bag2Size) {
            iterator1 = anOtherBag.iterator();
        }
        else {
            iterator1 = this.iterator();
        }

        while (iterator1.hasNext()) {
            E obj = iterator1.next();
            if ( anOtherBag.contains(obj) ) {
                result.add(obj);
            }
        }
        
        return result;
    }
    

    /** 
     * A new <code>DBag</code> instance is created that is the union of this object
     * and <code>anOtherBag</code>.
     * This method is similar to the <code>addAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>addAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag The other bag to use in the union operation.
     * @return a <code>DBag</code> instance that contains the union of this object
     * and <code>otherBag</code>.
     */
    public DBag<E> union(DBag<E> anOtherBag)
    {
        return union((Collection<E>)anOtherBag);
    }
    

    /** 
     * A new <code>DBag</code> instance is created that is the union of this object
     * and <code>anOtherBag</code>.
     * This method is similar to the <code>addAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>addAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag The other Collection ("bag") to use in the union operation.
     * 
     * @return a <code>DBag</code> instance that contains the union of this object
     * and <code>anOtherBag</code>.
     */
    public DBag<E> union(Collection<E> anOtherBag) 
    {
        int bag1Size = size();
        int bag2Size = anOtherBag.size();
        // All items from both bags will be in the result.
        DBag<E> result = new PersistentBag<E>(bag1Size + bag2Size);
        
        result.addAll(this);
        result.addAll(anOtherBag);
        return result;
    }
    

    /** 
     * This method returns the number of occurrences of the object <code>obj</code>
     * in the <code>DBag</code> collection. Occurance comparisons are based on equals(),
     * unless anObject is null, in which case item == null is used.
     *
     * @param anObject the value that may have elements in the collection. May be null
     *  to count the number of null items in the bag.
     *
     * @return The number of occurrences of <code>obj</code> in this collection.
     */
    public int occurrences(Object anObject) 
    {
        int count = 0;
        if (anObject == null) {
            for (Object obj : mDelegateBag) {
                if (obj == null) {
                    ++count;
                }
            }
        }
        else {
            for (Object obj: mDelegateBag) {
                if (anObject.equals(obj) ) {
                    ++count;
                }
            }
        }
        
        return count;
    }
    

    // ...End of Interfaces: org.odmg.DBag, java.util.Collection, java.util.List.

}

