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
package org.enerj.core;

import java.util.List;


/**
 * Client-side object cache. Used by EnerJDatabase.
 * This is an LRU cache. Objects fall off the cache if they've
 * been GCed or the maximum size is reached.
 * EnerJTransaction takes care of holding strong references to objects that 
 * have been modified before they are stored in the database. This prevents modified objects
 * from being GCed before they are stored. 
 * This cache only holds weak references to the cached objects. As a cached object is
 * GCed, it will eventually be cleaned from the cache.
 *
 * @version $Id: ClientCache.java,v 1.3 2006/05/13 21:51:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Database
 */
public interface ClientCache
{
    //--------------------------------------------------------------------------------
    /**
     * Registers a transaction with the cache. This transaction will be used to flush
     * objects that fall off of the cache. 
     *
     * @param aTxn the transaction. May be null to unset the transaction. 
     */
    public void setTransaction(EnerJTransaction aTxn);

    //----------------------------------------------------------------------
    /**
     * Adds an object to the cache. If the OID is already in the cache,
     * the cache is left unchanged.
     *
     * @param anOID the OID of anObject.
     * @param aPersistable the Persistable to be cached.
     */
    void add(long anOID, Persistable aPersistable);

    //----------------------------------------------------------------------
    /**
     * Gets the cached object corresponding to anOID.
     *
     * @param anOID the OID of the desired object.
     *
     * @return the cached Persistable, or null if the OID does not exist in the cache.
     */
    Persistable get(long anOID);

    //----------------------------------------------------------------------
    /**
     * Evicts a specific entry from the cache.
     *
     * @param anOID the OID to evict.
     */
    void evict(long anOID);

    //----------------------------------------------------------------------
    /**
     * Evicts all entries from the cache.
     */
    void evictAll();

    //----------------------------------------------------------------------
    /**
     * Sets the pre-modification image of this object.
     *
     * @param anOID the OID of the object already in the cache.
     * @param anImage the byte[] representing the image. This may be null to 
     *  clear the saved image.
     */
    void setSavedImage(long anOID, byte[] anImage);

    //----------------------------------------------------------------------
    /**
     * Get the pre-modification image of this object. The image is cleared upon
     * return.
     *
     * @param anOID the OID of the object already in the cache.
     *
     * @return the byte[] representing the image, or null if anOID was not found
     *  or has no saved image.
     */
    byte[] getAndClearSavedImage(long anOID);

    //----------------------------------------------------------------------
    /**
     * Hollow all objects in the cache. The cached objects are preserved.
     */
    void hollowObjects();

    //----------------------------------------------------------------------
    /**
     * Makes all objects in the cache non-transaction, clear any saved image, 
     * and clears the cached lock state. The cached objects are preserved.
     */
    void makeObjectsNonTransactional();

    //----------------------------------------------------------------------
    /**
     * Clean up the cache by removing GCed entries.
     */
    void cleanup();

    
    //--------------------------------------------------------------------------------
    /**
     * Get a list of Persistables to be prefetched. The list of prefetches is cleared.
     *
     * @return a list of hollow Persistables.
     */
    List<Persistable> getAndClearPrefetches();
    
    
    //--------------------------------------------------------------------------------
    /**
     * Clears the prefetch list.
     */
    void clearPrefetches();
}
