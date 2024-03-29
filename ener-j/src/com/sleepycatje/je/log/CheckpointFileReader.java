/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: CheckpointFileReader.java,v 1.26.2.1 2007/02/01 14:49:47 cwl Exp $
 */

package com.sleepycatje.je.log;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.dbi.EnvironmentImpl;

/**
 * CheckpointFileReader searches for root and checkpoint entries.
 */
public class CheckpointFileReader extends FileReader {
    /* Status about the last entry. */
    private boolean isRoot;
    private boolean isCheckpointEnd;
    private boolean isCheckpointStart;

    /**
     * Create this reader to start at a given LSN.
     */
    public CheckpointFileReader(EnvironmentImpl env,
                                int readBufferSize, 
                                boolean forward,
                                long startLsn,
                                long finishLsn,
                                long endOfFileLsn) 
        throws IOException, DatabaseException {

        super(env, readBufferSize, forward, startLsn,
	      null, endOfFileLsn, finishLsn);
    }

    /** 
     * @return true if this is a targetted entry.
     */
    protected boolean isTargetEntry(byte logEntryTypeNumber,
                                    byte logEntryTypeVersion) {
        boolean isTarget = false;
        isRoot = false;
        isCheckpointEnd = false;
        isCheckpointStart = false;
        if (LogEntryType.LOG_CKPT_END.equalsType(logEntryTypeNumber,
						 logEntryTypeVersion)) {
            isTarget = true;
            isCheckpointEnd = true;
        } else if (LogEntryType.LOG_CKPT_START.equalsType(logEntryTypeNumber,
                                                        logEntryTypeVersion)) {
            isTarget = true;
            isCheckpointStart = true;
        } else if (LogEntryType.LOG_ROOT.equalsType(logEntryTypeNumber,
						    logEntryTypeVersion)) {
            isTarget = true;
            isRoot = true;
        }
        return isTarget;
    }
    
    /**
     * This reader instantiate the first object of a given log entry
     */
    protected boolean processEntry(ByteBuffer entryBuffer)
        throws DatabaseException {

        /* Don't need to read the entry, since we just use the LSN. */
        return true;
    }

    /**
     * @return true if last entry was a root entry.
     */
    public boolean isRoot() {
        return isRoot;
    }

    /**
     * @return true if last entry was a checkpoint end entry.
     */
    public boolean isCheckpointEnd() {
        return isCheckpointEnd;
    }

    /**
     * @return true if last entry was a checkpoint start entry.
     */
    public boolean isCheckpointStart() {
        return isCheckpointStart;
    }
}
