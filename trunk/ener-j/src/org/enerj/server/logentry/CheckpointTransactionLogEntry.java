// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/logentry/CheckpointTransactionLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.server.logentry;

import java.io.*;


/** 
 * Log Entry for "Checkpoint Transaction". This is essentially a commit which
 * retains locks. Hence the Begin Transaction position changes to this entry
 * after it is written to the log.
 *
 * @version $Id: CheckpointTransactionLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see RedoLogServer
 */
public class CheckpointTransactionLogEntry extends LogEntry
{
    //----------------------------------------------------------------------
    /**
     * Constructs an empty CheckpointTransactionLogEntry.
     */
    protected CheckpointTransactionLogEntry()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a CheckpointTransactionLogEntry with the given transaction id.
     *
     * @param aTransactionId the transaction id. This id must not conflict with any
     *  pre-existing transaction id in the log.
     */
    public CheckpointTransactionLogEntry(long aTransactionId)
    {
        setTransactionId(aTransactionId);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the entry type of this log entry.
     *
     * @return CHECKPOINT_TXN_ENTRY_TYPE.
     */
    public byte getEntryType()
    {
        return CHECKPOINT_TXN_ENTRY_TYPE;
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected int loadFromLog(DataInput aDataInput) throws IOException
    {
        return 0;
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        String name = this.getClass().getName();
        return name.substring( name.lastIndexOf('.') + 1 ) + "[txnId=" + getTransactionId() + ']';
    }
}
