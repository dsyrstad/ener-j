// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/logentry/RollbackTransactionLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.server.logentry;

import java.io.*;


/** 
 * Log Entry for "Rollback Transaction".
 *
 * @version $Id: RollbackTransactionLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see RedoLogServer
 */
public class RollbackTransactionLogEntry extends LogEntry
{
    //----------------------------------------------------------------------
    /**
     * Constructs an empty RollbackTransactionLogEntry.
     */
    protected RollbackTransactionLogEntry()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a RollbackTransactionLogEntry with the given transaction id.
     *
     * @param aTransactionId the transaction id. This id must not conflict with any
     *  pre-existing transaction id in the log.
     */
    public RollbackTransactionLogEntry(long aTransactionId)
    {
        setTransactionId(aTransactionId);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the entry type of this log entry.
     *
     * @return ROLLBACK_TXN_ENTRY_TYPE.
     */
    public byte getEntryType()
    {
        return ROLLBACK_TXN_ENTRY_TYPE;
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
