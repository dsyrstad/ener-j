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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/BDBObjectServer.java,v 1.6 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server.bdb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enerj.core.ClassVersionSchema;
import org.enerj.core.EnerJTransaction;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Schema;
import org.enerj.core.SystemCIDMap;
import org.enerj.server.ClassInfo;
import org.enerj.server.FilePageServer;
import org.enerj.server.LockMode;
import org.enerj.server.LockServer;
import org.enerj.server.LockServerTransaction;
import org.enerj.server.ObjectServer;
import org.enerj.server.ObjectServerSession;
import org.enerj.server.PageServer;
import org.enerj.server.PageServerException;
import org.enerj.server.PageServerNotFoundException;
import org.enerj.server.PagedStore;
import org.enerj.server.PluginHelper;
import org.enerj.server.RedoLogServer;
import org.enerj.server.SerializedObject;
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
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseNotFoundException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.Transaction;
import org.odmg.TransactionNotInProgressException;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.txn.Lock;

/** 
 * Ener-J ObjectServer based on Berkeley DB Java Edition. Stores objects in BDB databases.
 * This class is thread-safe.<p>
 *
 * This server is referenced by the properties described in the {@link connect} method.
 *
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see ObjectServer
 */
public class BDBObjectServer extends BaseObjectServer
{
    private static final Logger sLogger = Logger.getLogger(BDBObjectServer.class.getName()); 
    
    /** HashMap of database names to BDBObjectServers. */
    private static HashMap<String, BDBObjectServer> sCurrentServers = new HashMap<String, BDBObjectServer>(20);

    /** Our shutdown hook. */
    private static Thread sShutdownHook = null;

    /** The database name. */
    private String mDBName;
    
    /** Berkeley DB Environment. */
    private Environment bdbEnvironment = null;
    /** Berkeley DB Database. This is the main OID to object map. */
    private Database bdbDatabase = null;
    
    /** List of active transactions. List of BDBObjectServer.Transaction. Synchronized
     * around mTransactionLock.
     */
    private List<Transaction> mActiveTransactions = new LinkedList<Transaction>();
    
    /** List of active sessions. */
    private List<Session> mActiveSessions = new LinkedList<Session>();
    
    /** True if any session that connected was running locally in the client. */
    private boolean mIsLocal = false;
    
    /** True if the server should become quiescent, i.e. don't allow new transactions to 
     * start. 
     */
    private boolean mQuiescent = false;

    
    /**
     * Construct a BDBObjectServer.
     *
     * @param someProperties the properties given to the connect() method.
     *
     * @throws ODMGException if an error occurs.
     */
    private BDBObjectServer(Properties someProperties) throws ODMGException  
    {
        super(someProperties);
        
        String dbDir = getRequiredProperty(someProperties, ENERJ_DBDIR_PROP);
        mDBName = getRequiredProperty(someProperties, ENERJ_DBNAME_PROP);
        mIsLocal = getBooleanProperty(someProperties, ENERJ_CLIENT_LOCAL);
        
        String localModeMsg = (mIsLocal ? " in local mode" : "");
        sLogger.fine("Server " + this + " is starting up" + localModeMsg + "...");

        // Register a shutdown hook...
        sShutdownHook = new ShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(sShutdownHook);

        boolean success = false;
        try {
            EnvironmentConfig bdbEnvConfig = new EnvironmentConfig();
            bdbEnvConfig.setTransactional(true);
    
            bdbEnvironment = new Environment( new File(dbDir), bdbEnvConfig);
            
            DatabaseConfig bdbDBConfig = new DatabaseConfig();
            bdbDBConfig.setTransactional(true);
            bdbDBConfig.setDeferredWrite(false);
            // TODOLOW In-memory db's could be supported with DeferredWrite(true)
            // TODO bdbDBConfig.setReadOnly(true); based on Prop
    
            bdbDatabase = bdbEnvironment.openDatabase(null, mDBName, bdbDBConfig);
            success = true;
        }
        catch (DatabaseException e) {
            throw new ODMGException("Error opening database", e);
        }
        finally {
            if (!success) {
                try {
                    if (bdbDatabase != null) {
                        bdbDatabase.close();
                    }
    
                    if (bdbEnvironment != null) {
                        bdbEnvironment.close();
                    }
                }
                catch (DatabaseException e) {
                    // Ignore. Already in exception.
                }
            }
        }

        sLogger.fine("Server " + this + " is started" + localModeMsg + '.');
    }
    
