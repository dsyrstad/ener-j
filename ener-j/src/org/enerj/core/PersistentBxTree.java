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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.enerj.annotations.Persist;
import org.odmg.DCollection;
import org.odmg.DMap;
import org.odmg.QueryInvalidException;

/*
 * Portions of this code originated from Apache Harmony's TreeMap.java:
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
 * A Persistent B+Tree Map.  <p>
 * 
 * This implementation allows leaf nodes to go empty during deletion so that less tree
 * reorganization occurs. <p>
 * 
 * By default, a put of a duplicate key simply replaces the existing key in this implementation. This
 * conforms to the Map contract. This can be overridden by specifying the duplicate key option
 * when constructing a tree. <p> 
 * 
 * TODO handle null keys
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class PersistentBxTree<K, V> extends AbstractMap<K, V> implements DMap<K, V>, SortedMap<K, V>
{
    /** This is roughly the right size to fill-out an 8K page when keys are SCOs and are 8 bytes in length. */
    public static final int DEFAULT_KEYS_PER_NODE = 450;
    
    private Comparator<K> mComparator = null;
    /** The root node of the tree. */
    private Node<K> mRootNode;
    /** Maxmimum number of keys in a node. */
    private int mNodeSize;
    /** The number of entries in the map. */
    private int mSize = 0;
    /** If true, duplicate keys are allowed in the map. */
    private boolean mAllowDuplicateKeys = false;
    /** If true, nodes are dynamically resized to match the number of keys. Otherwise they are
     * fully allocated to mNodeSize. */
    private boolean mDynamicallyResizeNode = false;
    
    transient private Set<Map.Entry<K, V>> mEntrySet;
    transient private int mModCount = 0;

    /**
     * Construct a PersistentBxTree using natural ordering of the keys and no duplicate keys. 
     *
     */
    public PersistentBxTree()
    {
        this(DEFAULT_KEYS_PER_NODE);
    }

    /**
     * Construct a PersistentBxTree using natural ordering of the keys and no duplicate keys. 
     *
     * @param aNumKeysPerNode the maximum number of keys per node. This must be greater than
     *  two, but is usually much larger. 
     */
    public PersistentBxTree(int aNumKeysPerNode)
    {
        this(aNumKeysPerNode, null, false, false);
    }

    /**
     * Construct a PersistentBxTree using the specified Comparator for ordering of the keys.
     * 
     * @param aNodeSize the maximum number of keys per node. This must be greater than
     *  two, but is usually much larger. 
     * 
     * @param aComparator the Comparator to use for comparing keys. May be null to use 
     *  natural ordering, in which case the key object must implement {@link Comparable}.
     * @param allowDuplicateKeys true if duplicate keys are allowed (this breaks the Map contract).
     * @param shouldDynamicallyResizeNodes true if the number of keys in tree nodes should be dynamically resized
     *  up to aNodeSize. Otherwise, aNodeSize keys are always allocated regardless of the number of keys in the node.
     */
    public PersistentBxTree(int aNodeSize, Comparator<K> aComparator, boolean allowDuplicateKeys, 
                    boolean shouldDynamicallyResizeNodes)
    {
        if (aNodeSize <= 2 || aNodeSize > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Node size must be > 2 and less than " + Short.MAX_VALUE);
        }
        
        mNodeSize = aNodeSize;
        mComparator = aComparator;
        // The root begins its life as a leaf. However, this is not common in real trees. Which came first: the leaf or the seed?
        mRootNode = new Node<K>(this, true);
        mAllowDuplicateKeys = allowDuplicateKeys;
        mDynamicallyResizeNode = shouldDynamicallyResizeNodes;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.SortedMap#comparator()
     */
    public Comparator<? super K> comparator()
    {
        return mComparator;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.SortedMap#firstKey()
     */
    public K firstKey()
    {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty");
        }
        
        Node<K> node = getLeftMostLeaf();
        return node.mKeys[0];
    }

    /** 
     * {@inheritDoc}
     * @see java.util.SortedMap#headMap(java.lang.Object)
     */
    public SortedMap<K, V> headMap(K toKey)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.SortedMap#lastKey()
     */
    public K lastKey()
    {
        if (isEmpty()) {
            throw new NoSuchElementException("Tree is empty");
        }
        
        Node<K> node = getRightMostLeaf();
        return node.mKeys[ node.mNumKeys - 1 ];
    }

    /** 
     * {@inheritDoc}
     * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
     */
    public SortedMap<K, V> subMap(K fromKey, K toKey)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.SortedMap#tailMap(java.lang.Object)
     */
    public SortedMap<K, V> tailMap(K fromKey)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        mRootNode = new Node<K>(this, true);
        mSize = 0;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key)
    {
        NodePos<K> pos = search((K)key);
        return pos.mIsMatch;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#entrySet()
     */
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        if (mEntrySet == null) {
            mEntrySet = new AbstractSet<Map.Entry<K, V>>() {
                 @Override
                public int size() {
                    return mSize;
                }

                @Override
                public void clear() {
                    PersistentBxTree.this.clear();
                }

                @SuppressWarnings("unchecked")
                @Override
                public boolean contains(Object object) {
                    if (object instanceof Map.Entry) {
                        Map.Entry<K, V> entry = (Map.Entry<K, V>) object;
                        Object v1 = get(entry.getKey()), v2 = entry.getValue();
                        return v1 == null ? v2 == null : v1.equals(v2);
                    }
                    return false;
                }

                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return new EntryIterator<K, V>(PersistentBxTree.this);
                }
            };
        }

        return mEntrySet;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object key)
    {
        Persister persister = PersistableHelper.getPersister(this);
        NodePos<K> nodePos = search((K)key);
        if (nodePos.mIsMatch) {
            long oid = nodePos.mNode.mOIDRefs[nodePos.mKeyIdx];
            return (V)(Object)persister.getObjectForOID(oid);
        }
        
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value)
    {
        V prevValue = get(key);
        insert(key, value);
        return prevValue;
    }
    
    /**
     * Like put(), but doesn't return the current value.
     *
     * @param aKey
     * @param aValue
     */
    public void insert(K aKey, V aValue)
    {
        Persister persister = PersistableHelper.getPersister(this);
        insert(aKey, persister.getOID(aValue));
        ++mSize;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#size()
     */
    public int size()
    {
        return mSize;
    }

    /** 
     * {@inheritDoc}
     * @see org.odmg.QueryableCollection#existsElement(java.lang.String)
     */
    public boolean existsElement(String predicate) throws QueryInvalidException
    {
        // TODO Auto-generated method stub
        return false;
    }

    /** 
     * {@inheritDoc}
     * @see org.odmg.QueryableCollection#query(java.lang.String)
     */
    public DCollection<V> query(String predicate) throws QueryInvalidException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** 
     * {@inheritDoc}
     * @see org.odmg.QueryableCollection#select(java.lang.String)
     */
    public Iterator<V> select(String predicate) throws QueryInvalidException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.QueryableCollection#selectElement(java.lang.String)
     */
    public V selectElement(String predicate) throws QueryInvalidException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Traverse the tree to find the given key. The returned position
     * will reflect the proper insertion point if the key is not found.
     *
     * @param aKey the target key to be found.
     * 
     * @return the NodePos of the key.
     */
    private NodePos<K> search(K aKey)
    {
        Node<K> node = mRootNode;
        NodePos<K> nodePos = node.findKey(this, aKey);
        while (!node.mIsLeaf) {
            node = node.getChildNodeAt(nodePos.mKeyIdx);
            nodePos = node.findKey(this, aKey);
        }
        
        return nodePos;
    }

    /**
     * Finds the left-most non-empty leaf.
     *
     * @return the left-most leaf.
     */
    private Node<K> getLeftMostLeaf()
    {
        Node<K> node = mRootNode;
        while (!node.mIsLeaf) {
            node = node.getChildNodeAt(0);
        }
        
        // Scan forward thru leaves while empty.
        while (node.mNumKeys <= 0) {
            node = node.getRightNode();
        }
        
        return node;
    }

    /**
     * Finds the left-most key's NodePos.
     *
     * @return the left-most key's NodePos.
     */
    private NodePos<K> getLeftMostNodePos()
    {
        Node<K> node = getLeftMostLeaf();
        return new NodePos<K>(node, 0, true);
    }

    /**
     * Finds the right-most leaf.
     *
     * @return the right-most leaf.
     */
    private Node<K> getRightMostLeaf()
    {
        Node<K> node = mRootNode;
        while (!node.mIsLeaf) {
            node = node.getChildNodeAt(node.mNumKeys);
        }
        
        // TODO fix to handle deleted right leaves.
        
        return node;
    }

    /**
     * Inserts a key into and it's corresponding value OID into the tree.
     *
     * @param aKey the key.
     * @param aValueOID the OID refering to the value for the key. 
     */
    private void insert(K aKey, long aValueOID)
    {
        PushUpInfo<K> pushUp = mRootNode.pushDown(this, aKey, aValueOID);
        if (pushUp != null) {
            // A key was pushed up to the root. Create a new root.
            Persister persister = PersistableHelper.getPersister(this);  
            Node<K> newRoot = new Node<K>(this, false);
            newRoot.ensureLength(this, 1);
            newRoot.mKeys[0] = pushUp.mPushUpKey;
            newRoot.mNumKeys = 1;
            newRoot.mOIDRefs[0] = persister.getOID(mRootNode);
            newRoot.mOIDRefs[1] = persister.getOID(pushUp.mRightNodeOfKey);
            mRootNode = newRoot;
        }
    }

    public void dumpTree()
    {
        List<Node<K>> nodes = Collections.singletonList(mRootNode);
        while (!nodes.isEmpty()) {
            nodes = dumpNodes(nodes);
        }
    }

    private List<Node<K>> dumpNodes(List<Node<K>> someNodes)
    {
        List<Node<K>> childNodes = new ArrayList<Node<K>>();
        for (Node<K> node : someNodes) {
            node.dumpNode();
            System.out.print(" | ");
            if (!node.mIsLeaf) {
                for (int i = 0; i < (node.mNumKeys + 1); i++) {
                    childNodes.add( node.getChildNodeAt(i) );
                }
            }
            
        }

        System.out.println();
        return childNodes;
    }

    /**
     * Info returned by Node.pushDown() during insert.
     */
    private static final class PushUpInfo<K>
    {
        K mPushUpKey; 
        Node<K> mRightNodeOfKey;
        
        PushUpInfo(K aPushUpKey, Node<K> aRightNodeOfKey)
        {
            mPushUpKey = aPushUpKey;
            mRightNodeOfKey = aRightNodeOfKey;
        }
    }
    
    /** Tracks a node and the position of within it.
     */
    private static final class NodePos<K>
    {
        Node<K> mNode;
        int mKeyIdx;
        boolean mIsMatch;  
        
        NodePos(Node<K> node, int keyIdx, boolean isMatch)
        {
            mNode = node;
            mKeyIdx = keyIdx;
            mIsMatch = isMatch;
        }
    }
    
    /** A Root, interior, or leaf node in the B+tree. 
     */
    @Persist
    private static final class Node<K>
    {
        /** True if this is a leaf node, otherwise it's an interior node. */
        private boolean mIsLeaf;
        
        /** Keys. Usually these are SCOs, but they can be FCOs if a Comparator is specified or the
         * key object itself is a comparable. */
        private K[] mKeys;
        private short mNumKeys;
        
        /** 
         * OIDs pointing to the child nodes or values. Note that OIDs are used rather than references to the actual
         * objects so that we don't load up a bunch of hollow objects when the node is referenced. This always
         * contains one more element than mKeys.
         * 
         * If this a leaf node, the OIDs reference the values. <p><pre>
         * mOIDRefs[0..n] references value for mKeys[0..n].
         * The last OID ref (mOIDRefs[n+1]) references the next leaf node to the right. 
         * This will be NULL_OID if there's not another leaf node. 
         * </pre><p>
         * 
         * If this is an interior node, the OIDs reference child nodes.<p><pre> 
         * mOIDRefs[m] (the left child) contains keys < mKeys[m].
         * mOIDRefs[m+1] (the right child) contains keys >= mKeys[m] and < mKeys[m+1].
         * </pre><p>
         */
        private long[] mOIDRefs;
        
        
        Node(PersistentBxTree<K, ?> aTree, boolean isLeaf)
        {
            mIsLeaf = isLeaf;
            int allocLen = 0;
            if (!aTree.mDynamicallyResizeNode) {
                allocLen = aTree.mNodeSize;
            }
            
            mKeys = (K[])new Object[allocLen];
            mNumKeys = 0;
            mOIDRefs = new long[mKeys.length + 1];
        }
        
        /**
         * Construct a Node that is a copy of the entries in aNode starting at aStartIdx to the end of the node.
         * The last right child OID is also copied from aNode. 
         *
         * @param aNode the node to copy from.
         * @param aStartIdx the starting index.
         */
        Node(PersistentBxTree<K, ?> aTree, Node<K> aNode, int aStartIdx) 
        {
            mIsLeaf = aNode.mIsLeaf;
            int length = aNode.mNumKeys - aStartIdx;
            int allocLen = length;
            if (!aTree.mDynamicallyResizeNode) {
                allocLen = aTree.mNodeSize;
            }
            mKeys = (K[])new Object[allocLen];
            mNumKeys = (short)length;
            mOIDRefs = new long[ mKeys.length + 1];
            System.arraycopy(aNode.mKeys, aStartIdx, mKeys, 0, length);
            System.arraycopy(aNode.mOIDRefs, aStartIdx, mOIDRefs, 0, length + 1);
        }
        
        /**
         * Ensures that at least aLength keys have been allocated.
         */
        void ensureLength(PersistentBxTree<K, ?> aTree, int aLength)
        {
            if (aTree.mDynamicallyResizeNode && aLength != mKeys.length) {
                K[] newKeys = (K[])new Object[aLength];
                long[] newOIDRefs = new long[ newKeys.length + 1];
                int copyLength = (aLength > mKeys.length ? mKeys.length : aLength);
                System.arraycopy(mKeys, 0, newKeys, 0, copyLength);
                System.arraycopy(mOIDRefs, 0, newOIDRefs, 0, copyLength + 1);
                mKeys = newKeys;
                mOIDRefs = newOIDRefs;
            }
        }
        
        /**
         * Truncates this node aNumKeys.
         */
        void truncate(PersistentBxTree<K, ?> aTree, int aNumKeys)
        {
            if (aTree.mDynamicallyResizeNode) {
                ensureLength(aTree, aNumKeys);
            }
            else {
                // Make sure key references are cleared so that they can be collected and won't be loaded again.
                // Note that we don't need to clear the OIDs.
                Arrays.fill(mKeys, aNumKeys, mNumKeys, null);
            }
            
            mNumKeys = (short)aNumKeys;
        }
        
        /**
         * Finds the given key within the node.
         *
         * @param aKey the target key to be found.
         * 
         * @return a node position. If an exact match is not found, the
         * index at which the target key would be inserted is returned in the node position and mIsMatch
         * is set to false.   
         */
        NodePos<K> findKey(PersistentBxTree<K, ?> aTree, K aKey)
        {
            Comparator<K> comparator = aTree.mComparator;
            if (comparator == null) {
                return findKey(aKey);
            }

            int low = 0;
            int mid = 0;
            int high = mNumKeys - 1;
            boolean found = false;
            while (low <= high) {
                mid = (low + high) >> 1;
                int result = comparator.compare(mKeys[mid], aKey);
                if (result > 0) {
                    low = mid + 1;
                }
                else if (result < 0) {
                    high = mid - 1;
                }
                else {
                    found = true;
                    break;
                }
            }

            return new NodePos<K>(this, low, found);
        }

        /**
         * Finds the given key within the node.
         *
         * @param aKey the target key to be found.
         * 
         * @return a node position. If an exact match is not found, the
         * index at which the target key would be inserted is returned in the node position and mIsMatch
         * is set to false.   
         */
        NodePos<K> findKey(K aKey)
        {
            Comparable<K> key = (Comparable<K>)aKey;
            int low = 0;
            int mid = 0;
            int high = mNumKeys - 1;
            boolean found = false;
            while (low <= high) {
                mid = (low + high) >> 1;
                int result = key.compareTo(mKeys[mid]);
                if (result > 0) {
                    low = mid + 1;
                }
                else if (result < 0) {
                    high = mid - 1;
                }
                else {
                    found = true;
                    break;
                }
            }

            return new NodePos<K>(this, low, found);
        }
        
        /**
         * Recursively pushes down the tree starting at this node to eventually inserts aKey with
         * aValueOID at a leaf.
         * 
         * @param aKey the key to be inserted.
         * @param aValueOID the OID of the value.
         * 
         * @return a PushUpInfo if a key is to be propagated to a higher level node. Otherwise null is returned.
         */
        PushUpInfo<K> pushDown(PersistentBxTree<K, ?> aTree, K aKey, long aValueOID)
        {
            if (mIsLeaf) {
                return insertInLeaf(aTree, aKey, aValueOID);
            }
            
            // Else this is an interior node. Drill down proper branch.
            NodePos<K> nodePos = findKey(aTree, aKey);
            Node<K> branch = getChildNodeAt(nodePos.mKeyIdx);
            PushUpInfo<K> pushUp = branch.pushDown(aTree, aKey, aValueOID);
            if (pushUp == null) {
                return null; // Nothing to do at this node.
            }
            
            int keyIdx = nodePos.mKeyIdx;
            // Insert key that was pushed up into this interior node.
            if (mNumKeys < aTree.mNodeSize) {
                // Room to insert. Just put it in place.
                pushInInterior(aTree, pushUp, keyIdx);
                return null;
            }

            // Split interior node.
            int medianIdx = mNumKeys >> 1; // Divide by 2
            boolean insertOnRight = (keyIdx > medianIdx); 
            if (insertOnRight) {
                // Key goes on right half. Increment median by one.
                ++medianIdx;
            }
            
            // Everything from medianIdx + 1 to the right goes into the new right node.
            Node<K> newRightNode = new Node<K>(aTree, this, medianIdx + 1);
            // Key at medianIdx gets pushed up and is no longer included in this interior node.
            PushUpInfo<K> pushUpInfo = new PushUpInfo<K>(mKeys[medianIdx], newRightNode);
            
            // This node (the left node) gets truncated to a length of medianIdx (elements 0..medianIdx - 1).
            truncate(aTree, medianIdx);
            
            if (insertOnRight) {
                // Key gets inserted on right node. Adjust the key index.
                newRightNode.pushInInterior(aTree, pushUp, keyIdx - medianIdx);
            }
            else {
                pushInInterior(aTree, pushUp, keyIdx);
            }

            return pushUpInfo;
        }
        
        /**
         * Push a key and right node into this interior node at keyIdx. Assumes that the node is not full.
         */
        void pushInInterior(PersistentBxTree<K, ?> aTree, PushUpInfo<K> aPushUp, int aKeyIdx)
        {
            ensureLength(aTree, mNumKeys + 1);
            int length = mNumKeys - aKeyIdx;
            if (length > 0) {
                System.arraycopy(mKeys, aKeyIdx, mKeys, aKeyIdx + 1, length);
                System.arraycopy(mOIDRefs, aKeyIdx + 1, mOIDRefs, aKeyIdx + 2, length);
            }
            
            mKeys[aKeyIdx] = aPushUp.mPushUpKey;
            mOIDRefs[aKeyIdx + 1] = PersistableHelper.getPersister(this).getOID(aPushUp.mRightNodeOfKey);
            ++mNumKeys;
        }
        
        /**
         * Inserts aKey with aValueOID this leaf node.
         * 
         * @param aKey the key to be inserted.
         * @param aValueOID the OID of the value.
         * @param aComparator if not null, used to compare keys. Otherwise aKey is assumed to be a
         *  {@link Comparable}.
         * 
         * @return a PushUpInfo if a key is to be propagated to a higher level node. Otherwise null is returned.
         */
        PushUpInfo<K> insertInLeaf(PersistentBxTree<K, ?> aTree, K aKey, long aValueOID)
        {
            NodePos<K> nodePos = findKey(aTree, aKey);
            if (nodePos.mIsMatch && !aTree.mAllowDuplicateKeys) {
                throw new IllegalStateException("Attempted to insert duplicate key " + aKey);
            }
            
            int keyIdx = nodePos.mKeyIdx;
            if (mNumKeys < aTree.mNodeSize) {
                // Simply insert into leaf. We're done.
                pushInLeaf(aTree, aKey, aValueOID, keyIdx);
                return null;
            }

            // Split leaf. Note that medain key stays in the leaf, unlike interior nodes.
            int medianIdx;
            // If inserting on right-most leaf, split leaving 2 keys (optimized sorted insert).
            // The right leaf pointer on the right most leaf is always null.
            if (keyIdx == mNumKeys && mOIDRefs[mNumKeys] == ObjectSerializer.NULL_OID) {
                medianIdx = mNumKeys - 2;
            }
            else {
                medianIdx = mNumKeys >> 1; // Divide by 2
            }
            
            boolean insertOnRight = (keyIdx > medianIdx); 
            if (insertOnRight) {
                // Key goes on right half. Increment median by one.
                ++medianIdx;
            }
            
            // Everything from medianIdx to the right goes into the new right node.
            Node<K> newRightNode = new Node<K>(aTree, this, medianIdx);
            // Key at medianIdx gets pushed up and is no longer included in this interior node.
            PushUpInfo<K> pushUpInfo = new PushUpInfo<K>(mKeys[medianIdx], newRightNode);
            
            // This node (the left node) gets truncated to a length of medianIdx (elements 0..medianIdx - 1).
            truncate(aTree, medianIdx);
            
            // Make left node point to the new right node.
            mOIDRefs[mNumKeys] = PersistableHelper.getPersister(this).getOID(newRightNode);
            
            if (insertOnRight) {
                // Key gets inserted on right node. Adjust the key index.
                newRightNode.pushInLeaf(aTree, aKey, aValueOID, keyIdx - medianIdx);
            }
            else {
                pushInLeaf(aTree, aKey, aValueOID, keyIdx);
            }

            return pushUpInfo;
        }

        /**
         * Push a key and value OID into this leaf node at keyIdx. Assumes that the node is not full.
         */
        void pushInLeaf(PersistentBxTree<K, ?> aTree, K aKey, long aValueOID, int aKeyIdx)
        {
            ensureLength(aTree, mNumKeys + 1);
            int length = mNumKeys - aKeyIdx;
            if (length > 0) {
                System.arraycopy(mKeys, aKeyIdx, mKeys, aKeyIdx + 1, length);
            }

            if (length >= 0) {
                System.arraycopy(mOIDRefs, aKeyIdx, mOIDRefs, aKeyIdx + 1, length + 1);
            }

            mKeys[aKeyIdx] = aKey;
            mOIDRefs[aKeyIdx] = aValueOID;
            ++mNumKeys;
        }
        
        /**
         * Deletes the key at the given key index. This must be a leaf node.
         * Note that we simply allow leaf nodes to go empty. We never reorganize up the tree.  
         */
        void delete(PersistentBxTree<K, ?> aTree, int aKeyIdx)
        {
            assert mIsLeaf;

            int length = mNumKeys - (aKeyIdx + 1);
            if (length > 0) {
                System.arraycopy(mKeys, aKeyIdx + 1, mKeys, aKeyIdx, length);
            }
            
            if (length >= 0) {
                System.arraycopy(mOIDRefs, aKeyIdx + 1, mOIDRefs, aKeyIdx, length + 1);
            }
            
            truncate(aTree, mNumKeys - 1);
        }
        
        /**
         * @return aNodePos which is updated to the next leaf node position that follows the given position 
         * in this node. Returns null if there are no more keys following this one.
         * 
         */
        NodePos<K> successor(NodePos<K> aNodePos)
        {
            assert aNodePos.mNode == this;
            
            ++aNodePos.mKeyIdx;
            if (aNodePos.mKeyIdx < mNumKeys) {
                return aNodePos;
            }
            
            do {
                aNodePos.mNode = getRightNode();
                if (aNodePos.mNode == null) {
                    return null;
                }
                
                aNodePos.mKeyIdx = 0;
            }
            while (aNodePos.mNode.mNumKeys <= 0);
            
            return aNodePos;
        }
        
        /**
         * Gets the child node at the given index. Assumes that this node is an interior node.
         *
         * @param anIndex the index of the child.
         * 
         * @return the child node.
         */
        Node<K> getChildNodeAt(int anIndex)
        {
            assert !mIsLeaf;
            
            long oid = mOIDRefs[anIndex];
            return (Node<K>)(Object)PersistableHelper.getPersister(this).getObjectForOID(oid);
        }
        
        /**
         * @return the leaf node to the right of this one, or null if there is no right node.
         */
        Node<K> getRightNode()
        {
            assert mIsLeaf;

            long oid = mOIDRefs[mNumKeys];
            return (Node<K>)(Object)PersistableHelper.getPersister(this).getObjectForOID(oid);
        }
        
        /**
         * @return the value at the given key index of the leaf.
         */
        Object getValueAt(int aKeyIdx)
        {
            assert mIsLeaf;

            long oid = mOIDRefs[aKeyIdx];
            return (Node<K>)(Object)PersistableHelper.getPersister(this).getObjectForOID(oid);
        }
        
        /**
         * Sets the value at the given key index of the leaf.
         */
        void setValueAt(int aKeyIdx, Object aValue)
        {
            assert mIsLeaf;

            mOIDRefs[aKeyIdx] = PersistableHelper.getPersister(this).getOID(aValue);
        }
        
        void dumpNode()
        {
            System.out.print(mNumKeys + ":");
            for (int i = 0; i < mNumKeys; i++) {
                if (i > 0) {
                    System.out.print(',');
                }
                System.out.print(mKeys[i]);
            }
        }
    }
    
    /**
     * Dynamic Map.Entry implementation.
     */
    private static final class Entry<K, V> implements Map.Entry<K, V>
    {
        NodePos<K> mNodePos;
        
        Entry(NodePos<K> aNodePos)
        {
            mNodePos = aNodePos;
        }
        
        public K getKey()
        {
            return mNodePos.mNode.mKeys[ mNodePos.mKeyIdx ];
        }

        public V getValue()
        {
            return (V)mNodePos.mNode.getValueAt(mNodePos.mKeyIdx);
        }

        public V setValue(V value)
        {
            V prevValue = getValue();
            mNodePos.mNode.setValueAt(mNodePos.mKeyIdx, value);
            return prevValue;
        }
    }
    
    /**
     * Base iterator implementation. If mEndKey is specified, an upper bound to the iteration is 
     * enforced.
     */
    private static class AbstractMapIterator<K, V>
    {
        PersistentBxTree<K, V> mTree;
        int mExpectedModCount;
        NodePos<K> mNextNodePos;
        NodePos<K> mCurrNodePos;
        Comparable<K> mEndKeyComparable;
        K mEndKey;

        AbstractMapIterator(PersistentBxTree<K, V> map, NodePos<K> startNodePos, K endKey)
        {
            mTree = map;
            mExpectedModCount = map.mModCount;
            mNextNodePos = startNodePos;
            mCurrNodePos = null;
            this.mEndKey = endKey;
            if (mTree.mComparator == null) {
                mEndKeyComparable = (Comparable<K>)endKey;
            }
        }

        public boolean hasNext()
        {
            return mNextNodePos != null;
        }

        final public void remove()
        {
            if (mExpectedModCount != mTree.mModCount) {
                throw new ConcurrentModificationException();
            }

            if (mCurrNodePos == null) {
                throw new IllegalStateException();
            }

            mCurrNodePos.mNode.delete(mTree, mCurrNodePos.mKeyIdx);
            mCurrNodePos = null;
            mExpectedModCount++;
        }

        final void makeNext()
        {
            if (mExpectedModCount != mTree.mModCount) {
                throw new ConcurrentModificationException();
            }
            
            if (mNextNodePos == null) {
                throw new NoSuchElementException();
            }
            
            mCurrNodePos.mNode = mNextNodePos.mNode;
            mCurrNodePos.mKeyIdx = mNextNodePos.mKeyIdx;

            mNextNodePos = mCurrNodePos.mNode.successor(mNextNodePos);
            if (mEndKey != null) {
                K key = mNextNodePos.mNode.mKeys[ mNextNodePos.mKeyIdx ];
                if (mEndKeyComparable != null) {
                    if (mEndKeyComparable.compareTo(key) > 0) {
                        mNextNodePos = null;
                    }
                }
                else if (mTree.mComparator.compare(key, mEndKey) > 0) {
                    mNextNodePos = null;
                }
            }
        }
        
        final Map.Entry<K,V> makeNextEntry()
        {
            makeNext();
            return new Entry<K, V>(mCurrNodePos);
        }
    }

    private static class EntryIterator<K, V> extends AbstractMapIterator<K, V> implements
                    Iterator<Map.Entry<K, V>>
    {

        EntryIterator(PersistentBxTree<K, V> map, NodePos<K> startNodePos, K endKey)
        {
            super(map, startNodePos, endKey);
        }

        EntryIterator(PersistentBxTree<K, V> map)
        {
            super(map, map.getLeftMostNodePos(), null);
        }

        public Map.Entry<K, V> next()
        {
            return makeNextEntry();
        }
    }


    static class KeyIterator<K, V> extends AbstractMapIterator<K, V> implements Iterator<K>
    {
        KeyIterator(PersistentBxTree<K, V> map, NodePos<K> startNodePos, K endKey)
        {
            super(map, startNodePos, endKey);
        }

        KeyIterator(PersistentBxTree<K, V> map)
        {
            super(map, map.getLeftMostNodePos(), null);
        }

        public K next()
        {
            return makeNextEntry().getKey();
        }
    }


    static class ValueIterator<K, V> extends AbstractMapIterator<K, V> implements Iterator<V>
    {
        ValueIterator(PersistentBxTree<K, V> map, NodePos<K> startNodePos, K endKey)
        {
            super(map, startNodePos, endKey);
        }

        ValueIterator(PersistentBxTree<K, V> map)
        {
            super(map, map.getLeftMostNodePos(), null);
        }

        public V next()
        {
            return makeNextEntry().getValue();
        }
    }


    static final class SubMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V>, Serializable
    {
        private static final long serialVersionUID = -6520786458950516097L;

        private PersistentBxTree<K, V> mTree;
        boolean mHasStart;
        boolean mHasEnd;
        K mStartKey;
        K mEndKey;
        Comparable<K> mStartKeyComparable;
        Comparable<K> mEndKeyComparable;

        transient Set<Map.Entry<K, V>> entrySet = null;

        SubMap(K start, PersistentBxTree<K, V> aTree)
        {
            this(start, aTree, null);
        }

        SubMap(K start, PersistentBxTree<K, V> aTree, K end)
        {
            mTree = aTree;
            mHasStart = mHasEnd = true;
            mStartKey = start;
            mEndKey = end;
            if (mTree.mComparator == null) {
                mStartKeyComparable = (Comparable<K>)mStartKey;
                mEndKeyComparable = (Comparable<K>)mEndKey;
            }
        }

        SubMap(PersistentBxTree<K, V> aTree, K end)
        {
            this(null, aTree, end);
        }

        private void checkRange(K key)
        {
            if (!isInRange(key)) {
                throw new IllegalArgumentException();
            }
        }

        private boolean isInRange(K key)
        {
            return checkUpperBound(key) && checkLowerBound(key);
        }

        private boolean checkUpperBound(K key)
        {
            if (mHasEnd) {
                if (mEndKeyComparable != null) {
                    return (mEndKeyComparable.compareTo(key) >= 0);
                }
                
                return (mTree.mComparator.compare(key, mEndKey) < 0);
            }
            
            return true;
        }

        private boolean checkLowerBound(K key)
        {
            if (mHasStart) {
                if (mStartKeyComparable != null) {
                    return (mStartKeyComparable.compareTo(key) < 0);
                }
                
                return (mTree.mComparator.compare(mStartKey, key) >= 0);
            }
            return true;
        }

        public Comparator<? super K> comparator()
        {
            return mTree.comparator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean containsKey(Object key)
        {
            return isInRange((K)key) && mTree.containsKey(key);
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet()
        {
            if (entrySet == null) {
                entrySet = new SubMapEntrySet<K, V>(this);
            }
            return entrySet;
        }

        public K firstKey()
        {
            PersistentBxTree.Entry<K, V> node = firstEntry();
            if (node != null) {
                return node.key;
            }
            throw new NoSuchElementException();
        }

        PersistentBxTree.Entry<K, V> firstEntry()
        {
            if (!mHasStart) {
                PersistentBxTree.Entry<K, V> root = mTree.root;
                return (root == null) ? null : minimum(mTree.root);
            }
            PersistentBxTree.Entry<K, V> node = mTree.findAfter(mStartKey);
            if (node != null && checkUpperBound(node.key)) {
                return node;
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public V get(Object key)
        {
            if (isInRange((K)key)) {
                return mTree.get(key);
            }

            return null;
        }

        public SortedMap<K, V> headMap(K endKey)
        {
            checkRange(endKey);
            if (mHasStart) {
                return new SubMap<K, V>(mStartKey, mTree, endKey);
            }
         
            return new SubMap<K, V>(mTree, endKey);
        }

        @Override
        public boolean isEmpty()
        {
            if (mHasStart) {
                PersistentBxTree.Entry<K, V> node = mTree.findAfter(mStartKey);
                return node == null || !checkUpperBound(node.key);
            }
            
            return mTree.findBefore(mEndKey) == null;
        }

        @Override
        public Set<K> keySet()
        {
            if (keySet == null) {
                keySet = new SubMapKeySet<K, V>(this);
            }
            
            return keySet;
        }

        public K lastKey()
        {
            if (!mHasEnd) {
                return mTree.lastKey();
            }
            
            PersistentBxTree.Entry<K, V> node = mTree.findBefore(mEndKey);
            if (node != null && checkLowerBound(node.key)) {
                return node.key;
            }
            
            throw new NoSuchElementException();
        }

        @Override
        public V put(K key, V value)
        {
            checkRange(key);
            return mTree.put(key, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public V remove(Object key)
        {
            if (isInRange((K)key)) {
                return mTree.remove(key);
            }
            return null;
        }

        public SortedMap<K, V> subMap(K startKey, K endKey)
        {
            checkRange(startKey);
            checkRange(endKey);
            Comparator<? super K> c = mTree.comparator();
            if (c == null) {
                if (toComparable(startKey).compareTo(endKey) > 0) {
                    throw new IllegalArgumentException();
                }
            }
            else {
                if (c.compare(startKey, endKey) > 0) {
                    throw new IllegalArgumentException();
                }
            }
            
            return new SubMap<K, V>(startKey, mTree, endKey);
        }

        public SortedMap<K, V> tailMap(K startKey)
        {
            checkRange(startKey);
            return new SubMap<K, V>(startKey, mTree, mEndKey);
        }

        @Override
        public Collection<V> values()
        {
            if (valuesCollection == null) {
                valuesCollection = new SubMapValuesCollection<K, V>(this);
            }

            return valuesCollection;
        }
    }


    static class SubMapEntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> implements Set<Map.Entry<K, V>>
    {
        SubMap<K, V> subMap;

        SubMapEntrySet(SubMap<K, V> map)
        {
            subMap = map;
        }

        @Override
        public boolean isEmpty()
        {
            return subMap.isEmpty();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
            PersistentBxTree.Entry<K, V> startNode = subMap.firstEntry();
            if (subMap.mHasEnd) {
                Comparator<? super K> cmp = subMap.comparator();
                if (cmp == null) {
                    return new ComparableBoundedEntryIterator<K, V>(subMap.mTree, startNode,
                                    toComparable(subMap.mEndKey));
                }
                return new ComparatorBoundedEntryIterator<K, V>(subMap.mTree, startNode, subMap.mEndKey);
            }
            return new UnboundedEntryIterator<K, V>(subMap.mTree, startNode);
        }

        @Override
        public int size()
        {
            int size = 0;
            Iterator<Map.Entry<K, V>> it = iterator();
            while (it.hasNext()) {
                size++;
                it.next();
            }
            return size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object object)
        {
            if (object instanceof Map.Entry) {
                Map.Entry<K, V> entry = (Map.Entry<K, V>)object;
                K key = entry.getKey();
                if (subMap.isInRange(key)) {
                    V v1 = subMap.get(key), v2 = entry.getValue();
                    return v1 == null ? v2 == null : v1.equals(v2);
                }
            }
            return false;
        }

    }


    static class SubMapKeySet<K, V> extends AbstractSet<K> implements Set<K>
    {
        SubMap<K, V> subMap;

        SubMapKeySet(SubMap<K, V> map)
        {
            subMap = map;
        }

        @Override
        public boolean contains(Object object)
        {
            return subMap.containsKey(object);
        }

        @Override
        public boolean isEmpty()
        {
            return subMap.isEmpty();
        }

        @Override
        public int size()
        {
            int size = 0;
            Iterator<K> it = iterator();
            while (it.hasNext()) {
                size++;
                it.next();
            }
            return size;
        }

        @Override
        public Iterator<K> iterator()
        {
            PersistentBxTree.Entry<K, V> startNode = subMap.firstEntry();
            if (subMap.mHasEnd) {
                Comparator<? super K> cmp = subMap.comparator();
                if (cmp == null) {
                    return new ComparableBoundedKeyIterator<K, V>(subMap.mTree, startNode,
                                    toComparable(subMap.mEndKey));
                }
                return new ComparatorBoundedKeyIterator<K, V>(subMap.mTree, startNode, subMap.mEndKey);
            }
            return new UnboundedKeyIterator<K, V>(subMap.mTree, startNode);
        }
    }


    static class SubMapValuesCollection<K, V> extends AbstractCollection<V>
    {
        SubMap<K, V> subMap;

        public SubMapValuesCollection(SubMap<K, V> subMap)
        {
            this.subMap = subMap;
        }

        @Override
        public boolean isEmpty()
        {
            return subMap.isEmpty();
        }

        @Override
        public Iterator<V> iterator()
        {
            PersistentBxTree.Entry<K, V> startNode = subMap.firstEntry();
            if (subMap.mHasEnd) {
                Comparator<? super K> cmp = subMap.comparator();
                if (cmp == null) {
                    return new ComparableBoundedValueIterator<K, V>(subMap.mTree, startNode,
                                    toComparable(subMap.mEndKey));
                }
                return new ComparatorBoundedValueIterator<K, V>(subMap.mTree, startNode, subMap.mEndKey);
            }
            return new ValueIterator<K, V>(subMap.mTree, startNode);
        }

        @Override
        public int size()
        {
            int cnt = 0;
            for (Iterator<V> it = iterator(); it.hasNext();) {
                it.next();
                cnt++;
            }
            return cnt;
        }
    }
}
