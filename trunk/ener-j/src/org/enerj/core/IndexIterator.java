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

package org.enerj.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.enerj.server.DBIterator;
import org.odmg.ODMGRuntimeException;

/**
 * Client-side Index Iterator.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class IndexIterator<T> implements Iterator<T>
{
    private static final int DEFAULT_CHUNK_SIZE = 50;

    private EnerJDatabase database;
    /** DBIterator returned from the session. */
    private DBIterator dbIterator;
    /** Queue of Objects represent the chunk of objects we got back from next(). */
    private Persistable[] objects = null;
    /** Queue position. */
    private int objectIdx = 0;
    private boolean isOpen = true;

    IndexIterator(EnerJDatabase database, DBIterator dbIterator)
    {
        this.database = database;
        this.dbIterator = dbIterator;
    }

    /**
     * Closes this iterator.
     */
    public void close()
    {
        dbIterator.close();
        isOpen = false;
    }

    /**
     * Checks if the iterator is open.
     *
     * @return true if it's open, else false.
     */
    boolean isOpen()
    {
        return isOpen;
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

    public boolean hasNext()
    {
        checkOpen();
        if (objects == null || objectIdx >= objects.length) {
            if (!dbIterator.hasNext()) {
                return false;
            }

            long[] oids = dbIterator.next(DEFAULT_CHUNK_SIZE);
            // Put the objects in the prefetch queue.
            objects = database.getObjectsForOIDs(oids);
            objectIdx = 0;
        }

        return true;
    }

    public T next()
    {
        checkOpen();
        if (!hasNext()) {
            throw new NoSuchElementException("Attempted to go past the end of the Extent iterator.");
        }

        return (T)objects[objectIdx++];
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Cannot remove from an Extent iterator");
    }

}
