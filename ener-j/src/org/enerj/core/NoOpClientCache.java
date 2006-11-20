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
//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/NoOpClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $

package org.enerj.core;

import java.util.Collections;
import java.util.List;

/**
 * A client-cache that doesn't cache anything. <p>
 * 
 * @version $Id: NoOpClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class NoOpClientCache implements PersistableObjectCache
{

    //--------------------------------------------------------------------------------
    /**
     * Construct a NoOpClientCache. 
     *
     */
    NoOpClientCache()
    {
    }

    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#setTransaction(org.enerj.core.EnerJTransaction)
     */
    public void setTransaction(EnerJTransaction aTxn)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#add(long, java.lang.Object)
     */
    public void add(long anOID, Persistable aPersistable)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#findEntry(long)
     */
    public CacheWeakReference findEntry(long anOID)
    {
        return null;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#get(long)
     */
    public Persistable get(long anOID)
    {
        return null;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#evict(long)
     */
    public void evict(long anOID)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#evictAll()
     */
    public void evictAll()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#setSavedImage(long, byte[])
     */
    public void setSavedImage(long anOID, byte[] anImage)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#getAndClearSavedImage(long)
     */
    public byte[] getAndClearSavedImage(long anOID)
    {
        return null;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#hollowObjects()
     */
    public void hollowObjects()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#makeObjectsNonTransactional()
     */
    public void makeObjectsNonTransactional()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#cleanup()
     */
    public void cleanup()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#getAndClearPrefetches()
     */
    public List<Persistable> getAndClearPrefetches()
    {
        return (List<Persistable>)Collections.EMPTY_LIST;
    }


    
    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.PersistableObjectCache#clearPrefetches()
     */
    public void clearPrefetches()
    {
    }
    
}
