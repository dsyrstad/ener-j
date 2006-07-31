// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/CachedObjectSource.java,v 1.3 2006/01/20 01:33:38 dsyrstad Exp $

package com.vscorp.ui.model;

import java.util.*;

/**
 * Remote object cache. Provides a object cache for slow ObjectSources. The primary intent is to cache
 * object returned from a server component, but this class is generally useful for
 * any type of object.<p>
 * <p>
 * Objects in the cache are represented as an ObjectStore. Objects may be
 * accessed by index (like a linear array), or by an object Id.<p>
 * <p>
 * This cache will hold onto objects for a period of time even if they're no
 * longer referenced by the application. This is important so that the object
 * is locally available if it is referenced again by index or object ID.
 * However, this means that you'll need to explictly evict the entire cache
 * with evictCache(), or dereference the CachedObjectSource itself to get rid
 * of objects you don't need anymore.
 * <p>
 * 
 * @version $Id: CachedObjectSource.java,v 1.3 2006/01/20 01:33:38 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CachedObjectSource extends BaseObjectSource
        implements ObjectSourceListener
{
    /** Head of MRU (Most Recently Used) list of cached objects. */
    private CachedObject mMRUObject;

    /** Tail of MRU (Most Recently Used) list of cached objects (the LRU object). */
    private CachedObject mLRUObject;

    /** The base object source that we wrap */
    private ObjectSource mObjectSource;

    /** Cached size of the source. -1 = not cached yet. */
    private int mBaseSize = -1;

    /**
     * HashMap of objects by Object Id. Not every object in this map is in
     * mIndexMap because we don't always know the index. However, every cached
     * object is in this map.
     */
    private HashMap mObjectIdMap;

    /** HashMap of objects by index. Every object in this map is in mObjectIdMap. */
    private HashMap mIndexMap;

    /** Maximum size of cache */
    private int mMaxCacheSize;

    /** Up to this many objects may be retrieved from mObjectSource in a single call */
    private int mChunkSize;

    //----------------------------------------------------------------------
    /**
     * Constructor for sub-classes. Does nothing. Expects that setIObjectSource
     * will be called by the sub-class constructor.
     */
    protected CachedObjectSource()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct with a subservient ObjectSource. The default retrieval "chunk"
     * size is aMaxCacheSize / 4.
     *
     * @param anObjectSource the ObjectSource which actually provides the
     * objects.
     *
     * @param aMaxCacheSize the maximum number of objects to be cached.
     *
     * @throws ObjectSourceException if there is an error accessing anObjectSource
     */
    public CachedObjectSource(ObjectSource anObjectSource, int aMaxCacheSize)
            throws ObjectSourceException
    {
        setObjectSource(anObjectSource, aMaxCacheSize, aMaxCacheSize / 4);
    }

    //----------------------------------------------------------------------
    /**
     * Construct with a subservient ObjectSource.
     *
     * @param anObjectSource the ObjectSource which actually provides the
     * objects.
     *
     * @param aMaxCacheSize the maximum number of objects to be cached.
     *
     * @param aChunkSize this many contiguous objects can be retrieved if
     * cache is "missed". Must be less than or equal to aMaxCacheSize.
     *
     * @throws ObjectSourceException if there is an error accessing anObjectSource
     *  or if aChunkSize is greater than aMaxCacheSize.
     */
    public CachedObjectSource(ObjectSource anObjectSource, int aMaxCacheSize,
                                int aChunkSize)
            throws ObjectSourceException
    {
        setObjectSource(anObjectSource, aMaxCacheSize, aChunkSize);
    }

    //----------------------------------------------------------------------
    /**
     * Sets the subservient ObjectSource and evicts the current cache.
     *
     * @param anObjectSource the ObjectSource which actually provides the
     * objects.
     *
     * @param aMaxCacheSize the maximum number of objects to be cached.
     *
     * @param aChunkSize this many contiguous objects can be retrieved if
     * cache is "missed". Must be less than or equal to aMaxCacheSize.
     *
     * @throws ObjectSourceException if there is an error accessing anObjectSource
     *  or if aChunkSize is greater than aMaxCacheSize.
     */
    public void setObjectSource(ObjectSource anObjectSource, int aMaxCacheSize,
                                 int aChunkSize)
            throws ObjectSourceException
    {
        if (anObjectSource == null) {
            throw new ObjectSourceException("Null source");
        }

        // Unregister ourself as a listener from the old source
        if (mObjectSource != null) {
            mObjectSource.removeObjectSourceListener(this);
        }

        mObjectSource = anObjectSource;
        mMaxCacheSize = aMaxCacheSize;
        mChunkSize = aChunkSize;
        if (mChunkSize < 1) {
            mChunkSize = 1;
        }

        if (mChunkSize > mMaxCacheSize) {
            throw new ObjectSourceException("Chunk size " + mChunkSize +
                        " is greater than cache size " + mMaxCacheSize);
        }

        createCacheSpace();
        // Add ourself as a listener to the base source so that we can
        // manage the cache and propagate the event to our listeners
        mObjectSource.addObjectSourceListener(this);
   }

    //----------------------------------------------------------------------
    /**
     * Gets the subservient ObjectSource.
     *
     * @return the subservient ObjectSource
     */
    public ObjectSource getObjectSource()
    {
        return mObjectSource;
    }

    //----------------------------------------------------------------------
    // From ObjectSourceListener...
    public void notifyObjectSourceChanged(ObjectSourceEvent anEvent)
    {
        // Evict changed objects out of the cache and then
        // pass on the same notification to our listeners
        int eventType = anEvent.getType();
        Object objectId = anEvent.getObjectId();
        int startIndex = anEvent.getStartIndex();
        int endIndex = anEvent.getEndIndex();

        switch (eventType) {
        case ObjectSourceEvent.CONTENTS_CHANGED:
            evictCache();
            this.fireObjectSourceContentsChanged();
            break;

        case ObjectSourceEvent.OBJECT_ADDED:
            // Not in cache yet, nothing to evict, size changed
            mBaseSize = -1;
            this.fireObjectSourceObjectAdded(objectId);
            break;

        case ObjectSourceEvent.OBJECT_CHANGED:
            evictObject(objectId);
            this.fireObjectSourceObjectChanged(objectId);
            break;

        case ObjectSourceEvent.OBJECT_DELETED:
            // Entire index range could have changed, evict the whole thing
            evictCache();
            this.fireObjectSourceObjectDeleted(objectId);
            break;

        case ObjectSourceEvent.RANGE_ADDED:
            // Entire index range could have changed, evict the whole thing
            evictCache();
            this.fireObjectSourceRangeAdded(startIndex, endIndex);
            break;

        case ObjectSourceEvent.RANGE_CHANGED:
            evictObjects(startIndex, endIndex);
            this.fireObjectSourceRangeChanged(startIndex, endIndex);
            break;

        case ObjectSourceEvent.RANGE_DELETED:
            // Entire index range could have changed, evict the whole thing
            evictCache();
            this.fireObjectSourceRangeDeleted(startIndex, endIndex);
            break;
        }
    }

    //----------------------------------------------------------------------
    /**
     * Evicts all objects from the cache.
     */
    public void evictCache()
    {
        createCacheSpace();
    }

    //----------------------------------------------------------------------
    /**
     * Evicts a specific CachedObject from the cache.
     *
     * @param aCachedObject the object to be evicted from the cache. This may be
     *   null, in which case nothing happens.
     */
    private void evictCachedObject(CachedObject aCachedObject)
    {
        // Ignore null objects
        if (aCachedObject == null) {
            return;
        }

        // Remove it from the MRU list.
        unlinkFromMRU(aCachedObject);

        // Remove LRU from maps.
        if (aCachedObject.mIndex != null) {
            mIndexMap.remove(aCachedObject.mIndex);
        }

        mObjectIdMap.remove(aCachedObject.mObjectId);
    }

    //----------------------------------------------------------------------
    /**
     * Evicts a specific object id from the cache.
     *
     * @param anObjectId the object id of the object to be evicted from the cache
     */
    public void evictObject(Object anObjectId)
    {
        evictCachedObject( (CachedObject)mObjectIdMap.get(anObjectId) );
    }

    //----------------------------------------------------------------------
    /**
     * Evicts an indexed object from the cache.
     *
     * @param anIndex the index of the object to be evicted
     */
    public void evictObject(int anIndex)
    {
        evictCachedObject( (CachedObject)mIndexMap.get( new MutableInteger(anIndex) ) );
    }

    //----------------------------------------------------------------------
    /**
     * Evicts a range of objects from the cache.
     *
     * @param aStartIndex the starting index of objects to be evicted
     * @param anEndIndex the ending index of objects to be evicted
     */
    public void evictObjects(int aStartIndex, int anEndIndex)
    {
        MutableInteger index  = new MutableInteger(0);
        for (int i = aStartIndex; i <= anEndIndex; i++) {
            index.setValue(i);
            evictCachedObject( (CachedObject)mIndexMap.get(index) );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Creates all of the cache data structures
     */
    private void createCacheSpace()
    {
        mMRUObject = null;
        mLRUObject = null;
        float loadFactor = .75F;
        int mapSize = (int)(mMaxCacheSize / loadFactor) + 1;
        mObjectIdMap = new HashMap(mapSize, loadFactor);
        mIndexMap = new HashMap(mapSize, loadFactor);
        mBaseSize = -1;
    }

    //----------------------------------------------------------------------
    /**
     * Add an CachedObject to the head of the MRU list.
     *
     * @param aCachedObject the object to be added
     */
    private void addToMRU(CachedObject aCachedObject)
    {
        aCachedObject.mPrev = null;
        aCachedObject.mNext = mMRUObject;
        
        // If there was a MRU, its prev should be null.  Since the MRU is 
        // changing, need to change the prev to point to the new MRU
        if (null != mMRUObject)
        {
            mMRUObject.mPrev = aCachedObject;
        }
        
        mMRUObject = aCachedObject;
        if (mLRUObject == null) {
            mLRUObject = aCachedObject;
        }
    }

    //----------------------------------------------------------------------
    /**
     * Unlink a CachedObject from the MRU list.
     *
     * @param aCachedObject the object to be unlinked
     */
    private void unlinkFromMRU(CachedObject aCachedObject)
    {
        if (aCachedObject.mPrev != null) {
            aCachedObject.mPrev.mNext = aCachedObject.mNext;
        }

        if (aCachedObject.mNext != null) {
            aCachedObject.mNext.mPrev = aCachedObject.mPrev;
        }

        // Unlink from head?
        if (aCachedObject == mMRUObject) {
            mMRUObject = aCachedObject.mNext;
        }

        // Unlink from tail?
        if (aCachedObject == mLRUObject) {
            mLRUObject = aCachedObject.mPrev;
        }

        aCachedObject.mPrev = null;
        aCachedObject.mNext = null;
    }

    //----------------------------------------------------------------------
    /**
     * Unlink the LRU CachedObject from the MRU list.
     *
     * @param aCachedObject the object to be unlinked
     *
     * @return the least recently used cached object.
     */
    private CachedObject unlinkLRUObject()
    {
        CachedObject returnObject = mLRUObject;
        unlinkFromMRU(mLRUObject);
        return returnObject;
    }

    //----------------------------------------------------------------------
    /**
     * Checks to see if anObject is cached. If it's not, then it will be
     * cached. In the process of adding new objects to the cache, LRU objects
     * may be removed. This does not make the object most recently used if
     * it was already cached.
     *
     * @param anObject the object that desired to be cached
     * @param anIndex the index of the object, null if not known.
     *
     * @return the CachedObject representing anObject.
     */
    private CachedObject validateObject(Object anObject, MutableInteger anIndex)
    {
        Object objectId = mObjectSource.getObjectId(anObject);

        // Check the object id map first -- it has every object.
        CachedObject cachedObject = (CachedObject)mObjectIdMap.get(objectId);
        // Found it...
        if (cachedObject != null) {
            // Set index now if not set before, and add to index map
            if (cachedObject.mIndex == null) {
                cachedObject.mIndex = anIndex;
                mIndexMap.put(anIndex, cachedObject);
            }

            return cachedObject;
        } // End if already in cache

        // At this point, anObject is not in the cache.
        cachedObject = new CachedObject(anObject, anIndex, objectId);

        // Dump Least Recently Used (LRU) object if the cache is full
        if (mObjectIdMap.size() >= this.mMaxCacheSize) {
            CachedObject lruObject = unlinkLRUObject();

            // Remove LRU from maps.
            if (lruObject.mIndex != null) {
                mIndexMap.remove(lruObject.mIndex);
            }

            mObjectIdMap.remove(lruObject.mObjectId);
        } // End if cache overflow

        // Add object to the maps. Index map is only update if we have an index.
        mObjectIdMap.put(cachedObject.mObjectId, cachedObject);
        if (anIndex != null) {
            mIndexMap.put(anIndex, cachedObject);
        }

        // Add it to the MRU list.
        addToMRU(cachedObject);

        return cachedObject;
    }

    //----------------------------------------------------------------------
    // Methods from ObjectSource...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // From ObjectSource...
    public int size() throws ObjectSourceException
    {
        if (mBaseSize == -1) {
            mBaseSize = mObjectSource.size();
        }

        return mBaseSize;
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public Object get(int anIndex)
        throws ObjectSourceException, ArrayIndexOutOfBoundsException
    {
        if (anIndex < 0 || anIndex >= size()) {
            throw new ArrayIndexOutOfBoundsException(
                "The specified index " + anIndex + " is not in the range [0.." +
                size() + "]");
        }

        // Is it in the index map?
        MutableInteger indexKey = new MutableInteger(anIndex);
        CachedObject cachedObject = (CachedObject)mIndexMap.get(indexKey);

        // Found it.
        if (cachedObject != null) {
            // Make it MRU and return it
            unlinkFromMRU(cachedObject);
            addToMRU(cachedObject);
            return cachedObject.mObject;
        }

        // The object at anIndex is not in the index map at this point.
        // Since we'll have to make a trip to the server, get a chunk of
        // objects at time. Try to center anIndex in the middle of mChunkSize.
        int startIndex = anIndex - (mChunkSize / 2);
        if (startIndex < 0) {
            startIndex = 0;
        }

        int endIndex = startIndex + mChunkSize - 1;
        if (endIndex >= size()) {
            endIndex = size() - 1;
        }

        // Try to find the largest contiguous set of non-cached objects in the
        // chunk range. We start at anIndex looking forwards and backwards.
        // Let's say startIndex to endIndex looks like:
        //             4  5  6  7  8  9 10 11 12 13 14
        // anIndex is:                I
        // Cached:           X                 X     X
        // What we get:         G  G  G  G  G

        // Go Backwards
        int fetchStartIndex = anIndex - 1;
        if (fetchStartIndex < 0) {
            fetchStartIndex = 0;
        }

        MutableInteger testKey = new MutableInteger(0);
        for (; fetchStartIndex >= startIndex;  --fetchStartIndex) {
            testKey.setValue(fetchStartIndex);
            if (mIndexMap.get(testKey) != null) {
                // Found one.
                break;
            }
        }

        ++fetchStartIndex;

        // Go Forwards
        int fetchEndIndex = anIndex + 1;
        for (; fetchEndIndex <= endIndex; ++fetchEndIndex) {
            testKey.setValue(fetchEndIndex);
            if (mIndexMap.get(testKey) != null) {
                // Found one.
                break;
            }
        }

        --fetchEndIndex;

        Object[] objects = new Object[ (fetchEndIndex - fetchStartIndex) + 1 ];
        mObjectSource.get(fetchStartIndex, objects.length, objects);

        // Update the cache with these objects. Some may be in the object id
        // cache, but not the indexed cache.
        for (int i = 0; i < objects.length; i++) {
            int realIndex = fetchStartIndex + i;
            CachedObject tmp = validateObject(objects[i], new MutableInteger(realIndex) );
            if (realIndex == anIndex) {
                // Save the object we have to return
                cachedObject = tmp;
            }
        }

        if (cachedObject == null) {
            throw new ObjectSourceException("Cache Internal Error: didn't load object at index " + anIndex);
        }

        // Make this one MRU
        unlinkFromMRU(cachedObject);
        addToMRU(cachedObject);
        return cachedObject.mObject;
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public void get(int anIndex, int aLength, Object[] anObjectArray)
        throws ObjectSourceException, ArrayIndexOutOfBoundsException
    {
        if (anIndex < 0 || anIndex >= size() ||
            (anIndex + aLength) < 0 || (anIndex + aLength) > size()) {

            throw new ArrayIndexOutOfBoundsException(
                "The specified index " + anIndex + " is not in the range [0.." +
                size() + "]");
        }

        // Are all requested items already cached?
        MutableInteger testKey = new MutableInteger(0);
        // Keep track of the starting offset. I.e., this is the
        // offset from anIndex where we already have cached items
        int startOffset = 0;
        for (int i = 0; i < aLength; ++i) {
            testKey.setValue(anIndex + i);
            CachedObject cachedObject = (CachedObject)mIndexMap.get(testKey);
            if (cachedObject == null) {
                // Stop as soon as we know that one is not cached.
                break;
            }

            // Found one. Save it in case we can satisfy the whole request.
            anObjectArray[i] = cachedObject.mObject;
            startOffset = i + 1;

            // Make it MRU
            unlinkFromMRU(cachedObject);
            addToMRU(cachedObject);
        }

        if (startOffset == aLength) {
            // We have them all in the array already.
            return;
        }

        int startIndex = anIndex + startOffset;
        int chunkLength = aLength - startOffset;
        Object[] chunkArray = new Object[chunkLength];

        // Not all cached at this point. Just get the requested chunk.
        mObjectSource.get(startIndex, chunkLength, chunkArray);

        // Validate all elements
        for (int i = 0; i < chunkLength; ++i) {
            CachedObject cachedObject = validateObject(chunkArray[i],
                        new MutableInteger(anIndex + startOffset + i) );

            anObjectArray[startOffset + i] = cachedObject.mObject;

            // Make it MRU
            unlinkFromMRU(cachedObject);
            addToMRU(cachedObject);
        }
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public Object get(Object anObjectId) throws ObjectSourceException
    {
        if (anObjectId == null) {
            throw new ObjectSourceException("Id is null");
        }

        Object object = null;
        CachedObject cachedObject = (CachedObject)mObjectIdMap.get(anObjectId);

        if (cachedObject == null) {
            // Not in cache. Try to get it from base source.
            object = mObjectSource.get(anObjectId);
            if (object == null) {
                // It really doesn't exist.
                return null;
            }

            cachedObject = validateObject(object, null);
        }

        // We found it.
        object = cachedObject.mObject;

        // Make it MRU
        unlinkFromMRU(cachedObject);
        addToMRU(cachedObject);
        return object;
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public Object getObjectId(Object anObject)
    {
        return mObjectSource.getObjectId(anObject);
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public void update(Object anObject, int anIndex) throws ObjectSourceException
    {
        // This update will fire an event which will cause us to evict the cache
        // and propagate the event to our listeners
        mObjectSource.update(anObject, anIndex);
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public void update(Object anObject) throws ObjectSourceException
    {
        // This update will fire an event which will cause us to evict the cache
        // and propagate the event to our listeners
        mObjectSource.update(anObject);
    }

    //----------------------------------------------------------------------
    // ...End of methods from ObjectSource
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Nested classes...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Represents a cached object and a node in MRU list.
     * Yep, we're implementing our own linked list here.
     * We're doing it because the HashMaps have to store the node in the list
     * so we can quickly move cached objects to the head of the list. If we
     * didn't do this and used java.util.LinkedList instead, we'd have to do:
     *     linkedList.remove(object);
     *     linkedList.addFirst(object);
     * The remove method involves a linear search of the list for object. By implementing
     * our own list and storing list nodes, we avoid the linear search.
     */
    private static final class CachedObject
    {
        CachedObject mNext = null;
        CachedObject mPrev = null;
        Object mObject;
        /** Index if known, else null */
        MutableInteger mIndex;
        Object mObjectId;

        //----------------------------------------------------------------------
        CachedObject(Object anObject, MutableInteger anIndex, Object anObjectId)
        {
            mObject = anObject;
            mIndex = anIndex;
            mObjectId = anObjectId;
        }
    }

    //----------------------------------------------------------------------
    /**
     * Similar to the MutableInteger class, but mutable.
     * We use this instead of integer so that we can quickly generate keys for
     * mIndexMap.
     */
    private static final class MutableInteger
    {
        private int mValue;

        //----------------------------------------------------------------------
        /** Construct using a value.
         *
         * @param aValue the value to be represented by this object.
         */
        MutableInteger(int aValue)
        {
            setValue(aValue);
        }

        //----------------------------------------------------------------------
        /**
         * Sets the integer value.
         *
         * @param aValue the value to be represented by this object.
         */
        void setValue(int aValue)
        {
            mValue = aValue;
        }

        //----------------------------------------------------------------------
        /**
         * Gets the integer value. Mirrors the method on MutableInteger.
         *
         * @return the value represented by this object as an int.
         */
        int intValue()
        {
            return mValue;
        }

        //----------------------------------------------------------------------
        /**
         * Returns a String object representing this MutableInteger's value. The
         * value is returned exactly as if the integer value were given as an
         * argument to the {@link java.lang.String#valueOf(int)} method.
         *
         * @return  a string representation of the value of this object in
         *          base 10.
         */
        public String toString() {
            return String.valueOf(mValue);
        }

        //----------------------------------------------------------------------
        /**
         * Returns a hashcode for this MutableInteger.
         *
         * @return  a hash code value for this object, equal to the
         * value represented by this object.
         */
        public int hashCode() {
            return mValue;
        }

        //----------------------------------------------------------------------
        /**
         * Compares this object to the specified object.
         * The result is true if and only if the argument is not
         * null and is a MutableInteger object that contains
         * the same value as this object.
         *
         * @param anObject the object to compare with.
         * @return true if the objects are equal; false otherwise.
         */
        public boolean equals(Object anObject) {
            if (anObject instanceof MutableInteger) {
                return mValue == ((MutableInteger)anObject).intValue();
            }

            return false;
        }
    }
}
