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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/DefaultClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.ref.*;

/**
 * Client-side object cache. Used by EnerJDatabase.
 * This is NOT an LRU cache. Objects only fall off the cache if they've
 * been GCed. This means that the cache can grow from its initial size.
 * EnerJTransaction takes care of holding strong references to objects that 
 * have been modified before they are stored in the database. This prevents modified objects
 * from being GCed before they are stored. 
 * This cache only holds weak references to the cached objects. As a cached object is
 * GCed, it will eventually be cleaned from the cache.
 * 
 * TODO This needs to flush() when the Cache reaches the max size, and then drop the LRU entry.
 *
 * @version $Id: DefaultClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Database
 */
class DefaultClientCache implements ClientCache 
{
    /** Client-side Object Cache. Key is Long(oid), value is CacheWeakReference (whose referent is a 
     * Persistable). 
     */
    private HashMap mCache;

    /** ReferenceQueue for CacheWeakReferences stored in mClientCache. This allows us
     * to clean GCed objects out of the cache. 
     */
    private ReferenceQueue mCacheReferenceQueue = new ReferenceQueue();

    //----------------------------------------------------------------------
    /**
     * Constructs a new client-side object cache of the specified size.
     *
     * @param anInitialSize the initial number of objects that the cache can hold.
     *  The cache will grow if more than this many objects are added. The growth
     *  behaviour is the same as java.util.HashMap.
     */
    DefaultClientCache(int anInitialSize)
    {
        // The calculation below is to offset the effect of the load factor.
        mCache = new HashMap(anInitialSize + (anInitialSize / 3), .75F);
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#add(long, java.lang.Object)
     */
    public void add(long anOID, Object anObject)
    {
        cleanup();
        Long oidKey = new Long(anOID);
        if (!mCache.containsKey(oidKey)) {
            mCache.put(oidKey, new CacheWeakReference(anOID, anObject, mCacheReferenceQueue) );
        }
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#findEntry(long)
     */
    public CacheWeakReference findEntry(long anOID)
    {
        cleanup();
        Long oidKey = new Long(anOID);
        return (CacheWeakReference)mCache.get(oidKey);
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#get(long)
     */
    public Object get(long anOID)
    {
        CacheWeakReference ref = findEntry(anOID);
        if (ref == null) {
            return null;
        }
        
        Object referent = ref.get();
        if (referent == null) {
            // Referent was GCed. Remove the entry and return null.
            mCache.remove( new Long(anOID) );
            return null;
        }
        
        return referent;
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#evict(long)
     */
    public void evict(long anOID)
    {
        cleanup();
        mCache.remove( new Long(anOID) );
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#evictAll()
     */
    public void evictAll()
    {
        mCache.clear();
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#setSavedImage(long, byte[])
     */
    public void setSavedImage(long anOID, byte[] anImage)
    {
        CacheWeakReference ref = findEntry(anOID);
        if (ref == null) {
            return;
        }
        
        ref.setSavedImage(anImage);
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#getAndClearSavedImage(long)
     */
    public byte[] getAndClearSavedImage(long anOID)
    {
        CacheWeakReference ref = findEntry(anOID);
        if (ref == null) {
            return null;
        }
        
        byte[] image = ref.getSavedImage();
        ref.setSavedImage(null);
        return image;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#hollowObjects()
     */
    public void hollowObjects()
    {
        cleanup();
        
        Iterator iterator = mCache.values().iterator();
        while (iterator.hasNext()) {
            CacheWeakReference ref = (CacheWeakReference)iterator.next();
            Persistable persistable = (Persistable)ref.get();
            if (persistable != null) {
                persistable.enerj_Hollow();
            }
        }
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#makeObjectsNonTransactional()
     */
    public void makeObjectsNonTransactional()
    {
        cleanup();
        
        Iterator iterator = mCache.values().iterator();
        while (iterator.hasNext()) {
            CacheWeakReference ref = (CacheWeakReference)iterator.next();
            ref.setSavedImage(null);
            Persistable persistable = (Persistable)ref.get();
            if (persistable != null) {
                PersistableHelper.setNonTransactional(persistable);
           }
        }
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#cleanup()
     */
    public void cleanup()
    {
        CacheWeakReference ref;
        
        while ((ref = (CacheWeakReference)mCacheReferenceQueue.poll()) != null) {
            mCache.remove( new Long( ref.getOID() ) );
        }
    }
}

