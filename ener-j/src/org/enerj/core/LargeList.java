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
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/LargeList.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $

package org.enerj.core;


/**
 * Represents additional Collection methods for a "large" list. A large list
 * is one which contain more than 2 billion items.
 *
 * @version $Id: LargeList.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface LargeList<E> extends LargeCollection<E>, java.util.List<E>
{

    /**
     * Inserts anElementCount null elements into the array (expands array). 
     * Elements from anIndex (inclusive)  to the end of the array are shifted 
     * to the right (the zeroth element being the left) by anElementCount elements. 
     * The array grows by anElementCount elements. mModCount is incremented by anElementCount.
     *
     * @param anIndex the index to start at. May equal sizeAsLong() to append to list.
     * @param anElementCount the number of empty (null) elements to insert.
     *
     * @throws IndexOutOfBoundsException if index is out of range (anIndex < 0 || 
     *  anIndex > sizeAsLong() || anElementCount < 0).
     */
    public void insertElements(long anIndex, long anElementCount);
    

    /**
     * Removes anElementCount elements from the array (shrinks array). 
     * Elements from (anIndex + anElementCount) (inclusive) to the end of 
     * the array are shifted to the left (the zeroth element being the left). 
     * The array shrinks  by anElementCount elements. mModCount is incremented.
     *
     * @param anIndex the index to start at.
     * @param anElementCount the number of elements to remove.
     *
     * @throws IndexOutOfBoundsException if index is out of range (anIndex < 0 || 
     *  anIndex >= sizeAsLong() || (anIndex + anElementCount) > sizeAsLong()).
     */
    public void removeElements(long anIndex, long anElementCount);
    

    /**
     * Sets the element at the specified index. Grows the array if necessary.
     * If the array grows, new elements between sizeAsLong() and anIndex-1 are
     * filled with nulls and mModCount is incremented.
     *
     * @param anIndex the index of the element.
     * @param anElement the element to be set. May be null.
     */
    public void setAtIndex(long anIndex, E anElement);
    

    /**
     * Gets the element at the specified index.
     *
     * @param anIndex the index of the element.
     *
     * @return the object at anIndex, which may be null.
     *
     * @throws IndexOutOfBoundsException if index is out of range (anIndex < 0 || 
     *  anIndex >= sizeAsLong()).
     */
    public E getAtIndex(long anIndex);


    /**
     * Like Collection.indexOf(), but returns result as long.
     *
     * @param anObject the object to find the first index of.
     *
     * @return the first index of anObject, or -1L if the object is not in the array.
     */
    public long indexOfAsLong(Object anObject);


    /**
     * Like Collection.lastIndexOf(), but returns result as long.
     *
     * @param anObject the object to find the last index of.
     *
     * @return the last index of anObject, or -1L if the object is not in the array.
     */
    public long lastIndexOfAsLong(Object anObject);

}

