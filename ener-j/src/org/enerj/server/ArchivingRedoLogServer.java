// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ArchivingRedoLogServer.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server;

import java.util.*;
import java.io.*;

import org.odmg.*;

import org.enerj.server.logentry.*;
import org.enerj.util.*;

/**
 * An Archiving RedoLogServer implementation. Logs are optionally archived once a 
 * checkpoint has been reached.
 * <p>
 *
 *
 * @version $Id: ArchivingRedoLogServer.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class ArchivingRedoLogServer implements RedoLogServer
{
    private boolean mDisconnected = false;
    private String mConfigResourceName;
    private String mLogFileName;
    
    // A stream for reading the log file. This is a RandomAccessFile so we can seek on the stream.
    private RandomAccessFile mLogFileInputRAF = null;
    // A Stream for writing the log file.
    private FileOutputStream mLogFileOutputStream = null;

    // These are mLogFileRAF wrapped in a File and buffered Input/OutputStreams.
    private ResettableBufferedInputStream mLogBufferedInputStream;
    private BufferedOutputStream mLogBufferedOuptutStream;

    // These are the above buffered streams wrapped in DataInput/OutputStreams.
    private DataInputStream mLogDataInputStream;
    private TrackedDataOutputStream mLogDataOutputStream;

    //----------------------------------------------------------------------
    /**
     * Construct an ArchivingRedoLogServer.
     *
     * @param someProperties properties which specify the connect parameters. See {@link #connect(Properties)}.
     *
     * @throws ODMGException if an error occurs.
     */
    private ArchivingRedoLogServer(Properties someProperties) throws ODMGException  
    {
        String logFileName = someProperties.getProperty("ArchivingRedoLogServer.logName");
        if (logFileName == null) {
            throw new ODMGException("Cannot find ArchivingRedoLogServer.logName property");
        }
        
        logFileName = StringUtil.substituteMacros(logFileName, someProperties);
        //  TODO  handle other resources

        init(logFileName);
    }
        
    //----------------------------------------------------------------------
    /**
     * Construct an ArchivingRedoLogServer.
     *
     * @param aLogFileName the name of the log file.
     *
     * @throws ODMGException if an error occurs.
     */
    public ArchivingRedoLogServer(String aLogFileName) throws ODMGException  
    {
        init(aLogFileName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Common Constructor initialization.
     *
     * @param aLogFileName the name of the log file.
     *
     * @throws ODMGException if an error occurs.
     */
    private void init(String aLogFileName) throws ODMGException  
    {
        mLogFileName = aLogFileName;
        try {
            mLogFileOutputStream = new FileOutputStream(mLogFileName, true);
            // Lock the log so no other process can manipulate it.
            if (mLogFileOutputStream.getChannel().tryLock() == null) {
                throw new DatabaseOpenException("Log file " + mLogFileName + " is in use by another process.");
            }
            
            mLogFileInputRAF = new RandomAccessFile(mLogFileName, "r");

            FileInputStream logFIS = new FileInputStream( mLogFileInputRAF.getFD() );

            //  TODO  make these buffer sizes configurable?
            mLogBufferedInputStream = new ResettableBufferedInputStream(logFIS, 8192);
            mLogBufferedOuptutStream = new BufferedOutputStream(mLogFileOutputStream, 8192);

            mLogDataInputStream = new DataInputStream(mLogBufferedInputStream);
            
            // This will be the first append position.
            long eofPosition = mLogFileInputRAF.length();
            mLogDataOutputStream = new TrackedDataOutputStream(mLogBufferedOuptutStream, eofPosition);
        }
        catch (IOException e) {
            throw new ODMGException("Unable to open log file: " + mLogFileName + ": " + e, e);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Flushes pending output physically to disk.
     *
     * @throws ODMGException if an errors occurs.
     */
    private void flush() throws ODMGException
    {
        synchronized (mLogFileOutputStream) {
            try {
                mLogBufferedOuptutStream.flush();
                //Alternate: mLogFileOutputStream.getFD().sync();
                mLogFileOutputStream.getChannel().force(false);
            }
            catch (IOException e) {
                throw new ODMGException("Error flushing log to disk: " + e, e);
            }
        }
    }
    
    //----------------------------------------------------------------------
    // Start of RedoLogServer interface...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Get an instance of a RedoLogServer.
     *
     * @param someProperties properties which specify the connect parameters.
     * The properties may have the following keys:<br>
     * <ul>
     * <li> ArchivingRedoLogServer.logName - a string representing the log name. NOTE: If
     *      this is not set, <em>logging</em> will not be active and database recovery will not
     *      be possible. Also, the size of all combined transactions will be limited to
     *      what can be kept in memory.
     * <li> ArchivingRedoLogServer.shouldArchive - "true" or "false" whether logs should be archived online.
     * <li> ArchivingRedoLogServer.requestedLogSize - requested maximum log size. After
     *      the log grows to this size, no more transactions may start, the log will be
     *      fully checkpointed, and a new log will start. Because active transactions need
     *      to complete, the log may grow larger than this value. 
     * </ul>
     *
     * @return a RedoLogServer.
     *
     * @throws ODMGException in the event of an error. 
     */
    public static RedoLogServer connect(Properties someProperties) throws ODMGException
    {
        return new ArchivingRedoLogServer(someProperties);
    }

    //----------------------------------------------------------------------
    public void disconnect() throws ODMGException
    {
        synchronized (mLogFileOutputStream) {
            try {
                if (mLogFileOutputStream != null) {
                    flush();
                    mLogFileOutputStream.close();
                }
                
                if (mLogFileInputRAF != null) {
                    mLogFileInputRAF.close();
                }
            }
            catch (IOException e) {
                throw new ODMGException("Error closing log " + mLogFileName + ": " + e, e);
            }
            finally {
                mDisconnected = true;
                mLogFileInputRAF = null;
                mLogFileOutputStream = null;
                mLogBufferedInputStream = null;
                mLogBufferedOuptutStream = null;
                mLogDataInputStream = null;
                mLogDataOutputStream = null;
            }
        }
    }

    //----------------------------------------------------------------------
    public void append(LogEntry aLogEntry) throws ODMGException
    {
        synchronized (mLogFileOutputStream) {
            try {
                // We use the input RandomAccessFile's length (same file as the
                // output) to determine the log position.
                long entryPosition =  mLogDataOutputStream.getPosition();
                aLogEntry.setLogPosition(entryPosition);
                if (aLogEntry instanceof BeginTransactionLogEntry) {
                    // Assign a transaction id.
                    aLogEntry.setTransactionId(entryPosition);
                }
                
                aLogEntry.appendToLog(mLogDataOutputStream);

                if (aLogEntry instanceof CommitTransactionLogEntry ||
                    aLogEntry instanceof CheckpointTransactionLogEntry ||
                    aLogEntry instanceof EndDatabaseCheckpointLogEntry) {
                    flush();
                }
            }
            catch (IOException e) {
                throw new ODMGException("Error writing log entry: " + e, e);
            }
        }
    }

    //----------------------------------------------------------------------
    public long getFirstLogEntryPosition() throws ODMGException
    {
        return 0L;
    }

    //----------------------------------------------------------------------
    /**
     * Reads an entry to the log.
     *
     * @param aLogPosition the position of the entry in the log.
     *
     * @return a LogEntry. LogEntry.getNextLogEntryPosition() will provide
     *  the position of the next log entry.
     *
     * @throws ODMGException in the event of an error. 
     */
    public LogEntry read(long aLogPosition) throws ODMGException
    {
        synchronized (mLogFileInputRAF) {
            try {
                if (mLogFileInputRAF.getFilePointer() != aLogPosition) {
                    mLogBufferedInputStream.resetBuffer();
                    mLogFileInputRAF.seek(aLogPosition);
                }
                
                return LogEntry.createFromLog(mLogDataInputStream, aLogPosition);
            }
            catch (IOException e) {
                throw new ODMGException("Error reading log entry at " + aLogPosition + ": " + e, e);
            }
        }
    }

    //----------------------------------------------------------------------
    // ...End of RedoLogServer interface.
    //----------------------------------------------------------------------
}
