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
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/MemoryObjectServer.java,v 1.5 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.enerj.core.ClassVersionSchema;
import org.enerj.core.EnerJTransaction;
import org.enerj.core.LogicalClassSchema;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.Schema;
import org.enerj.core.SystemCIDMap;
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseNotFoundException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.TransactionNotInProgressException;

/**
 * In-memory ObjectServer.
 * Primarily used for testing. This class is thread-safe via course-grained locking.
 * Because of this, it may not be well suited for access by many threads.
 *
 * @version $Id: MemoryObjectServer.java,v 1.5 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see ObjectServer
 */
public class MemoryObjectServer extends BaseObjectServer
{
    /** HashMap of database names to MemoryObjectServers. */
    private static HashMap sCurrentServers = new HashMap(20);
    private static HashMap sDatabaseMap = new HashMap(20);

    private MemoryDB mDatabase;
    private boolean mNeedsInit = false;


    /**
     * Construct a MemoryObjectServer.
     *
     * @param aDatabaseName the name of the database.
     * @param aFileName the name of the file to persistent memory contents to. May be null.
     *
     * @throws ODMGException if an error occurs
     */
    private MemoryObjectServer(String aDatabaseName, String aFileName, Properties someProperties) throws ODMGException
    {
        super(someProperties);
        
        //  TODO  synchronized around sDatabaseMap access.
        // Find or create database.
        MemoryDB db = (MemoryDB)sDatabaseMap.get(aDatabaseName);
        if (db == null) {
            File file = null;
            if (aFileName != null) {
                file = new File(aFileName);
            }

            if (file == null || !file.exists()) {
                // Create it.... Normally you wouldn't do this...
                db = new MemoryDB(aDatabaseName);
                db.setFile(file);
                mNeedsInit = true;
            }
            else {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    db = (MemoryDB)ois.readObject();
                    ois.close();
                    fis.close();
                }
                catch (Exception e) {
                    throw new DatabaseNotFoundException("Error reading file " + file + ": " + e);
                }
            }

            sDatabaseMap.put(aDatabaseName, db);
        }
        
