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

package org.enerj.server.bdb;

import java.util.List;
import java.util.NoSuchElementException;

import org.enerj.server.DBIterator;
import org.odmg.ODMGRuntimeException;

import com.sleepycatje.bind.tuple.TupleBinding;
import com.sleepycatje.je.Cursor;
import com.sleepycatje.je.DatabaseEntry;
import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.OperationStatus;

/**
 * BDB DBIterator implementation for extents.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class BDBExtentIterator implements DBIterator
{
    private Cursor cursor;
    /** List of CIDs to iterate over. */
    private List<DatabaseEntry> cidxKeys;
    /** List of CIDXs with 1-1 correspondence with cidxKeys. */
    private List<Integer> cidxs;
    private int cidxKeyIdx = -1;
    private DatabaseEntry nextOIDToReturn = null;
    private boolean isOpen = true;
    private BDBObjectServer.Session session;


    /**
     * Constructs a BDBExtentIterator.
     *
     * @param session the session that owns this extent iterator.
     * @param cursor the cursor to use.
     * @param cidxs a List of CIDXs corresponding to cidxKeys
     * @param cidxKeys the CIDX keys to be iterated over. These are partial OID keys.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public BDBExtentIterator(BDBObjectServer.Session session, Cursor cursor, List<Integer> cidxs, List<DatabaseEntry> cidxKeys) throws ODMGRuntimeException
    {
        this.cursor = cursor;
        this.cidxs = cidxs;
        this.cidxKeys = cidxKeys;
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
            DatabaseEntry next = new DatabaseEntry();
            try {
                for (++cidxKeyIdx; cidxKeyIdx < cidxKeys.size(); ++cidxKeyIdx) {
                    if (cursor.getSearchKeyRange(cidxKeys.get(cidxKeyIdx), next, null) != OperationStatus.NOTFOUND) {
                        // Found one, but make sure CIDX matches.
                        TupleBinding binding = new OIDKeyTupleBinding(true);
                        OIDKey oidKey = (OIDKey)binding.entryToObject(cidxKeys.get(cidxKeyIdx));
                        if (oidKey.cidx == cidxs.get(cidxKeyIdx)) {
                            break;
                        }
                    }
                }
                
                if (cidxKeyIdx >= cidxKeys.size()) {
                    return false;
                }
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error reading extent cursor", e);
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
        TupleBinding binding = new OIDKeyTupleBinding(true);
       
        for (numObjs = 0; numObjs < aMaxNumObjects && hasNext(); ) {
            DatabaseEntry key = cidxKeys.get(cidxKeyIdx);
            OIDKey oidKey = (OIDKey)binding.entryToObject(key);
            oids[numObjs++] = oidKey.getOID();

            try {
                // Prime next OID.
                if (cursor.getNext(key, nextOIDToReturn, null) == OperationStatus.NOTFOUND) {
                    nextOIDToReturn = null; // Move to next CIDX.
                    continue;
                }

                oidKey = (OIDKey)binding.entryToObject(key);
                if (oidKey.cidx != cidxs.get(cidxKeyIdx)) {
                    nextOIDToReturn = null; // Move to next CIDX.
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
        
        session.removeIterator(this);
    }
}

