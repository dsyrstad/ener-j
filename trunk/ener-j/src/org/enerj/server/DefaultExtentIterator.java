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
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/DefaultExtentIterator.java,v 1.6 2006/01/17 02:41:09 dsyrstad Exp $

package org.enerj.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import org.odmg.ODMGRuntimeException;
import org.enerj.core.ClassVersionSchema;
import org.enerj.core.LogicalClassSchema;
import org.enerj.core.Schema;
import org.enerj.core.SparseBitSet;

/**
 * Ener-J's default ExtentIterator implementation.
 *
 * @version $Id: DefaultExtentIterator.java,v 1.6 2006/01/17 02:41:09 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class DefaultExtentIterator implements ExtentIterator
{
    /** LinkedList/Queue of LogicalClassSchema representing the extents to iterate over. */
    private LinkedList mClassQueue = new LinkedList();
    private SparseBitSet.Iterator mCurrentIterator = null;
    private boolean mIsOpen = true;
    private MetaObjectServerSession mSession;

    //----------------------------------------------------------------------
    /**
     * Constructs a DefaultExtentIterator.
     *
     * @param aClassName the class name to iterate over. If wantSubclasses is true,
     *  then aClassName does not have to be a persistable class.
     * @param wantSubclasses if true, all subclasses of aClassName are also included in the iterator.
     * @param aSchema the Schema from the database in question.
     * @param aSession the MetaObjectServerSession associated with this iterator.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public DefaultExtentIterator(String aClassName, boolean wantSubclasses, Schema aSchema,
                                 MetaObjectServerSession aSession) throws ODMGRuntimeException
    {
        mSession = aSession;
        LogicalClassSchema candidateClassSchema = aSchema.findLogicalClass(aClassName);
        if (candidateClassSchema != null) {
            mClassQueue.add(candidateClassSchema);
        }

        if (wantSubclasses) {
            Set subclasses = aSchema.getPersistableSubclasses(aClassName);
            Iterator iter = subclasses.iterator();
            while (iter.hasNext()) {
                ClassVersionSchema classVersion = (ClassVersionSchema)iter.next();
                mClassQueue.add( classVersion.getLogicalClassSchema() );
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Verifies that the iterator is open.
     */
    private void checkOpen() throws ODMGRuntimeException
    {
        if (!mIsOpen) {
            throw new ODMGRuntimeException("Iterator is closed.");
        }
    }

    //----------------------------------------------------------------------
    /**
     * Determines if more objects are available from this iterator.
     *
     * @return true if more objects are available, otherwise false.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public boolean hasNext() throws ODMGRuntimeException
    {
        checkOpen();
        if (mCurrentIterator == null || !mCurrentIterator.hasNext()) {
            mCurrentIterator = null;
            while ( !mClassQueue.isEmpty()) {
                LogicalClassSchema classSchema = (LogicalClassSchema)mClassQueue.removeFirst();
                SparseBitSet.Iterator iterator = classSchema.getExtentBitSet().getIterator();
                if (iterator.hasNext()) {
                    mCurrentIterator = iterator;
                    break;
                }
            }

            if (mCurrentIterator == null) {
                return false;
            }
        }

        return mCurrentIterator.hasNext();
    }

    //----------------------------------------------------------------------
    /**
     * Gets, at most, the next N objects from the iterator, where N is aMaxNumObjects.
     *
     * @param aMaxNumObjects the maximum number of objects to be retrieved. Must be >= 1.
     *
     * @return an array of OIDs. This array may be from 1 to
     *  aMaxNumObjects elements in length.
     *
     * @throws ODMGRuntimeException if an error occurs.
     * @throws NoSuchElementException if there are no more objects available from the iterator.
     */
    public long[] next(int aMaxNumObjects) throws ODMGRuntimeException, NoSuchElementException
    {
        if (aMaxNumObjects < 1) {
            throw new IllegalArgumentException("Maximum Number of objects must be >= 1");
        }

        checkOpen();
        if (!hasNext()) {
            throw new NoSuchElementException("Attempted to go past the end of the Extent iterator.");
        }

        long[] oids = new long[aMaxNumObjects];
        int numObjs;
        for (numObjs = 0; numObjs < aMaxNumObjects && hasNext(); numObjs++) {
            // TODO we must unlock a bitset node after getting it. Unlocked nodes must not be kept in the client cache.
            oids[numObjs] = mCurrentIterator.next();
        }

        if (numObjs < oids.length) {
            // Resize the array.
            long[] tmpOIDs = new long[numObjs];
            System.arraycopy(oids, 0, tmpOIDs, 0, numObjs);
            oids = tmpOIDs;
        }

        return oids;
    }

    //----------------------------------------------------------------------
    /**
     * Closes this iterator.
     */
    public void close()
    {
        mIsOpen = false;
    }
}