    /**
     * Creates a new database on disk. The "enerj.dbpath" system property must be set.
     *
     * @param aDescription a description for the database. May be null.
     * @param aDBName The database name. See {@link #connect(Properties)}.

     * @throws ODMGException if an error occurs.
     */
    public static void createDatabase(String aDescription, String aDBName) throws ODMGException
    {
        // Load database properties.  First copy system properties.
        Properties props = new Properties( System.getProperties() );

        String propFileName = aDBName + File.separatorChar + aDBName + ".properties";
        String dbPath = getRequiredProperty(props, ENERJ_DBPATH_PROP);
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

        createDatabase(aDescription, aDBName, props, propFile);
    }


    /**
     * Creates a new database on disk. The "enerj.dbpath" system property must be set.
     *
     * @param aDescription a description for the database. May be null.
     * @param aDBName The database name. See {@link #connect(Properties)}.
     * @param someDBProps the database properties, normally read from a database propeties file.
     * @param aDBDir the base directory of the database.
     * 
     * @throws ODMGException if an error occurs.
     */
	private static void createDatabase(String aDescription, String aDBName, Properties someDBProps, File aDBDir) throws ODMGException 
    {
	    sLogger.fine("Creating database: " + aDBName);
        
		someDBProps.setProperty(ENERJ_DBDIR_PROP, aDBDir.getParent() );
        someDBProps.setProperty(ENERJ_DBNAME_PROP, aDBName);

        Session session = null;
        boolean completed = false;
        Environment bdbEnv = null;
        Database bdbDB = null;
        try {
            EnvironmentConfig bdbEnvConfig = new EnvironmentConfig();
            bdbEnvConfig.setAllowCreate(true);
            bdbEnvConfig.setTransactional(true);

            bdbEnv = new Environment(aDBDir, bdbEnvConfig);
            
            DatabaseConfig bdbDBConfig = new DatabaseConfig();
            bdbDBConfig.setAllowCreate(true);
            bdbDBConfig.setExclusiveCreate(true);
            bdbDBConfig.setTransactional(true);
            bdbDBConfig.setDeferredWrite(false);
            
            // This database's key is an OID.
            bdbDB = bdbEnv.openDatabase(null, aDBName, bdbDBConfig);
            bdbDB.close();
            bdbDB = null;
            
            bdbEnv.close();
            bdbEnv = null;
            
            // Create a session so that we can initialize the schema.
            Properties tmpDBProps = new Properties(someDBProps);
            tmpDBProps.setProperty(ENERJ_CLIENT_LOCAL, "true");

            session = (Session)connect(tmpDBProps, true);
            initDBObjects(session, aDescription);
            completed = true;
        }
        catch (ODMGException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGException("Error creating database: " + e, e);
        }
        finally {
            try {
                if (bdbDB != null) {
                    bdbDB.close();
                }
                
                if (bdbEnv != null) {
                    bdbEnv.close();
                }
            }
            catch (DatabaseException e) {
                throw new ODMGException("Error closing database", e);
            }
            
            if (session != null) {
                if (!completed) {
                    session.rollbackTransaction();
                }
                
                session.disconnect();
                // Shuts down the BDBObjectServer for the database. This should happen
                // on the session disconnect because we're local, but just in case...
                session.shutdown();
            }
        }
	}

    /**
     * Shuts down this server.
     */
    public void shutdown() throws ODMGException
    {
        if (mQuiescent) {
            return; // Already shutting down...
        }
        
        sLogger.fine("Server " + this + " is shutting down...");
        
        // Quiesce the system. Stops new connections and txns. 
        mQuiescent = true;

        // Force any open sessions to disconnect and their open transactions to abort.
        while (true) {
            synchronized (sCurrentServers) {
                if (mActiveSessions.isEmpty()) {
                    break;
                }

                Session session = mActiveSessions.get(0);
                session.disconnect();
            }
        }

        synchronized (sCurrentServers) {
            if (!mActiveTransactions.isEmpty()) {
                //  TODO  log msg
                throw new ODMGException("Transaction list is not clear! INTERNAL ERROR!");
            }

            // Remove this server from the global list.
            sCurrentServers.remove(mDBName);
        } // End synchronized
        
        try {
            if (bdbDatabase != null) {
                bdbDatabase.close();
            }

            if (bdbEnvironment != null) {
                bdbEnvironment.close();
            }
        }
        catch (DatabaseException e) {
            throw new ODMGException("Error closing database", e);
        }

        super.shutdown();
        
        try {
            Runtime.getRuntime().removeShutdownHook(sShutdownHook);
        }
        catch (Exception e) {
            // Ignore - shutdown is in progress.
        }

        sLogger.fine("Server " + this + " is shutdown.");
    }


