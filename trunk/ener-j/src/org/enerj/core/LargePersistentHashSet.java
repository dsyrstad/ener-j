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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/PersistentMap.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
package org.enerj.core;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.enerj.annotations.Persist;
import org.odmg.DSet;
import org.odmg.QueryInvalidException;

/*
 * Note: This class was derived from Apache's Harmony project. 
 * 
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * Ener-J implementation of org.odmg.DSet which supports large persistable hash sets
 * as first-class objects (FCOs). This type of set is useful when the set itself cannot
 * fit entirely in memory at one time. The set never needs to be resized like a
 * HashSet sometimes does. If you have an set that can fit
 * reasonably in memory or you want to conserve disk storage space, consider {@link PersistentHashSet}.
 * <p>
 * 
 * If you reference this type of map in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the set to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.HashSet directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 *
 * @version $Id: PersistentMap.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DMap
 * @see PersistentHashSet
 */
@Persist
public class LargePersistentHashSet<E> extends AbstractSet<E> implements org.odmg.DSet<E>, Cloneable
{
    private static final int DEFAULT_NODE_SIZE = 1024;

    private LargePersistentHashMap<E, E> backingMap;

    /**
     * Constructs a new empty instance of LargePersistentHashSet with a node size of 1024.
     * This will comfortably support about 1 billion objects. 
     */
    public LargePersistentHashSet()
    {
        this(DEFAULT_NODE_SIZE);
    }

    /**
     * Constructs a new instance of LargePersistentHashSet with the specified node size. The node size is the 
     * node size of the backing {@link LargePersistentArrayList}. See {@link LargePersistentArrayList} 
     * for an explanation of node sizes.  
     * 
     * @param nodeSize the 
     *            the initial capacity of this LargePersistentHashSet.
     * 
     * @exception IllegalArgumentException
     *                when the nodeSize is less than zero.
     */
    public LargePersistentHashSet(int nodeSize)
    {
        if (nodeSize < 0) {
            throw new IllegalArgumentException();
        }

        backingMap = new LargePersistentHashMap<E, E>(nodeSize);
    }

    /**
     * Constructs a new instance of LargePersistentHashSet containing the entries from the
     * specified collection.
     * 
     * @param collection the entries to add
     */
    public LargePersistentHashSet(Collection<? extends E> collection)
    {
        this();
        addAll(collection);
    }

    /**
     * Adds the specified object to this HashSet.
     * 
     * @param object
     *            the object to add
     * @return true when this HashSet did not already contain the object, false
     *         otherwise
     */
    @Override
    public boolean add(E object)
    {
        return backingMap.put(object, object) == null;
    }

    /**
     * Removes all elements from this HashSet, leaving it empty.
     * 
     * @see #isEmpty
     * @see #size
     */
    @Override
    public void clear()
    {
        backingMap.clear();
    }

    /**
     * Answers a new HashSet with the same elements and size as this HashSet.
     * 
     * @return a shallow copy of this HashSet
     * 
     * @see java.lang.Cloneable
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone()
    {
        try {
            LargePersistentHashSet<E> clone = (LargePersistentHashSet<E>)super.clone();
            clone.backingMap = (LargePersistentHashMap<E, E>)backingMap.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Searches this HashSet for the specified object.
     * 
     * @param object
     *            the object to search for
     * @return true if <code>object</code> is an element of this HashSet,
     *         false otherwise
     */
    @Override
    public boolean contains(Object object)
    {
        return backingMap.containsKey(object);
    }

    /**
     * Answers if this HashSet has no elements, a size of zero.
     * 
     * @return true if this HashSet has no elements, false otherwise
     * 
     * @see #size
     */
    @Override
    public boolean isEmpty()
    {
        return backingMap.isEmpty();
    }

    /**
     * Answers an Iterator on the elements of this HashSet.
     * 
     * @return an Iterator on the elements of this HashSet
     * 
     * @see Iterator
     */
    @Override
    public Iterator<E> iterator()
    {
        return backingMap.keySet().iterator();
    }

    /**
     * Removes an occurrence of the specified object from this HashSet.
     * 
     * @param object
     *            the object to remove
     * @return true if this HashSet is modified, false otherwise
     */
    @Override
    public boolean remove(Object object)
    {
        return backingMap.remove(object) != null;
    }

    /**
     * Answers the number of elements in this HashSet.
     * 
     * @return the number of elements in this HashSet
     */
    @Override
    public int size()
    {
        return backingMap.size();
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
    public DSet<E> difference(DSet<E> anOtherSet)
    {
        return difference((Set<E>)anOtherSet);
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
    public DSet<E> difference(Set<E> anOtherSet) 
    {
        int set1Size = size();
        int set2Size = anOtherSet.size();
        // Worst case: all items from both sets will be in the result.
        DSet<E> result = new PersistentHashSet<E>(set1Size + set2Size);
        
        Iterator<E> iterator1 = this.iterator();
        while (iterator1.hasNext()) {
            E obj = iterator1.next();
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
    public DSet<E> intersection(DSet<E> anOtherSet)
    {
        return intersection((Set<E>)anOtherSet);
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
    public DSet<E> intersection(Set<E> anOtherSet) 
    {
        int set1Size = size();
        int set2Size = anOtherSet.size();
        // Worst case: all items from both sets will be in the result.
        DSet<E> result = new PersistentHashSet<E>(set1Size + set2Size);
        
        Iterator<E> iterator1;
        // Iterate over the smaller set
        if (set1Size > set2Size) {
            iterator1 = anOtherSet.iterator();
        }
        else {
            iterator1 = this.iterator();
        }

        while (iterator1.hasNext()) {
            E obj = iterator1.next();
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
    public DSet<E> union(DSet<E> anOtherSet)
    {
        return union((Set<E>)anOtherSet);
    }


    /** 
     * Create a new <code>DSet</code> object that is the set union of this
     * <code>DSet</code> object and the set referenced by <code>anOtherSet</code>.
     *
     * @param anOtherSet the other Set to be used in the union operation.
     *
     * @return a newly created <code>DSet</code> instance that contains the union of the two sets.
     */
    public DSet<E> union(Set<E> anOtherSet) 
    {
        int set1Size = size();
        int set2Size = anOtherSet.size();
        // All items from both sets will be in the result.
        DSet<E> result = new PersistentHashSet<E>(set1Size + set2Size);
        
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
    public boolean properSubsetOf(DSet<E> anOtherSet) 
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
    public boolean properSupersetOf(DSet<E> anOtherSet) 
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
    public boolean subsetOf(DSet<E> anOtherSet) 
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
    

}
