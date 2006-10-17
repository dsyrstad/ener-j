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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/RandomAccessLargeListIterator.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.enerj.annotations.Persist;

/**
 * A java.util.ListIterator implementation for a LargeList.
 * Works best if the LargeList is also a RandomAccessList.
 *
 * @version $Id: RandomAccessLargeListIterator.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DArray
 * @see RegularDArray
 * @see LargeList
 */
@Persist
public class RandomAccessLargeListIterator implements ListIterator 
{
    /** The list we're iterating over. */
    private LargeList mLargeList;

    /** Current index position.  */
    private long mPosition;

    /** Index returned on last call to next or previous. -1L if not set yet or the
     * previous index was removed.
     */
    private long mPreviousIdx = -1L;

    /** The last mModCount value we tracked on the array when the iterator modified the list. */
    private transient int mLastModCount;

    //----------------------------------------------------------------------
    /**
     * Construct a new iterator on the specified LargeList, starting at aStartIndex.
     *
     * @param aLargeList the list to iterator on.
     * @param aStartIndex the starting position.
     *
     * @throws IndexOutOfBoundsException if aStartIndex is outside the bounds of the list.
     */
    public RandomAccessLargeListIterator(LargeList aLargeList, long aStartIndex)
    {
        if (aStartIndex < 0 || aStartIndex > aLargeList.sizeAsLong()) {
            throw new IndexOutOfBoundsException("Iterator Index: " + aStartIndex);
        }

        mLargeList = aLargeList;
        mPosition = aStartIndex;
        mLastModCount = mLargeList.getModificationCount();
    }

    //----------------------------------------------------------------------
    // Start of Iterator interface....
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public boolean hasNext() 
    {
        return mPosition < mLargeList.sizeAsLong();
    }

    //----------------------------------------------------------------------
    public Object next() 
    {
        if (mPosition >= mLargeList.sizeAsLong()) {
            throw new NoSuchElementException();
        }

        assertNoConcurrentModification();

        mPreviousIdx = mPosition;
        return mLargeList.getAtIndex(mPosition++);
    }

    //----------------------------------------------------------------------
    public void remove() 
    {
        if (mPreviousIdx == -1L) {
            throw new IllegalStateException();
        }

        assertNoConcurrentModification();

        mLargeList.removeElements(mPreviousIdx, 1L);
        --mPosition;
        mPreviousIdx = -1L;
        mLastModCount = mLargeList.getModificationCount();
    }

    //----------------------------------------------------------------------
    // ...End of Iterator interface.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Start of ListIterator interface....
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public void add(Object anObject)
    {
        if (mPreviousIdx < 0L) {
            throw new IllegalStateException();
        }

        assertNoConcurrentModification();

        mLargeList.insertElements(mPosition, 1L);
        mLargeList.setAtIndex(mPosition, anObject);
        ++mPosition;
        mPreviousIdx = -1L;
        mLastModCount = mLargeList.getModificationCount();
    }

    //----------------------------------------------------------------------
    public boolean hasPrevious()
    {
        return mPosition > 0;
    }

    //----------------------------------------------------------------------
    public int nextIndex()
    {
        return (int)mPosition;
    }

    //----------------------------------------------------------------------
    public Object previous()
    {
        if (mPosition <= 0) {
            throw new NoSuchElementException();
        }

        assertNoConcurrentModification();

        --mPosition;
        mPreviousIdx = mPosition;
        return mLargeList.getAtIndex(mPosition);
    }

    //----------------------------------------------------------------------
    public int previousIndex()
    {
        return (int)mPosition - 1;
    }

    //----------------------------------------------------------------------
    public void set(Object anObject)
    {
        if (mPreviousIdx < 0L) {
            throw new IllegalStateException();
        }

        assertNoConcurrentModification();

        mLargeList.setAtIndex(mPreviousIdx, anObject);
        mLastModCount = mLargeList.getModificationCount();
    }

    //----------------------------------------------------------------------
    // ...End of ListIterator interface.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Ensures that a concurrent modification to the backing list has
     * not occured outside of the iterator.
     *
     * @throws ConcurrentModificationException if a concurrent modification occurred.
     */
    private void assertNoConcurrentModification() 
    {
        if (mLargeList.getModificationCount() != mLastModCount) {
            throw new ConcurrentModificationException();
        }
    }

}

