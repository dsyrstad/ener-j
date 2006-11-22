/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
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

    /**
     * Constructs an empty CheckpointTransactionLogEntry.
     */
    protected CheckpointTransactionLogEntry()
    {
    }


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


    /**
     * Gets the entry type of this log entry.
     *
     * @return CHECKPOINT_TXN_ENTRY_TYPE.
     */
    public byte getEntryType()
    {
        return CHECKPOINT_TXN_ENTRY_TYPE;
    }


    /**
     * {@inheritDoc}
     */
    protected int loadFromLog(DataInput aDataInput) throws IOException
    {
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        String name = this.getClass().getName();
        return name.substring( name.lastIndexOf('.') + 1 ) + "[txnId=" + getTransactionId() + ']';
    }
}
