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
// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PagedObjectServer.java,v 1.6 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.enerj.core.DatabaseRoot;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJTransaction;
import org.enerj.server.logentry.BeginTransactionLogEntry;
import org.enerj.server.logentry.CheckpointTransactionLogEntry;
import org.enerj.server.logentry.CommitTransactionLogEntry;
import org.enerj.server.logentry.EndDatabaseCheckpointLogEntry;
import org.enerj.server.logentry.RollbackTransactionLogEntry;
import org.enerj.server.logentry.StartDatabaseCheckpointLogEntry;
import org.enerj.server.logentry.StoreObjectLogEntry;
import org.enerj.util.FileUtil;
import org.enerj.util.RequestProcessor;
import org.enerj.util.StringUtil;
import org.odmg.Database;
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseNotFoundException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.TransactionNotInProgressException;

/** 
 * The defautl Ener-J ObjectServer. Stores objects in pages on a PageServer.
 * This class is thread-safe.<p>
 *
 * This server is referenced by the properties described in the {@link connect} method.
 *
 *
 * @version $Id: PagedObjectServer.java,v 1.6 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see ObjectServer
 */
public class PagedObjectServer implements ObjectServer
{
    /** HashMap of database names to PagedObjectServers. */
    private static HashMap<String, PagedObjectServer> sCurrentServers = new HashMap<String, PagedObjectServer>(20);

    /** The database name. */
    private String mDBName;
    
    /** The Lock server. */
    private LockServer mLockServer;
    
    /** Paged storage module. */
    private PagedStore mPagedStore;

    /** The recovery log server. */
    private RedoLogServer mRedoLogServer;
    
    /** List of active transactions. List of PagedObjectServer.Transaction. Synchronized
     * around mTransactionLock.
     */
    private LinkedList mActiveTransactions = new LinkedList();
    
    /** True if the server should become quiescent, i.e. don't allow new transactions to 
     * start. 
     */
    private boolean mQuiescent = false;

    /** Our shutdown hook. */
    private static Thread mShutdownHook = null;

    /** True if a non-quiescent database checkpoint is in progress. */
    private boolean mCheckpointInProgress = false;
    /** Time of last database checkpoint (System.currentTimeInMillis()). */
    private long mLastCheckpointTime = 0L;

    /** The Update Cache. Caches updates prior to committing a transaction. */
    private UpdateCache mUpdateCache = null;
    
    /** Synchronization lock for transaction-oriented methods. */
    private Object mTransactionLock = new Object();
    

