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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.enerj.core.ClassVersionSchema;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Schema;
import org.enerj.core.SystemCIDMap;
import org.enerj.server.ClassInfo;
import org.enerj.server.ObjectServer;
import org.enerj.server.ObjectServerSession;
import org.enerj.server.SerializedObject;
import org.enerj.util.FileUtil;
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseNotFoundException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
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
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

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
    
    /** List of active transactions. List of BDB Transaction. Synchronized
     * around mTransactionLock.
     */
    private List<Transaction> mActiveTransactions = new LinkedList<Transaction>();

    /** Synchronization lock for transaction-oriented methods. */
    private Object mTransactionLock = new Object();
    
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
            //bdbEnvConfig.setCacheSize(512000000); // TODO config
            bdbEnvConfig.setTxnSerializableIsolation(true); // TODO This should be config, and should be able to override by txn
            // THIS IS IMPORTANT -- read up writes thru BDB buffers, but not thru OS. We use Transaction.commitSync() to force OS update.
            bdbEnvConfig.setTxnWriteNoSync(true); 
            bdbEnvironment = new Environment( new File(dbDir), bdbEnvConfig);
            
            DatabaseConfig bdbDBConfig = new DatabaseConfig();
            bdbDBConfig.setTransactional(true);

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
     * @param aDBPropFile the properties file of the database.
     * 
     * @throws ODMGException if an error occurs.
     */
	private static void createDatabase(String aDescription, String aDBName, Properties someDBProps, File aDBPropFile) throws ODMGException 
    {
	    sLogger.fine("Creating database: " + aDBName);
        
	    File dbDir = aDBPropFile.getParentFile();
		someDBProps.setProperty(ENERJ_DBDIR_PROP, dbDir.getAbsolutePath());
        someDBProps.setProperty(ENERJ_DBNAME_PROP, aDBName);

        Session session = null;
        boolean completed = false;
        Environment bdbEnv = null;
        Database bdbDB = null;
        try {
            EnvironmentConfig bdbEnvConfig = new EnvironmentConfig();
            bdbEnvConfig.setAllowCreate(true);
            bdbEnvConfig.setTransactional(true);

            bdbEnv = new Environment(dbDir, bdbEnvConfig);
            
            DatabaseConfig bdbDBConfig = new DatabaseConfig();
            bdbDBConfig.setAllowCreate(true);
            bdbDBConfig.setExclusiveCreate(true);
            bdbDBConfig.setTransactional(true);
            bdbDBConfig.setDeferredWrite(false);
            bdbDBConfig.setNodeMaxEntries(1024);
            
            // This database's key is an OID.
            bdbDB = bdbEnv.openDatabase(null, aDBName, bdbDBConfig);

            // Create the OID number sequence.
            DatabaseEntry key = createOIDKey(NEXT_OID_NUM_OID);
            SequenceConfig config = new SequenceConfig();
            config.setAllowCreate(true);
            config.setExclusiveCreate(true);
            config.setInitialValue(ObjectSerializer.FIRST_USER_OID);
            config.setCacheSize(1);
            Sequence seq = bdbDB.openSequence(null, key, config);
            seq.close();
            
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
     * Creates a DatabaseEntry key from an OID.
     *
     * @param anOID
     * 
     * @return the key.
     */
    private static DatabaseEntry createOIDKey(long anOID)
    {
        DatabaseEntry key = new DatabaseEntry();
        TupleBinding.getPrimitiveBinding(Long.class).objectToEntry(anOID, key);
        return key;
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
        private LockMode getReadLockMode()
        {
            return getAllowNontransactionalReads() ? LockMode.READ_UNCOMMITTED : LockMode.READ_COMMITTED;
        }

        public ClassInfo[] getClassInfoForOIDs(long[] someOIDs) throws ODMGException
        {
            Transaction txn = getTransaction();

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
            Transaction txn = getTransaction();
            if (!getAllowNontransactionalReads()) {
                // Validate txn active - interface requirement
                txn = getTransaction();
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
         * @return the CID stored in an object entry.
         */
        private long getCIDFromEntry(DatabaseEntry entry)
        {
            return (Long)TupleBinding.getPrimitiveBinding(Long.class).entryToObject(entry);
        }


        public void storeObjects(SerializedObject[] someObjects) throws ODMGException
        {
            Transaction txn = getTransaction();
            
            // TODO - SerializedObject should contain the version #. We should compare the object's version
            // to the current version before writing.

            SerializedObjectTupleBinding binding = new SerializedObjectTupleBinding(true);
            for (SerializedObject object : someObjects) {
                long oid = object.getOID();

                // Prevent schema OIDs from being stored unless this is the schema session.
                if (!mIsSchemaSession && oid == SCHEMA_OID) {
                    throw new ODMGException("Client is not allowed to update schema via object modification.");
                }
                
                DatabaseEntry key = createOIDKey(oid);
                DatabaseEntry data = new DatabaseEntry();
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
            Transaction txn = getTransaction();
            SerializedObjectTupleBinding binding = new SerializedObjectTupleBinding(true);

            byte[][] objects = new byte[someOIDs.length][];
            int idx = 0;
            for (long oid : someOIDs) {
                DatabaseEntry data = readObjectEntry(oid);
                if (data == null) {
                    throw new ODMGException("Cannot find object for OID " + oid);
                }
                
                SerializedObject serializedObj = (SerializedObject)binding.entryToObject(data);
                objects[idx++] = serializedObj.getImage();
            }
            
            return objects;
        }

        public long[] getNewOIDBlock(int anOIDCount) throws ODMGException
        {
            // Validate txn active - interface requirement
            getTransaction();

            long oidNum;
            try {
                SequenceConfig config = new SequenceConfig();
                config.setCacheSize(1);
                Sequence seq = bdbDatabase.openSequence(null, createOIDKey(NEXT_OID_NUM_OID), config);
                oidNum = seq.get(null, anOIDCount);
            }
            catch (DatabaseException e) {
                throw new ODMGException("Unable to get an OID block", e);
            }
            
            long[] oids = new long[anOIDCount];
            for (int i = 0; i < anOIDCount; i++, oidNum++) {
                oids[i] = oidNum; 
            }
            
            return oids;
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
            } // ...end synchronized (mTransactionLock).

            Transaction txn;
            try {
                // TODO Nested transactions are allowed by BDB, as well as txn semantics.
                txn = bdbEnvironment.beginTransaction(null, null);
                setTransaction(txn);
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error starting transaction", e);
            }

            synchronized (mTransactionLock) {
                mActiveTransactions.add(txn);
            } // ...end synchronized (mTransactionLock).
        }


        public void checkpointTransaction() throws ODMGRuntimeException 
        {
            // TODO Hmmmm... How to support this? Suppose to be like a commit, but with locks retained and txn stays active. 
            // Rollback rolls back to checkpoint.
            super.checkpointTransaction();
        }


        public void commitTransaction() throws ODMGRuntimeException 
        {
            super.commitTransaction();

            Transaction txn = getTransaction();
            try {
                // commitSync is because the environment is set to txnWriteNoSync() which doesn't flush OS buffers by default.
                txn.commitSync(); 
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error committing transaction", e);
            }
            
            synchronized (mTransactionLock) {
                mActiveTransactions.remove(txn);
                mTransactionLock.notifyAll();
            } // ...end synchronized (mTransactionLock).

            // Transaction no longer active.
            setTransaction(null);
        }


        public void rollbackTransaction() throws ODMGRuntimeException 
        {
            super.rollbackTransaction();
            
            Transaction txn = getTransaction();
            try {
                txn.abort();
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error committing transaction", e);
            }
            
            synchronized (mTransactionLock) {
                mActiveTransactions.remove(txn);
                mTransactionLock.notifyAll();
            } // ...end synchronized (mTransactionLock).

            // Transaction no longer active.
            setTransaction(null);
        }

        public void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException 
        {
            if (getAllowNontransactionalReads() && aLockLevel == org.odmg.Transaction.READ) {
                // Don't bother to check for a txn or get read locks if we're doing non-transactional reads.
                return;
            }

            Object lockObj = anOID;

            LockMode lockMode;
            switch (aLockLevel) {
            case org.odmg.Transaction.READ:
                lockMode = getReadLockMode();
                break;

            case org.odmg.Transaction.UPGRADE:
            case org.odmg.Transaction.WRITE:
                lockMode = LockMode.RMW;
                break;

            default:
                throw new LockNotGrantedException("Invalid lock mode: " + aLockLevel);
            }

            DatabaseEntry key = createOIDKey(anOID);
            DatabaseEntry data = new DatabaseEntry();
            OperationStatus status;
            try {
                status = bdbDatabase.get(getTransaction(), key, data, lockMode);
            }
            catch (DatabaseException e) {
                throw new LockNotGrantedException("Error reading object", e);
            }
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
