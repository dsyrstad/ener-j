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

import static org.enerj.core.ObjectSerializer.NULL_OID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
 * This implementation allows nodes to go empty during deletion so that less tree
 * reorganization occurs.
 * 
 * A put of a duplicate key simply replaces the existing key in this implementation. This
 * conforms to the Map contract. TODO support duplicate/unique key behavior. Need supporting methods.
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class PersistentBxTree<K, V extends Persistable> implements DMap<K, V>, SortedMap<K, V>
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
        this(aNumKeysPerNode, null, false);
    }

    /**
     * Construct a PersistentBxTree using the specified Comparator for ordering of the keys.
     * 
     * @param aNodeSize the maximum number of keys per node. This must be greater than
     *  two, but is usually much larger. 
     * 
     * @param aComparator the Comparator to use for comparing keys. May be null to use 
     *  natural ordering, in which case the key object must implement {@link Comparable}.
     */
    public PersistentBxTree(int aNodeSize, Comparator<K> aComparator, boolean allowDuplicateKeys)
    {
        if (aNodeSize <= 2 || aNodeSize > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Node size must be > 2 and less than " + Short.MAX_VALUE);
        }
        
        mNodeSize = aNodeSize;
        mComparator = aComparator;
        // The root begins its life as a leaf. However, this is not common in real trees. Which came first: the leaf or the seed?
        mRootNode = new Node<K>(true, mNodeSize);
        mAllowDuplicateKeys = allowDuplicateKeys;
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
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
     * Traverse the tree to produce the Path to the given key. The returned path
     * will reflect the proper insertion point if the key is not found.
     *
     * @param aKey the target key to be found.
     * 
     * @return the path.
     */
    private Path<K> getPathToKey(K aKey)
    {
        Path<K> path = new Path<K>();
        Node<K> node = mRootNode;
        do {
            NodePos<K> nodePos = node.findKey(aKey, mComparator);
            path.push(nodePos);
        }
        while (!node.isLeaf());
        
        return path;
    }

    /**
     * Inserts a key into and it's corresponding value OID into the tree.
     *
     * @param aKey the key.
     * @param aValueOID the OID refering to the value for the key. 
     */
    private void insert(K aKey, long aValueOID)
    {
        Path<K> path = getPathToKey(aKey);
        boolean keyFound = path.peek().mIsMatch;
        if (keyFound && !mAllowDuplicateKeys) {
            throw new IllegalStateException("Attempted to insert duplicate key in index");
        }
        
        K passKey = aKey;
        long passOID = aValueOID;

        NodePos<K> nodePos;
        while ((nodePos = path.pop()) != null && passKey != null) {
            K currKey = passKey;
            long currOID = passOID;
            
            Node<K> node = nodePos.mNode;
            int keyIdx = nodePos.mKeyIdx;
            
            // If not a enough room for a new key, split the node first.
            int numKeys = node.getNumKeys(); 
            if (numKeys >= mNodeSize) {
                // Split and pass new key/oid up. 
                // If this node is a leaf, this is teh OID pointing next right leaf, 
                long rightOID = node.getOverflowOID();
                int splitIdx;
                if (node.isLeaf() && rightOID == NULL_OID && keyIdx == numKeys) {
                    // If inserting on right-most leaf, split leaving 1 keys on the 
                    // right (optimized sequential insert).
                    splitIdx = numKeys - 2;
                }
                else {
                    // Round up, right half gets same or more. >> 1 divides by 2.
                    splitIdx = (numKeys + 1) >> 1; 
                }

                // We have to create a new right node rather than a left node, because if 
                // this is a leaf, the leaf to the left may already be pointing to the existing node.
                Node<K> newRightNode = new Node<K>(node, splitIdx);
                long newRightNodeOID = PersisterRegistry.getCurrentPersisterForThread().getOID(newRightNode);
                if (node.isLeaf()) {
                    // Make new right node point to the same leaf as the existing node.
                    newRightNode.setOverflowOID(rightOID);
                    // Make left (existing) node point to the new right node.
                    node.setOverflowOID(newRightNodeOID);
                }
                else {
                    // TODO HANDLING of overflow oid on interior nodes.
                }

                // The key to pass up is the first one on the right node.
                passKey = newRightNode.getKeyAt(0);
                passOID = newRightNodeOID;

                // TODO choose the node that the key will be inserted in. Set node to it.
            }
            else {
                passKey = null;
            }
            
            // TODO insert currKey/currOID into node
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
    
    /**
     * Tracks a path through the B+Tree.
     */
    private static final class Path<K>
    {
        List<NodePos<K>> mNodeList = new ArrayList<NodePos<K>>(20);
        
        void push(NodePos<K> aNode)
        {
            mNodeList.add(aNode);
        }
        
        /**
         * Pops a node. Returns null if there are no more nodes. 
         */
        NodePos<K> pop()
        {
            if (mNodeList.isEmpty()) {
                return null;
            }
            
            return mNodeList.remove( mNodeList.size() - 1 );
        }
        
        NodePos<K> peek()
        {
            if (mNodeList.isEmpty()) {
                return null;
            }
            
            return mNodeList.get( mNodeList.size() - 1 );
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
        
        /** OIDs pointing to the child nodes or values. Note that OIDs are used rather than references to the actual
         * objects so that we don't load up a bunch of hollow objects when the node is referenced.
         * If this a leaf node, the OIDs reference the values. <p><pre>
         * mOIDRefs[0..n] references value for mKeys[0..n].
         * </pre><p>
         * 
         * If this is an interior node, the OIDs reference child nodes.<p><pre> 
         * mOIDRefs[0] contains keys < mKeys[0].
         * mOIDRefs[1] contains keys >= mKeys[0] and < mKeys[1].
         * </pre><p>
         */
        private long[] mOIDRefs;
        
        /** For leafs, this references the next leaf node to the right. This will be NULL_OID if there's
        * not another leaf node. For interior nodes, this references the node that 
        * contains keys >= mKey[mNumKeys - 1].
        */
        private long mOverflowOID;
        
        Node(boolean isLeaf, int aNodeSize)
        {
            setIsLeaf(isLeaf);
            mKeys = (K[])new Object[aNodeSize];
            mNumKeys = 0;
            mOIDRefs = new long[mKeys.length];
        }
        
        /**
         * Construct a Node that is a copy of the entries in aNode starting at aStartIdx to the end of the node.
         * The overflow OID is also copied from aNode. 
         *
         * @param aNode the node to copy from.
         * @param aStartIdx the starting index.
         * @param aLength the length to copy.
         */
        Node(Node<K> aNode, int aStartIdx) 
        {
            mIsLeaf = aNode.mIsLeaf;
            mKeys = (K[])new Object[ aNode.mKeys.length ];
            int length = mNumKeys - aStartIdx;
            mNumKeys = (short)length;
            mOIDRefs = new long[ mKeys.length];
            System.arraycopy(aNode.mKeys, aStartIdx, mKeys, 0, length);
            System.arraycopy(aNode.mOIDRefs, aStartIdx, mOIDRefs, 0, length + 1);
            
            mOverflowOID = aNode.mOverflowOID;
        }
        
        boolean isLeaf()
        {
            return mIsLeaf;
        }
        
        void setIsLeaf(boolean isLeaf)
        {
            mIsLeaf = isLeaf;
        }
        
        int getNumKeys()
        {
            return mNumKeys;
        }
        
        long getOverflowOID()
        {
            return mOverflowOID;
        }
        
        void setOverflowOID(long aOverflowOID)
        {
            mOverflowOID = aOverflowOID;
        }
        
        /**
         * Truncates this node aNumKeys.
         */
        void truncate(int aNumKeys)
        {
            mNumKeys = (short)aNumKeys;
            // Make sure key references are cleared so that they can be collected and won't be loaded again.
            // Note that we don't need to clear the OIDs.
            Arrays.fill(mKeys, aNumKeys, mKeys.length - 1, null);
        }
        
        /**
         * Finds the given key within the node.
         *
         * @param aKey the target key to be found.
         * @param aComparator if not null, used to compare keys. Otherwise aKey is assumed to be a
         *  {@link Comparable}.
         * 
         * @return a node position. If an exact match is not found, the
         * index at which the target key would be inserted is returned in the node position and mIsMatch
         * is set to false.   
         */
        NodePos<K> findKey(K aKey, Comparator<K> aComparator)
        {
            if (aComparator == null) {
                return findKey(aKey);
            }

            int low = 0;
            int mid = 0;
            int high = mNumKeys - 1;
            boolean found = false;
            while (low <= high) {
                mid = (low + high) >> 1;
                int result = aComparator.compare(mKeys[mid], aKey);
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

            return new NodePos<K>(this, mid, found);
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
        private NodePos<K> findKey(K aKey)
        {
            Comparable<Object> key = (Comparable<Object>)aKey;
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

            return new NodePos<K>(this, mid, found);
        }
        
        /**
         * Gets the key at the specified index.
         *
         * @param anIndex the index.
         * 
         * @return the key.
         */
        K getKeyAt(int anIndex) 
        {
            return mKeys[anIndex];
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
            return (Node<K>)(Object)PersisterRegistry.getCurrentPersisterForThread().getObjectForOID(oid);
        }

        /**
         * Gets the value OID at the given index. Assumes that this node is a leaf node.
         *
         * @param anIndex the index of the value.
         * 
         * @return the value's OID.
         */
        long getValueOIDAt(int anIndex)
        {
            if (!mIsLeaf) {
                throw new IllegalStateException("Node is not a leaf node.");
            }
            
            return mOIDRefs[anIndex];
        }
    }
    
}
