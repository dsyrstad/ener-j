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

import java.util.NoSuchElementException;

import org.enerj.core.GenericKey;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.Persister;
import org.enerj.core.PersisterRegistry;
import org.enerj.server.DBIterator;
import org.odmg.ODMGRuntimeException;

import com.sleepycatje.bind.tuple.TupleBinding;
import com.sleepycatje.je.DatabaseEntry;
import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.OperationStatus;
import com.sleepycatje.je.SecondaryCursor;

/**
 * BDB DBIterator implementation for indexes.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class BDBIndexIterator implements DBIterator
{
    private SecondaryCursor cursor;
    private boolean isOpen = true;
    private BDBObjectServer.Session session;
    private GenericKey startKey;
    private GenericKey endKey;
    // Current OID key in the iteration.
    private DatabaseEntry currKey = null;
    private boolean exhusted = false;


    /**
     * Constructs a BDBIndexIterator.
     *
     * @param session the session that owns this iterator.
     * @param cursor the cursor to use.
     * @param startKey the starting key, inclusive. May be null to start at the first key.
     * @param endKey the ending key, inclusive. May be null to iterator through the last key in the index.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public BDBIndexIterator(BDBObjectServer.Session session, SecondaryCursor cursor, GenericKey startKey, GenericKey endKey) throws ODMGRuntimeException
    {
        this.cursor = cursor;
        this.session = session;
        this.startKey = startKey;
        this.endKey = endKey;
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
     * Determines if the given key is between startKey and endKey, inclusive.
     *
     * @return true if the key is in range, else false.
     */
    private boolean isKeyInRange(DatabaseEntry key)
    {
        // Short-cut to eliminate de-serialization.
        if (startKey == null && endKey == null) {
            return true;  
        }
        
        Persister persister = PersisterRegistry.getCurrentPersisterForThread();
        GenericKey genericKey = new GenericKey();
        PersistableHelper.loadSerializedImage(persister, (Persistable)genericKey, key.getData());

        if (startKey != null) {
            if (genericKey.compareTo(startKey) < 0) {
                return false;
            }
        }

        if (endKey != null) {
            if (genericKey.compareTo(endKey) > 0) {
                return false;
            }
        }
        
        return true;
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

        if (exhusted) {
            return false;
        }
        
        if (currKey == null) {
            try {
                // Prime the iterator
                OperationStatus status;
                DatabaseEntry searchKey; 
                if (startKey == null) {
                    searchKey = new DatabaseEntry();
                    status = cursor.getFirst(searchKey, currKey, new DatabaseEntry(), null);
                }
                else {
                    byte[] keyBytes = PersistableHelper.createSerializedImage((Persistable)(Object)startKey);
                    searchKey = new DatabaseEntry(keyBytes);
                    status = cursor.getSearchKeyRange(searchKey, currKey, new DatabaseEntry(), null);
                }
                
                return status != OperationStatus.NOTFOUND && isKeyInRange(searchKey);
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error reading extent cursor", e);
            }
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
        if (aMaxNumObjects < 1) {
            throw new IllegalArgumentException("Maximum Number of objects must be >= 1");
        }

        checkOpen();
        if (!hasNext()) {
            throw new NoSuchElementException("Attempted to go past the end of the iterator.");
        }

        long[] oids = new long[aMaxNumObjects];
        int numObjs;
        TupleBinding binding = new OIDKeyTupleBinding(true);
       
        for (numObjs = 0; numObjs < aMaxNumObjects && hasNext(); ) {
            DatabaseEntry key = currKey;
            OIDKey oidKey = (OIDKey)binding.entryToObject(key);
            oids[numObjs++] = oidKey.getOID();

            try {
                // Prime next OID.
                DatabaseEntry searchKey = new DatabaseEntry();
                OperationStatus status = cursor.getNext(searchKey, currKey, new DatabaseEntry(), null);
                if (status == OperationStatus.NOTFOUND || !isKeyInRange(searchKey)) {
                    exhusted = true;
                    break;
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

