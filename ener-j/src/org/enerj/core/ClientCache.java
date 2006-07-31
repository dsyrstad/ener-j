package org.enerj.core;


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
 * @version $Id: ClientCache.java,v 1.3 2006/05/13 21:51:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Database
 */
interface ClientCache
{

    //----------------------------------------------------------------------
    /**
     * Adds an object to the cache. If the OID is already in the cache,
     * the cache is left unchanged.
     *
     * @param anOID the OID of anObject.
     * @param anObject the Object to be cached.
     */
    void add(long anOID, Object anObject);

    //----------------------------------------------------------------------
    /**
     * Finds the CacheWeakReference entry corresponding to anOID.
     * Calls cleanup() prior to the lookup.
     *
     * @param anOID the OID of the desired object.
     *
     * @return the entry, or null if the OID does not exist in the cache.
     */
    CacheWeakReference findEntry(long anOID);

    //----------------------------------------------------------------------
    /**
     * Gets the cached object corresponding to anOID.
     *
     * @param anOID the OID of the desired object.
     *
     * @return the cached object, or null if the OID does not exist in the cache.
     */
    Object get(long anOID);

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

}