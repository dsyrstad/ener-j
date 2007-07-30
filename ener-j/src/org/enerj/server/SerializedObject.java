/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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

package org.enerj.server;

import java.io.Serializable;

/**
 * Represents an object serialized as a byte[] with its associated OID.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SerializedObject implements Serializable
{
    private byte[] mImage;
    private long mOID;
    private long mCID;
    private boolean mIsNew;

    /**
     * Constructs a SerializedObject.
     *
     * @param anOID the OID of the object.
     * @param aCID the CID of the object.
     * @param anImage the serialized image of the object, as serialized by ObjectSerializer.
     * @param isNew true if the object is new.
     */
    public SerializedObject(long anOID, long aCID, byte[] anImage, boolean isNew)
    {
        mOID = anOID;
        mCID = aCID;
        mImage = anImage;
        mIsNew = isNew;
    }


    public byte[] getImage()
    {
        return mImage;
    }


    public long getOID()
    {
        return mOID;
    }

    /**
     * Determines if the object is new.
     *
     * @return true if the object is new.
     */
    public boolean isNew()
    {
        return mIsNew;
    }

    /**
     * Sets whether the object is new.
     *
     * @param true if the object is new.
     */
    public void setIsNew(boolean isNew)
    {
        mIsNew = isNew;
    }

    /**
     * Gets the CID.
     *
     * @return the CID.
     */
    public long getCID()
    {
        return mCID;
    }
}

