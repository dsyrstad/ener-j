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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/RegularDMap.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DMap which supports persistable dynamic maps
 * as first-class objects (FCOs). This type of map is useful when the map itself can
 * fit entirely in memory at one time.  If you have an map that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use  TODO  VeryLargeDMap.
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
 * @version $Id: RegularDMap.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DMap
 * @see VeryLargeDMap
 */
@Persist
public class RegularDMap implements org.odmg.DMap, Cloneable
{
    /** The delegate map. This is treated as an SCO when this FCO is persisted. */
    private HashMap mHashMap;
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new RegularDMap with the specified initial capacity. 
     * 
     * @param anInitialCapacity the initially allocated capacity of the map.
     *  This does not affect the size of the map.
     */
    public RegularDMap(int anInitialCapacity)
    {
        mHashMap = new HashMap(anInitialCapacity);
    }
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new RegularDMap with an initial capacity of 100. 
     */
    public RegularDMap()
    {
        this(100);
    }
    
    //----------------------------------------------------------------------
    // Start of Interfaces: org.odmg.DMap, java.util.Map...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public void clear()
    {
        mHashMap.clear();
    }
    
    //----------------------------------------------------------------------
    public boolean isEmpty() 
    {
        return mHashMap.isEmpty();
    }
    
    //----------------------------------------------------------------------
    public Object remove(Object anObject) 
    {
        return mHashMap.remove(anObject);
    }
    
    //----------------------------------------------------------------------
    public int size() 
    {
        return mHashMap.size();
    }
    
    //----------------------------------------------------------------------
    public boolean containsKey(Object aKey)
    {
        return mHashMap.containsKey(aKey);
    }
    
    //----------------------------------------------------------------------
    public boolean containsValue(Object aValue)
    {
        return mHashMap.containsValue(aValue);
    }
    
    //----------------------------------------------------------------------
    public Set entrySet()
    {
        return mHashMap.entrySet();
    }
    
    //----------------------------------------------------------------------
    public Object get(Object aKey)
    {
        return mHashMap.get(aKey);
    }
    
    //----------------------------------------------------------------------
    public Set keySet()
    {
        return mHashMap.keySet();
    }
    
    //----------------------------------------------------------------------
    public Object put(Object aKey, Object aValue)
    {
        return mHashMap.put(aKey, aValue);
    }
    
    //----------------------------------------------------------------------
    public void putAll(Map aMap)
    {
        mHashMap.putAll(aMap);
    }
    
    //----------------------------------------------------------------------
    public Collection values() 
    {
        return mHashMap.values();
    }
    
    //----------------------------------------------------------------------
    public java.util.Iterator select(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    public boolean existsElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    public org.odmg.DCollection query(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    public Object selectElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public int hashCode() 
    {
        return mHashMap.hashCode();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mHashMap.equals(anObject);
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mHashMap.toString();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException
    {
        RegularDMap clone = (RegularDMap)super.clone();
        clone.mHashMap = (HashMap)mHashMap.clone();
        return clone;
    }
    
    //----------------------------------------------------------------------
    // ...End of Interfaces: org.odmg.DMap, java.util.Map.
    //----------------------------------------------------------------------
}
