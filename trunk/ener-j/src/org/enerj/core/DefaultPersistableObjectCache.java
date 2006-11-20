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

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Persistable object cache.
 * This is not an LRU cache. Objects fall off the cache only if they've
 * been GCed. This is a requirement so that objects with the same OID resolve to the 
 * same instance.
 * The Persister takes care of holding strong references to objects that 
 * have been modified before they are stored in the database. This prevents modified objects
 * from being GCed before they are stored. 
 * This cache only holds weak references to the cached objects. As a cached object is
 * GCed, it will eventually be cleaned from the cache.
 * 
 * @version $Id: DefaultClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Database
 */
public class DefaultPersistableObjectCache implements PersistableObjectCache 
{
    /** Client-side Object Cache. Key is Long(oid), value is CacheWeakReference (whose referent is a 
     * Persistable). List order is by most recently accessed. 
     */
    private LinkedHashMap<Long, CacheWeakReference> mCache;
    /** A weakly referenced list of Persistables that have not been loaded yet, but probably will be. This
     * is used to do pre-fetching.
     */
    private List<CacheWeakReference> mPrefetchList;

    /** ReferenceQueue for CacheWeakReferences stored in mClientCache. This allows us
     * to clean GCed objects out of the cache. 
     */
    private ReferenceQueue mCacheReferenceQueue = new ReferenceQueue();
    
    /** Transaction registered with this cache, if any. */
    private EnerJTransaction mTxn = null;

    //----------------------------------------------------------------------
    /**
     * Constructs a new client-side object cache of the specified size.
     *
     * @param anInitialSize the initial number of objects that the cache can hold.
     *  The cache will grow if more than this many objects are added. The growth
     *  behaviour is the same as java.util.HashMap.
     */
    public DefaultPersistableObjectCache(int anInitialSize)
    {
        // The calculation below is to offset the effect of the load factor.
        mCache = new LinkedHashMap<Long, CacheWeakReference>(anInitialSize + (anInitialSize / 3), .75F, true);

        mPrefetchList = new ArrayList<CacheWeakReference>(anInitialSize / 4); 
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Registers a transaction with the cache. This transaction will be used to flush
     * objects that fall off of the cache. 
     *
     * @param aTxn the transaction. May be null to unset the transaction. 
     */
    public void setTransaction(EnerJTransaction aTxn)
    {
        mTxn = aTxn;
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#add(long, java.lang.Object)
     */
    public void add(long anOID, Persistable aPersistable)
    {
        cleanup();
        
        if (!mCache.containsKey(anOID)) {
            CacheWeakReference weakRef = new CacheWeakReference(anOID, aPersistable, mCacheReferenceQueue);
            mCache.put(anOID, weakRef);
            if (!aPersistable.enerj_IsNew() && !aPersistable.enerj_IsLoaded()) {
                // Non-new Persistable that has not been loaded yet. It is a prefetch candidate.
                mPrefetchList.add(weakRef);
            }
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Finds the CacheWeakReference entry corresponding to anOID.
     * Calls cleanup() prior to the lookup.
     *
     * @param anOID the OID of the desired object.
     *
     * @return the entry, or null if the OID does not exist in the cache.
     */
    private CacheWeakReference findEntry(long anOID)
    {
        cleanup();
        return (CacheWeakReference)mCache.get(anOID);
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#get(long)
     */
    public Persistable get(long anOID)
    {
        CacheWeakReference ref = findEntry(anOID);
        if (ref == null) {
            return null;
        }
        
        Persistable referent = (Persistable)ref.get();
        if (referent == null) {
            // Referent was GCed. Remove the entry and return null.
            mCache.remove(anOID);
            return null;
        }
        
        return referent;
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#evict(long)
     */
    public void evict(long anOID)
    {
        cleanup();
        mCache.remove(anOID);
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#evictAll()
     */
    public void evictAll()
    {
        mCache.clear();
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#setSavedImage(long, byte[])
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
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#getAndClearSavedImage(long)
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
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#hollowObjects()
     */
    public void hollowObjects()
    {
        cleanup();
        
        for (CacheWeakReference ref : mCache.values()) {
            Persistable persistable = (Persistable)ref.get();
            if (persistable != null) {
                persistable.enerj_Hollow();
            }
        }
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#makeObjectsNonTransactional()
     */
    public void makeObjectsNonTransactional()
    {
        cleanup();
        
        for (CacheWeakReference ref : mCache.values()) {
            ref.setSavedImage(null);
            Persistable persistable = (Persistable)ref.get();
            if (persistable != null) {
                PersistableHelper.setNonTransactional(persistable);
           }
        }
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#cleanup()
     */
    public void cleanup()
    {
        CacheWeakReference ref;
        
        while ((ref = (CacheWeakReference)mCacheReferenceQueue.poll()) != null) {
            mCache.remove(ref.getOID());
        }
    }
    
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#getAndClearPrefetches()
     */
    public List<Persistable> getAndClearPrefetches()
    {
        cleanup();
        List<Persistable> prefetches = new ArrayList<Persistable>(mPrefetchList.size());
        for (CacheWeakReference ref : mPrefetchList) {
            Persistable obj = (Persistable)ref.get();
            if (obj != null && !obj.enerj_IsLoaded() && mCache.containsKey(obj.enerj_GetPrivateOID())) {
                prefetches.add(obj);
            }
        }
        
        clearPrefetches();

        return prefetches;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#clearPrefetches()
     */
    public void clearPrefetches()
    {
        mPrefetchList.clear();
    }
}

