// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/SerializedObject.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.io.*;

/**
 * Represents an object serialized as a byte[] with its associated CID and OID.
 *
 * @version $Id: SerializedObject.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SerializedObject implements Serializable
{
    private byte[] mImage;
    private long mOID;
    private long mCID;

    //----------------------------------------------------------------------
    /**
     * Constructs a SerializedObject.
     *
     * @param anOID the OID of the object.
     * @param aCID the CID of the object.
     * @param anImage the serialized image of the object, as serialized by ObjectSerializer.
     */
    public SerializedObject(long anOID, long aCID, byte[] anImage)
    {
        mOID = anOID;
        mCID = aCID;
        mImage = anImage;
    }

    //----------------------------------------------------------------------
    public byte[] getImage()
    {
        return mImage;
    }

    //----------------------------------------------------------------------
    public long getOID()
    {
        return mOID;
    }

    //----------------------------------------------------------------------
    public long getCID()
    {
        return mCID;
    }
}

