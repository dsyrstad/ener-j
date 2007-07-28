/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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

package org.enerj.server;

import org.enerj.core.IndexAlreadyExistsException;
import org.enerj.core.IndexSchema;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Schema;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNotPersistentException;

/**
 * Represents a session returned by a ObjectServer. Only one thread may use
 * a session at any given time. Only one top-level transaction may be active on
 * a session at any give time.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface ObjectServerSession
{

    /**
     * Gets the ObjectServer associated with this session.
     *
     * @return a ObjectServer, or null if this session is disconnected.
     */
    public ObjectServer getObjectServer();


    /**
     * Disconnects from a database.
     * If a transaction is active on session, it is aborted.
     *
     * @throws ODMGException in the event of an error. 
     */
    public void disconnect() throws ODMGException;


    /**
     * Request that the server shuts down once all active transactions have closed. The call may return
     * immediately, and the server may shutdown at a later time. However, all connection
     * and new transaction requests will be denied upon return from this method. The session will be
     * disconnected via disconnect().
     *
     * @throws ODMGException in the event of an error. 
     */
    public void shutdown() throws ODMGException;


    /**
     * Sets whether the session allows non-transactional (dirty) reads.  
     *
     * @param isNontransactional true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException;
    

    /**
     * Determines whether the session allows non-transactional (dirty) reads.  
     *
     * @return true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public boolean getAllowNontransactionalReads() throws ODMGException;


    /**
     * Gets ClassInfos for the given OIDs.
     * A transaction must be active on session, or non-transactional reads must be allowed.
     *
     * @param someOIDs an array of OIDs to get ClassInfos for. Note that if any element
     *  of this array is {@link ObjectSerializer#NULL_OID}, the corresponding ClassInfo will
     *  be null.
     *
     * @return the ClassInfo. A null ClassInfo element will be returned if an OID doesn't exist.
     *
     * @throws ODMGException in the event of an error. 
     */
    public ClassInfo[] getClassInfoForOIDs(long[] someOIDs) throws ODMGException;


    /**
     * Stores some objects in the database.
     * A transaction must be active on session. A WRITE lock is forced on an 
     * object if it isn't WRITE locked already.
     *
     * @param someObjects an array of {@link SerializedObject}.
     *
     * @throws ODMGException in the event of an error.
     */
    public void storeObjects(SerializedObject[] someObjects) throws ODMGException;
    

    /**
     * Loads objects from the database.
     * A transaction must be active on session, or non-transactional reads must be allowed.
     * READ locks are automatically taken on the objects.
     *
     * @param someOIDs an array of OIDs of the objects to be loaded.
     *
     * @return an array of byte[] of the same size and in the same order as someOIDs that represent 
     *  the serialized images of the objects.
     *
     * @throws ODMGException in the event of an error. 
     */
    public byte[][] loadObjects(long[] someOIDs) throws ODMGException;
    

    /**
     * Get a block of unused Object Indexes from the database. No other transaction
     * can use these OIDXs while this transaction is active.
     * A transaction must be active on session.
     * <p>
     * Typically if an object is never stored under an
     * OIDX by the commit of a transaction, the OIDX is released back to the 
     * unallocated pool. If the transaction is rolled back, all of the OIDXs
     * are released.
     * 
     * @param anOIDXCount the number of OIDXs to get.
     *
     * @return an array of unused OIDXs.
     *
     * @throws ODMGException in the event of an error. 
     */
    public long[] getNewOIDXBlock(int anOIDXCount) throws ODMGException;
    

    // Transaction support...

    // TODO Note: Nested transactions could be supported by calling this again
    // TODO while a transaction is active.
    /**
     * Begins a transaction. 
     * TODO Nested transactions are not currently supported.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void beginTransaction() throws ODMGRuntimeException;


    /**
     * Commits a transaction.
     * A transaction must be active on session.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void commitTransaction() throws ODMGRuntimeException;


    /**
     * Rolls back (aborts) a transaction.
     * A transaction must be active on session.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void rollbackTransaction() throws ODMGRuntimeException;


    /**
     * Checkpoints a transaction. See the ODMG checkpoint semantics.
     * A transaction must be active on session.
     *
     * @throws ODMGRuntimeException in the event on an error.
     */
    public void checkpointTransaction() throws ODMGRuntimeException;


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


    // ...Transaction support.



    /**
     * Associate a name with an object and make it persistent.
     * An object instance may be bound to more than one name.
     * Binding a previously transient object to a name makes that object persistent.
     *
     * @param anOID The OID of the object to be named.
     * @param aName The name to be given to the object.
     *
     * @throws org.odmg.ObjectNameNotUniqueException If an attempt is made to bind a name
     * to an object and that name is already bound to an object.
     */
    public void bind(long anOID, String aName) throws ObjectNameNotUniqueException;
    

    /**
     * Lookup an object via its name.
     *
     * @param aName The name of an object.
     *
     * @return The OID of the object corresponding to aName.
     *
     * @throws ObjectNameNotFoundException If there is no object with the specified name.
     */
    public long lookup(String aName) throws ObjectNameNotFoundException;
    

    /**
     * Disassociate a name with an object
     *
     * @param aName The name of an object.
     *
     * @throws ObjectNameNotFoundException If no object by aName exists in the database.
     */
    public void unbind(String aName) throws ObjectNameNotFoundException;


    /**
     * Removes an object from the extent and any indexes. Does not necessarily cause
     * it to be garbage collected.
     *
     * @param anOID the OID of the object to be removed.
     *
     * @throws ObjectNotPersistentException if the object does not exist in the extent.
     */
    public void removeFromExtent(long anOID) throws ObjectNotPersistentException;
    

    /**
     * Determines the number of objects in an Extent.
     *
     * @param aClassName the class name to iterate over. If wantSubclasses is true,
     *  then aClassName does not have to be a persistable class.
     *
     * @param wantSubclasses if true, the sizes of all subclasses of aClassName are also included in the result.
     *
     * @return the number of objects in the Extent.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException;
    

    /**
     * Creates an ExtentIterator.
     *
     * @param aClassName the class name to iterate over. If wantSubclasses is true,
     *  then aClassName does not have to be a persistable class.
     *
     * @param wantSubclasses if true, all subclasses of aClassName are also included in the iterator.
     *
     * @return an ExtentIterator used to iterate over the extent.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public ExtentIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException;
    
    /**
     * Gets the schema for the database.
     *
     * @return the Schema.
     * 
     * @throws ODMGException if an error occurs (e.g., the schema does not exist).
     */
    public Schema getSchema() throws ODMGException;
    
    /**
     * Adds a new ClassVersion to the schema if it doesn't already exist.
     * 
     * @param aClassName
     * @param aCID the class Id of this class.
     * @param someSuperTypeNames  Array of superclass and superinterface names that go all of the way up the hierarchy.
     *  Class names might not be Persistable in our schema.
     * @param anOriginalBytecodeDef the original, unenhanced bytecodes for the class.
     * @param somePersistentFieldNames the names of the persistent fields of the class.
     * @param someTransientFieldNames the names of the transient fields of the class.
     * 
     * @throws ODMGException if an error occurs.
     */
    public void addClassVersionToSchema(String aClassName, long aCID, String[] someSuperTypeNames, 
                    byte[] anOriginalByteCodeDef, String[] somePersistentFieldNames, 
                    String[] someTransientFieldNames) throws ODMGException;
    
    /**
     * Adds an index to the schema.
     *
     * @param aClassName the name of the class to add the index to.
     * @param anIndexSchema the schema for the index. The name of the index must be unique within the
     *  indexes defined for the class.
     * 
     * @throws ODMGException
     * @throws IndexAlreadyExistsException if the index already exists.
     */
    public void addIndex(String aClassName, IndexSchema anIndexSchema) throws ODMGException, IndexAlreadyExistsException;
}

