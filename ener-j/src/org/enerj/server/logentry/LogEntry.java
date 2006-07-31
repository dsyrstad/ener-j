// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/logentry/LogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.server.logentry;

import java.io.*;

/** 
 * Base class for all recovery log entries.
 *
 * @version $Id: LogEntry.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see RedoLogServer
 */
abstract public class LogEntry
{
    public static final byte BEGIN_TXN_ENTRY_TYPE             = 0;
    public static final byte CHECKPOINT_TXN_ENTRY_TYPE        = 1;
    public static final byte COMMIT_TXN_ENTRY_TYPE            = 2;
    public static final byte ROLLBACK_TXN_ENTRY_TYPE          = 3;
    public static final byte START_DB_CHECKPOINT_ENTRY_TYPE   = 4;
    public static final byte END_DB_CHECKPOINT_ENTRY_TYPE     = 5;
    public static final byte STORE_OBJECT_ENTRY_TYPE          = 6;
    public static final byte DELETE_OBJECT_ENTRY_TYPE         = 7;

    // type (1) + timestamp (8) + txn id (8).
    private static final int HDR_LENGTH = 1 + 8 + 8;
    
    private long mLogPosition = -1L;
    private long mNextLogEntryPosition = -1L;
    private long mTransactionId = -1;
    private long mTimestamp;
    
    //----------------------------------------------------------------------
    /**
     * Constructs an empty LogEntry.
     */
    protected LogEntry()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Gets the entry type of this log entry.
     *
     * @return the entry type, one of the *_ENTRY_TYPE values defined within.
     */
    abstract public byte getEntryType();

    //----------------------------------------------------------------------
    /**
     * Loads the type-specific information for the LogEntry from aDataInput.
     * This method is not, by itself, thread-safe. However, it is usually invoked
     * from createFromLog(), which is thread-safe.
     *
     * @param aDataInput a DataInput to read from.
     *
     * @return the number of bytes read.
     *
     * @throws IOException if an error is encountered while reading the log.
     */
    abstract protected int loadFromLog(DataInput aDataInput) throws IOException;
    
    //----------------------------------------------------------------------
    /**
     * Appends this LogEntry to the end of aDataOutput.
     * Subtypes must call super.appendToLog(aDataOutput)
     * prior to writing their information.
     * This method is not, by itself, thread-safe.
     *
     * @param aDataOutput a DataOutput to read from.
     *
     * @throws IOException if an error is encountered while reading the log.
     */
    public void appendToLog(DataOutput aDataOutput) throws IOException
    {
        mTimestamp = System.currentTimeMillis();
        aDataOutput.writeByte( getEntryType() );
        aDataOutput.writeLong(mTimestamp);
        aDataOutput.writeLong(mTransactionId);
    }
    
    //----------------------------------------------------------------------
    /**
     * Creates a concrete LogEntry from aDataInput.
     * This method is thread-safe.
     * I/O is synchronized around aDataInput.
     *
     * @param aDataInput a DataInput to read from.
     * @param aLogPosition the starting position in the log. This is simply used to 
     *  the Log Position on this entry.
     *
     * @return the concrete LogEntry, or null if the end of the log was reached.
     *
     * @throws IOException if an error is encountered while reading the log.
     */
    public static LogEntry createFromLog(DataInput aDataInput, long aLogPosition) throws IOException
    {
        synchronized (aDataInput) {
            byte type;
            try {
                // This is the only place where we expect EOFException.
                type = aDataInput.readByte();
            }
            catch (EOFException e) {
                return null;
            }

            long timestamp = aDataInput.readLong();
            long txnId = aDataInput.readLong();

            LogEntry entry;
            switch (type) {
            case BEGIN_TXN_ENTRY_TYPE:
                entry = new BeginTransactionLogEntry();
                break;

            case CHECKPOINT_TXN_ENTRY_TYPE:
                entry = new CheckpointTransactionLogEntry();
                break;

            case COMMIT_TXN_ENTRY_TYPE:
                entry = new CommitTransactionLogEntry();
                break;

            case ROLLBACK_TXN_ENTRY_TYPE:
                entry = new RollbackTransactionLogEntry();
                break;

            case START_DB_CHECKPOINT_ENTRY_TYPE:
                entry = new StartDatabaseCheckpointLogEntry();
                break;

            case END_DB_CHECKPOINT_ENTRY_TYPE:
                entry = new EndDatabaseCheckpointLogEntry();
                break;

            case STORE_OBJECT_ENTRY_TYPE:
                entry = new StoreObjectLogEntry();
                break;

            case DELETE_OBJECT_ENTRY_TYPE:
                entry = new DeleteObjectLogEntry();
                break;

            default:
                throw new IOException("Invalid log entry type: " + type);
            }

            entry.mLogPosition = aLogPosition;
            entry.mTimestamp = timestamp;
            entry.mTransactionId = txnId;

            // Read the sub-type's information.
            int numRead = entry.loadFromLog(aDataInput);
            entry.mNextLogEntryPosition = aLogPosition + HDR_LENGTH + numRead;
            return entry;
        } // ...end synchronized
    }

    //----------------------------------------------------------------------
    /**
     * Gets the log position of this entry.
     *
     * @return the log position of this entry, or -1 if the entry has not been
     *  written to the log yet.
     */
    public long getLogPosition()
    {
        return mLogPosition;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the log position of this entry.
     *
     * @param aLogPosition the log position of this entry, or -1 if the entry has not been
     *  written to the log yet.
     */
    public void setLogPosition(long aLogPosition)
    {
        mLogPosition = aLogPosition;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the transaction id of this entry.
     *
     * @return the transaction id of this entry, or -1 if the id has not been
     *  written to the log yet.
     */
    public long getTransactionId()
    {
        return mTransactionId;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the transaction id of this entry.
     *
     * @param aTransactionId the transaction id. This id must not conflict with any
     *  pre-existing transaction id in the log.
     */
    public void setTransactionId(long aTransactionId)
    {
        mTransactionId = aTransactionId;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the next log entry position (the log record following this one).
     *
     * @return the next log entry position, or -1 if the entry was not read from or
     *  written to the log.
     */
    public long getNextLogEntryPosition()
    {
        return mNextLogEntryPosition;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the next log entry position (the log record following this one).
     *
     * @param aPosition the next log entry position, or -1 if the entry was not read from or
     *  written to the log.
     */
    public void setNextLogEntryPosition(long aPosition)
    {
        mNextLogEntryPosition = aPosition;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the UTC timestamp of the log entry.
     *
     * @return the timestamp.
     */
    public long getTimestamp()
    {
        return mTimestamp;
    }
}
