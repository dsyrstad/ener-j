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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/PersistentSet.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DSet which supports persistable sets
 * as first-class objects (FCOs).
 * <p>
 * The set is implemented as a container of java.util.Set. However,
 * if you reference this type of collection in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the collection to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.HashSet directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 *
 * @version $Id: PersistentSet.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DSet
 */
@Persist
public class PersistentHashSet implements org.odmg.DSet, Cloneable
{
    /** The delegate Set. This is treated as an SCO when this FCO is persisted. */
    private Set mDelegateSet;
    

    /**
     * Constructs a new PersistentSet backed by the given Set. Changes made to this
     * DSet are seen in the delegate Set, and vice-verse.  
     * 
     * @param aDelegateSet a delegate Set.
     */
    public PersistentHashSet(Set aDelegateSet)
    {
        mDelegateSet = aDelegateSet;
    }
    

    /**
     * Constructs a new PersistentSet with the specified initial capacity. 
     * 
     * @param anInitialCapacity the initially allocated capacity of the set.
     *  This does not affect the size of the set.
     */
    public PersistentHashSet(int anInitialCapacity)
    {
        mDelegateSet = new HashSet(anInitialCapacity, 1.0F);
    }
    

    /**
     * Constructs a new PersistentSet with an initial capacity of 10. 
     */
    public PersistentHashSet()
    {
        this(10);
    }
    

    // Start of Interfaces: org.odmg.DSet, java.util.Collection



    public boolean add(Object o)
    {
        return mDelegateSet.add(o);
    }
    

    public boolean addAll(Collection c)
    {
        return mDelegateSet.addAll(c);
    }
    

    public void clear()
    {
        mDelegateSet.clear();
    }
    

    public boolean contains(Object o)
    {
        return mDelegateSet.contains(o);
    }
    

    public boolean containsAll(Collection c)
    {
        return mDelegateSet.containsAll(c);
    }
    

    public boolean isEmpty() 
    {
        return mDelegateSet.isEmpty();
    }
    

    public Iterator iterator() 
    {
        return mDelegateSet.iterator();
    }
    

    public boolean remove(Object o) 
    {
        return mDelegateSet.remove(o);
    }
    

    public boolean removeAll(Collection c) 
    {
        return mDelegateSet.removeAll(c);
    }
    

    public boolean retainAll(Collection c) 
    {
        return mDelegateSet.retainAll(c);
    }
    

    public int size() 
    {
        return mDelegateSet.size();
    }
    

    public Object[] toArray() 
    {
        return mDelegateSet.toArray();
    }
    

    public Object[] toArray(Object[] a) 
    {
        return mDelegateSet.toArray(a);
    }
    

    /** 
     * Create a new <code>DSet</code> object that contains the elements of this
     * collection minus the elements in <code>anOtherSet</code>.
     *
     * @param anOtherSet a set containing elements that should not be in the result set.
     *
     * @return a newly created <code>DSet</code> instance that contains the elements
     * of this set minus those elements in <code>anOtherSet</code>.
     */
    public DSet difference(DSet anOtherSet)
    {
        return difference((Set)anOtherSet);
    }
    

    /** 
     * Create a new <code>DSet</code> object that contains the elements of this
     * collection minus the elements in <code>anOtherSet</code>.
     *
     * @param anOtherSet a Set containing elements that should not be in the result set.
     *
     * @return a newly created <code>DSet</code> instance that contains the elements
     * of this set minus those elements in <code>anOtherSet</code>.
     */
    public DSet difference(Set anOtherSet) 
    {
        int set1Size = size();
        int set2Size = anOtherSet.size();
        // Worst case: all items from both sets will be in the result.
        DSet result = new PersistentHashSet(set1Size + set2Size);
        
        Iterator iterator1 = this.iterator();
        while (iterator1.hasNext()) {
            Object obj = iterator1.next();
            if ( !anOtherSet.contains(obj) ) {
                result.add(obj);
            }
        }
        
        return result;
    }
    

    /** 
     * Create a new <code>DSet</code> object that is the set intersection of this
     * <code>DSet</code> object and the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet the other set to be used in the intersection operation.
     *
     * @return a newly created <code>DSet</code> instance that contains the
     * intersection of the two sets.
     */
    public DSet intersection(DSet anOtherSet)
    {
        return intersection((Set)anOtherSet);
    }
    