    // Start of ObjectServer interface methods...

    /**
     * Connects to a database.
     *
     * @param someProperties properties which specify the connect parameters.
     * The properties must contain the following keys:<br>
     * <ul>
     * <li><i>enerj.dbname</i> - the database name. </li>
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
        return connect(someProperties, getBooleanProperty(someProperties, ENERJ_SCHEMA_SESSION_PROPERTY));
    }
    
    /**
     * Helper for {@link BDBObjectServer#connect(Properties)}. 
     */
    private static ObjectServerSession connect(Properties someProperties, boolean isSchemaSession) throws ODMGException 
    {
        // Look up {dbname}.properties on enerj.dbpath.  
        String dbPath = someProperties.getProperty(ENERJ_DBPATH_PROP);
        if (dbPath == null) {
            dbPath = ".";
        }
        
        String dbname = getRequiredProperty(someProperties, ENERJ_DBNAME_PROP);

        Properties dbConfigProps = new Properties(someProperties);

        String propFileName = dbname + File.separatorChar + dbname + ".properties";
        File propFile = FileUtil.findFileOnPath(propFileName, dbPath);
        // NOTE: It's OK not to find the property file. In this case, we just try to use URI/System properties.
        boolean foundPropFile = false;
        if (propFile != null) {
            // Combine the given properties and the ones from the config file, with the latter overriding the former.
            try {
                FileInputStream inPropFile = new FileInputStream(propFile);
                dbConfigProps.load(inPropFile);
                dbConfigProps.setProperty(ENERJ_DBDIR_PROP, propFile.getParent() );
                foundPropFile = true;
            }
            catch (IOException e) {
                throw new ODMGException("Error reading " + propFile, e);
            }
        } 
        
        if (!foundPropFile && dbConfigProps.getProperty("BDBObjectServer.PageServerClass") == null) {
            throw new ODMGException("Cannot open database " + dbname + " because it was not found or not configured properly.");
        }

        // See if there is already a ObjectServer for this database.
        // Synchronize on sCurrentServers during the get/put process so that another thread
        // cannot create one at the same time.
        synchronized (sCurrentServers) {
            BDBObjectServer server = (BDBObjectServer)sCurrentServers.get(dbname);
            if (server == null) {
                server = new BDBObjectServer(dbConfigProps);
                sCurrentServers.put(dbname, server);
            }
            
            if (server.mQuiescent) {
                throw new ODMGException("Cannot connect. System in quiescent state.");
            }
        
            Session session = server.new Session(server, isSchemaSession);
            server.mActiveSessions.add(session);
            
            return session;
        } // End synchronized (sCurrentServers)
    }
    
    /**
     * Removes a session from the active session list.
     */
    private void removeSession(Session aSession)
    {
        boolean shutdown = false;
        synchronized (sCurrentServers) {
            mActiveSessions.remove(aSession);
            // If no more sessions and running locally or if schema session is the only one left.
            shutdown = mIsLocal && (mActiveSessions.isEmpty() ||
                        (mActiveSessions.size() == 1 && mActiveSessions.get(0) == getSchemaSessionOrNull()));
        }
        
        if (shutdown) {
            try {
                shutdown();
            }
            catch (ODMGException e) {
                throw new ODMGRuntimeException(e);
            }
        }
    }


    // ...End of ObjectServer interface methods.

    /**
     * The ObjectServerSession object returned by this server.
     */
    private final class Session extends BaseObjectServerSession
    {
        private Transaction mTxn = null;
        /** If true, this is a privileged session that may update the schema. */
        private boolean mIsSchemaSession = false;

        /**
         * Constructs a new Session in a connected state.
         *
         * @param anObjectServer the BDBObjectServer to associate with the session.
         */
        Session(BDBObjectServer anObjectServer, boolean isSchemaSession)
        {
            super(anObjectServer);
            mIsSchemaSession = isSchemaSession;
        }
        
