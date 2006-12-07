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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/VeryLargeDArray.java,v 1.4 2006/06/09 02:39:38 dsyrstad Exp $

package org.enerj.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.odmg.QueryInvalidException;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DArray which supports very large dynamic arrays
 * in a semi-sparse format. This type of array is useful when the array itself cannot
 * fit entirely in memory at one time. It is fairly expensive (in terms of performance and
 * storage) to use this type of DArray for smaller arrays. For arrays that can fit
 * reasonably in memory at once, you should use PersistentArray (which is returned by
 * EnerJImplementation.newDArray()).
 * <p>
 * The array is implemented as a three-level tree of array nodes with
 * nodeSize elements on each node. The top level (root) is a single array of 
 * nodeSize references to the second-level nodes. The second-level nodes, in turn,
 * point to third-level nodes which point to the actual objects.
 * For example, an array with a nodeSize of 16,384 can support a maximum of
 * 4,398,046,511,104 (16,384^3 or 4 trillion) objects. However, to allocate
 * just the four trillionth element, only three nodes need to be allocated.
 * <p>
 * The add(int, Object), addAll(int, Collection), and remove(...) methods can be 
 * very expensive with this type of array because shifting must occur.
 * <p>
 * Note that the standard Collection methods that take or return an index as
 * an int are limited to 2 billion (2^31, 2,147,483,648) elements and may operate
 * incorrectly if the array is larger than this. Replacement methods for some
 * of the Collection methods are defined by the LargeList interface.
 *
 * @version $Id: VeryLargeDArray.java,v 1.4 2006/06/09 02:39:38 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DArray
 * @see PersistentArrayList
 * @see LargeList
 */
