//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/NoOpClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $

package org.enerj.core;

/**
 * A client-cache that doesn't cache anything. <p>
 * 
 * @version $Id: NoOpClientCache.java,v 1.1 2006/05/13 21:51:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class NoOpClientCache implements ClientCache
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
     * @see org.enerj.core.ClientCache#add(long, java.lang.Object)
     */
    public void add(long anOID, Object anObject)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#findEntry(long)
     */
    public CacheWeakReference findEntry(long anOID)
    {
        return null;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#get(long)
     */
    public Object get(long anOID)
    {
        return null;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#evict(long)
     */
    public void evict(long anOID)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#evictAll()
     */
    public void evictAll()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#setSavedImage(long, byte[])
     */
    public void setSavedImage(long anOID, byte[] anImage)
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#getAndClearSavedImage(long)
     */
    public byte[] getAndClearSavedImage(long anOID)
    {
        return null;
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#hollowObjects()
     */
    public void hollowObjects()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#makeObjectsNonTransactional()
     */
    public void makeObjectsNonTransactional()
    {
    }

    //--------------------------------------------------------------------------------
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.ClientCache#cleanup()
     */
    public void cleanup()
    {
    }

}