        /**
         * Gets the transaction associated with this session.
         *
         * @return a Transaction.
         *
         * @throws TransactionNotInProgressException if transaction is not in progress and non-transactional reads are not allowed.
         */
        Transaction getTransaction() throws TransactionNotInProgressException
        {
            if (mTxn == null && !getAllowNontransactionalReads()) {
                throw new TransactionNotInProgressException("Transaction not in progress.");
            }

            return mTxn;
        }
        
        /**
         * Gets the BDB transaction associated with this session.
         *
         * @return a Transaction.
         *
         * @throws TransactionNotInProgressException if transaction is not in progress and non-transactional reads are not allowed.
         */
        private com.sleepycat.je.Transaction getBDBTransaction() throws TransactionNotInProgressException
        {
            Transaction txn = getTransaction();
            if (txn == null) {
                return null;
            }
            
            return txn.getBdbTransaction();
        }

        /**
         * Gets the transaction associated with this session.
         *
         * @return a Transaction, or null if none is active.
         */
        Transaction getTransactionOrNull()
        {
            return mTxn;
        }
        

        /**
         * Sets the transaction associated with this session.
         *
         * @param aTransaction a Transaction, or null if no transaction is active.
         */
        void setTransaction(Transaction aTransaction)
        {
            mTxn = aTransaction;
            setTransactionActive(mTxn != null);
        }
        

        // Start of ObjectServerSession interface methods...

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

            super.disconnect();
            removeSession(this);
        }
        
        /**
         * @return the LockMode to use for reads.
         */
        private com.sleepycat.je.LockMode getReadLockMode()
        {
            return getAllowNontransactionalReads() ? com.sleepycat.je.LockMode.READ_UNCOMMITTED : com.sleepycat.je.LockMode.DEFAULT;
        }

        public ClassInfo[] getClassInfoForOIDs(long[] someOIDs) throws ODMGException
        {
            com.sleepycat.je.Transaction txn = getBDBTransaction();

            ClassInfo[] classInfo = new ClassInfo[someOIDs.length];
            for (int i = 0; i < someOIDs.length; i++) {
                long anOID = someOIDs[i];
                if (anOID == BaseObjectServer.SCHEMA_OID) {
                    long cid = SystemCIDMap.getSystemCIDForClassName(SCHEMA_CLASS_NAME);
                    classInfo[i] = new ClassInfo(cid, SCHEMA_CLASS_NAME);
                }
                else if (anOID != ObjectSerializer.NULL_OID) {
                    long cid;
                    DatabaseEntry data = readObjectEntry(anOID);
                    if (data == null) {
                        cid = ObjectSerializer.NULL_CID;
                    }
                    else {
                        cid = getCIDFromEntry(data);
                    }
                    
                    // Resolve the class name. Try system CIDs first.
                    String className = SystemCIDMap.getSystemClassNameForCID(cid);
                    if (className == null) {
                        Schema schema = getSchema();
                        ClassVersionSchema version = schema.findClassVersion(cid);
                        if (version != null) {
                            className = version.getClassSchema().getClassName();
                        }
                    }
                    
                    if (className != null) {
                        classInfo[i] = new ClassInfo(cid, className);
                    }
                }
            }

            return classInfo;
        }

        /**
         * Reads an object entry.
         *
         * @param anOID the OID of the object.
         * 
         * @return the object's DatabaseEntry, or null if not found.
         *  
         * @throws ODMGException if an error occurs.
         */
        private DatabaseEntry readObjectEntry(long anOID) throws ODMGException
        {
            com.sleepycat.je.Transaction txn = getBDBTransaction();
            if (!getAllowNontransactionalReads()) {
                // Validate txn active - interface requirement
                txn = getBDBTransaction();
            }

            DatabaseEntry key = createOIDKey(anOID);
            DatabaseEntry data = new DatabaseEntry();
            OperationStatus status;
            try {
                status = bdbDatabase.get(txn, key, data, getReadLockMode());
            }
            catch (DatabaseException e) {
                throw new ODMGException("Error reading object", e);
            }
            
            if (status == OperationStatus.NOTFOUND) {
                return null;
            }
            else if (status == OperationStatus.SUCCESS) {
                return data;
            }
            else {
                throw new ODMGException("Error reading object, operation status=" + status);
            }
        }

        /**
         * Creates a DatabaseEntry key from an OID.
         *
         * @param anOID
         * 
         * @return the key.
         */
        private DatabaseEntry createOIDKey(long anOID)
        {
            DatabaseEntry key = new DatabaseEntry();
            TupleBinding.getPrimitiveBinding(Long.class).objectToEntry(anOID, key);
            return key;
        }
        
