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
