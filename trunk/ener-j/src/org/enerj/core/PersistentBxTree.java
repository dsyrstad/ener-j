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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.enerj.annotations.Persist;
import org.odmg.DCollection;
import org.odmg.DMap;
import org.odmg.QueryInvalidException;

/**
 * A Persistent B+Tree Map.  <p>
 * 
 * This implementation allows leaf nodes to go empty during deletion so that less tree
 * reorganization occurs.
 * 
 * A put of a duplicate key simply replaces the existing key in this implementation. This
 * conforms to the Map contract. TODO support duplicate/unique key behavior. Need supporting methods.
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class PersistentBxTree<K, V> implements DMap<K, V>, SortedMap<K, V>
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key)
    {
        // TODO Auto-generated method stub
        return false;
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
        // TODO Auto-generated method stub
        return null;
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
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> t)
    {
        // TODO Auto-generated method stub
        
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
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        return mSize == 0;
    }

    /** 
     * {@inheritDoc}
     * @see java.util.Map#values()
     */
    public Collection<V> values()
    {
        // TODO Auto-generated method stub
        return null;
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
                System.arraycopy(mOIDRefs, aKeyIdx, mOIDRefs, aKeyIdx + 1, length + 1);
            }

            mKeys[aKeyIdx] = aKey;
            mOIDRefs[aKeyIdx] = aValueOID;
            ++mNumKeys;
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
            if (mIsLeaf) {
                throw new IllegalStateException("Node is not an interior node.");
            }
            
            long oid = mOIDRefs[anIndex];
            return (Node<K>)(Object)PersistableHelper.getPersister(this).getObjectForOID(oid);
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
    
}
