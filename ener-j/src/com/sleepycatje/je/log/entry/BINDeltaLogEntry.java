/*
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: BINDeltaLogEntry.java,v 1.23.2.1 2007/02/01 14:49:48 cwl Exp $ 
 */

package com.sleepycatje.je.log.entry;

import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.dbi.DatabaseId;
import com.sleepycatje.je.dbi.EnvironmentImpl;
import com.sleepycatje.je.tree.BINDelta;
import com.sleepycatje.je.tree.IN;

/**
 * A BINDeltaLogEntry knows how to create a whole BIN from a delta entry.
 */
public class BINDeltaLogEntry extends SingleItemEntry
    implements INContainingEntry {

    /**
     * @param logClass
     */
    public BINDeltaLogEntry(Class logClass) {
        super(logClass);
    }

    /* 
     * @see com.sleepycatje.je.log.entry.INContainingEntry#getIN()
     */
    public IN getIN(EnvironmentImpl env) 
    	throws DatabaseException {

        BINDelta delta = (BINDelta) getMainItem();
        return delta.reconstituteBIN(env);
    }

    /*
     * @see com.sleepycatje.je.log.entry.INContainingEntry#getDbId()
     */
    public DatabaseId getDbId() {

        BINDelta delta = (BINDelta) getMainItem();
        return delta.getDbId();	
    }

    /**
     * @return the LSN that represents this IN. For this BINDelta, it's
     * the last full version.
     */
    public long getLsnOfIN(long lastReadLsn) {

        BINDelta delta = (BINDelta) getMainItem();
        return delta.getLastFullLsn();
    }
}
