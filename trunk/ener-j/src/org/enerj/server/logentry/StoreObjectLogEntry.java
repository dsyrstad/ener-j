// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/logentry/StoreObjectLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.server.logentry;

import java.io.*;


/** 
 * Log Entry for "Store Object Transaction". This entry is only suitable for a redo-style log.
 *
 * @version $Id: StoreObjectLogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see RedoLogServer
 */
public class StoreObjectLogEntry extends LogEntry
{
    private long mOID = -1;
    private long mNewCID = -1;
    private byte[] mNewObjectValue = null;

    //----------------------------------------------------------------------
    /**
     * Constructs an empty StoreObjectLogEntry.
     */
    protected StoreObjectLogEntry()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a StoreObjectLogEntry.
     *
     * @param aTransactionId the transaction id. This id must not conflict with any
     *  pre-existing transaction id in the log.
     * @param anOID the OID for the object.
     * @param aNewCID the CID, possibly revisied.
     * @param aNewObjectValue the new serialized object value being stored.
     */
    public StoreObjectLogEntry(long aTransactionId, long anOID, long aNewCID, byte[] aNewObjectValue)
    {
        setTransactionId(aTransactionId);
        mOID = anOID;
        mNewCID = aNewCID;
        mNewObjectValue = aNewObjectValue;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the entry type of this log entry.
     *
     * @return STORE_OBJECT_ENTRY_TYPE.
     */
    public byte getEntryType()
    {
        return STORE_OBJECT_ENTRY_TYPE;
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected int loadFromLog(DataInput aDataInput) throws IOException
    {
        mOID = aDataInput.readLong();
        mNewCID = aDataInput.readLong();
        int objectLength = aDataInput.readInt();
        mNewObjectValue = new byte[objectLength];
        aDataInput.readFully(mNewObjectValue);

        // 8 = OID, 8 = CID, 4 = int = length of serialized object
        return 8 + 8 + 4 + objectLength;
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void appendToLog(DataOutput aDataOutput) throws IOException
    {
        super.appendToLog(aDataOutput);

        aDataOutput.writeLong(mOID);
        aDataOutput.writeLong(mNewCID);
        aDataOutput.writeInt(mNewObjectValue.length);
        aDataOutput.write(mNewObjectValue);
    }

    //----------------------------------------------------------------------
    /**
     * Gets OID that was stored.
     *
     * @return the OID.
     */
    public long getOID()
    {
        return mOID;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the CID that was stored.
     *
     * @return the CID.
     */
    public long getCID()
    {
        return mNewCID;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the serialized object value that was stored.
     *
     * @return the serialized object value.
     */
    public byte[] getObjectValue()
    {
        return mNewObjectValue;
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        String name = this.getClass().getName();
        return name.substring( name.lastIndexOf('.') + 1 ) + "[txnId=" + getTransactionId() + " oid=" + mOID + " cid=" + Long.toHexString(mNewCID) + " objLength=" + mNewObjectValue.length + ']';
    }
}