    //----------------------------------------------------------------------
    /**
     * Construct a PagedObjectServer.
     *
     * @param someProperties the properties given to the connect() method.
     *
     * @throws ODMGException if an error occurs.
     */
    private PagedObjectServer(Properties someProperties) throws ODMGException  
    {
        // This get is here just to require the property. It is actually used by PagedStore. 
        getRequiredProperty(someProperties, "PagedObjectServer.PageServerClass");
        String lockServerClassName = getRequiredProperty(someProperties, "PagedObjectServer.LockServerClass");
        String logServerClassName = getRequiredProperty(someProperties, "PagedObjectServer.RedoLogServerClass");
        
        int maxUpdateCacheSize = getRequiredIntProperty(someProperties, "PagedObjectServer.MaxUpdateCacheSize");
        int updateCacheInitialHashSize = getRequiredIntProperty(someProperties, "PagedObjectServer.UpdateCacheInitialHashSize");
        mUpdateCache = new UpdateCache(maxUpdateCacheSize, updateCacheInitialHashSize);
        
        mDBName = getRequiredProperty(someProperties, MetaObjectServer.ENERJ_DBNAME_PROP);
        
        try {
            mRedoLogServer = (RedoLogServer)PluginHelper.connect(logServerClassName, someProperties);
        }
        catch (ODMGRuntimeException e) {
            throw e;    // Don't remap
        }
        catch (ODMGException e) {
            throw e;    // Don't remap
        }
        catch (Exception e) {
            throw new ODMGException("Cannot connect to RedoLogServer " + logServerClassName, e);
        }

        try {
            mPagedStore = new PagedStore(someProperties, mRedoLogServer, this, false);
        }
        catch (PageServerNotFoundException e) {
            throw new DatabaseNotFoundException("Cannot connect to PagedStore", e);
        }
        catch (Exception e) {
            throw new ODMGException("Cannot connect to PagedStore", e);
        }
        
        try {
            mLockServer = (LockServer)PluginHelper.connect(lockServerClassName, someProperties);
        }
        catch (ODMGRuntimeException e) {
            throw e;    // Don't remap
        }
        catch (ODMGException e) {
            throw e;    // Don't remap
        }
        catch (Exception e) {
            throw new ODMGException("Cannot connect to LockServer " + lockServerClassName, e);
        }

        // Register a shutdown hook...
        mShutdownHook = new ShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(mShutdownHook);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets a required property from the specified properties.
     *
     * @param someProperties the properties.
     * @param aKey the property key.
     *
     * @return the String value.
     *
     * @throws ODMGException if the property was not found.
     */
    private static String getRequiredProperty(Properties someProperties, String aKey) throws ODMGException
    {
        String value = someProperties.getProperty(aKey);
        if (value == null) {
            throw new ODMGException(aKey + " was not specified");
        }
        
        return value;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets a required property as an int from the specified properties.
     *
     * @param someProperties the properties.
     * @param aKey the property key.
     *
     * @return the int value.
     *
     * @throws ODMGException if the property was not found or the 
     *  value is not a number.
     */
    private static int getRequiredIntProperty(Properties someProperties, String aKey) throws ODMGException
    {
        String value = getRequiredProperty(someProperties, aKey);
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new ODMGException("Value '" + value + " for key " + aKey + " is not an integer");
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Creates a new database on disk. The "enerj.dbpath" system property must be set.
     *
     * @param aDescription a description for the database. May be null.
     * @param aDBName The database name. See {@link #connect(Properties)}.
     * @param aMaximumSize the maximum size for the volume. This will be rounded up
     *  to the nearest page boundary. If this value is zero, the volume will grow unbounded.
     * @param aPreAllocatedSize the number of bytes to pre-allocate. This will be rounded up
     *  to the nearest page boundary.

     * @throws ODMGException if an error occurs.
     *
     *  TODO  Allow multiple volumes to be specfiied.
     */
    public static void createDatabase(String aDescription, String aDBName, long aMaximumSize, long aPreAllocatedSize) throws ODMGException
    {
        // Load database properties.  First copy system properties.
        Properties props = new Properties( System.getProperties() );

        String propFileName = aDBName + File.separatorChar + aDBName + ".properties";
        String dbPath = getRequiredProperty(props, MetaObjectServer.ENERJ_DBPATH_PROP);
        File propFile = FileUtil.findFileOnPath(propFileName, dbPath);
        if (propFile == null) {
            throw new DatabaseNotFoundException("Cannot find " + propFileName + " in any of the directories " + dbPath); 
        }

        FileInputStream inPropFile = null;
        try {
            inPropFile = new FileInputStream(propFile);
            props.load(inPropFile);
        }
        catch (IOException e) {
            throw new ODMGException("Error reading " + propFile, e);
        }
        finally {
            if (inPropFile != null) {
                try {
                    inPropFile.close();
                }
                catch (IOException e) {
                    throw new ODMGException("Error closing properties file: " + propFile, e);
                }
                
                inPropFile = null;
            }
        }

        createDatabase(aDescription, aDBName, aMaximumSize, aPreAllocatedSize, props, propFile);
    }

    //----------------------------------------------------------------------
    /**
     * Creates a new database on disk. The "enerj.dbpath" system property must be set.
     *
     * @param aDescription a description for the database. May be null.
     * @param aDBName The database name. See {@link #connect(Properties)}.
     * @param aMaximumSize the maximum size for the volume. This will be rounded up
     *  to the nearest page boundary. If this value is zero, the volume will grow unbounded.
     * @param aPreAllocatedSize the number of bytes to pre-allocate. This will be rounded up
     *  to the nearest page boundary.
     * @param someDBProps the database properties, normally read from a database propeties file.
     * @param aDBDir the base directory of the database.
     * 
     * @throws ODMGException if an error occurs.
     *
     *  TODO  Allow multiple volumes to be specfiied.
     */
	private static void createDatabase(String aDescription, String aDBName, long aMaximumSize, long aPreAllocatedSize, 
			Properties someDBProps, File aDBDir) throws ODMGException {
		
		someDBProps.setProperty(MetaObjectServer.ENERJ_DBDIR_PROP, aDBDir.getParent() );
        String volumeFileName = StringUtil.substituteMacros( getRequiredProperty(someDBProps, FilePageServer.VOLUME_PROP), someDBProps);
        int pageSize = getRequiredIntProperty(someDBProps, FilePageServer.PAGE_SIZE_PROP);

        boolean completed = false;
        EnerJDatabase db = null;
        EnerJTransaction txn = null;

        try {
            // Generate a database Id.
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1024);
            DataOutputStream dataStream = new DataOutputStream(byteStream);
            dataStream.writeLong( System.currentTimeMillis() );
            dataStream.writeUTF(volumeFileName);
            dataStream.writeInt(pageSize);
            dataStream.writeUTF( System.getProperty("user.name") );
            dataStream.write( java.net.InetAddress.getLocalHost().getAddress() );
            dataStream.flush();

            java.security.MessageDigest sha1Digest = java.security.MessageDigest.getInstance("SHA-1");
            byte[] sha1 = sha1Digest.digest( byteStream.toByteArray() );
            long databaseId =
                         (long)(sha1[0] & 0xff)        |
                        ((long)(sha1[1] & 0xff) <<  8) |
                        ((long)(sha1[2] & 0xff) << 16) |
                        ((long)(sha1[3] & 0xff) << 24) |
                        ((long)(sha1[4] & 0xff) << 32) |
                        ((long)(sha1[5] & 0xff) << 40) |
                        ((long)(sha1[6] & 0xff) << 48) |
                        ((long)(sha1[7] & 0xff) << 56);

            // Create the volume
            FilePageServer.createVolume(volumeFileName, pageSize, databaseId, 0L, aMaximumSize, Math.min((long)pageSize, aPreAllocatedSize) );

            // Pre-allocate first page so it's all zeros.
            // Make sure page server knows that first page is allocated.
            PageServer pageServer = FilePageServer.connect(someDBProps); 
            long allocatedPage = pageServer.allocatePage();
            long logicalFirstPage = pageServer.getLogicalFirstPageOffset();
            if (allocatedPage != 0 || logicalFirstPage != 0) {
                throw new ODMGException("Couldn't allocate page at offset zero");
            }
            
            pageServer.disconnect();
            
            // Init the database root.
            db = new EnerJDatabase();
            // Open in local mode, exclusive.
            db.open("enerj://root:root@-/" + aDBName, Database.OPEN_EXCLUSIVE);
            
            txn = new EnerJTransaction();
            txn.begin(db);
            
            DatabaseRoot root = new DatabaseRoot(aDescription);
            db.setDatabaseRoot(root);
            
            txn.commit();
            completed = true;
        }
        catch (ODMGException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGException("Error creating database: " + e, e);
        }
        finally {
            if (txn != null && txn.isOpen()) {
                txn.abort();
            }
            
            if (db != null && db.isOpen()) {
                db.close();
            }

            if (!completed) {
                new File(volumeFileName).delete();
            }
        }
	}

    //----------------------------------------------------------------------
    /**
     * Handles an unmonitored storage exception from the PagedStore's processing thread.
     *
     * @param aRequest the request that encountered an error.
     * @param anException the exception.
     */
    void handleStorageException(RequestProcessor.Request aRequest, Exception anException)
    {
        //  TODO  alot of work to be done here.....
        System.err.println("---> STORAGE EXCEPTION:");
        anException.printStackTrace(System.err);
        System.exit(1); //  TODO  Do NOT do this.
    }
    
    //----------------------------------------------------------------------
    /**
     * Handles the completion of an unmonitored StoreObjectRequest.
     *
     * @param aRequest the request.
     */
    void completeStoreObjectRequest(PagedStore.StoreObjectRequest aRequest)
    {
        // This request is now in the database - we can stop caching it.
        mUpdateCache.removeStoreRequest(aRequest);
    }
    
    //----------------------------------------------------------------------
    /**
     * Start a database checkpoint if one is not in progress.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    private void startCheckpoint() throws ODMGRuntimeException
    {
        int numActiveTxns = mActiveTransactions.size();
        long[] txnIds = new long[numActiveTxns];
        long[] txnPositions = new long[numActiveTxns];
        Iterator iter = mActiveTransactions.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            Transaction txn = (Transaction)iter.next();
            txnIds[i] = txn.getLogTransactionId();
            txnPositions[i] = txn.getLogStartPosition();
        }

        try {
            StartDatabaseCheckpointLogEntry logEntry = new StartDatabaseCheckpointLogEntry(txnIds, txnPositions);
            mRedoLogServer.append(logEntry);

            PagedStore.EndDatabaseCheckpointRequest request = mPagedStore.new EndDatabaseCheckpointRequest();
            mPagedStore.queueStorageRequest(request);
        }
        catch (ODMGRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException(e.toString(), e);
        }

        mCheckpointInProgress = true;
    }
    
    //----------------------------------------------------------------------
    /**
     * Removes aTransaction from the active transaction list, checks if
     * a database checkpoint should be performed, and notifies all waiters
     * of mTransactionLock. Caller must be synchronized on mTransactionLock.
     * This is a helper for commit and rollback.
     *
     * @param aTransaction the transaction to end.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    private void endTransactionAndCheckpoint(Transaction aTransaction) throws ODMGRuntimeException
    {
        mActiveTransactions.remove(aTransaction);

        int numActiveTxns = mActiveTransactions.size();
        long timeSinceLastCheckpoint = System.currentTimeMillis() - mLastCheckpointTime;
        //  TODO  make the number of active transactions  and time delay a parameter?
        if (!mCheckpointInProgress && (numActiveTxns == 0 || (numActiveTxns < 5 && timeSinceLastCheckpoint > 10000L))) {
            startCheckpoint();
        }
        
        mTransactionLock.notifyAll();
    }
    
    //----------------------------------------------------------------------
    /**
     * Called from the PagedStore request thread to finalize an EndDatabaseCheckpoint.
     *
     * @throws ODMGException if an error occurs.
     */
    void endDatabaseCheckpoint() throws ODMGException
    {
        mCheckpointInProgress = false;
        mLastCheckpointTime = System.currentTimeMillis();

        EndDatabaseCheckpointLogEntry logEntry = new EndDatabaseCheckpointLogEntry();
        mRedoLogServer.append(logEntry);
    }
    
    //----------------------------------------------------------------------
    /**
     * Shuts down this server.
     */
    void shutdown() throws ODMGException
    {
        //  TODO  quiesce the system . stop new connections and txns. shutdown when all txns closed.
        //  TODO  NEED THIS NOW!
        mQuiescent = true;

        // Transaction list must be clear. This is really an assertion.
        //  TODO  need to kill transactions, maybe after waiting a couple of seconds....
        if (mActiveTransactions.size() != 0) {
            //  TODO  log msg
            throw new ODMGException("Transaction list is not clear! INTERNAL ERROR!");
        }

        synchronized (sCurrentServers) {
            // Remove this server from the global list.
            sCurrentServers.remove(mDBName);

            // Checkpoint now. Should be a clean checkpoint.
            if (!mCheckpointInProgress) {
                try {
                    startCheckpoint();
                }
                catch (Exception e) {
                    //  TODO  log message, but keep going...
                }
            }

            try {
                //  TODO  send paged store a disconnect request (or it sends itself one in the disconnect method.
                mPagedStore.disconnect();
            }
            catch (PageServerException e) {
                throw new ODMGException("Could not close PageServer properly - database may be corrupt", e);
            }

            if (mLockServer != null) {
                //  TODO  send paged store a disconnect request (or it sends itself one in the disconnect method.
                mLockServer.disconnect();
                mLockServer = null;
            }

            if (mRedoLogServer != null) {
                //  TODO  send paged store a disconnect request (or it sends itself one in the disconnect method.
                mRedoLogServer.disconnect();
                mRedoLogServer = null;
            }
        } // End synchronized
        
        try {
            Runtime.getRuntime().removeShutdownHook(mShutdownHook);
        }
        catch (Exception e) {
            // Ignore - shutdown may be in progress.
        }
    }

    //----------------------------------------------------------------------
    // Start of ObjectServer interface methods...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Connects to a database.
     *
     * @param someProperties properties which specify the connect parameters.
     * The properties must contain the following keys:<br>
     * <ul>
     * <li><i>vo.dbname</i> - the database name. </li>
     * <li><i>PagedObjectServer.PageServerClass</i> - the class name for the PageServer. </li>
     * <li><i>PagedObjectServer.LockServerClass</i> - the class name for the LockServer. </li>
     * <li><i>PagedObjectServer.RedoLogServerClass</i> - the class name for the RedoLogServer. </li>
     * <li><i>PagedObjectServer.MaxUpdateCacheSize</i> - the maximum size in bytes of the 
     *       Update Cache. The Update Cache is used to hold updated objects for all uncommitted
     *       and recently commited transactions. After this specified maximum size is
     *       reached, newly updated objects are not held in memory. Instead, the recovery
     *       log entry is referenced. This reduces performance but allows for large/long
     *       transactions that would otherwise consume a large amount of memory. </li>
     * <li><i>PagedObjectServer.UpdateCacheInitialHashSize</i> - Initial size of the Hash Map for the Update Cache.
     *       For good performance, this should generally be the number of objects
     *       that can be updated at one time by all simultaneous transactions. However,
     *       if necessary, the Hash Map will grow to accomodate more entries.</li>
     * </ul>
     *
     * @return an ObjectServerSession.
     *  
     * @throws ODMGException in the event of an error. These errors include, but are not limited to:
     *  DatabaseNotFoundException if the database doesn't exist;  DatabaseIsReadOnlyException if the 
     *  database is read-only (e.g., on a read-only filesystem), but OPEN_READ_ONLY was not specified
     *  (note that this is really an ODMGRuntimeException).
     */
    public static ObjectServerSession connect(Properties someProperties) throws ODMGException 
    {
        // See if there is already a ObjectServer for this database.
        // Synchronize on sCurrentServers during the get/put process so that another thread
        // cannot create one at the same time.
        String dbName = getRequiredProperty(someProperties, MetaObjectServer.ENERJ_DBNAME_PROP);
        synchronized (sCurrentServers) {
            Session session;
            PagedObjectServer server = (PagedObjectServer)sCurrentServers.get(dbName);
            if (server == null) {
                server = new PagedObjectServer(someProperties);
                sCurrentServers.put(dbName, server);
            }
            
            if (server.mQuiescent) {
                throw new ODMGException("Cannot connect. System in quiescent state.");
            }
        
            //  TODO  - write connect/session info in log.
            session = new Session(server);
            return session;
        } // End synchronized (sCurrentServers)
        
    }

    //----------------------------------------------------------------------
    // ...End of ObjectServer interface methods.
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * The ObjectServerSession object returned by this server.
     */
    private static final class Session implements ObjectServerSession
    {
        private PagedObjectServer mObjectServer;
        private boolean mConnected;
        private Transaction mTxn = null;
        private boolean mAllowNontransactionalReads = false;
        
        //----------------------------------------------------------------------
        /**
         * Constructs a new Session in a connected state.
         *
         * @param aObjectServer the PagedObjectServer to associate with the session.
         */
        Session(PagedObjectServer aObjectServer)
        {
            mObjectServer = aObjectServer;
            mConnected = true;
        }
        
        //----------------------------------------------------------------------
        /**
         * Determines whether the session is connected.
         *
         * @return true if it is, false if not.
         */
        boolean isConnected()
        {
            return mConnected;
        }
        
        //----------------------------------------------------------------------
        /**
         * Marks this session as disconnected.
         */
        void setDisconnected()
        {
            mConnected = false;
        }
        
        //----------------------------------------------------------------------
        /**
         * Gets the transaction associated with this session.
         *
         * @return a Transaction.
         *
         * @throws TransactionNotInProgressException if transaction is null.
         */
        Transaction getTransaction() throws TransactionNotInProgressException
        {
            if (mTxn == null) {
                throw new TransactionNotInProgressException("Transaction not active on session");
            }

            return mTxn;
        }
        
        //----------------------------------------------------------------------
        /**
         * Gets the transaction associated with this session.
         *
         * @return a Transaction, or null if none is active.
         */
        Transaction getTransactionOrNull()
        {
            return mTxn;
        }
        
        //----------------------------------------------------------------------
        /**
         * Sets the transaction associated with this session.
         *
         * @param aTransaction a Transaction, or null if no transaction is active.
         */
        void setTransaction(Transaction aTransaction)
        {
            mTxn = aTransaction;
        }
        
        //----------------------------------------------------------------------
        // Start of ObjectServerSession interface methods...
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        public ObjectServer getObjectServer()
        {
            return mObjectServer;
        }

        //----------------------------------------------------------------------
        public void disconnect() throws ODMGException 
        {
            if (!isConnected()) {
                throw new DatabaseClosedException("Session is not connected");
            }


            // If transaction is active on session, roll it back.
            if (mTxn != null) {
                //  TODO  Log in messages as forced rollback.
                rollbackTransaction();
            }

            //  TODO  - write disconnect/session info in log.
            setDisconnected();
        }

        //----------------------------------------------------------------------
        /**
         * @{inheritDoc}
         */
        public void shutdown() throws ODMGException
        {
            if (isConnected()) {
                disconnect();
            }

            mObjectServer.shutdown();
        }

        //----------------------------------------------------------------------
        public boolean getAllowNontransactionalReads() throws ODMGException
        {
            return mAllowNontransactionalReads;
        }

        //----------------------------------------------------------------------
        public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException
        {
            mAllowNontransactionalReads = isNontransactional;
        }

        //----------------------------------------------------------------------
        public long getCIDForOID(long anOID) throws ODMGException
        {
            if (!mAllowNontransactionalReads) {
                // Validate txn active - interface requirement
                getTransaction();
            }

            // Check the update cache first and get CID from the store request, if there is one.
            PagedStore.StoreObjectRequest storeRequest = mObjectServer.mUpdateCache.lookupStoreRequest(anOID);
            if (storeRequest == null) {
                // Get a read lock on the object otherwise the CID can change for the object after getting the CID.
                // TODO make timeout configurable.
                getLock(anOID, EnerJTransaction.READ, -1);
                return mObjectServer.mPagedStore.getCIDForOID(anOID);
            }
            else {
                // Found an updated version of the object. Use the updated CID.
                return storeRequest.mCID;
            }
        }

        //----------------------------------------------------------------------
        public void storeObjects(SerializedObject[] someObjects) throws ODMGException
        {
            for (SerializedObject object : someObjects) {
                long anOID = object.getOID();
                long aCID = object.getCID();
                
                // Make sure the object is WRITE locked.
                getLock(anOID, EnerJTransaction.WRITE, -1);
                
                Transaction txn = getTransaction();
    
                StoreObjectLogEntry logEntry = new StoreObjectLogEntry( txn.getLogTransactionId(), anOID, aCID, object.getImage());
                mObjectServer.mRedoLogServer.append(logEntry);
    
                PagedStore.StoreObjectRequest request = mObjectServer.mPagedStore.new StoreObjectRequest(aCID, anOID, object.getImage());
                request.mLogEntryPosition = logEntry.getLogPosition();
    
                // Throw update into the cache. It doesn't hit the database (PagedStore) until checkpoint or commit.
                mObjectServer.mUpdateCache.cacheUpdateRequest(request, txn);
            }
        }

        //----------------------------------------------------------------------
        public byte[][] loadObjects(long[] someOIDs) throws ODMGException
        {
            if (!mAllowNontransactionalReads) {
                // Validate txn active - interface requirement
                getTransaction();
            }

            byte[][] objects = new byte[someOIDs.length][];
            int idx = 0;
            for (long oid : someOIDs) {
                // Check the update cache first and get the object from the store request, if there is one.
                PagedStore.StoreObjectRequest storeRequest = mObjectServer.mUpdateCache.lookupStoreRequest(oid);
                if (storeRequest == null) {
                    // Get a READ lock. 
                    // TODO Configurable timeout?
                    getLock(oid, EnerJTransaction.READ, -1);
                    // Not found - load object from PagedStore.
                    objects[idx++] = mObjectServer.mPagedStore.loadObject(oid); // TODO Make PagedStore take a array of oids.
                }
                else {
                    // Found in update cache. use updated object.
                    objects[idx++] = storeRequest.resolveSerializedObject();
                }
            }
            
            return objects;
        }

        //----------------------------------------------------------------------
        public long[] getNewOIDBlock() throws ODMGException
        {
            // Validate txn active - interface requirement
            getTransaction();

            return mObjectServer.mPagedStore.getNewOIDBlock();
        }

        //----------------------------------------------------------------------
        public void beginTransaction() throws ODMGRuntimeException 
        {

            synchronized (mObjectServer.mTransactionLock) {
                // Wait if the server is quiescent.
                while (mObjectServer.mQuiescent) {
                    try {
                        mObjectServer.mTransactionLock.wait();
                    }
                    catch (InterruptedException e) {
                        // Ignore
                    }
                }

                try {
                    BeginTransactionLogEntry logEntry = new BeginTransactionLogEntry();
                    // This append gives us the transaction id too.
                    mObjectServer.mRedoLogServer.append(logEntry);

                    LockServerTransaction lockTxn = mObjectServer.mLockServer.startTransaction();
                    Transaction txn = new Transaction(this, lockTxn, logEntry.getLogPosition(), logEntry.getTransactionId() );
                    setTransaction(txn);
                    mObjectServer.mActiveTransactions.add(txn);
                }
                catch (ODMGRuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error starting transaction: " + e, e);
                }
            } // ...end synchronized (mTransactionLock).
        }

        //----------------------------------------------------------------------
        public void checkpointTransaction() throws ODMGRuntimeException 
        {

            Transaction txn = getTransaction();

            synchronized (mObjectServer.mTransactionLock) {
                // Basically a commit without releasing locks or closing the transaction.
                try {
                    CheckpointTransactionLogEntry logEntry = new CheckpointTransactionLogEntry( txn.getLogTransactionId() );
                    mObjectServer.mRedoLogServer.append(logEntry);

                    txn.prepareTransaction(mObjectServer.mPagedStore);
                }
                catch (ODMGRuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error checkpointing transaction: " + e, e);
                }
            } // ...end synchronized (mTransactionLock).
        }

        //----------------------------------------------------------------------
        public void commitTransaction() throws ODMGRuntimeException 
        {

            Transaction txn = getTransaction();

            synchronized (mObjectServer.mTransactionLock) {
                try {
                    CommitTransactionLogEntry logEntry = new CommitTransactionLogEntry( txn.getLogTransactionId() );
                    mObjectServer.mRedoLogServer.append(logEntry);

                    txn.prepareTransaction(mObjectServer.mPagedStore);
                }
                catch (ODMGRuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error committing transaction: " + e, e);
                }

                mObjectServer.endTransactionAndCheckpoint(txn);
            } // ...end synchronized (mTransactionLock).

            // Release all locks.
            txn.getLockServerTransaction().end();

            // Transaction no longer active.
            setTransaction(null);
        }

        //----------------------------------------------------------------------
        public void rollbackTransaction() throws ODMGRuntimeException 
        {
            Transaction txn = getTransaction();

            synchronized (mObjectServer.mTransactionLock) {
                try {
                    RollbackTransactionLogEntry logEntry = new RollbackTransactionLogEntry( txn.getLogTransactionId() );
                    mObjectServer.mRedoLogServer.append(logEntry);
                }
                catch (ODMGRuntimeException  e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error rolling back transaction: " + e, e);
                }

                mObjectServer.endTransactionAndCheckpoint(txn);
            } // ...end synchronized (mTransactionLock).

            // Dump the update list.
            PagedStore.UpdateRequest request = txn.getFirstUpdateRequest();
            for (; request != null; request = request.mNext) {
                if (request instanceof PagedStore.StoreObjectRequest) {
                    mObjectServer.mUpdateCache.removeStoreRequest((PagedStore.StoreObjectRequest)request);
                }
            }

            txn.clearUpdateRequests();

            // Release all locks.
            txn.getLockServerTransaction().end();

            // Transaction no longer active.
            setTransaction(null);
        }

        //----------------------------------------------------------------------
        public void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException 
        {
            if (mAllowNontransactionalReads && aLockLevel == org.odmg.Transaction.READ) {
                // Don't bother to check for a txn or get read locks if we're doing non-transactional reads.
                return;
            }

            Transaction txn = getTransaction();

            //  TODO  check granularity here...
            Object lockObj = new Long(anOID);
            //Object lockObj = new Long(1);   // Database - make static, class is CID

            LockMode lockMode;
            switch (aLockLevel) {
            case org.odmg.Transaction.READ:
                lockMode = LockMode.READ;
                break;

            case org.odmg.Transaction.UPGRADE:
                lockMode = LockMode.UPGRADE;
                break;

            case org.odmg.Transaction.WRITE:
                lockMode = LockMode.WRITE;
                break;

            default:
                throw new LockNotGrantedException("Invalid lock mode: " + aLockLevel);
            }

            txn.getLockServerTransaction().lock(lockObj, lockMode, aWaitTime);
        }

        //----------------------------------------------------------------------
        // ...End of ObjectServerSession interface methods.
        //----------------------------------------------------------------------

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Our JVM ShutdownHook thread.
     */
    private static final class ShutdownHook extends Thread
    {
        private PagedObjectServer mObjectServer;
        
        //----------------------------------------------------------------------
        ShutdownHook(PagedObjectServer anObjectServer)
        {
            mObjectServer = anObjectServer;
        }
        
        //----------------------------------------------------------------------
        public void run()
        {
            // TODO log shutdown hook invoked.
            //System.err.println("PagedObjectServer Shutdown hook invoked.");
            try {
                mObjectServer.shutdown();
            }
            catch (ODMGException e) {
                //  TODO  log...
                System.err.println("Shutdown problem:");
                e.printStackTrace();
            }
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Internal Transaction representation.
     * This class is <em>not</em> thread-safe - it doesn't need to be.
     */
    private static final class Transaction
    {
        private Session mSession;
        private LockServerTransaction mLockTransaction;
        /** Log position of the BeginTransactionLogEntry, or last CheckpointTransactionLogEntry. */
        private long mLogStartPosition;
        private long mLogTransactionId;

        /** Head of Update request list. */
        private PagedStore.UpdateRequest mUpdateRequestHead;
        /** Head of Update request list. New requests are added to the tail. */
        private PagedStore.UpdateRequest mUpdateRequestTail;
        
        //----------------------------------------------------------------------
        /**
         * Constructs a new Transaction.
         *
         * @param aSession the Session for the ObjectServer.
         * @param aLockTransaction the transaction for the LockServer.
         * @param aLogStartPosition the log position of the BeginTransactionLogEntry.
         * @param aLogTransactionId the transaction id for purposes of logging.
         */
        Transaction(Session aSession, LockServerTransaction aLockTransaction, long aLogStartPosition,
                    long aLogTransactionId)
        {
            mSession = aSession;
            mLockTransaction = aLockTransaction;
            mLogStartPosition = aLogStartPosition;
            mLogTransactionId = aLogTransactionId;
        }

        //----------------------------------------------------------------------
        /**
         * Gets the Session of this transaction.
         *
         * @return the Session.
         */
        Session getSession()
        {
            return mSession;
        }

        //----------------------------------------------------------------------
        /**
         * Gets the LockServerTransaction of this transaction.
         *
         * @return the LockServerTransaction.
         */
        LockServerTransaction getLockServerTransaction()
        {
            return mLockTransaction;
        }

        //----------------------------------------------------------------------
        /**
         * Gets the log transaction id. This id is unique across runs of the server
         * for a particular log.
         *
         * @return the log transaction id.
         */
        long getLogTransactionId()
        {
            return mLogTransactionId;
        }
        
        //----------------------------------------------------------------------
        /**
         * Gets the log position of the BeginTransactionLogEntry, or last CheckpointTransactionLogEntry.
         *
         * @return the log position.
         */
        long getLogStartPosition()
        {
            return mLogStartPosition;
        }
        
        //----------------------------------------------------------------------
        /**
         * Sets the log position of the last CheckpointTransactionLogEntry.
         *
         * @param aLogPosition the log position.
         */
        void setLogStartPosition(long aLogPosition)
        {
            mLogStartPosition = aLogPosition;
        }
        
        //----------------------------------------------------------------------
        /**
         * Gets the first update request. The requests are in FIFO order.
         *
         * @return the first PagedStore.UpdateRequest, or null if there are no updates.
         */
        PagedStore.UpdateRequest getFirstUpdateRequest()
        {
            return mUpdateRequestHead;
        }
        
        //----------------------------------------------------------------------
        /**
         * Adds an update request to the tail of the list of updates.
         *
         * @param anUpdateRequest a PagedStore.UpdateRequest.
         */
        void addUpdateRequest(PagedStore.UpdateRequest anUpdateRequest)
        {
            // First entry?
            if (mUpdateRequestTail == null) {
                mUpdateRequestHead = anUpdateRequest;
            }
            else {
                mUpdateRequestTail.mNext = anUpdateRequest;
            }
            
            anUpdateRequest.mPrev = mUpdateRequestTail;
            anUpdateRequest.mNext = null;
            mUpdateRequestTail = anUpdateRequest;
        }

        //----------------------------------------------------------------------
        /**
         * Removes an update request from the list of updates.
         *
         * @param anUpdateRequest a PagedStore.UpdateRequest.
         */
        void removeUpdateRequest(PagedStore.UpdateRequest anUpdateRequest)
        {
            // Unlink.
            if (anUpdateRequest.mPrev != null) {
                anUpdateRequest.mPrev.mNext = anUpdateRequest.mNext;
            }
            else {
                // Unlink from the head.
                mUpdateRequestHead = anUpdateRequest.mNext;
            }
            
            if (anUpdateRequest.mNext != null) {
                anUpdateRequest.mNext.mPrev = anUpdateRequest.mPrev;
            }
            else {
                // Unlink from the tail.
                mUpdateRequestTail = anUpdateRequest.mPrev;
            }
            
            anUpdateRequest.mNext = null;
            anUpdateRequest.mPrev = null;
        }

        //----------------------------------------------------------------------
        /**
         * Clears the entire update request list.
         */
        void clearUpdateRequests()
        {
            mUpdateRequestHead = null;
            mUpdateRequestTail = null;
        }

        //----------------------------------------------------------------------
        /**
         * Prepare to checkpoint or commit this transaction. The transaction's updates
         * are moved to aPagedStore's queue (without waiting for them to be processed)
         * and this transaction's update list is cleared. The log entry is not
         * written and this does not release locks. To conform to redo logging 
         * rules, the log entry MUST be written to prior to calling this method.
         *
         * @param aPagedStore the PagedStore where transaction will be stored.
         *
         * @throws ODMGException if an error occurs.
         */
        private void prepareTransaction(PagedStore aPagedStore) throws ODMGException
        {
            PagedStore.UpdateRequest request = getFirstUpdateRequest();
            for (; request != null; request = request.mNext) {
                aPagedStore.queueStorageRequest(request);
            }

            clearUpdateRequests();
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * The Update Cache.
     * This class is thread-safe.
     */
    private static final class UpdateCache
    {
        private int mMaxCacheSize;
        private HashMap mHashMap;
        private int mUsedCacheSize = 0;
        
        //----------------------------------------------------------------------
        UpdateCache(int aMaxCacheSize, int aHashMapSize)
        {
            mMaxCacheSize = aMaxCacheSize;
            mHashMap = new HashMap(aHashMapSize, 1.0F);
        }

        //----------------------------------------------------------------------
        /**
         * Attempts to find a PagedStore.StoreObjectRequest in the cache.
         *
         * @param anOID the OID corresponding to the desired store request.
         *
         * @return a PagedStore.StoreObjectRequest representing an updated object,
         *  or null if the object has not been updated.
         */
        PagedStore.StoreObjectRequest lookupStoreRequest(long anOID)
        {
            synchronized (mHashMap) {
                return (PagedStore.StoreObjectRequest)mHashMap.get( new Long(anOID) );
            }
        }

        //----------------------------------------------------------------------
        /**
         * Removes a PagedStore.StoreObjectRequest from the cache. The supplied request
         * and the request in the cache must be the same object in order to be
         * removed. This prevents removal of a later update to an object by another
         * transaction. The cache size
         * is reduced by the amount used by the request. This method
         * does nothing if there is no corresponding update in the cache.
         *
         * @param aStoreRequest the store request to remove.
         */
        void removeStoreRequest(PagedStore.StoreObjectRequest aStoreRequest)
        {
            synchronized (mHashMap) {
                Long oid = new Long(aStoreRequest.mOID);
                PagedStore.StoreObjectRequest cachedStoreRequest = (PagedStore.StoreObjectRequest)mHashMap.get(oid);
                
                // Must be the exact same request.
                if (cachedStoreRequest != aStoreRequest) {
                    return;
                }
                
                mHashMap.remove(oid);

                if (aStoreRequest != null && aStoreRequest.mSerializedObject != null) {
                    // Remove the previous object's length from the used size.
                    mUsedCacheSize -= aStoreRequest.mSerializedObject.length;
                }
            }
        }

        //----------------------------------------------------------------------
        /**
         * Adds an UpdateRequest to this UpdateCache and the specified Transaction.
         * UpdateRequest.mLogEntryPosition must be set prior to calling this method.
         * If the request is to store an object and the object is already in
         * the update cache, the cache entry is replaced and the previous request
         * is removed from the transaction's update list. If the request is to delete
         * an object and an update request for the object is in the cache, the cache entry is removed
         * and the previous update request is removed from the transaction's update
         * list.
         * <p>
         * If the request is to store an object and the serialized size would exceed
         * the maximum cache size, the serialized bytes are cleared from the entry
         * and the log position/entry is used instead.
         *
         * @param anUpdateRequest the PagedStore.UpdateRequest
         * @param aTransaction the Transaction performing the update.
         */
        void cacheUpdateRequest(PagedStore.UpdateRequest anUpdateRequest, Transaction aTransaction)
        {
            PagedStore.StoreObjectRequest prevStoreRequest = null;
            Long oid = new Long(anUpdateRequest.mOID);

            if (anUpdateRequest instanceof PagedStore.StoreObjectRequest) {
                PagedStore.StoreObjectRequest storeRequest = (PagedStore.StoreObjectRequest)anUpdateRequest;
                synchronized (mHashMap) {
                    prevStoreRequest = (PagedStore.StoreObjectRequest)mHashMap.put(oid, storeRequest);

                    if (prevStoreRequest != null && prevStoreRequest.mSerializedObject != null) {
                        // Remove the previous object's length from the used size.
                        mUsedCacheSize -= prevStoreRequest.mSerializedObject.length;
                    }

                    // Check if we'd exceed the maximum cache size.
                    if ((mUsedCacheSize + storeRequest.mSerializedObject.length) > mMaxCacheSize) {
                        // Use the log entry for the serialized bytes.
                        storeRequest.mSerializedObject = null;
                    }
                    else {
                        mUsedCacheSize += storeRequest.mSerializedObject.length;
                    }
                }
            }
            else { // DeleteObjectRequest
                synchronized (mHashMap) {
                    prevStoreRequest = (PagedStore.StoreObjectRequest)mHashMap.remove(oid);
                    if (prevStoreRequest != null && prevStoreRequest.mSerializedObject != null) {
                        // Remove the previous object's length from the used size.
                        mUsedCacheSize -= prevStoreRequest.mSerializedObject.length;
                    }
                }
            }
            
            // Update transaction. Remove any previous store request.
            if (prevStoreRequest != null) {
                aTransaction.removeUpdateRequest(prevStoreRequest);
            }
            
            aTransaction.addUpdateRequest(anUpdateRequest);
        }
    }
}


