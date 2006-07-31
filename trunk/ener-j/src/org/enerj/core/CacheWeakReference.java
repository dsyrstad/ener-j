package org.enerj.core;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** 
 * A WeakReference for the client-side cache which carries additional
 * info required to clean-up GCed objects.
 */
class CacheWeakReference extends WeakReference
{
    /** OID of cached object */
    private long mOID;
    /** Pre-modification image of the object. */
    private byte[] mSavedImage;
    
    //----------------------------------------------------------------------
    /**
     * Construct a CacheWeakReference that belongs to a ReferenceQueue.
     *
     * @param anOID the OID of aReferent.
     * @param aReferent the referent object.
     * @param aReferenceQueue ReferenceQueue to which aReferent will be registered.
     */
    CacheWeakReference(long anOID, Object aReferent, ReferenceQueue aReferenceQueue)
    {
        super(aReferent, aReferenceQueue);
        mOID = anOID;
    }

    //----------------------------------------------------------------------
    long getOID()
    {
        return mOID;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the pre-modification image of this object.
     *
     * @param anImage the byte[] representing the image. This may be null to 
     *  clear the saved image.
     */
    void setSavedImage(byte[] anImage)
    {
        mSavedImage = anImage;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the pre-modification image of this object.
     *
     * @return the byte[] representing the image. This may be null if there is
     *  no saved image.
     */
    byte[] getSavedImage()
    {
        return mSavedImage;
    }
}