        /**
         * @return the CID stored in an object entry.
         */
        private long getCIDFromEntry(DatabaseEntry entry)
        {
            return (Long)TupleBinding.getPrimitiveBinding(Long.class).entryToObject(entry);
        }


        public void storeObjects(SerializedObject[] someObjects) throws ODMGException
        {
            com.sleepycat.je.Transaction txn = getBDBTransaction();
            
            // TODO - SerializedObject should contain the version #. We should compare the object's version
            // to the current version before writing.

            for (SerializedObject object : someObjects) {
                long oid = object.getOID();

                // Prevent schema OIDs from being stored unless this is the schema session.
                if (!mIsSchemaSession && oid == SCHEMA_OID) {
                    throw new ODMGException("Client is not allowed to update schema via object modification.");
                }
                
                DatabaseEntry key = createOIDKey(oid);
                DatabaseEntry data = new DatabaseEntry();
                SerializedObjectTupleBinding binding = new SerializedObjectTupleBinding(true);
                binding.objectToEntry(object, data);
                
                try {
                    bdbDatabase.put(txn, key, data);
                }
                catch (DatabaseException e) {
                    throw new ODMGException("Error writing object", e);
                }
            }
            
            super.storeObjects(someObjects);
        }


        public byte[][] loadObjects(long[] someOIDs) throws ODMGException
        {
            if (!getAllowNontransactionalReads()) {
                // Validate txn active - interface requirement
                getTransaction();
            }

            byte[][] objects = new byte[someOIDs.length][];
            int idx = 0;
            for (long oid : someOIDs) {
                // Check the update cache first and get the object from the store request, if there is one.
                PagedStore.StoreObjectRequest storeRequest = mServerUpdateCache.lookupStoreRequest(oid);
                if (storeRequest == null) {
                    // Get a READ lock. 
                    // TODO Configurable timeout?
                    getLock(oid, EnerJTransaction.READ, -1);
                    // Not found - load object from PagedStore.
                    objects[idx++] = mPagedStore.loadObject(oid); // TODO Make PagedStore take a array of oids.
                }
                else {
                    // Found in update cache. use updated object.
                    objects[idx++] = storeRequest.resolveSerializedObject();
                }
            }
            
            return objects;
        }


        public long[] getNewOIDBlock(int anOIDCount) throws ODMGException
        {
            // Validate txn active - interface requirement
            getTransaction();

            return mPagedStore.getNewOIDBlock(anOIDCount);
        }


        public void beginTransaction() throws ODMGRuntimeException 
        {
            super.beginTransaction();

            synchronized (mTransactionLock) {
                // Wait if the server is quiescent.
                while (mQuiescent) {
                    try {
                        mTransactionLock.wait();
                    }
                    catch (InterruptedException e) {
                        // Ignore
                    }
                }

                try {
                    BeginTransactionLogEntry logEntry = new BeginTransactionLogEntry();
                    // This append gives us the transaction id too.
                    mRedoLogServer.append(logEntry);

                    LockServerTransaction lockTxn = mLockServer.startTransaction();
                    Transaction txn = new Transaction(this, lockTxn, logEntry.getLogPosition(), logEntry.getTransactionId() );
                    setTransaction(txn);
                    mActiveTransactions.add(txn);
                }
                catch (ODMGRuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error starting transaction: " + e, e);
                }
            } // ...end synchronized (mTransactionLock).
        }


        public void checkpointTransaction() throws ODMGRuntimeException 
        {
            super.checkpointTransaction();

            synchronized (mTransactionLock) {
                // Basically a commit without releasing locks or closing the transaction.
                try {
                    Transaction txn = getTransaction();
                    CheckpointTransactionLogEntry logEntry = new CheckpointTransactionLogEntry( txn.getLogTransactionId() );
                    mRedoLogServer.append(logEntry);

                    txn.prepareTransaction(mPagedStore);
                }
                catch (ODMGRuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error checkpointing transaction: " + e, e);
                }
            } // ...end synchronized (mTransactionLock).
        }


