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

package org.enerj.server.bdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.enerj.core.ClassSchema;
import org.enerj.core.ClassVersionSchema;
import org.enerj.core.GenericKey;
import org.enerj.core.IndexSchema;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.Schema;
import org.enerj.core.SystemCIDMap;
import org.enerj.server.ClassInfo;
import org.enerj.server.DBIterator;
import org.enerj.server.ObjectServer;
import org.enerj.server.ObjectServerSession;
import org.enerj.server.SerializedObject;
import org.enerj.util.FileUtil;
import org.enerj.util.OIDUtil;
import org.odmg.DatabaseClosedException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNotPersistentException;
import org.odmg.TransactionNotInProgressException;

import com.sleepycatje.bind.tuple.TupleBinding;
import com.sleepycatje.bind.tuple.TupleInput;
import com.sleepycatje.bind.tuple.TupleOutput;
import com.sleepycatje.je.Cursor;
import com.sleepycatje.je.CursorConfig;
import com.sleepycatje.je.Database;
import com.sleepycatje.je.DatabaseConfig;
import com.sleepycatje.je.DatabaseEntry;
import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.Environment;
import com.sleepycatje.je.EnvironmentConfig;
import com.sleepycatje.je.LockMode;
import com.sleepycatje.je.OperationStatus;
import com.sleepycatje.je.SecondaryConfig;
import com.sleepycatje.je.SecondaryCursor;
import com.sleepycatje.je.SecondaryDatabase;
import com.sleepycatje.je.Sequence;
import com.sleepycatje.je.SequenceConfig;
import com.sleepycatje.je.Transaction;

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
    
    private static final String BINDERY_SUFFIX = ":Bindery";
    
    /** HashMap of database names to BDBObjectServers. */
    private static HashMap<String, BDBObjectServer> sCurrentServers = new HashMap<String, BDBObjectServer>(20);

    /** Our shutdown hook. */
    private static Thread sShutdownHook = null;

    /** The database name. */
    private String mDBName;
    
    /** Berkeley DB Environment. */
    private Environment bdbEnvironment = null;
    /** Common BDB Database Configuration - post-create. */
    DatabaseConfig bdbDBConfig;
    /** Common BDB Database Configuration - post-create for duplicate keys. */
    DatabaseConfig bdbDBDuplicateConfig;
    /** Berkeley DB Database. This is the main OID to object map. */
    private Database bdbDatabase = null;
    /** Bindery Database. Key is binding name, value is OID. */
    private Database bdbBinderyDatabase = null;
    /** The OID Sequence. */
    private Sequence oidSequence = null; 
    /** Map of all open indexes. Key is the CIDX of the index(es). Value is a List of SecondaryDatabase. */
    private Map<Integer, List<SecondaryDatabase>> bdbIndexes = new HashMap<Integer, List<SecondaryDatabase>>(); 
    
    /** List of active transactions. List of BDB Transaction. Synchronized
     * around mTransactionLock.
     */
    private List<Transaction> mActiveTransactions = new LinkedList<Transaction>();

    /** Synchronization lock for transaction-oriented methods. */
    private Object mTransactionLock = new Object();
    
    /** List of active sessions. */
    private List<Session> mActiveSessions = new LinkedList<Session>();
    
    /** List of active iterators. */
    private List<DBIterator> mActiveIterators = Collections.synchronizedList( new LinkedList<DBIterator>() );

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
            bdbEnvConfig.setCacheSize(256000000); // TODO COnfig only
            bdbEnvConfig.setTxnSerializableIsolation(true); // TODO This should be config, and should be able to override by txn
            // THIS IS IMPORTANT -- writes thru BDB buffers, but not thru OS. We use Transaction.commitSync() to force OS update.
            bdbEnvConfig.setTxnWriteNoSync(true);
            bdbEnvConfig.setTxnNoSync(true);
            bdbEnvironment = new Environment( new File(dbDir), bdbEnvConfig);
            
            bdbDBConfig = new DatabaseConfig();
            bdbDBConfig.setTransactional(true);
            // TODO bdbDBConfig.setReadOnly(true); based on Prop

            bdbDatabase = bdbEnvironment.openDatabase(null, mDBName, bdbDBConfig);
            bdbBinderyDatabase = bdbEnvironment.openDatabase(null, mDBName + BINDERY_SUFFIX, bdbDBConfig);

            SequenceConfig config = new SequenceConfig();
            config.setCacheSize(1);
            DatabaseEntry key = new DatabaseEntry();
            new OIDKeyTupleBinding(true).objectToEntry( new OIDKey(0, NEXT_OID_NUM_OID), key);
            oidSequence = bdbDatabase.openSequence(null, key, config);

            success = true;
        }
        catch (DatabaseException e) {
            throw new ODMGException("Error opening database", e);
        }
        finally {
            if (!success) {
                try {
                    if (oidSequence != null) {
                        oidSequence.close();
                        oidSequence = null;
                    }
                    
                    if (bdbDatabase != null) {
                        bdbDatabase.close();
                        bdbDatabase = null;
                    }
    
                    if (bdbBinderyDatabase != null) {
                        bdbBinderyDatabase.close();
                        bdbBinderyDatabase = null;
                    }
    
                    if (bdbEnvironment != null) {
                        bdbEnvironment.close();
                        bdbEnvironment = null;
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
     * Builds a BDBJEKeyCreator for the index and adds it to the map of key creators.
     *
     * @param schema
     * @param classSchema
     * @param indexSchema
     * 
     * @return a BDBJEKeyCreator.
     */
    private BDBJEKeyCreator createKeyCreator(ClassSchema classSchema, IndexSchema indexSchema)
    {
        // Build a list of valid CIDXs for this index. Any subclasses of the given class are eligible.
        Set<Integer> cidxs = new HashSet<Integer>();
        cidxs.add( classSchema.getClassIndex() );
        
        // Note: This only detects sub-classes available at the time the index is created. We also have to
        // actively monitor schema changes for sub-class additions and add their cidxs to the key creator.
        Schema schema = classSchema.getSchema();
        for (ClassVersionSchema versionSchema : schema.getPersistableSubclasses(classSchema.getClassName())) {
            cidxs.add( versionSchema.getClassSchema().getClassIndex() );
        }


        // IndexSchema may be new and not yet committed yet. Clone it and disassociate it so the key creator can use it.
        indexSchema = indexSchema.clone();
        Persistable indexSchemaPersistable = (Persistable)(Object)indexSchema;
        indexSchemaPersistable.enerj_SetNew(false);
        indexSchemaPersistable.enerj_SetLoaded(true);
        indexSchemaPersistable.enerj_SetPersister(null);
        
        return new BDBJEKeyCreator(cidxs, indexSchema);
    }

    /**
     * Creates a new database on disk. The "enerj.dbpath" and "enerj.dbdir" system property must be set.
     *
     * @param someDBProps the database properties, normally read from a database properties file.
     * 
     * @throws ODMGException if an error occurs.
     */
	public static void createDatabase(Properties someDBProps) throws ODMGException 
    {
	    String aDBName = getRequiredProperty(someDBProps, ObjectServer.ENERJ_DBNAME_PROP);
	    String dbDir = getRequiredProperty(someDBProps, ObjectServer.ENERJ_DBDIR_PROP);
	    sLogger.fine("Creating database: " + aDBName);

        Session session = null;
        boolean completed = false;
        Environment bdbEnv = null;
        Database bdbDB = null;
        try {
            EnvironmentConfig bdbEnvConfig = new EnvironmentConfig();
            bdbEnvConfig.setAllowCreate(true);
            bdbEnvConfig.setTransactional(true);

            bdbEnv = new Environment(new File(dbDir), bdbEnvConfig);
            
            DatabaseConfig bdbDBConfig = new DatabaseConfig();
            bdbDBConfig.setAllowCreate(true);
            bdbDBConfig.setExclusiveCreate(true);
            bdbDBConfig.setTransactional(true);
            bdbDBConfig.setDeferredWrite(false);
            bdbDBConfig.setNodeMaxEntries(400); // Tunable - 256-400 looks good for OO7
            
            // The main database's key is an OID.
            bdbDB = bdbEnv.openDatabase(null, aDBName, bdbDBConfig);

            // Create the OID number sequence.
            DatabaseEntry key = new DatabaseEntry();
            new OIDKeyTupleBinding(true).objectToEntry( new OIDKey(0, NEXT_OID_NUM_OID), key);

            SequenceConfig config = new SequenceConfig();
            config.setAllowCreate(true);
            config.setExclusiveCreate(true);
            config.setInitialValue(ObjectSerializer.FIRST_USER_OID);
            config.setCacheSize(1);
            Sequence seq = bdbDB.openSequence(null, key, config);
            seq.close();
            
            bdbDB.close();
            bdbDB = null;
            
            // The Bindery's database key is the binding name, the data is an OID.
            bdbDB = bdbEnv.openDatabase(null, aDBName + BINDERY_SUFFIX, bdbDBConfig);
            bdbDB.close();
            bdbDB = null;

            bdbEnv.close();
            bdbEnv = null;
            
            // Create a session so that we can initialize the schema.
            Properties tmpDBProps = new Properties(someDBProps);
            tmpDBProps.setProperty(ENERJ_CLIENT_LOCAL, "true");

            session = (Session)connect(tmpDBProps, true);
            initDBObjects(session);
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

	@Override
	protected void createPhysicalIndex(ClassSchema aClassSchema, IndexSchema anIndexSchema) throws ODMGException
	{
	    String indexDBName = createIndexDBName(aClassSchema.getClassName(), anIndexSchema.getName());
        
	    SecondaryConfig indexConfig = new SecondaryConfig();
        indexConfig.setAllowCreate(true);
        indexConfig.setExclusiveCreate(true);
        indexConfig.setTransactional(true);
        indexConfig.setDeferredWrite(false);
        indexConfig.setAllowPopulate(true); // Build index if DB not empty.
        indexConfig.setNodeMaxEntries(512); // TODO Tunable
        indexConfig.setSortedDuplicates( anIndexSchema.allowsDuplicateKeys() );
        indexConfig.setBtreeComparator(GenericKeyBDBComparator.class);
        indexConfig.setKeyCreator( createKeyCreator(aClassSchema, anIndexSchema)  );

        // The index's key is a serialized GenericKey and value is an OID.
        try {
            SecondaryDatabase indexDB = bdbEnvironment.openSecondaryDatabase(null, indexDBName, bdbDatabase, indexConfig); 
            addIndexToOpenMap(aClassSchema.getClassIndex(), indexDB);
        }
        catch (DatabaseException e) {
            throw new ODMGException("Error creating index '" + anIndexSchema.getName() + "' on class " +
                aClassSchema.getClassName(), e);
        }
	}
    
    @Override
    protected void updateIndexesForNewClass(ClassVersionSchema classVersion) throws ODMGException
    {
        // Update the BDBJEKeyCreators for all super-class indexes of this class to contain 
        // this class' cidx.
        ClassSchema classSchema = classVersion.getClassSchema();
        int cidx = classSchema.getClassIndex();
        Schema schema = classSchema.getSchema();
        
        try {
            for (String superTypeName : classVersion.getSuperTypeNames()) {
                ClassSchema superClassSchema = schema.findClassSchema(superTypeName);
                if (superClassSchema != null) {
                    List<SecondaryDatabase> indexes  = bdbIndexes.get(superClassSchema.getClassIndex());
                    if (indexes != null) {
                        for (SecondaryDatabase indexDB : indexes) {
                            BDBJEKeyCreator keyCreator = (BDBJEKeyCreator)indexDB.getSecondaryConfig().getKeyCreator();
                            keyCreator.addValidClassIndex(cidx);
                        }
                    }
                }
            }
        }
        catch (DatabaseException e) {
            throw new ODMGException("Error getting index configuration", e);
        }
    }

    /**
	 * Adds an index to the map of open indexes.
	 *
	 * @param classIndex
	 * @param indexDB
	 */
	private void addIndexToOpenMap(int classIndex, SecondaryDatabase indexDB)
	{
	    List<SecondaryDatabase> indexDBs = bdbIndexes.get(classIndex);
	    if (indexDBs == null) {
	        indexDBs = new ArrayList<SecondaryDatabase>();
	        bdbIndexes.put(classIndex, indexDBs);
	    }
	    
	    indexDBs.add(indexDB);
	}
	
	/**
	 * Creates the BDB database name for an index.
	 * 
	 * @param className
	 * @param indexName
	 * 
	 * @return the database name.
	 */
	private String createIndexDBName(String className, String indexName)
	{
	    return mDBName + ':' + className + ':' + indexName;	    
	}
	
    /**
     * Creates a DatabaseEntry key from an OID.
     *
     * @param oid the oid.
     * 
     * @return the key.
     */
    private static DatabaseEntry createOIDKey(long oid)
    {
        OIDKey oidKey = new OIDKey(OIDUtil.getCIDX(oid), OIDUtil.getOIDX(oid));
        
        DatabaseEntry key = new DatabaseEntry();
        new OIDKeyTupleBinding(true).objectToEntry(oidKey, key);
        return key;
    }
    
    /**
     * Creates a partial DatabaseEntry OID key from a CIDX.
     *
     * @param cidx the CIDX.
     * 
     * @return the partial key.
     */
    private static DatabaseEntry createPartialOIDKey(int cidx)
    {
        OIDKey oidKey = new OIDKey(cidx, 0L);
        
        DatabaseEntry key = new DatabaseEntry();
        new OIDKeyTupleBinding(false).objectToEntry(oidKey, key);
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
                String msg = "Transaction list is not clear! INTERNAL ERROR!";
                sLogger.severe(msg);
                throw new ODMGException(msg);
            }

            // Remove this server from the global list.
            sCurrentServers.remove(mDBName);
        } // End synchronized
        
        try {
            //EnvironmentStats stats = bdbEnvironment.getStats(null);
            //sLogger.info("Stats:" + stats);
            
            // We need a copy because extents are removed from this list and would cause a concurrent operation exception.
            List<DBIterator> copyActiveIterators = new ArrayList<DBIterator>(mActiveIterators);
            for (DBIterator iter : copyActiveIterators) {
                iter.close();
            }
            
            // Close secondary databases - a.k.a. Indexes
            for (List<SecondaryDatabase> indexDBs : bdbIndexes.values()) {
                for (SecondaryDatabase indexDB : indexDBs) {
                    indexDB.close();
                }
            }
            
            if (oidSequence != null) {
                oidSequence.close();
            }
            
            if (bdbDatabase != null) {
                bdbDatabase.close();
            }

            if (bdbBinderyDatabase != null) {
                bdbBinderyDatabase.close();
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
        
        if (!foundPropFile) {
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

            if (!isSchemaSession && server.bdbIndexes.isEmpty()) {
                server.openIndexes();
            }
            
            return session;
        } // End synchronized (sCurrentServers)
    }
    
    /**
     * Opens the secondary databases for indexes.
     *
     * @throws ODMGException
     */
    private void openIndexes() throws ODMGException
    {
        try {
            // Open all indexes.
            Schema schema = getSchema();
            for (ClassSchema classSchema : schema.getClassSchemas()) {
                for (IndexSchema indexSchema : classSchema.getIndexes()) {
                    String indexDBName = createIndexDBName(classSchema.getClassName(), indexSchema.getName());
    
                    SecondaryConfig indexConfig = new SecondaryConfig();
                    indexConfig.setSortedDuplicates( indexSchema.allowsDuplicateKeys() );
                    indexConfig.setKeyCreator( createKeyCreator(classSchema, indexSchema) );
    
                    SecondaryDatabase indexDB = bdbEnvironment.openSecondaryDatabase(null, indexDBName, bdbDatabase, indexConfig); 
                    addIndexToOpenMap(classSchema.getClassIndex(), indexDB);
                }
            }
        }
        catch (DatabaseException e) {
            throw new ODMGException("Error opening indexes", e);
        }
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
    class Session extends BaseObjectServerSession
    {
        private Transaction txn = null;
        /** If true, this is a privileged session that may update the schema. */
        private boolean isSchemaSession = false;
        private List<DBIterator> sessionIterators = new ArrayList<DBIterator>(); 

        /**
         * Constructs a new Session in a connected state.
         *
         * @param anObjectServer the BDBObjectServer to associate with the session.
         */
        Session(BDBObjectServer anObjectServer, boolean isSchemaSession)
        {
            super(anObjectServer);
            this.isSchemaSession = isSchemaSession;
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
            if (txn == null && !getAllowNontransactionalReads()) {
                throw new TransactionNotInProgressException("Transaction not in progress.");
            }

            return txn;
        }

        /**
         * Gets the transaction associated with this session.
         *
         * @return a Transaction, or null if none is active.
         */
        Transaction getTransactionOrNull()
        {
            return txn;
        }

        /**
         * Sets the transaction associated with this session.
         *
         * @param aTransaction a Transaction, or null if no transaction is active.
         */
        void setTransaction(Transaction aTransaction)
        {
            txn = aTransaction;
            setTransactionActive(txn != null);
        }
        

        // Start of ObjectServerSession interface methods...

        public void disconnect() throws ODMGException 
        {
            if (!isConnected()) {
                throw new DatabaseClosedException("Session is not connected");
            }

            // If transaction is active on session, roll it back.
            if (txn != null) {
                sLogger.warning("Forcing rollback on session disconnect");
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
                long oid = someOIDs[i];
                if (oid != ObjectSerializer.NULL_OID) {
                    int cidx = OIDUtil.getCIDX(oid);
                    
                    // Resolve the class name. Try system CIDs first, which is the same as a CIDX for system classes.
                    String className = SystemCIDMap.getSystemClassNameForCID(cidx);
                    if (className == null) {
                        Schema schema = getSchema();
                        ClassSchema classSchema = schema.findClassSchema(cidx);
                        if (classSchema != null) {
                            className = classSchema.getClassName();
                        }
                    }
                    
                    if (className != null) {
                        classInfo[i] = new ClassInfo(className, cidx);
                    }
                }
            }

            return classInfo;
        }

        public ClassInfo[] getClassInfoForCIDs(long[] someCIDs) throws ODMGException
        {
            if (!getAllowNontransactionalReads()) {
                // Validate txn active - interface requirement
                getTransaction();
            }

            ClassInfo[] classInfo = new ClassInfo[someCIDs.length];
            for (int i = 0; i < someCIDs.length; i++) {
                long cid = someCIDs[i];
                
                if (cid != ObjectSerializer.NULL_CID) {
                    // Resolve the class name. Try system CIDs first.
                    String className = SystemCIDMap.getSystemClassNameForCID(cid);
                    int cidx = 0;
                    if (className == null) {
                        Schema schema = getSchema();
                        // TODO When we implement Schema Evolution, this will need to return version-specific information.
                        ClassVersionSchema classVersion = schema.findClassVersion(cid);
                        if (classVersion != null) {
                            ClassSchema classSchema = classVersion.getClassSchema(); 
                            className = classSchema.getClassName();
                            cidx = classSchema.getClassIndex();
                        }
                    }
                    else {
                        cidx = (int)cid;
                    }
                    
                    if (className != null) {
                        classInfo[i] = new ClassInfo(className, cidx);
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
            return ((SerializedObject)new SerializedObjectTupleBinding(false).entryToObject(entry)).getCID();
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
                if (!isSchemaSession && oid == SCHEMA_OID) {
                    throw new ODMGException("Client is not allowed to update schema via object modification.");
                }
                
                DatabaseEntry oidKey = createOIDKey(oid);
                DatabaseEntry data = new DatabaseEntry();
                binding.objectToEntry(object, data);
                
                try {
                    // Because the Extent is implicitly part of the OID index (via the CIDX), this also makes the object part of
                    // the extent for the class.
                    OperationStatus status = bdbDatabase.put(txn, oidKey, data);
                    if (status != OperationStatus.SUCCESS) {
                        throw new ODMGException("Error writing object. Status is " + status);
                    }
                }
                catch (DatabaseException e) {
                    throw new ODMGException("Error writing object", e);
                }
            }
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

        public long[] getNewOIDXBlock(int anOIDCount) throws ODMGException
        {
            // Validate txn active - interface requirement
            getTransaction();

            long oidNum;
            try {
                oidNum = oidSequence.get(null, anOIDCount);
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
                // TODO Nested transactions are allowed by BDB (JE doesn't support them as of 3.2.23)
                // TODO BDB can handle txn semantics.
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
            
            closeActiveIterators();
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

            closeActiveIterators();
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


        /** 
         * {@inheritDoc}
         * @see org.enerj.server.ObjectServerSession#bind(long, java.lang.String)
         */
        public void bind(long anOID, String aName) throws ObjectNameNotUniqueException
        {
            Transaction txn = getTransaction();
            
            try {
                DatabaseEntry key = new DatabaseEntry();
                TupleBinding.getPrimitiveBinding(String.class).objectToEntry(aName, key);
                DatabaseEntry data = new DatabaseEntry();
                TupleBinding.getPrimitiveBinding(Long.class).objectToEntry(anOID, data);
                OperationStatus status = bdbBinderyDatabase.putNoOverwrite(txn, key, data);
                if (status == OperationStatus.KEYEXIST) {
                    throw new ObjectNameNotUniqueException("Bind name " + aName + " is not unique");
                }
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error binding to " + aName, e);
            }
        }

        /** 
         * {@inheritDoc}
         * @see org.enerj.server.ObjectServerSession#lookup(java.lang.String)
         */
        public long lookup(String aName) throws ObjectNameNotFoundException
        {
            Transaction txn = getTransaction();
            
            try {
                DatabaseEntry key = new DatabaseEntry();
                TupleBinding.getPrimitiveBinding(String.class).objectToEntry(aName, key);
                DatabaseEntry data = new DatabaseEntry();
                OperationStatus status = bdbBinderyDatabase.get(txn, key, data, getReadLockMode());
                if (status == OperationStatus.NOTFOUND) {
                    throw new ObjectNameNotFoundException("Bind name " + aName + " was not found");
                }
                
                return (Long)TupleBinding.getPrimitiveBinding(Long.class).entryToObject(data);
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error looking up binding to " + aName, e);
            }
        }

        /** 
         * {@inheritDoc}
         * @see org.enerj.server.ObjectServerSession#unbind(java.lang.String)
         */
        public void unbind(String aName) throws ObjectNameNotFoundException
        {
            Transaction txn = getTransaction();
            
            try {
                DatabaseEntry key = new DatabaseEntry();
                TupleBinding.getPrimitiveBinding(String.class).objectToEntry(aName, key);
                OperationStatus status = bdbBinderyDatabase.delete(txn, key);
                if (status == OperationStatus.NOTFOUND) {
                    throw new ObjectNameNotFoundException("Bind name " + aName + " was not found");
                }
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error deleting binding to " + aName, e);
            }
        }
        
        /** 
         * {@inheritDoc}
         * @see org.enerj.server.ObjectServerSession#removeFromExtent(long)
         */
        public void removeFromExtent(long anOID) throws ObjectNotPersistentException
        {
            Transaction txn = getTransaction();
            // TODO Hmmm... The object is implicitly part of its extent. Mark as deleted from extent/indexes?
        }

        /**
         * @return the common CursorConfig.
         */
        private CursorConfig getCursorConfig()
        {
            CursorConfig config = new CursorConfig();
            if (getAllowNontransactionalReads()) {
                config.setReadUncommitted(true);
            }
            else {
                config.setReadCommitted(true);
            }
            
            return config;
        }
        
        /** 
         * {@inheritDoc}
         * @see org.enerj.server.ObjectServerSession#getExtentSize(java.lang.String, boolean)
         */
        public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
            List<Integer> cidxs = getExtentCIDXs(aClassName, wantSubclasses);
            
            Cursor cursor = null;
            try {
                long size = 0;
                cursor = bdbDatabase.openCursor(getTransaction(), getCursorConfig());
                OIDKeyTupleBinding binding = new OIDKeyTupleBinding(true);
                for (Integer cidx : cidxs) {
                    DatabaseEntry partialKey = createPartialOIDKey(cidx);
                    DatabaseEntry data = new DatabaseEntry();
                    if (cursor.getSearchKeyRange(partialKey, data, null) == OperationStatus.SUCCESS) {
                        do {
                            OIDKey oidKey = (OIDKey)binding.entryToObject(partialKey);
                            if (oidKey.cidx != cidx) {
                                break;
                            }
                            
                            ++size;
                        }
                        while (cursor.getNext(partialKey, data, null) == OperationStatus.SUCCESS);
                    }
                }
                
                return size;
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error opening extent", e);
            }
            finally {
                if (cursor != null) {
                    try { cursor.close(); } catch (DatabaseException e) { /* Ignore */ }
                }
            }
        }

        /**
         * Gets all of the CIDXs that make up the extent, optionally with subclasses.
         *
         * @param aClassName
         * @param wantSubclasses if true, subclasses will be included.
         * 
         * @return a List of CIDXs.
         * 
         * @throws ODMGRuntimeException if an error occurs.
         */
        private List<Integer> getExtentCIDXs(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
            Schema schema;
            try {
                schema = getSchema();
            }
            catch (ODMGException e) {
                throw new ODMGRuntimeException(e);
            }
            
            List<Integer> cidxs = new ArrayList<Integer>();
            ClassSchema classSchema = schema.findClassSchema(aClassName);
            if (classSchema != null) {
                cidxs.add( classSchema.getClassIndex() );
            }
                
            if (wantSubclasses) {
                Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
                for (ClassVersionSchema version : subclasses) {
                    cidxs.add( version.getClassSchema().getClassIndex() );
                }
            }
            
            return cidxs;
        }
        
        /** 
         * {@inheritDoc}
         * @see org.enerj.server.ObjectServerSession#createExtentIterator(java.lang.String, boolean)
         */
        public DBIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
            List<Integer> cidxs = getExtentCIDXs(aClassName, wantSubclasses);
            List<DatabaseEntry> cidxKeys = new ArrayList<DatabaseEntry>(cidxs.size());
            for (Integer cidx : cidxs) {
                cidxKeys.add( createPartialOIDKey(cidx) );
            }

            try {
                Cursor cursor = bdbDatabase.openCursor(getTransaction(), getCursorConfig());
                BDBExtentIterator iter = new BDBExtentIterator(this, cursor, cidxs, cidxKeys);
                sessionIterators.add(iter);
                mActiveIterators.add(iter);
                return iter;
            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error creating extent iterator", e);
            }
            
        }
        
        void closeActiveIterators()
        {
            // Make a copy of the list before closing because closing removes the iterator from the list.
            List<DBIterator> iters = new ArrayList<DBIterator>(sessionIterators);
            for (DBIterator iter : iters) {
                if (iter.isOpen()) {
                    iter.close();
                }
            }
        }
        
        
        /**
         * Removes an iterator from the list of active ones.
         *
         * @param iter
         */
        void removeIterator(DBIterator iter)
        {
            sessionIterators.remove(iter);
            mActiveIterators.remove(iter);
        }
        // ...End of ObjectServerSession interface methods.

        @Override
        public long getIndexKeyRangeSize(String aClassName, String anIndexName, GenericKey aStartKey,
                        GenericKey anEndKey) throws ODMGRuntimeException
        {
            long size = 0;
            DBIterator iterator = createIndexIterator(aClassName, anIndexName, aStartKey, anEndKey);
            while (iterator.hasNext()) {
                size += iterator.next(1000).length;
            }
            
            return size;
        }

        @Override
        public DBIterator createIndexIterator(String aClassName, String anIndexName, GenericKey aStartKey,
                        GenericKey anEndKey) throws ODMGRuntimeException
        {
            Schema schema;
            try {
                schema = getSchema();
            }
            catch (ODMGException e) {
                throw new ODMGRuntimeException(e);
            }
            
            ClassSchema classSchema = schema.findClassSchema(aClassName);
            if (classSchema == null) {
                throw new ODMGRuntimeException("Cannot find class " + aClassName);
            }
            
            int cidx = classSchema.getClassIndex();
            List<SecondaryDatabase> indexes = bdbIndexes.get(cidx);
            if (indexes == null) {
                throw new ODMGRuntimeException("Cannot find index " + anIndexName + " for class " + aClassName);
            }
            
            try {
                String indexDBName = createIndexDBName(aClassName, anIndexName);
                SecondaryDatabase index = null;
                for (SecondaryDatabase targetIndex : indexes) {
                    if (targetIndex.getDatabaseName().equals(indexDBName)) {
                        index = targetIndex;
                        break;
                    }
                }
                
                if (index == null) {
                    throw new ODMGRuntimeException("Cannot find index " + anIndexName + " for class " + aClassName);
                }
                
                SecondaryCursor cursor = index.openSecondaryCursor(getTransaction(), getCursorConfig());
                BDBIndexIterator iterator = new BDBIndexIterator(this, cursor, aStartKey, anEndKey);
                sessionIterators.add(iterator);
                mActiveIterators.add(iterator);
                return iterator;

            }
            catch (DatabaseException e) {
                throw new ODMGRuntimeException("Error creating index iterator", e);
            }
        }
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
            sLogger.fine("BDBObjectServer Shutdown hook invoked.");
            try {
                mObjectServer.shutdown();
            }
            catch (ODMGException e) {
                sLogger.severe("Shutdown problem:");
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
            byte[] obj = null;
            if (readObjBytes) {
                obj = input.getBufferBytes(); // Avoid copying this buffer. Use it directly.
            }

            input.skipFast(len - 8);
            long cid = input.readLong();
            return new SerializedObject(0, cid, obj, false);
        }

        @Override
        public void objectToEntry(Object object, TupleOutput output)
        {
            SerializedObject serializedObj = (SerializedObject)object;
            output.writeFast(serializedObj.getImage());
            output.writeLong(serializedObj.getCID());
        }
    }

    /**
     * Holder for an association between the index and its IndexSchema.
     */
    private static final class IndexInfo
    {
        Database indexDB;
        IndexSchema indexSchema;

        public IndexInfo(Database indexDB, IndexSchema indexSchema)
        {
            this.indexDB = indexDB;
            this.indexSchema = indexSchema;
        }
    }
}
