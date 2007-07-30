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

package org.enerj.server.bdb;

import java.util.List;
import java.util.NoSuchElementException;

import org.enerj.server.ExtentIterator;
import org.odmg.ODMGRuntimeException;

import com.sleepycatje.bind.tuple.TupleBinding;
import com.sleepycatje.je.Cursor;
import com.sleepycatje.je.DatabaseEntry;
import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.LockMode;
import com.sleepycatje.je.OperationStatus;

/**
 * BDB ExtentIterator implementation.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class BDBExtentIterator implements ExtentIterator
{
    private Cursor cursor;
    /** List of CIDs to iterate over. */
    private List<DatabaseEntry> cidxKeys;
    private int cidxKeyIdx = -1;
    private int currCIDX;
    private DatabaseEntry nextOIDToReturn = null;
    private LockMode lockMode;
    private boolean isOpen = true;
    private BDBObjectServer.Session session;


    /**
     * Constructs a DefaultExtentIterator.
     *
     * @param session the session that owns this extent iterator.
     * @param cursor the cursor to use.
     * @param cidxKeys the CIDXs to be iterated over. These are partial OID keys.
     * @param lockMode the lock mode for the Cursor. May be null.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public BDBExtentIterator(BDBObjectServer.Session session, Cursor cursor, List<DatabaseEntry> cidxKeys, LockMode lockMode) throws ODMGRuntimeException
    {
        this.cursor = cursor;
        this.cidxKeys = cidxKeys;
        this.lockMode = lockMode;
        this.session = session;
    }

    /**
     * Verifies that the iterator is open.
     */
    private void checkOpen() throws ODMGRuntimeException
    {
        if (!isOpen) {
            throw new ODMGRuntimeException("Iterator is closed.");
        }
    }


    public boolean isOpen()
    {
        return isOpen;
    }

    /**
     * Determines if more objects are available from this iterator. Primes nextOIDToReturn if necessary.
     *
     * @return true if more objects are available, otherwise false.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public boolean hasNext() throws ODMGRuntimeException
    {
        checkOpen();
        if (nextOIDToReturn == null) {
            ++cidxKeyIdx;
            DatabaseEntry next = new DatabaseEntry();
            try {
                while (cidxKeyIdx < cidxKeys.size() && cursor.getSearchKeyRange(cidxKeys.get(cidxKeyIdx), next, lockMode) == OperationStatus.NOTFOUND) {
                    ++cidxKeyIdx;
                }
                
                // TOOD SAVE CIDX and compare while iterating
                TupleBinding binding = new BDBObjectServer.OIDKeyTupleBinding(true);
                BDBObjectServer.OIDKey oidKey = (BDBObjectServer.OIDKey)binding.entryToObject(cidxKeys.get(cidxKeyIdx));
                currCIDX = oidKey.cidx;
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error reading extent cursor", e);
            }
            
            if (cidxKeyIdx >= cidxKeys.size()) {
                return false;
            }
            
            nextOIDToReturn = next;
        }

        return true;
    }


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
        // TODO This should return SerializedObjects, or at least oid and ClassInfo
        if (aMaxNumObjects < 1) {
            throw new IllegalArgumentException("Maximum Number of objects must be >= 1");
        }

        checkOpen();
        if (!hasNext()) {
            throw new NoSuchElementException("Attempted to go past the end of the Extent iterator.");
        }

        long[] oids = new long[aMaxNumObjects];
        int numObjs;
        TupleBinding binding = new BDBObjectServer.OIDKeyTupleBinding(true);
       
        for (numObjs = 0; numObjs < aMaxNumObjects && hasNext(); ) {
            DatabaseEntry key = cidxKeys.get(cidxKeyIdx);
            BDBObjectServer.OIDKey oidKey = (BDBObjectServer.OIDKey)binding.entryToObject(key);
            if (oidKey.cidx != currCIDX) {
                nextOIDToReturn = null; // Move to next CID.
                continue;
            }

            oids[numObjs++] = oidKey.getOID();
            try {
                // Prime next OID.
                if (cursor.getNext(key, nextOIDToReturn, lockMode) == OperationStatus.NOTFOUND) {
                    nextOIDToReturn = null; // Move to next CID.
                    continue;
                }
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error reading from extent", e);
            }
        }

        if (numObjs < oids.length) {
            // Resize the array.
            long[] tmpOIDs = new long[numObjs];
            System.arraycopy(oids, 0, tmpOIDs, 0, numObjs);
            oids = tmpOIDs;
        }

        return oids;
    }


    /**
     * Closes this iterator.
     */
    public void close()
    {
        isOpen = false;
        try {
            cursor.close();
        }
        catch (DatabaseException e) {
            throw new ODMGRuntimeException("Error closing cursor", e);
        }
        
        session.removeExtentIterator(this);
    }
}