    /** 
     * Create a new <code>DSet</code> object that is the set intersection of this
     * <code>DSet</code> object and the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet the other Set to be used in the intersection operation.
     *
     * @return a newly created <code>DSet</code> instance that contains the
     * intersection of the two sets.
     */
    public DSet intersection(Set anOtherSet) 
    {
        int set1Size = size();
        int set2Size = anOtherSet.size();
        // Worst case: all items from both sets will be in the result.
        DSet result = new PersistentHashSet(set1Size + set2Size);
        
        Iterator iterator1;
        Set set2;
        // Iterate over the smaller set
        if (set1Size > set2Size) {
            iterator1 = anOtherSet.iterator();
            set2 = this;
        }
        else {
            iterator1 = this.iterator();
            set2 = anOtherSet;
        }

        while (iterator1.hasNext()) {
            Object obj = iterator1.next();
            if ( anOtherSet.contains(obj) ) {
                result.add(obj);
            }
        }
        
        return result;
    }
    

    /** 
     * Create a new <code>DSet</code> object that is the set union of this
     * <code>DSet</code> object and the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet the other set to be used in the union operation.
     *
     * @return a newly created <code>DSet</code> instance that contains the union of the two sets.
     */
    public DSet union(DSet anOtherSet)
    {
        return union((Set)anOtherSet);
    }


    /** 
     * Create a new <code>DSet</code> object that is the set union of this
     * <code>DSet</code> object and the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet the other Set to be used in the union operation.
     *
     * @return a newly created <code>DSet</code> instance that contains the union of the two sets.
     */
    public DSet union(Set anOtherSet) 
    {
        int set1Size = size();
        int set2Size = anOtherSet.size();
        // All items from both sets will be in the result.
        DSet result = new PersistentHashSet(set1Size + set2Size);
        
        result.addAll(this);
        result.addAll(anOtherSet);
        return result;
    }
    

    /** 
     * Determine whether this set is a proper subset of the set referenced by
     * <code>anOtherSet</code>. It is a proper subset if subsetOf(anOtherSet) is 
     * true and anOtherSet.size() > size() (anOtherSet has at least one other item
     * not in this set).
     *
     * @param anOtherSet another set.
     *
     * @return true if this set is a proper subset of the set referenced by
     * <code>anOtherSet</code>, otherwise false.
     */
    public boolean properSubsetOf(DSet anOtherSet) 
    {
        return subsetOf(anOtherSet) && anOtherSet.size() > size();
    }
    

    /** 
     * Determine whether this set is a proper superset of the set referenced by
     * <code>anOtherSet</code>.  It is a proper superset if supersetOf(anOtherSet) is 
     * true and size() > anOtherSet.size() (this set has at least one other item
     * not in anOtherSet).
     *
     * @param anOtherSet another set.
     *
     * @return true if this set is a proper superset of the set referenced by
     * <code>anOtherSet</code>, otherwise false.
     */
    public boolean properSupersetOf(DSet anOtherSet) 
    {
        return supersetOf(anOtherSet) && size() > anOtherSet.size();
    }
    

    /** 
     * Determine whether this set is a subset of the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet another set.
     *
     * @return true if this set is a subset of the set referenced by <code>anOtherSet</code>,
     * otherwise false.
     */
    public boolean subsetOf(DSet anOtherSet) 
    {
        return anOtherSet.containsAll(this);
    }
    

    /** 
     * Determine whether this set is a superset of the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet another set.
     *
     * @return true if this set is a superset of the set referenced by <code>anOtherSet</code>,
     * otherwise false.
     */
    public boolean supersetOf(DSet anOtherSet) 
    {
        return containsAll(anOtherSet);
    }
    

    public java.util.Iterator select(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public boolean existsElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public org.odmg.DCollection query(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public Object selectElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    /**
     * {@inheritDoc}
     */
    public int hashCode() 
    {
        return mDelegateSet.hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mDelegateSet.equals(anObject);
    }


    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mDelegateSet.toString();
    }


    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException
    {
        PersistentHashSet clone = (PersistentHashSet)super.clone();
        HashSet set = new HashSet( mDelegateSet.size(), 1.0F);
        set.addAll(mDelegateSet);
        clone.mDelegateSet = set;
        return clone;
    }
    

    // ...End of Interfaces: org.odmg.DSet, java.util.Collection.

}