        mDatabase = db;
    }
    

    /**
     * Shuts down this ObjectServer.
     */
    public void shutdown() throws ODMGException
    {
        synchronized (sCurrentServers) {
            sCurrentServers.remove( mDatabase.getDatabaseName() );
            File file = mDatabase.getFile();
            if (file != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(mDatabase);
                    oos.close();
                    fos.close();
                }
                catch (Exception e) {
                    throw new ODMGException("Error writing file " + file + ": " + e);
                }
            }
        } // End synchronized
    }
        

    // Start of ObjectServer interface methods...



    /**
     * Connects to a database.
     * An empty memory database is created on the fly using "vo.dbname" if it doesn't
     * already exist in memory. The data disappears when the process terminates.
     *
     * @param someProperties properties which specify the connect parameters.
     * The properties must contain the following keys:<br>
     * <ul>
     * <li><i>enerj.dbname</i> - the database name. </li>
     * <li><i>MemoryObjectServer.file</i> - optional file name. If specified, the contents of the memory database are written to
     *  the file using Java Serialization when the database is closed. If the file
     *  exists when the database is opened again, the database is reloaded from the 
     *  file. This provides a cheap form of persistence for testing purposes.</li>
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
     * Helper for {@link MemoryObjectServer#connect(Properties)}. 
     */
    private static ObjectServerSession connect(Properties someProperties, boolean isSchemaSession) throws ODMGException 
    {
        String dbName = someProperties.getProperty(ObjectServer.ENERJ_DBNAME_PROP);
        if (dbName ==  null) {
            throw new DatabaseNotFoundException(ObjectServer.ENERJ_DBNAME_PROP + " parameter expected");
        }

        String fileName = someProperties.getProperty("MemoryObjectServer.file");
        
        // See if there is already a ObjectServer for this volume.
        // Synchronize on sCurrentServers during the get/put process so that another thread
        // cannot create one at the same time.
        MemoryObjectServer server;

        synchronized (sCurrentServers) {
            server = (MemoryObjectServer)sCurrentServers.get(dbName);
            if (server == null) {
                server = new MemoryObjectServer(dbName, fileName, someProperties);
                sCurrentServers.put(dbName, server);
            }
        } // End synchronized (sCurrentServers)

        return new Session(server, server.mDatabase, isSchemaSession);
    }


    /**
     * Initialize a new database if mNeedsInit is set.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    private void initDB() throws ODMGRuntimeException
    {
        // Initialize DB if new
        if (mNeedsInit) {
            // Unset this now so we don't recursively init.
            mNeedsInit = false;
            
            Session session = null;
            boolean completed = false;
            try {
                // Create a session so that we can initialize the schema.
                session = (Session)connect(getConnectProperties(), true);
                session.beginTransaction();

                Schema schema = new Schema("");
                // Create Extent map.
                ExtentMap extentMap = new ExtentMap();

                // Initialize DB Schema. Add schema classes themselves to schema to bootstrap it.
                for (Class schemaClass : sSchemaClasses) {
                    String schemaClassName = schemaClass.getName();
                    LogicalClassSchema classSchema = new LogicalClassSchema(schema, schemaClassName, null);
                    long cid = SystemCIDMap.getSystemCIDForClassName(schemaClassName);
                    new ClassVersionSchema(classSchema, cid, sObjectNameArray, null, null, null, null);
                    
                    // Create an extent for this class.
                    extentMap.createExtentForClassName(schemaClassName);
                }
                
                // Special OID for schema.
                Persistable schemaPersistable = (Persistable)schema;
                schemaPersistable.enerj_SetPrivateOID(BaseObjectServer.SCHEMA_OID);
                session.addToModifiedList(schemaPersistable);
                
                // Special OID for ExtentMap.
                Persistable extentMapPersistable = (Persistable)extentMap;
                extentMapPersistable.enerj_SetPrivateOID(BaseObjectServer.EXTENTS_OID);
                session.addToModifiedList(extentMapPersistable);
                
                session.flushModifiedObjects();
                
                session.commitTransaction();

                completed = true;
            }
            catch (ODMGRuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ODMGRuntimeException("Error creating database: " + e, e);
            }
            finally {
                if (session != null) {
                    if (!completed) {
                        session.rollbackTransaction();
                    }
                    
                    try {
                        session.disconnect();
                        session.shutdown(); // Shuts down the MemoryObjectServer for the database.
                    }
                    catch (ODMGException e) {
                        if (completed) {
                            throw new ODMGRuntimeException(e);
                        }
                        // else Ingore - in recovery
                    }
                }
            }
        }
    }



    /**
     * A memory database.
     */
    private static final class MemoryDB implements Serializable
    {
        private long mNextOID = ObjectSerializer.FIRST_USER_OID;
        private String mDatabaseName;
        // HashMap of MemoryDBEntry. Key is Long(OID), value is MemoryDBEntry.
        private Map mEntryMap = Collections.synchronizedMap( new HashMap(5000) );
        // File used to persist memory contents.
        private File mFile = null;


        MemoryDB(String aDatabaseName)
        {
            mDatabaseName = aDatabaseName;
        }


        String getDatabaseName()
        {
            return mDatabaseName;
        }


        void storeEntry(MemoryDBEntry anEntry)
        {
            Long oidKey = new Long(anEntry.getOID());
            // Remove it if it's in the set already
            mEntryMap.remove(oidKey);
            mEntryMap.put(oidKey, anEntry);
        }


        /**
         * @return the MemoryDBEntry, or null if anOID is not in the database.
         */
        MemoryDBEntry getEntry(long anOID)
        {
            return (MemoryDBEntry)mEntryMap.get( new Long(anOID) );
        }


        /**
         * Gets the next unused OID.
         * @return Gets the next unused OID.
         */
        long getNextNewOID()
        {
            return mNextOID++;
        }


        File getFile()
        {
            return mFile;
        }


        void setFile(File aFile)
        {
            mFile = aFile;
        }
    }



    /**
     * An object entry in a database.
     */
    private static final class MemoryDBEntry implements Serializable
    {
        private long mOID;
        private byte[] mObjectBytes;
        private long mCID;


        MemoryDBEntry(long aCID, long anOID, byte[] aSerializedObject)
        {
            mOID = anOID;
            mCID = aCID;
            mObjectBytes = aSerializedObject;
        }


        long getOID()
        {
            return mOID;
        }


        long getObjectCID()
        {
            return mCID;
        }


        byte[] getSerializedObject()
        {
            return mObjectBytes;
        }
    }
    


    /**
     * Internal transaction.
     */
    private static final class MemoryTxn implements Serializable
    {
        private ObjectServerSession mSession;
        

        MemoryTxn(ObjectServerSession aSession)
        {
            mSession = aSession;
        }


        ObjectServerSession getSession()
        {
            return mSession;
        }
    }



    /**
     * The ObjectServerSession object returned by this server.
     */
    private static final class Session extends BaseObjectServerSession
    {
        private boolean mIsSchemaSession = false;
        private MemoryTxn mTxn = null;
        private MemoryDB mDatabase;
        


        /**
         * Constructs a new Session in a connected state.
         *
         * @param anObjectServer the ObjectServer to associate with the session.
         * @param aDatabase the MemoryDB associated with the session.
         * @param isSchemaSession if true, this is the privileged schema session.
         */
        Session(MemoryObjectServer anObjectServer, MemoryDB aDatabase, boolean isSchemaSession)
        {
            super(anObjectServer);
            mDatabase = aDatabase;
            mIsSchemaSession = isSchemaSession;
        }
        

        /**
         * Gets the transaction associated with this session.
         *
         * @return a MemoryTxn.
         *
         * @throws TransactionNotInProgressException if transaction is null.
         */
        MemoryTxn getTransaction() throws TransactionNotInProgressException
        {
            if (mTxn == null) {
                throw new TransactionNotInProgressException("Transaction not active on session");
            }

            return mTxn;
        }
        

        /**
         * Sets the transaction associated with this session.
         *
         * @param aTransaction a MemoryTxn.
         */
        void setTransaction(MemoryTxn aTransaction)
        {
            mTxn = aTransaction;
            setTransactionActive(mTxn != null);
        }
        

        // Start of ObjectServerSession interface...



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
        }


        public void shutdown() throws ODMGException
        {
            ((MemoryObjectServer)getObjectServer()).initDB();
            super.shutdown();
        }

        /* (non-Javadoc)
         * @see org.enerj.server.ObjectServerSession#getClassInfoForOIDs(long[])
         */
        public ClassInfo[] getClassInfoForOIDs(long[] someOIDs) throws ODMGException
        {
            if (!getAllowNontransactionalReads()) {
                MemoryTxn txn = getTransaction();
            }
            
            ClassInfo[] classInfo = new ClassInfo[someOIDs.length];
            for (int i = 0; i < someOIDs.length; i++) {
                if (someOIDs[i] != ObjectSerializer.NULL_OID) {
                    long anOID = someOIDs[i];
                    if (anOID == BaseObjectServer.SCHEMA_OID) {
                        long cid = SystemCIDMap.getSystemCIDForClassName(SCHEMA_CLASS_NAME);
                        classInfo[i] = new ClassInfo(cid, SCHEMA_CLASS_NAME);
                    }
                    else if (anOID != ObjectSerializer.NULL_OID) {
                        long cid = ObjectSerializer.NULL_CID;
                        // Get a read lock on the object otherwise the CID can change for the object after getting the CID.
                        getLock(anOID, EnerJTransaction.READ, -1);
                        MemoryDBEntry entry = mDatabase.getEntry(someOIDs[i]);
                        if (entry != null) {
                            cid = entry.getObjectCID();
                        }
                        
                        // Resolve the class name. Try system CIDs first.
                        String className = SystemCIDMap.getSystemClassNameForCID(cid);
                        if (className == null) {
                            Schema schema = getSchema();
                            ClassVersionSchema version = schema.findClassVersion(cid);
                            if (version != null) {
                                className = version.getLogicalClassSchema().getClassName();
                            }
                        }
                        
                        if (cid != ObjectSerializer.NULL_CID && className != null) {
                            classInfo[i] = new ClassInfo(cid, className);
                        }
                    }
                }
            }
            
            return classInfo;
        }


        public synchronized void storeObjects(SerializedObject[] someObjects) throws ODMGException
        {
            for (SerializedObject object : someObjects) {
                long anOID = object.getOID();
                long aCID = object.getCID();
                
                // Make sure the object is WRITE locked.
                getLock(anOID, EnerJTransaction.WRITE, -1);
                
                MemoryTxn txn = getTransaction();
                MemoryDBEntry entry = new MemoryDBEntry(aCID, anOID, object.getImage());
                mDatabase.storeEntry(entry);
            }
            
            super.storeObjects(someObjects);
        }


        public synchronized byte[][] loadObjects(long[] someOIDs) throws ODMGException
        {
            if (!getAllowNontransactionalReads()) {
                // Validate txn active - interface requirement
                getTransaction();
            }
            
            byte[][] objects = new byte[someOIDs.length][];
            int idx = 0;
            for (long oid : someOIDs) {
                MemoryDBEntry entry = mDatabase.getEntry(oid);
                if (entry == null) {
                    throw new org.odmg.ODMGRuntimeException("INTERNAL ERROR: OID " + oid + " no longer exists.");
                }
    
                objects[idx++] = entry.getSerializedObject();
            }
            
            return objects;
        }


        public synchronized long[] getNewOIDBlock(int anOIDCount) throws ODMGException
        {
            // Validate txn active - interface requirement
            getTransaction();

            long[] oids = new long[anOIDCount];
            for (int i = 0; i < oids.length; i++) {
                oids[i] = mDatabase.getNextNewOID();
            }

            return oids;
        }


        public synchronized void beginTransaction() throws ODMGRuntimeException 
        {
            ((MemoryObjectServer)getObjectServer()).initDB();

            super.beginTransaction();

            MemoryTxn txn = new MemoryTxn(this);
            setTransaction(txn);
        }


        public synchronized void checkpointTransaction() throws ODMGRuntimeException 
        {
            super.checkpointTransaction();

            //  TODO 
        }


        public synchronized void commitTransaction() throws ODMGRuntimeException 
        {
            super.commitTransaction();
            
            setTransaction(null);
            //  TODO 
        }


        public synchronized void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException 
        {
            if (getAllowNontransactionalReads() && aLockLevel == org.odmg.Transaction.READ) {
                // Don't bother to check for a txn or get read locks if we're doing non-transactional reads.
                return;
            }

            // Validate txn active - interface requirement
            getTransaction();

            //  TODO 
        }


        public synchronized void rollbackTransaction() throws ODMGRuntimeException 
        {
            super.rollbackTransaction();
            
            setTransaction(null);
            //  TODO 
        }


        // ...End of ObjectServerSession interface methods.


    }
}


