// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/logentry/BeginTransactionLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.server.logentry;

import java.io.*;

/** 
 * Log Entry for "Begin Transaction".
 *
 * @version $Id: BeginTransactionLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see RedoLogServer
 */
public class BeginTransactionLogEntry extends LogEntry
{
    //----------------------------------------------------------------------
    /**
     * Constructs an empty BeginTransactionLogEntry.
     * The transaction id is assigned by the server when this entry is logged.
     */
    public BeginTransactionLogEntry()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Gets the entry type of this log entry.
     *
     * @return BEGIN_TXN_ENTRY_TYPE.
     */
    public byte getEntryType()
    {
        return BEGIN_TXN_ENTRY_TYPE;
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
