/*
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: INContainingEntry.java,v 1.16.2.1 2007/02/01 14:49:48 cwl Exp $ 
 */

package com.sleepycatje.je.log.entry;

import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.dbi.DatabaseId;
import com.sleepycatje.je.dbi.EnvironmentImpl;
import com.sleepycatje.je.tree.IN;

/**
 * An INContainingEntry is a log entry that contains internal nodes.
 */
public interface INContainingEntry {
	
    /**
     * @return the IN held within this log entry.
     */
    public IN getIN(EnvironmentImpl env) 
        throws DatabaseException;
	
    /**
     * @return the database id held within this log entry.
     */
    public DatabaseId getDbId();

    /**
     * @return the LSN that represents this IN.
     */
    public long getLsnOfIN(long lastReadLsn);
}
