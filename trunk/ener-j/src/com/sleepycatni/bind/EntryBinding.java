/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000-2006
 *      Oracle Corporation.  All rights reserved.
 *
 * $Id: EntryBinding.java,v 12.3 2006/08/31 18:14:05 bostic Exp $
 */

package com.sleepycatni.bind;

import com.sleepycatni.db.DatabaseEntry;

/**
 * A binding between a key or data entry and a key or data object.
 *
 * @author Mark Hayes
 */
public interface EntryBinding {

    /**
     * Converts a entry buffer into an Object.
     *
     * @param entry is the source entry buffer.
     *
     * @return the resulting Object.
     */
    Object entryToObject(DatabaseEntry entry);

    /**
     * Converts an Object into a entry buffer.
     *
     * @param object is the source Object.
     *
     * @param entry is the destination entry buffer.
     */
    void objectToEntry(Object object, DatabaseEntry entry);
}