        public void commitTransaction() throws ODMGRuntimeException 
        {
            super.commitTransaction();

            Transaction txn = getTransaction();
            
            synchronized (mTransactionLock) {
                try {
                    CommitTransactionLogEntry logEntry = new CommitTransactionLogEntry( txn.getLogTransactionId() );
                    mRedoLogServer.append(logEntry);

                    txn.prepareTransaction(mPagedStore);
                }
                catch (ODMGRuntimeException e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error committing transaction: " + e, e);
                }

                endTransactionAndCheckpoint(txn);
            } // ...end synchronized (mTransactionLock).

            // Release all locks.
            txn.getLockServerTransaction().end();

            // Transaction no longer active.
            setTransaction(null);
        }


        public void rollbackTransaction() throws ODMGRuntimeException 
        {
            super.rollbackTransaction();
            
            Transaction txn = getTransaction();

            synchronized (mTransactionLock) {
                try {
                    RollbackTransactionLogEntry logEntry = new RollbackTransactionLogEntry( txn.getLogTransactionId() );
                    mRedoLogServer.append(logEntry);
                }
                catch (ODMGRuntimeException  e) {
                    throw e;
                }
                catch (Exception e) {
                    throw new ODMGRuntimeException("Error rolling back transaction: " + e, e);
                }

                endTransactionAndCheckpoint(txn);
            } // ...end synchronized (mTransactionLock).

            // Dump the update list.
            PagedStore.UpdateRequest request = txn.getFirstUpdateRequest();
            for (; request != null; request = request.mNext) {
                if (request instanceof PagedStore.StoreObjectRequest) {
                    mServerUpdateCache.removeStoreRequest((PagedStore.StoreObjectRequest)request);
                }
            }

            txn.clearUpdateRequests();

            // Release all locks.
            if (txn.getLockServerTransaction().isActive()) {
                txn.getLockServerTransaction().end();
            }

            // Transaction no longer active.
            setTransaction(null);
        }

        public void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException 
        {
            if (getAllowNontransactionalReads() && aLockLevel == org.odmg.Transaction.READ) {
                // Don't bother to check for a txn or get read locks if we're doing non-transactional reads.
                return;
            }

            //  TODO  check granularity here...
            Object lockObj = anOID;

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

            getTransaction().getLockServerTransaction().lock(lockObj, lockMode, aWaitTime);
        }

        // ...End of ObjectServerSession interface methods.
    }



    /**
     * Our JVM ShutdownHook thread.
     */
    private static final class ShutdownHook extends Thread
    {
        private BDBObjectServer mObjectServer;
        

        ShutdownHook(BDBObjectServer anObjectServer)
        {
            mObjectServer = anObjectServer;
        }
        

        public void run()
        {
            // TODO log shutdown hook invoked.
            //System.err.println("BDBObjectServer Shutdown hook invoked.");
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
    


    /**
     * Internal Transaction representation.
     * This class is <em>not</em> thread-safe - it doesn't need to be.
     */
    private static final class Transaction
    {
        private Session mSession;
        private com.sleepycat.je.Transaction bdbTransaction; 

        /**
         * Constructs a new Transaction.
         *
         * @param session the Session for the ObjectServer.
         */
        Transaction(Session session, com.sleepycat.je.Transaction bdbTransaction)
        {
            this.mSession = session;
            this.bdbTransaction = bdbTransaction;
        }


        /**
         * Gets the Session of this transaction.
         *
         * @return the Session.
         */
        Session getSession()
        {
            return mSession;
        }


        com.sleepycat.je.Transaction getBdbTransaction()
        {
            return bdbTransaction;
        }
    }

    /**
     * Serialize and deserialize the internal object entry. A SerializedObject is returned, but OID will be zero.
     * Optionally, the serialized object's bytes can be ignored.
     */
    private static final class SerializedObjectTupleBinding extends TupleBinding
    {
        private boolean readObjBytes;
        
        SerializedObjectTupleBinding(boolean readObjBytes)
        {
            this.readObjBytes = readObjBytes;
        }
        
        @Override
        public SerializedObject entryToObject(TupleInput input)
        {
            int len = input.getBufferLength();
            long cid = input.readLong();
            byte[] obj;
            if (readObjBytes) {
                obj = new byte[ len - 8 ]; // long(CID) is 8 bytes
                input.readFast(obj);
            }
            else {
                obj = null;
            }

            return new SerializedObject(0, cid, obj);
        }

        @Override
        public void objectToEntry(Object object, TupleOutput output)
        {
            SerializedObject serializedObj = (SerializedObject)object;
            output.writeLong(serializedObj.getCID());
            output.writeFast(serializedObj.getImage());
        }
    }
}
