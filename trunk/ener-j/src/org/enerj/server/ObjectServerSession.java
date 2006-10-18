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
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $

package org.enerj.server;

import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;

/**
 * Represents a session returned by a ObjectServer. Only one thread may use
 * a session at any given time. Only one top-level transaction may be active on
 * a session at any give time.
 *
 * @version $Id: ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface ObjectServerSession
{
    //----------------------------------------------------------------------
    /**
     * Gets the ObjectServer associated with this session.
     *
     * @return a ObjectServer, or null if this session is disconnected.
     */
    public ObjectServer getObjectServer();

    //----------------------------------------------------------------------
    /**
     * Disconnects from a database.
     * If a transaction is active on session, it is aborted.
     *
     * @throws ODMGException in the event of an error. 
     */
    public void disconnect() throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Request that the server shuts down once all active transactions have closed. The call may return
     * immediately, and the server may shutdown at a later time. However, all connection
     * and new transaction requests will be denied upon return from this method. The session will be
     * disconnected via disconnect().
     *
     * @throws ODMGException in the event of an error. 
     */
    public void shutdown() throws ODMGException;

    //--------------------------------------------------------------------------------
    /**
     * Sets whether the session allows non-transactional (dirty) reads.  
     *
     * @param isNontransactional true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException;
    
    //--------------------------------------------------------------------------------
    /**
     * Determines whether the session allows non-transactional (dirty) reads.  
     *
     * @return true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public boolean getAllowNontransactionalReads() throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Gets the class Id (CID) for a given OID.
     * A transaction must be active on session, or non-transactional reads must be allowed.
     *
     * @param anOID the Object Id.
     *
     * @return the class Id, or zero (the null CID) if the OID doesn't exist.
     *
     * @throws ODMGException in the event of an error. 
     */
    public long getCIDForOID(long anOID) throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Store an object in the database.
     * A transaction must be active on session.
     *
     * @param aCID the Class Id of the object.
     * @param anOID the OID of the object.
     * @param aSerializedObject the object serialized to a byte array. This
     *  array must <em>NOT</em> be reused by the caller after this call completes.
     *  I.e., the caller should allocate a new byte array for each call to this method.
     * @param isNew true if this is a new object in the database.
     *
     * @throws ODMGException in the event of an error.
     */
    public void storeObject(long aCID, long anOID, byte[] aSerializedObject, boolean isNew)
         throws ODMGException;
    
    //----------------------------------------------------------------------
    /**
     * Load an object from the database.
     * A transaction must be active on session, or non-transactional reads must be allowed.
     *
     * @param anOID the OID of the object.
     *
     * @return the object serialized in a byte array.
     *
     * @throws ODMGException in the event of an error. 
     */
    public byte[] loadObject(long anOID) throws ODMGException;
    
    //----------------------------------------------------------------------
    /**
     * Get a block of unused OIDs from the database. No other transaction
     * can use these OIDs while this transaction is active.
     * A transaction must be active on session.
     * <p>
     * Typically if an object is never stored under an
     * OID by the commit of a transaction, the OID is released back to the 
     * unallocated pool. If the transaction is rolled back, all of the OIDs
     * are released.
     *
     * @return an array of unused OIDs. The number of OIDs returned is at least
     *  one, but the maximum number is not defined.
     *
     * @throws ODMGException in the event of an error. 
     */
    public long[] getNewOIDBlock() throws ODMGException;
    
    //----------------------------------------------------------------------
    // Transaction support...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // TODO Note: Nested transactions could be supported by calling this again
    // TODO while a transaction is active.
    /**
     * Begins a transaction. 
     * TODO Nested transactions are not currently supported.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void beginTransaction() throws ODMGRuntimeException;

    //----------------------------------------------------------------------
    /**
     * Commits a transaction.
     * A transaction must be active on session.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void commitTransaction() throws ODMGRuntimeException;

    //----------------------------------------------------------------------
    /**
     * Rolls back (aborts) a transaction.
     * A transaction must be active on session.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void rollbackTransaction() throws ODMGRuntimeException;

    //----------------------------------------------------------------------
    /**
     * Checkpoints a transaction. See the ODMG checkpoint semantics.
     * A transaction must be active on session.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void checkpointTransaction() throws ODMGRuntimeException;

    //----------------------------------------------------------------------
    /**
     * Gets the specified lock on the specified OID. If the OID is already at
     * above the specified lock level, the lock remains at its current level
     * (i.e., the lock is not downgraded and no error occurs). 
     * If the OID is below the requested lock level or it has no lock, its lock is upgraded.
     * A transaction must be active on session, or non-transactional reads must be allowed.
     *
     * @param anOID the OID to be locked.
     * @param aLockLevel one of the org.odmg.Transaction lock modes (READ, UPGRADE, WRITE).
     * @param aWaitTime The length of time in milliseconds to wait for the lock. Zero means
     *  don't wait at all and throw a LockNotGrantedException if the lock cannot be immediately 
     *  obtained. -1 means to to wait lock is granted or until an error (such as deadlock detection) occurs.
     *
     * @throws LockNotGrantedException in the event on an error.
     */
    public void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException;

    //----------------------------------------------------------------------
    // ...Transaction support.
    //----------------------------------------------------------------------
}