@Persist
public class LargePersistentArrayList 
    implements org.odmg.DArray, org.enerj.core.LargeList, java.util.RandomAccess, Cloneable
{
    /** Number of elements in each node. */
    private int mNodeSize;
    /** mNodeSize squared. */
    private long mNodeSizeSquared;
    /** The number of elements in the array. */
    private long mSize;
    /** The root node of the tree. */
    private Node mRootNode;
    
    protected transient int mModCount = 0;
    

    /**
     * Constructs a new VeryLargeDArray with the specified node size. 
     * The maximum size of the array is aNodeSize^3.
     * 
     * @param aNodeSize the size of a single node in the tree. See class
     *  description for more information.
     */
    public LargePersistentArrayList(int aNodeSize)
    {
        mNodeSize = aNodeSize;
        mNodeSizeSquared = (long)mNodeSize * (long)mNodeSize;
        mRootNode = new Node(mNodeSize);
        mSize = 0;
    }
    

    /**
     * Constructs a new VeryLargeDArray with the default node size (1024). 
     * This node size supports a maximum of 1,073,741,824 (about 1 billion) elements.
     */
    public LargePersistentArrayList()
    {
        this(1024);
    }
    

    /**
     * Gets the leaf node for the corresponding index. Allocates new nodes if 
     * shouldAllocate is true. If the size of the list changes, mModCount is
     * incremented.
     *
     * @param anIndex the index whose node will be retrieved.
     * @param shouldAllocate if true, nodes will be allocated if they don't
     *  exist. If false and nodes don't exist, null will be returned.
     *  If this is true and anIndex is larger than the number of elements currently
     *  in the array (as defined by sizeAsLong()), the size of the array grows.
     *
     * @return the leaf Node corresponding to anIndex, or null if shouldAllocate
     *  is false and a node does not exist.
     *
     * @throws IndexOutOfBoundsException if index is out of range (anIndex < 0 || 
     *  (anIndex >= sizeAsLong() && !shouldAllocate)).
     */
    protected Node getLeafNodeForIndex(long anIndex, boolean shouldAllocate)
    {
        if (anIndex < 0 || (!shouldAllocate && anIndex >= mSize)) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + anIndex);
        }
        
        long rootIndex = anIndex / mNodeSizeSquared;
        Node secondLevelNode = (Node)mRootNode.get((int)rootIndex);
        if (secondLevelNode == null) {
            if (shouldAllocate) {
                secondLevelNode = new Node(mNodeSize);
                mRootNode.set((int)rootIndex, secondLevelNode);
            }
            else {
                return null;
            }
        }
        
        int secondLevelIndex = (int)((anIndex - (rootIndex * mNodeSizeSquared)) / mNodeSize);
        Node leafNode = (Node)secondLevelNode.get(secondLevelIndex);
        if (leafNode == null) {
            if (shouldAllocate) {
                leafNode = new Node(mNodeSize);
                secondLevelNode.set(secondLevelIndex, leafNode);
            }
            else {
                return null;
            }
        }
        
        if (shouldAllocate && anIndex >= mSize) {
            mSize = anIndex + 1;
            ++mModCount;
        }
        
        return leafNode;
    }


    // Start of LargeList interface...



    public void insertElements(long anIndex, long anElementCount)
    {
        if (anIndex < 0 || anIndex > mSize) {
            throw new IndexOutOfBoundsException("Bad index: " + anIndex);
        }
        
        if (anElementCount < 0) {
            throw new IndexOutOfBoundsException("Bad element count: " + anElementCount);
        }

        // This could probably be optimized with the side-effect of complexity
        for (long srcIdx = mSize - 1, destIdx = srcIdx + anElementCount; 
                srcIdx >= anIndex; --srcIdx, --destIdx) {
            setAtIndex(destIdx, getAtIndex(srcIdx) );
        }
        
        // Null-out inserted elements
        long endIdx = anIndex + anElementCount;
        for (long i = anIndex; i < endIdx; ++i) {
            setAtIndex(i, null);
        }
        
        // Note: setAtIndex automatically increased mSize
    }
    

    public void removeElements(long anIndex, long anElementCount)
    {
        if (anIndex < 0 || anIndex >= mSize || (anIndex + anElementCount) > mSize || anElementCount < 0) {
            throw new IndexOutOfBoundsException("Bad index: " + anIndex + " element count: " + anElementCount);
        }

        // This could probably be optimized with the side-effect of complexity
        for (long srcIdx = anIndex + anElementCount, destIdx = anIndex; 
                srcIdx < mSize; ++srcIdx, ++destIdx) {
            setAtIndex(destIdx, getAtIndex(srcIdx) );
            // Clear out src element
            setAtIndex(srcIdx, null);
        }

        mSize -= anElementCount;
        ++mModCount;
    }
    

    public void setAtIndex(long anIndex, Object anElement) 
    {
        Node node = getLeafNodeForIndex(anIndex, true);
        node.set((int)(anIndex % mNodeSize), anElement);
    }
    

    public Object getAtIndex(long anIndex) 
    {
        Node node = getLeafNodeForIndex(anIndex, false);
        if (node == null) {
            return null;
        }
        
        return node.get((int)(anIndex % mNodeSize));
    }
    

    // ...End of LargeList interface.



    // Start of LargeCollection interface...



    public long sizeAsLong()
    {
        return mSize;
    }
    

    public long indexOfAsLong(Object anObject) 
    {
        if (anObject == null) {
            for (long i = 0L; i < mSize; i++) {
                if (getAtIndex(i) == null) {
                    return i;
                }
            }
        }
        else {
            for (long i = 0L; i < mSize; i++) {
                if (anObject.equals( getAtIndex(i) )) {
                    return i;
                }
            }
        }
        
        return -1L;
    }


    public long lastIndexOfAsLong(Object anObject) 
    {
        if (anObject == null) {
            for (long i = mSize - 1; i >= 0; i--) {
                if (getAtIndex(i) == null) {
                    return i;
                }
            }
        }
        else {
            for (long i = mSize - 1; i >= 0; i--) {
                if (anObject.equals( getAtIndex(i) )) {
                    return i;
                }
            }
        }
        
        return -1L;
    }


    public int getModificationCount()
    {
        return mModCount;
    }
    

    // ..End of LargeCollection interface.



    // Start of Interfaces: org.odmg.DArray, java.util.Collection, java.util.List...



    public boolean add(Object anElement) 
    {
        setAtIndex(mSize, anElement);
        return true;
    }
    

    public void add(int anIndex, Object anElement) 
    {
        insertElements(anIndex, 1L);
        setAtIndex((long)anIndex, anElement);
    }
    

    public boolean addAll(Collection aCollection) 
    {
        // This appends
        Iterator iterator = aCollection.iterator();
        boolean result = iterator.hasNext();
        while (iterator.hasNext()) {
            setAtIndex(mSize, iterator.next() );
        }
        
        return result;
    }
    

    public boolean addAll(int anIndex, Collection aCollection) 
    {
        long numElements;
        if (aCollection instanceof LargeCollection) {
            numElements = ((LargeCollection)aCollection).sizeAsLong();
        }
        else {
            numElements = aCollection.size();
        }
        
        insertElements(anIndex, numElements);

        Iterator iterator = aCollection.iterator();
        boolean result = iterator.hasNext();
        for (; iterator.hasNext(); ++anIndex) {
            setAtIndex(anIndex, iterator.next() );
        }
        
        return result;
    }
    

    public void clear() 
    {
        // The previous tree is GCed.
        mRootNode = new Node(mNodeSize);
        mSize = 0;
        ++mModCount;
    }
    

    public boolean contains(Object anObject) 
    {
        return indexOfAsLong(anObject) >= 0L;
    }
    

    public boolean containsAll(Collection aCollection) 
    {
        Iterator iterator = aCollection.iterator();
        while (iterator.hasNext()) {
            if ( indexOfAsLong( iterator.next() ) < 0L ) {
                return false;
            }
        }

        return true;
    }
    

    public Object get(int anIndex) 
    {
        return getAtIndex((long)anIndex);
    }
    

    public int indexOf(Object anObject) 
    {
        return (int)indexOfAsLong(anObject);
    }
    

    public boolean isEmpty() 
    {
        return mSize == 0;
    }
    

    public Iterator iterator() 
    {
        return listIterator(0);
    }
    

    public int lastIndexOf(Object anObject) 
    {
        return (int)lastIndexOfAsLong(anObject);
    }
    

    public ListIterator listIterator() 
    {
        return listIterator(0);
    }
    

    public ListIterator listIterator(int anIndex) 
    {
        return new RandomAccessLargeListIterator(this, anIndex);
    }
    

    public boolean remove(Object anObject) 
    {
        long idx = indexOfAsLong(anObject);
        if (idx < 0L) {
            return false;
        }
        
        removeElements(idx, 1L);
        return true;
    }
    

    public Object remove(int anIndex) 
    {
        Object prevElement = getAtIndex((long)anIndex);
        removeElements((long)anIndex, 1L);
        return prevElement;
    }
    

    public boolean removeAll(Collection aCollection) 
    {
        Iterator iterator = aCollection.iterator();
        boolean result = false;
        while (iterator.hasNext()) {
            if (remove( iterator.next() )) {
                result = true;
            }
        }

        return result;
    }
    

    public void resize(int aNewSize) 
    {
        // ODMG v3.0 2.3.6.4 says resize changes the maximum number of elements
        // the array can contain. It also says if aNewSize is smaller than
        // the actual number of elements, an exception is thrown. The
        // org.odmg javadoc says nothing about the behavior. Poet allows shrinking
        // or growing. IIOSS conforms to the spec.
        if (aNewSize < mSize) {
            throw new IllegalArgumentException("resize(" + aNewSize + ") is smaller than current size: " + mSize);
        }
        
        mSize = aNewSize;
        ++mModCount;
    }
    

    public boolean retainAll(Collection aCollection) 
    {
        boolean result = false;
        for (long i = 0; i < mSize; i++) {
            if ( !aCollection.contains( getAtIndex(i) )) {
                removeElements(i, 1L);
                result = true;
            }
        }
        
        return result;
    }
    

    public Object set(int anIndex, Object anElement) 
    {
        // This also checks that the index is in bounds.
        Object prevObj = getAtIndex((long)anIndex);
        setAtIndex((long)anIndex, anElement);
        return prevObj;
    }
    

    public int size() 
    {
        return (int)mSize;
    }
    

    public List subList(int fromIndex, int toIndex) 
    {
        /**  TODO  */
        throw new UnsupportedOperationException("subList");
    }
    

    public Object[] toArray() 
    {
        return toArray(new Object[(int)mSize]);
    }
    

    public Object[] toArray(Object[] anArray) 
    {
        if (anArray.length < mSize) {
            anArray = (Object[])java.lang.reflect.Array.newInstance(
                anArray.getClass().getComponentType(), (int)mSize);
        }

        for (long i = 0; i < mSize; i++) {
            anArray[(int)i] = getAtIndex(i);
        }

        if (anArray.length > mSize) {
            anArray[(int)mSize] = null;
        }
        
        return anArray;
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
        // This calculation is based on the List.hashCode() documentation.
        int hashCode = 1;
        long numElements = (mSize < 32 ? mSize : 32);
        for (long i = 0; i < numElements; i++) {
            Object obj = getAtIndex(i);
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }

        return hashCode;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        if (anObject == this) {
            return true;
        }
        
        if ( !(anObject instanceof List)) {
            return false;
        }

        List list2 = (List)anObject;
        long list2Size;
        if (anObject instanceof LargeList) {
            list2Size = ((LargeList)anObject).sizeAsLong();
        }
        else {
            list2Size = list2.size();
        }

        if (mSize != list2Size) {
            return false;
        }

        Iterator iterator2 = list2.iterator();
        for (long i = 0; i < mSize; i++) {
            Object obj1 = getAtIndex(i);
            Object obj2 = iterator2.next();
            if ((obj1 == null ? obj2 != null : !obj1.equals(obj2))) {
                return false;
            }
        }
        
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return this.getClass().getName() + ": size=" + mSize;
    }
    

    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException
    {
        LargePersistentArrayList clone = (LargePersistentArrayList)super.clone();
        clone.mRootNode = (Node)mRootNode.clone();
        return clone;
    }
    

    // ...End of Interfaces: org.odmg.DArray, java.util.Collection, java.util.List.




    /** 
     * Represents a node in the tree. This is a separate object so that it
     * is not directly recoginized as an SCO and will be demand-loaded.
     */
    @Persist
    private static final class Node implements Cloneable
    {
        private Object[] mObjects;
        

        Node(int mNodeSize)
        {
            mObjects = new Object[mNodeSize];
        }


        Object get(int anIndex)
        {
            return mObjects[anIndex];
        }


        void set(int anIndex, Object anObject)
        {
            if (mObjects[anIndex] != anObject) {
                mObjects[anIndex] = anObject;
                EnerJImplementation.setModified(this);
            }
        }


        /**
         * {@inheritDoc}
         */
        public Object clone() throws CloneNotSupportedException
        {
            Node clone = (Node)super.clone();
            clone.mObjects = new Object[ mObjects.length ];
            System.arraycopy(mObjects, 0, clone.mObjects, 0, mObjects.length);
            return clone;
        }
    }
}

