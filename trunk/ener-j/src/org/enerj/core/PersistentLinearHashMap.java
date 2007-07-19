/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.enerj.annotations.Persist;
import org.enerj.annotations.PersistenceAware;
import org.odmg.QueryInvalidException;

/**
 * Ener-J implementation of org.odmg.DMap which supports persistable linear hash tables (the linear hash
 * table algorithm as described by W. Litwin in 1980) as first-class objects (FCOs). 
 * This type of map is useful when the map itself cannot
 * fit entirely in memory at one time. If you have an map that can fit
 * reasonably in memory or you want to conserve disk storage space, consider {@link PersistentHashMap}.
 * <p>
 * The algorithm for deletion is somewhat different from Litwin's in that blocks are never removed from the
 * chain or combined. Instead, the key is simply removed from the block's array of Key/OID pairs. If the block
 * is reduced to zero keys, it is left in the chain for later reuse. A rebuild of the index is required to
 * remove this extra space if it is never reused.  
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
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DMap
 * @see PersistentHashMap
 */
@Persist
public class PersistentLinearHashMap<K, V> extends AbstractMap<K, V> 
    implements org.odmg.DMap<K, V>, Cloneable, DuplicateKeyMap<K, V>
{
    /** With this size, if every bucket has one block, 1,073,741,824,000 (1 trillion) entries are supported.
     * However, the table is not limited to this size because blocks can be chained.
     */
    public static final int DEFAULT_NUM_KEYS_PER_BLOCK = 500;
    /** Maximum number of bits that we'll use from the hash code. We always clear the sign bit, so we
     * use the least significant 31 bits. 
     */
    public static final int MAX_BITS = 31;
    
    private boolean allowDuplicateKeys;
    private int elementCount = 0;
    /* Number of key/oid pairs in a block. */
    private int blockSize;
    /** Number of bits currently being used from the hash code. buckets.length is always 2^numBits. 
     * We start out with 8 bits which allows for about 70,000 entries before growing the table.
     */ 
    private int numBits = 8;
    /* The Bucket table. The size of this table is always a power of 2 and always
     * 2^numBits. Each bucket points to the first block in the chain, or null. */
    private Block[] buckets = new Block[256];

    transient int modCount = 0;

    /**
     * Constructs a new empty instance of PersistentLinearHashMap that does not support duplicate keys.
     * The block size is DEFAULT_NUM_KEYS_PER_BLOCK.
     */
    public PersistentLinearHashMap()
    {
        this(DEFAULT_NUM_KEYS_PER_BLOCK, false);
    }

    /**
     * Constructs a new instance of PersistentLinearHashMap with the specified block size. The map does not support duplicate keys.  
     * 
     * @param blockSize the maximum number of keys per block. There may be multiple blocks chained to a bucket,
     *  but typically there is only one.
     * 
     * @exception IllegalArgumentException if blockSize is less than zero.
     */
    public PersistentLinearHashMap(int blockSize)
    {
        this(blockSize, false);
    }

    /**
     * Constructs a new instance of PersistentLinearHashMap with the specified block size. 
     * The map optionally supports duplicate keys.  
     * 
     * @param blockSize the maximum number of keys per block. There may be multiple blocks chained to a bucket,
     *  but typically there is only one.
     * 
     * @exception IllegalArgumentException if blockSize is less than zero.
     */
    public PersistentLinearHashMap(int blockSize, boolean allowDuplicateKeys)
    {
        if (blockSize < 0) {
            throw new IllegalArgumentException();
        }

        this.allowDuplicateKeys = allowDuplicateKeys;
        this.blockSize = blockSize;
    }

    /**
     * Removes all mappings from this PersistentLinearHashMap, leaving it empty.
     * 
     * @see #isEmpty
     * @see #size
     */
    @Override
    public void clear()
    {
        if (elementCount > 0) {
            elementCount = 0;
            Arrays.fill(buckets, null);
            modCount++;
        }
    }

    /**
     * Answers a new PersistentLinearHashMap with the same mappings and size as this PersistentLinearHashMap.
     * 
     * @return a shallow copy of this PersistentLinearHashMap
     * 
     * @see java.lang.Cloneable
     */
    @Override
    @SuppressWarnings("unchecked")
    public PersistentLinearHashMap<K, V> clone()
    {
        try {
            PersistentLinearHashMap<K, V> map = (PersistentLinearHashMap<K, V>)super.clone();
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
     * Searches this PersistentLinearHashMap for the specified key.
     * 
     * @param key
     *            the object to search for
     * @return true if <code>key</code> is a key of this PersistentLinearHashMap, false
     *         otherwise
     */
    @Override
    public boolean containsKey(Object key)
    {
        return getEntry(key) != null;
    }

    /**
     * Tests two keys for equality. This method calls key.equals() but also handles nulls.
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
     * Answers a Set of the mappings contained in this PersistentLinearHashMap. Each element in
     * the set is a Map.Entry. The set is backed by this PersistentLinearHashMap so changes to
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
     * one of the corresponding is returned. If you want all duplicate key values, use getValues().
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
        return new PersistentLinearHashMap.ValuesForKeyCollection<V>(getEntry(key), this);
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
     * Answers if this PersistentLinearHashMap has no elements, a size of zero.
     * 
     * @return true if this PersistentLinearHashMap has no elements, false otherwise
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
     * Removes a mapping with the specified key from this PersistentLinearHashMap.
     * 
     * @param key
     *            the key of the mapping to remove
     * @return the value of the removed mapping or null if key is not a key in
     *         this PersistentLinearHashMap
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
     * Answers the number of mappings in this PersistentLinearHashMap.
     * 
     * @return the number of mappings in this PersistentLinearHashMap
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
    private static class Entry<K, V> implements Map.Entry<K, V>
    {
        K key;
        V value;

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

        final PersistentLinearHashMap<KT, VT> associatedMap;

        HashMapIterator(Entry.Type<E, KT, VT> value, PersistentLinearHashMap<KT, VT> hm)
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
        private final PersistentLinearHashMap<KT, VT> associatedMap;

        HashMapEntrySet(PersistentLinearHashMap<KT, VT> hm)
        {
            associatedMap = hm;
        }

        PersistentLinearHashMap<KT, VT> hashMap()
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
        private PersistentLinearHashMap<?, V> mMap;
        private int mSize = -1;

        ValuesForKeyCollection(Entry<?,V> aStartingEntry, PersistentLinearHashMap<?, V> aMap)
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
        PersistentLinearHashMap<?, V> mMap;
        int expectedModCount;

        Entry<?, V> entry;
        Object mKey;

        ValuesForKeyIterator(Entry<?, V> aStartingEntry, PersistentLinearHashMap<?, V> aMap)
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

    
    /**
     * Describes a block of keys and their OIDs. A block may also chain to another block.
     */
    @Persist
    private static final class Block
    {
        /** For keys contained in this block, the number of bits of the hash code used. */ 
        private byte bitsUsed;
        /** Max number entries in this block. */
        private short blockSize;
        /** Number of entries consumed in keys and oids. */
        private int numEntriesUsed = 0;
        /** Typically these are SCOs (such as GenericKey) and are stored directly in the block. */
        private Object[] keys; 
        /** One-to-one correspondence with keys. The OID points to the object represented by the key. */
        private long[] oids;
        /** Next block in the chain. */
        private Block nextBlock = null;
        
        Block(int blockSize, byte bitsUsed)
        {
            this.bitsUsed = bitsUsed;
            this.blockSize = (short)blockSize;
            this.keys = new Object[blockSize];
            this.oids = new long[blockSize];
        }

        int getBitsUsed()
        {
            return bitsUsed;
        }

        boolean isFull()
        {
            return numEntriesUsed >= keys.length;
        }

        Block getNextBlock()
        {
            return nextBlock;
        }
        
        Object getKeyAt(BlockPos pos)
        {
            return pos.block.keys[ pos.idx ];
        }
        
        long getOidAt(BlockPos pos)
        {
            return pos.block.oids[ pos.idx ];
        }
        
        /**
         * Adds a key and an OID to the block. If this block is full, the chain is searched for available space.
         * If no space is available, a new block is added to the chain.
         */
        void add(Object key, long oid)
        {
            Block block = this;
            Block prevBlock = block;
            for (; block != null; prevBlock = block, block = block.nextBlock) {
                if (!block.isFull()) {
                    break;
                }
            }
            
            if (block == null) {
                block = new Block(blockSize, bitsUsed);
                prevBlock.nextBlock = block;
            }
            
            block.keys[numEntriesUsed] = key;
            block.oids[numEntriesUsed] = oid;
            ++block.numEntriesUsed;
            EnerJImplementation.setModified(block);
        }
        
        /**
         * Finds the specified key. If duplicate keys exist, the first one found is returned. 
         * If the key is not found in this block, subsequent blocks in the chain
         * are searched.
         * 
         * @param key the key to search for.
         * 
         * @return the BlockPos, or null if the key could not be found. 
         */
        BlockPos find(Object key)
        {
            for (Block block = this; block != null; block = block.nextBlock) {
                for (int i = 0; i < block.numEntriesUsed; i++) {
                    if (block.keys[i].equals(key)) {
                        return new BlockPos(block, i);
                    }
                }
            }
            
            return null;
        }
        
        /**
         * Finds the specified oid. If duplicate OIDs exist, the first one found is returned. 
         * If the OID is not found in this block, subsequent blocks in the chain
         * are searched.
         * 
         * @param oid the oid to search for.
         * 
         * @return the BlockPos, or null if the oid could not be found. 
         */
        BlockPos find(long oid)
        {
            for (Block block = this; block != null; block = block.nextBlock) {
                for (int i = 0; i < block.numEntriesUsed; i++) {
                    if (block.oids[i] == oid) {
                        return new BlockPos(block, i);
                    }
                }
            }
            
            return null;
        }

        /**
         * Removes the specified key and its OID. If duplicate keys exist, the first one found is removed. 
         * If the key is not found in this block, subsequent blocks in the chain
         * are searched.
         * 
         * @param key the key to be removed.
         * 
         * @return true if the entry was removed, else false.
         */
        boolean remove(Object key)
        {
            BlockPos pos = find(key);
            if (pos == null) {
                return false;
            }
            
            pos.block.removeAtIdx(pos.idx);
            return true;
        }
        
        /**
         * Removes the specified oid and its key. If duplicate OIDs exist, the first one found is removed. 
         * If the OID is not found in this block, subsequent blocks in the chain
         * are searched.
         * 
         * @return true if the entry was removed, else false.
         */
        boolean remove(long oid)
        {
            BlockPos pos = find(oid);
            if (pos == null) {
                return false;
            }
            
            pos.block.removeAtIdx(pos.idx);
            return true;
        }
        
        /**
         * Removes the key/oid pair at the specified index.
         *  
         * @param idx the index.
         */
        void removeAtIdx(int idx)
        {
            int length = (numEntriesUsed - idx) - 1;
            if (length > 0) {
                System.arraycopy(keys, idx + 1, keys, idx, length);
                System.arraycopy(oids, idx + 1, oids, idx, length);
            }
            
            --numEntriesUsed;
            // Null-out the old last entry.
            keys[numEntriesUsed] = null;
            oids[numEntriesUsed] = 0;
            EnerJImplementation.setModified(this);
        }
    }
    
    /**
     * Represents a position in a block. Used as a return value.
     */
    private static final class BlockPos
    {
        Block block;
        int idx;

        BlockPos(Block block, int idx)
        {
            this.block = block;
            this.idx = idx;
        }
    }
}
