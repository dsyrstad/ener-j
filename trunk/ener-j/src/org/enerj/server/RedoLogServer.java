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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/RedoLogServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import org.odmg.*;

import org.enerj.server.logentry.*;

/**
 * Interface for Redo Recovery Log Servers (a.k.a. Write-Ahead Log). <p>
 *
 * Beyond this interface, the following additional requirements
 * are placed on an RedoLogServer implementation:<p>
 *
 * 1. A static factory method "connect" must be defined. The definition
 *    for the method is:<p>
 *
 * public static RedoLogServer connect(String aRedoLogServerURI) throws ODMGException;<p>
 *
 * Parameters:<br> anRedoLogServerURI - a URI identifying the RedoLogServer to open. By the time 
 *  this method is called, the RedoLogServer has already determined the proper
 *  RedoLogServer implementation to use based on the protocol. The URI has the form: <br>
 * vorls://{log-server-class-name}[,args...]<p>
 *
 * Throws: ODMGException in the event of an error. <p>
 *
 * Returns: a RedoLogServer. <p>
 *
 * 2. The RedoLogServer must be thread-safe.<p>
 *
 * The disconnect() method must be used to complete the use of a RedoLogServer.
 * <p>
 * Log entries are found in the logentry subpackage and are derived from the LogEntry base class.
 *
 * @version $Id: RedoLogServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface RedoLogServer
{
    //----------------------------------------------------------------------
    /**
     * Disconnects from a RedoLogServer.
     *
     * @throws ODMGException in the event of an error. 
     */
    public void disconnect() throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Appends an entry to the log. If the log entry is an instance of CommitTransactionLogEntry,
     * CheckpointTransactionLogEntry, or EndDatabaseCheckpointLogEntry, 
     * the log will be physically flushed to disk. Upon return, aLogEntry.getLogPosition()
     * will return the log position where this entry was appended. If aLogEntry
     * is an instance of BeginTransactionLogEntry, a unique transaction id is assigned
     * to the entry (available via aLogEntry.getTransactionId()).
     *
     * @param aLogEntry the LogEntry to append.
     *
     * @throws ODMGException in the event of an error. 
     */
    public void append(LogEntry aLogEntry) throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Returns the position of the first log entry.
     *
     * @return the position of the first log entry.
     *
     * @throws ODMGException in the event of an error. 
     */
    public long getFirstLogEntryPosition() throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Reads an entry to the log.
     *
     * @param aLogPosition the position of the entry in the log.
     *
     * @return a LogEntry. LogEntry.getNextLogEntryPosition() will provide
     *  the position of the next log entry.
     *
     * @throws ODMGException in the event of an error. 
     */
    public LogEntry read(long aLogPosition) throws ODMGException;
}
