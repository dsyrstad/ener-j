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
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/ObjectSerializer.java,v 1.3 2006/05/30 19:05:26 dsyrstad Exp $

package org.enerj.core;

import java.util.LinkedList;
import java.util.List;

import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;


/**
 * An abstraction of a worker that persists objects to and from a store. This abstraction
 * is used by Persistable and ObjectSerializer so that serialization and deserialization of 
 * objects is not dependent on a particular database implementation.
 * <p>
 * This class is intended only for Ener-J internal use.
 *
 * @version $Id: Persister.java,v 1.3 2006/05/30 19:05:26 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface Persister
{
    //----------------------------------------------------------------------
    /**
     * Loads the contents of aPersistable from the database. Other objects may be
     * prefetched at the same time. A READ lock is returned on the object.
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param aPersistable the persistable to be loaded.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    void loadObject(Persistable aPersistable);
    
    //----------------------------------------------------------------------
    /**
     * Gets the Persistable object associated with anOID.
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param anOID the database Object ID.
     *
     * @return a hollow Persistable. Returns null if the OID doesn't exist.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    Persistable getObjectForOID(long anOID);

    //----------------------------------------------------------------------
    /**
     * Gets the Persistable objects associated with someOIDs.
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param someOIDs the database Object IDs to be retrieved.
     *
     * @return an array of hollow Persistables. 
     *  An element in the array my be null if the corresponding OID doesn't exist.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    Persistable[] getObjectsForOIDs(long[] someOIDs);
    
    //----------------------------------------------------------------------
    /**
     * Gets the OID for a persistable object. All Ener-J 
     * code should call this method. Application code should use EnerJImplementation.getEnerJObjectId or
     * org.odmg.Implementation.getObjectId.
     * to get the OID. The OID for new and cloned persistable objects is lazily
     * initialized. A call to this method implies that a new or cloned object has been 
     * tied to the persistable object graph and hence an OID should be assigned to it.
     * <p>
     * Code must NOT call {@link Persistable#enerj_GetPrivateOID()}.
     *
     * @param anObject an Object that is a Persistable (a FCO).
     *
     * @return an OID, or ObjectServer.NULL_OID if the object is not persistable, null, or
     *  somehow otherwise transient. ObjectServer.NULL_OID is also returned for new/cloned objects
     *  that have a ObjectServer.NULL_OID OID if a transaction is not active.
     */
    long getOID(Object anObject);

    //--------------------------------------------------------------------------------
    /**
     * Determines whether this persister allows non-transactional (dirty) reads.  
     *
     * @return true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    boolean getAllowNontransactionalReads() throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Add a Persistable to the list of modified objects. The object's lock
     * is upgraded to WRITE. If the Persister is not in a transactional context 
     * (e.g., modified objects would never be written), the object is NOT 
     * added to the modified list.
     *
     * @param aPersistable the object to be added.
     */
    void addToModifiedList(Persistable aPersistable);

    
    //--------------------------------------------------------------------------------
    /**
     * Gets the list of modified objects.
     *
     * @return a reference to the live list of modified objects. Adding a new modified object
     *  affects the returned List.
     */
    List<Persistable> getModifiedList();
    
    
    //--------------------------------------------------------------------------------
    /**
     * Clears the list of modified objects.
     */
    void clearModifiedList();
    
}
