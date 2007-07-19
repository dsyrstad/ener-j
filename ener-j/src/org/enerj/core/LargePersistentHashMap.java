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

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.enerj.annotations.Persist;
import org.enerj.annotations.PersistenceAware;
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
 * Ener-J implementation of org.odmg.DMap which supports large persistable hash maps
 * as first-class objects (FCOs). This type of map is useful when the map itself cannot
 * fit entirely in memory at one time. The map never needs to be resized like a
 * HashMap sometimes does. If you have an map that can fit
 * reasonably in memory or you want to conserve disk storage space, consider {@link PersistentHashMap}.
 * <p>
 * 
 * If you reference this type of map in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the map to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.HashMap directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 * <p>
 * This map optionally supports duplicate keys, which may be retrieved using the DuplicateKeyMap interface.
 *
 * @version $Id: PersistentMap.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DMap
 * @see PersistentHashMap
 */
@Persist
public class LargePersistentHashMap<K, V> extends AbstractMap<K, V> 
    implements org.odmg.DMap<K, V>, Cloneable, DuplicateKeyMap<K, V>
{
    public static final int DEFAULT_NODE_SIZE = 1024;

    private int elementCount;
    private LargePersistentArrayList<Entry<K, V>> elementData;
    private boolean allowDuplicateKeys;

    transient int modCount = 0;

    /**
     * Constructs a new empty instance of LargePersistentHashMap with a node size of 1024.
     * This will comfortably support about 1 billion objects. The map does not support duplicate keys.
     */
    public LargePersistentHashMap()
    {
        this(DEFAULT_NODE_SIZE, false);
    }

    /**
     * Constructs a new instance of LargePersistentHashMap with the specified node size. The node size is the 
     * node size of the backing {@link LargePersistentArrayList}. See {@link LargePersistentArrayList} 
     * for an explanation of node sizes. The map does not support duplicate keys.  
     * 
     * @param nodeSize the 
     *            the initial capacity of this LargePersistentHashMap.
     * 
     * @throws IllegalArgumentException
     *                when the nodeSize is less than zero.
     */
    public LargePersistentHashMap(int nodeSize)
    {
        this(nodeSize, false);
    }

    /**
     * Constructs a new instance of LargePersistentHashMap with the specified node size. The node size is the 
     * node size of the backing {@link LargePersistentArrayList}. See {@link LargePersistentArrayList} 
     * for an explanation of node sizes. The map optionally supports duplicate keys.  
     * 
     * @param nodeSize the 
     *            the initial capacity of this LargePersistentHashMap.
     * 
     * @throws IllegalArgumentException
     *                when the nodeSize is less than zero.
     */
    public LargePersistentHashMap(int nodeSize, boolean allowDuplicateKeys)
    {
        if (nodeSize < 0) {
            throw new IllegalArgumentException();
        }

        elementCount = 0;
        elementData = new LargePersistentArrayList<Entry<K,V>>(nodeSize);
        // Max-out the table size.
        elementData.resize( elementData.getMaximumSize() );
        this.allowDuplicateKeys = allowDuplicateKeys;
    }

    /**
     * Constructs a new instance of LargePersistentHashMap containing the mappings from the
     * specified Map. The map does not support duplicate keys. This is equivalent to creating the
     * map and calling newMap.putAll(map).
     * 
     * @param map
     *            the mappings to add
     */
    public LargePersistentHashMap(Map<? extends K, ? extends V> map)
    {
        this();
        putAll(map);
    }

    /**
     * Removes all mappings from this LargePersistentHashMap, leaving it empty.
     * 
     * @see #isEmpty
     * @see #size
     */
    @Override
    public void clear()
    {
        if (elementCount > 0) {
            elementCount = 0;
            elementData.clear();
            // Max-out the table size.
            elementData.resize( elementData.getMaximumSize() );
            modCount++;
        }
    }

    /**
     * Answers a new LargePersistentHashMap with the same mappings and size as this LargePersistentHashMap.
     * 
     * @return a shallow copy of this LargePersistentHashMap
     * 
     * @see java.lang.Cloneable
     */
    @Override
    @SuppressWarnings("unchecked")
    public LargePersistentHashMap<K, V> clone()
    {
        try {
            LargePersistentHashMap<K, V> map = (LargePersistentHashMap<K, V>)super.clone();
            map.elementData = new LargePersistentArrayList<Entry<K,V>>( elementData.getNodeSize() );
            long size = elementData.sizeAsLong();
            for (long i = 0; i < size; i++) {
                Entry<K, V> entry = elementData.getAtIndex(i);
                if (entry  != null) {
                    map.elementData.setAtIndex(i, (Entry<K, V>)entry.clone());
                }
            }
            
            return map;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Searches this LargePersistentHashMap for the specified key.
     * 
     * @param key
     *            the object to search for
     * @return true if <code>key</code> is a key of this LargePersistentHashMap, false
     *         otherwise
     */
    @Override
    public boolean containsKey(Object key)
    {
        return getEntry(key) != null;
    }

    /**
     * Tests two keys for equality. This method just calls key.equals but can be
     * overridden.
     * 
     * @param k1
     *            first key to compare
     * @param k2
     *            second key to compare
     * @return true if the keys are considered equal
     */
    private static boolean keysEqual(Object k1, Entry<?, ?> entry)
    {
        if (k1 == null && entry.key == null) {
            return true;
        }
        
        if (k1 == null || entry.key == null) {
            return false;
        }

        return k1.equals(entry.key);
    }

    /**
     * Answers a Set of the mappings contained in this LargePersistentHashMap. Each element in
     * the set is a Map.Entry. The set is backed by this LargePersistentHashMap so changes to
     * one are reflected by the other. The set does not support adding.
     * 
     * @return a Set of the mappings
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return new HashMapEntrySet<K, V>(this);
    }

    /**
     * Answers the value of the mapping with the specified key. If the map supports duplicate keys,
     * one of the corresponding is returned. If you want all duplicate key values, use getValues(Object key).
     * 
     * @param key
     *            the key
     * @return the value of the mapping with the specified key
     */
    @Override
    public V get(Object key)
    {
        Entry<K, V> m = getEntry(key);
        if (m != null) {
            return m.value;
        }
        return null;
    }
    
    public Collection<V> getValues(Object key)
    {
        return new LargePersistentHashMap.ValuesForKeyCollection<V>(getEntry(key), this);
    }

    private Entry<K, V> getEntry(Object key)
    {
        long index = getModuloHash(key);
        return findEntry(key, index);
    }

    private long getModuloHash(Object key)
    {
        if (key == null) {
            return 0;
        }
        
        long positiveHash = (key.hashCode() & 0x7FFFFFFF);
        long tableSize = elementData.sizeAsLong();
        if (positiveHash < tableSize) {
            return positiveHash; // Most typical case. Save a modulo op.
        }
        
        return positiveHash % tableSize;  
    }

    private Entry<K, V> findEntry(Object key, long index)
    {
        Entry<K, V> entry = elementData.getAtIndex(index);
        if (key != null) {
            while (entry != null && !keysEqual(key, entry)) {
                entry = entry.next;
            }
        }
        else {
            while (entry != null && entry.key != null) {
                entry = entry.next;
            }
        }
        return entry;
    }

    /**
     * Answers if this LargePersistentHashMap has no elements, a size of zero.
     * 
     * @return true if this LargePersistentHashMap has no elements, false otherwise
     * 
     * @see #size
     */
    @Override
    public boolean isEmpty()
    {
        return elementCount == 0;
    }

    /**
     * Maps the specified key to the specified value. If duplicate keys are support and a duplicate key 
     * already exists, a new entry will be added and null will be returned. If duplicate keys are
     * not supported and the key already exists, the entries value is replaced.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the value of any previous mapping with the specified key or null
     *         if there was no mapping.
     */
    @Override
    public V put(K key, V value)
    {
        long index = getModuloHash(key);
        Entry<K, V> entry = null;
        
        if (!allowDuplicateKeys) {
            entry = findEntry(key, index);
        }

        if (entry == null) {
            entry = createEntry(key, index, value);
            ++elementCount;
            return null;
        }

        // Replace existing value
        V result = entry.value;
        entry.value = value;
        return result;
    }

    private Entry<K, V> createEntry(K key, long index, V value)
    {
        Entry<K, V> entry = new Entry<K, V>(key, value);
        entry.next = elementData.getAtIndex(index);
        elementData.setAtIndex(index, entry);
        ++modCount;
        return entry;
    }

    /**
     * Removes a mapping with the specified key from this LargePersistentHashMap.
     * 
     * @param key
     *            the key of the mapping to remove
     * @return the value of the removed mapping or null if key is not a key in
     *         this LargePersistentHashMap
     */
    @Override
    public V remove(Object key)
    {
        Entry<K, V> entry = removeEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }

    private Entry<K, V> removeEntry(Object key)
    {
        long index = getModuloHash(key);
        Entry<K, V> last = null;
        Entry<K, V> entry = elementData.getAtIndex(index);
        while (entry != null && !keysEqual(key, entry)) {
            last = entry;
            entry = entry.next;
        }

        if (entry == null) {
            return null;
        }

        if (last == null) {
            elementData.setAtIndex(index, entry.next);
        }
        else {
            last.next = entry.next;
        }
        
        modCount++;
        elementCount--;
        return entry;
    }

    /**
     * Answers the number of mappings in this LargePersistentHashMap.
     * 
     * @return the number of mappings in this LargePersistentHashMap
     */
    @Override
    public int size()
    {
        return elementCount;
    }
    public java.util.Iterator<V> select(String str) throws org.odmg.QueryInvalidException
    {
        // TODO  finish
        throw new QueryInvalidException("Not implemented yet");
    }

    public boolean existsElement(String str) throws org.odmg.QueryInvalidException
    {
        // TODO  finish
        throw new QueryInvalidException("Not implemented yet");
    }

    public org.odmg.DCollection<V> query(String str) throws org.odmg.QueryInvalidException
    {
        // TODO  finish
        throw new QueryInvalidException("Not implemented yet");
    }

    public V selectElement(String str) throws org.odmg.QueryInvalidException
    {
        // TODO  finish
        throw new QueryInvalidException("Not implemented yet");
    }


    // Private Map.Entry implementation
    @Persist
    private static class Entry<K, V> implements Map.Entry<K, V>, Cloneable
    {
        K key;
        V value;
        /** Next in hash entry chain. */
        Entry<K, V> next;

        interface Type<RT, KT, VT>
        {
            RT get(Entry<KT, VT> entry);
        }

        Entry(K theKey, V theValue)
        {
            key = theKey;
            value = theValue;
        }

        public K getKey()
        {
            return key;
        }

        public V getValue()
        {
            return value;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object) {
                return true;
            }
            if (object instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
                return (key == null ? entry.getKey() == null : key.equals(entry.getKey()))
                                && (value == null ? entry.getValue() == null : value.equals(entry.getValue()));
            }
            return false;
        }
        
        @Override
        public int hashCode()
        {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        public V setValue(V object)
        {
            V result = value;
            value = object;
            return result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object clone()
        {
            try {
                Entry<K, V> entry = (Entry<K, V>)super.clone();
                if (next != null) {
                    entry.next = (Entry<K, V>)next.clone();
                }
                return entry;
            }
            catch (CloneNotSupportedException e) {
                return null; // Shouldn't happen.
            }
        }

        @Override
        public String toString()
        {
            return key + "=" + value;
        }
    }

    @PersistenceAware
    private static class HashMapIterator<E, KT, VT> implements Iterator<E>
    {
        private long position = 0;

        int expectedModCount;

        final Entry.Type<E, KT, VT> type;

        boolean canRemove = false;

        Entry<KT, VT> entry;

        Entry<KT, VT> lastEntry;

        final LargePersistentHashMap<KT, VT> associatedMap;

        HashMapIterator(Entry.Type<E, KT, VT> value, LargePersistentHashMap<KT, VT> hm)
        {
            associatedMap = hm;
            type = value;
            expectedModCount = hm.modCount;
        }

        public boolean hasNext()
        {
            if (entry != null) {
                return true;
            }
            
            position = associatedMap.elementData.getNextNonNullIndex(position);
            return position >= 0;
        }

        void checkConcurrentMod() throws ConcurrentModificationException
        {
            if (expectedModCount != associatedMap.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        public E next()
        {
            checkConcurrentMod();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<KT, VT> result;
            if (entry == null) {
                position = associatedMap.elementData.getNextNonNullIndex(position);
                result = lastEntry = associatedMap.elementData.getAtIndex(position++);
                entry = lastEntry.next;
            }
            else {
                if (lastEntry.next != entry) {
                    lastEntry = lastEntry.next;
                }
                
                result = entry;
                entry = entry.next;
            }
            
            canRemove = true;
            return type.get(result);
        }

        public void remove()
        {
            checkConcurrentMod();
            if (!canRemove) {
                throw new IllegalStateException();
            }

            canRemove = false;
            associatedMap.modCount++;
            if (lastEntry.next == entry) {
                // Unlink from chain.
                // Find previous non-null.
                while (associatedMap.elementData.getAtIndex(--position) == null) {
                }
            
                associatedMap.elementData.setAtIndex(position, associatedMap.elementData.getAtIndex(position).next);
                entry = null;
            }
            else {
                lastEntry.next = entry;
            }

            associatedMap.elementCount--;
            expectedModCount++;
        }
    }


    @PersistenceAware
    static class HashMapEntrySet<KT, VT> extends AbstractSet<Map.Entry<KT, VT>>
    {
        private final LargePersistentHashMap<KT, VT> associatedMap;

        HashMapEntrySet(LargePersistentHashMap<KT, VT> hm)
        {
            associatedMap = hm;
        }

        LargePersistentHashMap<KT, VT> hashMap()
        {
            return associatedMap;
        }

        @Override
        public int size()
        {
            return associatedMap.elementCount;
        }

        @Override
        public void clear()
        {
            associatedMap.clear();
        }

        @Override
        public boolean remove(Object object)
        {
            if (contains(object)) {
                associatedMap.remove(((Map.Entry)object).getKey());
                return true;
            }
            return false;
        }

        @Override
        public boolean contains(Object object)
        {
            if (object instanceof Map.Entry) {
                Entry<KT, VT> entry = associatedMap.getEntry(((Map.Entry)object).getKey());
                return object.equals(entry);
            }
            return false;
        }

        @Override
        public Iterator<Map.Entry<KT, VT>> iterator()
        {
            return new HashMapIterator<Map.Entry<KT, VT>, KT, VT>(new Entry.Type<Map.Entry<KT, VT>, KT, VT>() {
                public Map.Entry<KT, VT> get(Entry<KT, VT> entry)
                {
                    return entry;
                }
            }, associatedMap);
        }
    }

    @PersistenceAware
    private static final class ValuesForKeyCollection<V> extends AbstractCollection<V>
    {
        private Entry<?,V> mStartingEntry;
        private LargePersistentHashMap<?, V> mMap;
        private int mSize = -1;

        ValuesForKeyCollection(Entry<?,V> aStartingEntry, LargePersistentHashMap<?, V> aMap)
        {
            mStartingEntry = aStartingEntry;
            mMap = aMap;
        }

        @Override
        public Iterator<V> iterator()
        {
            return new ValuesForKeyIterator<V>(mStartingEntry, mMap);
        }

        @Override
        public int size()
        {
            if (mSize == -1) {
                mSize = 0;
                for (V v : this) {
                    ++mSize;
                }
            }
            
            return mSize;
        }
        
        
    }

    @PersistenceAware
    private static final class ValuesForKeyIterator<V> implements Iterator<V>
    {
        LargePersistentHashMap<?, V> mMap;
        int expectedModCount;

        Entry<?, V> entry;
        Object mKey;

        ValuesForKeyIterator(Entry<?, V> aStartingEntry, LargePersistentHashMap<?, V> aMap)
        {
            entry = aStartingEntry;
            mKey = aStartingEntry.getKey();
            mMap = aMap;
            expectedModCount = mMap.modCount;
        }

        public boolean hasNext()
        {
            return entry != null;
        }

        void checkConcurrentMod() throws ConcurrentModificationException
        {
            if (expectedModCount != mMap.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        public V next()
        {
            checkConcurrentMod();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<?, V> result = entry;
            // Set entry to next matching key.
            for (entry = entry.next; entry != null && !keysEqual(mKey, entry); entry = entry.next) 
                    ;
            
            return result.getValue();
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

}
