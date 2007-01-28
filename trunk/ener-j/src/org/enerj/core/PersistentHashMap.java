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

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DMap which supports persistable dynamic maps
 * as first-class objects (FCOs). This type of map is useful when the map itself can
 * fit entirely in memory at one time.  If you have an map that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use {@link LargePersistentHashMap}. 
 * <p>
 * The map is implemented as a container of java.util.HashMap. However,
 * if you reference this type of collection in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the collection to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.HashMap directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 *
 * @version $Id: PersistentMap.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DMap
 * @see VeryLargeDMap
 */
@Persist
public class PersistentHashMap<K,V> implements org.odmg.DMap<K,V>, Cloneable
{
    /** The delegate map. This is treated as an SCO when this FCO is persisted. */
    private HashMap<K,V> mHashMap;
    

    /**
     * Constructs a new PersistentHashMap with the specified initial capacity. 
     * 
     * @param anInitialCapacity the initially allocated capacity of the map.
     *  This does not affect the size of the map.
     */
    public PersistentHashMap(int anInitialCapacity)
    {
        mHashMap = new HashMap<K,V>(anInitialCapacity);
    }
    

    /**
     * Constructs a new PersistentHashMap with an initial capacity of 100. 
     */
    public PersistentHashMap()
    {
        this(100);
    }
    

    // Start of Interfaces: org.odmg.DMap, java.util.Map...



    public void clear()
    {
        mHashMap.clear();
    }
    

    public boolean isEmpty() 
    {
        return mHashMap.isEmpty();
    }
    

    public V remove(Object anObject) 
    {
        return mHashMap.remove(anObject);
    }
    

    public int size() 
    {
        return mHashMap.size();
    }
    

    public boolean containsKey(Object aKey)
    {
        return mHashMap.containsKey(aKey);
    }
    

    public boolean containsValue(Object aValue)
    {
        return mHashMap.containsValue(aValue);
    }
    

    public Set<Entry<K,V>> entrySet()
    {
        return mHashMap.entrySet();
    }
    

    public V get(Object aKey)
    {
        return mHashMap.get(aKey);
    }
    

    public Set<K> keySet()
    {
        return mHashMap.keySet();
    }
    

    public V put(K aKey, V aValue)
    {
        return mHashMap.put(aKey, aValue);
    }
    

    public void putAll(Map<? extends K,? extends V> aMap)
    {
        mHashMap.putAll(aMap);
    }
    

    public Collection<V> values() 
    {
        return mHashMap.values();
    }
    

    public java.util.Iterator<V> select(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public boolean existsElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public org.odmg.DCollection<V> query(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    public V selectElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    

    /**
     * {@inheritDoc}
     */
    public int hashCode() 
    {
        return mHashMap.hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mHashMap.equals(anObject);
    }


    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mHashMap.toString();
    }


    /**
     * {@inheritDoc}
     */
    public PersistentHashMap<K,V> clone() throws CloneNotSupportedException
    {
        PersistentHashMap<K,V> clone = (PersistentHashMap<K,V>)super.clone();
        clone.mHashMap = (HashMap<K,V>)mHashMap.clone();
        return clone;
    }
    

    // ...End of Interfaces: org.odmg.DMap, java.util.Map.

}
