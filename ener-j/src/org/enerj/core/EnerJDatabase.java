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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/EnerJDatabase.java,v 1.18 2006/05/31 01:58:22 dsyrstad Exp $

package org.enerj.core;

import static org.enerj.server.ObjectServer.ENERJ_ACCESS_MODE_PROP;
import static org.enerj.server.ObjectServer.ENERJ_CLIENT_LOCAL;
import static org.enerj.server.ObjectServer.ENERJ_DBNAME_PROP;
import static org.enerj.server.ObjectServer.ENERJ_HOSTNAME_PROP;
import static org.enerj.server.ObjectServer.ENERJ_PASSWORD_PROP;
import static org.enerj.server.ObjectServer.ENERJ_PORT_PROP;
import static org.enerj.server.ObjectServer.ENERJ_USERNAME_PROP;
import static org.odmg.Transaction.READ;
import static org.odmg.Transaction.UPGRADE;
import static org.odmg.Transaction.WRITE;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;

import org.enerj.annotations.Index;
import org.enerj.annotations.SchemaAnnotation;
import org.enerj.server.ClassInfo;
import org.enerj.server.ExtentIterator;
import org.enerj.server.ObjectServerSession;
import org.enerj.server.PagedObjectServer;
import org.enerj.server.PluginHelper;
import org.enerj.server.SerializedObject;
import org.enerj.util.ClassUtil;
import org.enerj.util.URIUtil;
import org.odmg.ClassNotPersistenceCapableException;
import org.odmg.Database;
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseOpenException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.TransactionInProgressException;
import org.odmg.TransactionNotInProgressException;

/**
 * Ener-J implementation of org.odmg.Database.
 *
 * @version $Id: EnerJDatabase.java,v 1.18 2006/05/31 01:58:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Database
 */
public class EnerJDatabase implements Database, Persister
{
    /** Maximum size of mSerializedObjectQueue. TODO make this configurable */
    private static final int sMaxSerializedObjectQueueSize = 100000;

    /** Current Open Database for JVM. Used when no curent open thread database exists. (i.e.,
     * Database was opened in a thread, but now the thread is gone). Entry exists until
     * the database is closed, even if the app has no reference to it.
     */
    private static EnerJDatabase sCurrentProcessDatabase = null;

    /** Current database for the thread.  */
    private static ThreadLocal<EnerJDatabase> sCurrentDatabaseForThread = new ThreadLocal<EnerJDatabase>();

    /** Client-side object cache. */
    private PersistableObjectCache mClientCache = null;

    /** List of Persistable objects created or modified during this transaction. */
    private ModifiedPersistableList mModifiedObjects;
    
    /** Cache of CIDs known to be in the database. Used so that we can avoid
     * grabbing a DatabaseRoot and read-locking it. */
    private Set<Long> mKnownSchemaCIDs; 

    /** True if the database is open. */
    private boolean mIsOpen = false;
    /** True if the server for this database is running locally. Only valid if the database is open. */
    private boolean mIsLocal = false;
    /** If mIsLocal is true, this is the RequestProcessor proxying server requests. */
    //private RequestProcessor mLocalRequestProcessor = null;
    
    /** Current transaction that this database is bound to. */
    private EnerJTransaction mBoundToTransaction = null;
    
    /** True if this instance allows non-transactional reads. */
    private boolean mAllowNontransactionalReads = false;
    
    /** ObjectServerSession we're bound to. */
    private ObjectServerSession mObjectServerSession = null;

    /** Cache of new OIDs to be used. Only available during a transaction. */
    private long[] mOIDCache = null;
    private int mOIDCachePosition = 0;
    
    /** Queue of serialized objects waiting to be flushed to database. */
    private List<SerializedObject> mSerializedObjectQueue;
    /** Number of bytes in mSerializedObjectQueue. */
    private int mSerializedObjectQueueSize = 0;

    /** Non-null if the transaction is in the process of flushing objects. This
     * represents the current position in mModifiedObjects. */
    private ListIterator<Persistable> mFlushIterator = null;
    
    /** The high-water mark for flushing. */
    // TODO This should be settable.
    private int mFlushLevel = 500;
    
    /**
     * Construct a unopened EnerJDatabase.
     */
    public EnerJDatabase()
    {
    }

    // Start of truly public EnerJ-specific methods...



    /** 
     * Gets the current open database for the process. This is initially set to
     * the first database opened by the process. When this database is closed, it
     * is set null. When the next database is opened and this is null, it is set to 
     * the newly opened database.
     *
     * @return a EnerJDatabase, or null if the process does not have a current database.
     */
    public static EnerJDatabase getCurrentDatabaseForProcess()
    {
        return sCurrentProcessDatabase;
    }
    

    /** 
     * Gets the current open database for the caller's thread. This is initially set to
     * the first database opened by the thread. When this database is closed, it
     * is set null. When the next database is opened and this is null, it is set to 
     * the newly opened database.
     *
     * @return a EnerJDatabase, or null if the caller's thread does not have a current database.
     */
    public static EnerJDatabase getCurrentDatabaseForThread()
    {
        return sCurrentDatabaseForThread.get();
    }


    /** 
     * Gets the current open database. This method attempts to get the current
     * database via getCurrentDatabaseForThread(). Failing that, getCurrentDatabaseForProcess
     * is called.
     *
     * @return a EnerJDatabase, or null if there is no current database.
     */
    public static EnerJDatabase getCurrentDatabase()
    {
        EnerJDatabase db = getCurrentDatabaseForThread();
        if (db == null) {
            db = getCurrentDatabaseForProcess();
        }
        
        return db;
    }
    

    /**
     * Determines if this database is open.
     *
     * @return true if it is open, false if it is closed.
     */
    public boolean isOpen()
    {
        return mIsOpen;
    }


    // ... End of truly public EnerJ-specific methods.

    // Start of Persister interface...

    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#isTransactionActive()
     */
    public boolean isTransactionActive()
    {
        return getTransaction() != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAllowNontransactionalReads() throws ODMGException
    {
        return mAllowNontransactionalReads;
    }

    /**
     * {@inheritDoc}
     */
    public Persistable getObjectForOID(long anOID)
    {
        // TODO Any callers using this method should be checked carefully for possible use of the plural method.
        return getObjectsForOIDs(new long[] { anOID } )[0];
    }


    /**
     * {@inheritDoc}
     */
    public Persistable[] getObjectsForOIDs(long[] someOIDs)
    {
        checkBoundTransaction(true);

        Persistable[] objects = new Persistable[someOIDs.length];
        long[] oidsToRetrieveClassInfoFor = new long[someOIDs.length];
        boolean foundAllInCache = true;
        for (int i = 0; i < someOIDs.length; i++) {
            Persistable checkPersistable = (Persistable)mClientCache.get(someOIDs[i]);
            
            // Object may have fallen off of cache, but still be in ModifiedList.
            if (checkPersistable == null) {
                checkPersistable = mModifiedObjects.getModifiedObjectByOID(someOIDs[i]);
            }
            
            if (checkPersistable != null) {
                if (isNontransactionalReadMode()) {
                    // If we're in non-transactional read mode and this object was
                    // never loaded, make sure that we can load it later by setting it
                    // to be non-transactional.
                    PersistableHelper.setNonTransactional(checkPersistable);
                }
    
                objects[i] = checkPersistable;
            }
            else {
                foundAllInCache = false;
                oidsToRetrieveClassInfoFor[i] = someOIDs[i];
            }
        }
        
        if (foundAllInCache) {
            return objects;
        }

        // Retrieve ClassInfo for these OIDs.
        ClassInfo[] classInfos;
        try {
            // This obtains a READ lock on each OID.
            classInfos = mObjectServerSession.getClassInfoForOIDs(oidsToRetrieveClassInfoFor);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Could not get CIDs for OIDs", e);
        }

        for (int i = 0; i < someOIDs.length; i++) {
            if (objects[i] != null) {
                continue;
            }
            
            ClassInfo classInfo = classInfos[i];
            long oid = someOIDs[i];
            
            Persistable persistable = null;
            if (classInfo != null && classInfo.getClassName() != null && classInfo.getCID() != ObjectSerializer.NULL_CID) {
                persistable = PersistableHelper.createHollowPersistable(classInfo, oid, this);                
                
                // Cache it
                mClientCache.add(oid, persistable);
            }

            objects[i] = persistable;
        }
        
        return objects;
    }


    /**
     * {@inheritDoc}
     */
    public long getOID(Object anObject)
    {
        if ( !(anObject instanceof Persistable)) {
            return ObjectSerializer.NULL_OID;
        }
        
        Persistable persistable = (Persistable)anObject;
        EnerJDatabase persistableDatabase = (EnerJDatabase)persistable.enerj_GetPersister();
        
        // Get the real current transaction here - not the one associated with this
        // database. Its database should match this database if the object is not being used
        // out of context.
        EnerJTransaction currentTxn = EnerJTransaction.getCurrentTransaction();
        EnerJDatabase currentTxnDatabase = null;
        if (currentTxn != null) {
            currentTxnDatabase = currentTxn.getDatabase();
        }
        
        if (persistableDatabase != null && 
            (persistableDatabase != this || 
             (currentTxnDatabase != null && currentTxnDatabase != persistableDatabase)) ) {
            throw new ODMGRuntimeException("Persistable object does not belong to this database");
        }
        
        long oid = persistable.enerj_GetPrivateOID();
        if (oid == ObjectSerializer.NULL_OID && persistable.enerj_IsNew() && mBoundToTransaction != null) {
            oid = addNewPersistable(persistable);
        }
        
        return oid;
    }


    /**
     * {@inheritDoc}
     */
    public void loadObject(Persistable aPersistable)
    {
        checkBoundTransaction(true);
        
        // Make sure that the requested object is in the cache. This will also cause it to come back
        // in the prefetch list.
        long oid = getOID(aPersistable);
        mClientCache.add(oid, aPersistable);

        List<Persistable> prefetches = mClientCache.getAndClearPrefetches();
        long[] oids = new long[prefetches.size()];
        int idx = 0;
        for (Persistable prefetch : prefetches) {
            oids[idx++] = getOID(prefetch);
        }
        
        // Look it up in the DB.
        byte[][] objects;
        try {
            objects = mObjectServerSession.loadObjects(oids);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Could not load object", e);
        }

        idx = 0;
        for (Persistable prefetch : prefetches) {
            PersistableHelper.loadSerializedImage(this, prefetch, objects[idx++]);

            if ( !isNontransactionalReadMode() && !EnerJDatabase.isAtLockLevel(prefetch, EnerJTransaction.READ)) {
                // loadObject() obtains a READ lock.
                prefetch.enerj_SetLockLevel(EnerJTransaction.READ);
            }
        }
    }
    

    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#addToModifiedList(org.enerj.core.Persistable)
     */
    public void addToModifiedList(Persistable aPersistable)
    {
        EnerJTransaction txn = getTransaction(); 
        if (txn == null) {
            return; // Ignore if txn not active.
        }

        boolean restoreValues = txn.getRestoreValues();
        
        if (!aPersistable.enerj_IsNew() && restoreValues) {
            savePersistableImage(aPersistable);
        }
        
        // Note that if we were to just call storePersistable() here, we could get
        // into a very deep recursion. See EnerJTransaction.flushAndKeepModifiedList() for more details.
        // If we're flushing, we need to add it to the iterator rather than the list.
        if (mFlushIterator != null) {
            mFlushIterator.add(aPersistable);
        }
        else {
            // Only flush if not restore values. Otherwise we have to keep them locally.
            // TODO We could still flush if we loaded values back from the database.
            if (!restoreValues && mModifiedObjects.getSize() >= mFlushLevel) {
                flush();
            }

            // This must be added AFTER any flush occurs otherwise PersistableHelper will mark this
            // as modified after we return, but it won't be in the modified list. 
            mModifiedObjects.addToModifiedList(aPersistable);
        }
    }

    

    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#clearModifiedList()
     */
    public void clearModifiedList()
    {
        mModifiedObjects.clearModifiedList();
    }

    

    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getModifiedList()
     */
    public ListIterator<Persistable> getModifiedListIterator()
    {
        return mModifiedObjects.getIterator();
    }


    // ...End of Persister interface.



    /**
     * Sets whether this database instance allows non-transactional (dirty) reads.  
     *
     * @param isNontransactional true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database has not been opened");
        }
        
        mObjectServerSession.setAllowNontransactionalReads(isNontransactional);
        mAllowNontransactionalReads = isNontransactional;
    }


    /**
     * Store a Persistable object whose modified or new flags are set to true. 
     * Afterwards, the object's new and modified flags are set to
     * false. The loaded flag is set to true (in case the object was new). Also ensures
     * that the object is WRITE-locked. The method {@link #flushSerializedObjectQueue()} must be 
     * called after the caller has finished storing Persistables. 
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param aPersistable a persistable object.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public void storePersistable(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        if (!aPersistable.enerj_IsModified() && !aPersistable.enerj_IsNew()) {
            return;
        }

        long cid = aPersistable.enerj_GetClassId();
        long oid = getOID(aPersistable);

        // Error if object is not loaded or not new at this point.
        if ( !aPersistable.enerj_IsLoaded() && !aPersistable.enerj_IsNew()) {
            throw new ODMGRuntimeException("INTERNAL: Attempted to store a persistable object that is not loaded or not new. OID=" + oid + " CID=" + cid);
        }


        // Force persister to be set if it currently null. If it's not null, it must match.
        Persister currentPersister = aPersistable.enerj_GetPersister();
        if (currentPersister != this) {
            if (currentPersister == null) {
                aPersistable.enerj_SetPersister(this);
            }
            else {
                throw new ODMGRuntimeException("A persistable object jumped between owner databases. OID=" + oid + " CID=" + cid);
            }
        }
        
        if (oid == ObjectSerializer.NULL_OID) {
            throw new ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null. CID=" + cid);
        }

        byte[] objectBytes = PersistableHelper.createSerializedImage(aPersistable);
        try {
            addToSerializedObjectQueue(cid, oid, objectBytes, aPersistable.enerj_IsNew() );
            // This will be write-locked by the server.
            aPersistable.enerj_SetLockLevel(EnerJTransaction.WRITE); 
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Could not store object. OID=" + oid + " CID=" + cid, e);
        }

        // It's not modified or new anymore (to the client's view), but it is loaded.
        aPersistable.enerj_SetNew(false);
        aPersistable.enerj_SetModified(false);
        aPersistable.enerj_SetLoaded(true);
    }


    /**
     * Queue a serialized object to be stored to the database. If a pre-configured
     * number of bytes have already been queued, the queue is flushed to the database. 
     * The method {@link #flushSerializedObjectQueue()} must be 
     * called after the caller has finished storing objects. 
     *
     * @param aCID the Class Id of the object.
     * @param anOID the OID of the object.
     * @param aSerializedObject the object serialized to a byte array. This
     *  array must <em>NOT</em> be reused by the caller after this call completes.
     *  I.e., the caller should allocate a new byte array for each call to this method.
     * @param isNew true if this is a new object in the database.
     *
     * @throws ODMGException in the event of an error.
     * 
     * TODO This and {@link #flushSerializedObjectQueue()} should be its own class.
     */
    private void addToSerializedObjectQueue(long aCID, long anOID, byte[] aSerializedObject, boolean isNew)
        throws ODMGException
    {
        mSerializedObjectQueue.add( new SerializedObject(anOID, aCID, aSerializedObject, isNew) );
        mSerializedObjectQueueSize += aSerializedObject.length;
        
        if (mSerializedObjectQueueSize >= sMaxSerializedObjectQueueSize) {
            flushSerializedObjectQueue();
        }
    }
    

    /**
     * Flushes previously serialized objects queued by {@link #storePersistable(Persistable)}
     * to the database.  
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @throws ODMGException in the event of an error.
     */
    public void flushSerializedObjectQueue() throws ODMGException
    {
        SerializedObject[] objects = mSerializedObjectQueue.toArray(new SerializedObject[ mSerializedObjectQueue.size() ]);
        try {
            mObjectServerSession.storeObjects(objects);
        }
        catch (RuntimeException e) {
            throw new ODMGException("Could not store object.", e);
        }
        finally {
            mSerializedObjectQueue.clear();
            mSerializedObjectQueueSize = 0;
        }
    }
    

    /**
     * Updates the database with the schema for aPersistable, if necessary.
     *
     * @param aPersistable the persistable.
     */
    private void updateSchema(Persistable aPersistable)
    {
        long cid = aPersistable.enerj_GetClassId();

        // Check if we already know that the database has this CID in the schema.
        if (mKnownSchemaCIDs.contains(cid)) {
            return;
        }
        
        // TODO what if schema already there, but index added? Need to handle this, so don't just return above.

        // Try to add this to schema, even if it might already exist.
        Class<? extends Persistable> persistableClass = aPersistable.getClass();
        SchemaAnnotation schemaAnn = persistableClass.getAnnotation(SchemaAnnotation.class);
        if (schemaAnn == null) {
            throw new ODMGRuntimeException("Cannot find SchemaAnnotation on " + aPersistable.getClass() + ". Class was not previously enhanced.");
        }

        String[] persistentFieldNames = schemaAnn.persistentFieldNames();
        String[] transientFieldNames = schemaAnn.transientFieldNames();
        String[] superTypeNames = ClassUtil.getAllSuperTypeNames(persistableClass);
        byte[] originalClassBytes = schemaAnn.originalByteCodes();
        String className = persistableClass.getName();

        try {
            mObjectServerSession.addClassVersionToSchema(className, cid, 
                            superTypeNames, originalClassBytes, persistentFieldNames, transientFieldNames);

            // Add indexes.
            //  Class level
            Index indexAnn = persistableClass.getAnnotation(Index.class);
            addIndexSchema(className, indexAnn, null);
            
            // Field Level
            for (Field field : persistableClass.getDeclaredFields()) {
                indexAnn = field.getAnnotation(Index.class);
                addIndexSchema(className, indexAnn, field.getName());
            }
            
            //  Accessor level
            for (Method method : persistableClass.getDeclaredMethods()) {
                indexAnn = method.getAnnotation(Index.class);
                addIndexSchema(className, indexAnn, method.getName());
            }
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException("Error adding new ClassVersionSchema", e);
        }
        
        // Cache the fact that it is known in the schema.
        mKnownSchemaCIDs.add(cid);
    }
    
    /**
     * If anIndexAnn is not null, add the index to the database.
     *
     * @param anIndexAnn
     * @param propertyName if not null, defines the single property for this index.
     */
    private void addIndexSchema(String aClassName, Index anIndexAnn, String propertyName) throws ODMGException
    {
        if (anIndexAnn == null) {
            return;
        }
        
        if (anIndexAnn.properties().length == 0 && propertyName == null) {
            throw new ODMGException("Class scoped annotation for Index " + anIndexAnn.name() + " for class " + 
                            aClassName + " does not define any properties.");
        }
        
        IndexSchema indexSchema = new IndexSchema(anIndexAnn, propertyName);
        mObjectServerSession.addIndex(aClassName, indexSchema);
    }

    /**
     * Saves a serialized image of the Persistable in the cache.
     *
     * @param aPersistable the Persistable to be saved.
     */
    void savePersistableImage(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        byte[] objectBytes = PersistableHelper.createSerializedImage(aPersistable);
        long oid = getOID(aPersistable);
        if (oid == ObjectSerializer.NULL_OID) {
            throw new org.odmg.ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null");
        }
        
        mClientCache.setSavedImage(oid, objectBytes);
    }
    

    /**
     * Restores the serialized image previously saved by savePersistableImage and
     * clears the saved image from the cache.
     *
     * @param aPersistable the Persistable to be restored.
     */
    void restoreAndClearPersistableImage(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        long oid = getOID(aPersistable);
        if (oid == ObjectSerializer.NULL_OID) {
            throw new org.odmg.ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null");
        }
        
        byte[] image = mClientCache.getAndClearSavedImage(oid);
        if (image != null) {
            PersistableHelper.loadSerializedImage(this, aPersistable, image);
        }
    }


    /**
     * Clears the serialized image previously saved by savePersistableImage.
     *
     * @param aPersistable the Persistable whose saved image will be cleared.
     */
    void clearPersistableImage(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        long oid = getOID(aPersistable);
        if (oid == ObjectSerializer.NULL_OID) {
            throw new org.odmg.ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null");
        }
        
        mClientCache.setSavedImage(oid, null);
    }

    public Schema getSchema() throws ODMGException
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database has not been opened");
        }
        
        return mObjectServerSession.getSchema();
    }

    /**
     * Evicts all cached objects from the local cache.
     */
    public void evictAll()
    {
        mClientCache.evictAll();
    }
    

    /**
     * Evicts the specified cached object from the local cache.
     * 
     * @param anOID the OID to be evicted.
     */
    public void evict(long anOID)
    {
        mClientCache.evict(anOID);
    }
    
    /**
     * Resolve the object's entire object graph recursively until all instances are fully loaded.
     * This allows the object's entire graph to be used without a dependence on the database.
     *
     * @param anObject the object to be resolved.
     * @param shouldDisassociate if true, the object tree will be disassociated from 
     *  its Persister.
     *
     * @throws IOException if an error occurs
     */
    public void resolveObject(Object anObject, boolean shouldDisassociate) throws IOException
    {
        PersistableHelper.resolveObject((Persistable)anObject, shouldDisassociate);
    }
    

    /** 
     * Gets the client-side cache for this database.
     *
     * @return a ClientCache.
     */
    PersistableObjectCache getClientCache()
    {
        return mClientCache;
    }
    
    /**
     * Gets an extent iterator for the given class. For Ener-J internal use only.
     * Clients should use getExtent().iterator().
     *
     * @param aPersistentCapableClass the class to retrieve the extent for.
     * @param wantSubClassInstances true if subclasses should be included in the extent.
     * 
     * @return the Extent.
     */
    ExtentIterator getExtentIterator(Class aPersistentCapableClass, boolean wantSubClassInstances)
    {
        ExtentIterator iterator = mObjectServerSession.createExtentIterator(aPersistentCapableClass.getName(), wantSubClassInstances);
        //if (mIsLocal) {
            // Server is running locally so we have to proxy the iterator using the session's RequestProcessor.
        //    iterator = (ExtentIterator)RequestProcessorProxy.newInstance(iterator, mLocalRequestProcessor);
        //}
        
        return iterator;
    }
    

    /**
     * Gets the open transaction that is bound to this database.
     *
     * @return the EnerJTransaction, or null if no transaction is bound. 
     */
    public EnerJTransaction getTransaction()
    {
        return mBoundToTransaction;
    }

    /**
     * Sets the current transaction for the database.
     *
     * @param aTransaction a EnerJTransaction. This may be null to clear the current
     *  transaction.
     *
     * @throws TransactionInProgressException if a transaction is already bound
     *  to this database.
     */
    void setTransaction(EnerJTransaction aTransaction)
    {
        if (mBoundToTransaction != null && aTransaction != null) {
            throw new TransactionInProgressException("Transaction already in progress and bound to this database");
        }

        mBoundToTransaction = aTransaction;
        // Reset the OID cache.
        mOIDCache = null;
        mOIDCachePosition = 0;
    }


    /**
     * Adds a new Persistable to the Persister's modified list.
     * The Persistable's database and new OID are set. 
     * The Persistable is added to the local database cache, if any.
     * The database schema is updated if aPersistable's class is not known to the database.
     *
     * @param aPersistable the new Persistable.
     *
     * @return returns the object's new OID.
     */
    private long addNewPersistable(Persistable aPersistable)
    {
        checkBoundTransaction();

        long oid = getNewOID();
        PersistableHelper.setOID(this, oid, aPersistable);

        // Make sure that the schema has this persistable's CID.
        updateSchema(aPersistable);

        // Add it to modified list. Must be done _after_ OID is set.
        addToModifiedList(aPersistable);
        
        // Cache it
        mClientCache.add(oid, aPersistable);
        
        return oid;
    }


    /**
     * Checks that the database is open, it currently bound to a transaction,
     * and that the transaction is current for the caller's thread.
     *
     * @throws TransactionNotInProgressException if a transaction is not bound,
     * or the transaction belongs to a different thread.
     * @throws DatabaseClosedException if the database has not been opened yet.
     */
    private void checkBoundTransaction()
    {
        checkBoundTransaction(false);
    }


    /**
     * Checks that the database is open, it currently bound to a transaction,
     * and that the transaction is current for the caller's thread.
     * 
     * @param readOnly true if the operation will be read-only.
     *
     * @throws TransactionNotInProgressException if a transaction is not bound,
     * or the transaction belongs to a different thread.
     * @throws DatabaseClosedException if the database has not been opened yet.
     */
    private void checkBoundTransaction(boolean readOnly)
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database has not been opened");
        }
        
        if (mBoundToTransaction == null) {
            if (readOnly && mAllowNontransactionalReads) {
                return; // That's all we need to check
            }

            throw new TransactionNotInProgressException("Transaction not in progress on the database");
        }
        
        if (EnerJTransaction.getCurrentTransaction() != mBoundToTransaction) {
            throw new TransactionNotInProgressException("Database bound to a Transaction in another thread");
        }
    }
    
    

    /**
     * Determines whether we're in non-transactional read mode. Non-transaction read mode
     * exists when the database is not bound to a transaction and it is set to allow non-transactional reads. 
     *
     * @return true if in non-transactional read mode, else false.
     */
    private boolean isNontransactionalReadMode()
    {
        if (mBoundToTransaction != null) {
            return false;
        }
        
        return mAllowNontransactionalReads;
    }
    

    /**
     * Gets a new OID.
     *
     * @return a new OID.
     *
     * @throws TransactionNotInProgressException if a transaction is not bound,
     * or the transaction belongs to a different thread.
     */
    private long getNewOID()
    {
        if (mOIDCache == null || mOIDCachePosition >= mOIDCache.length) {
            try {
                mOIDCache = mObjectServerSession.getNewOIDBlock(10);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ODMGRuntimeException("Could not load object", e);
            }

            mOIDCachePosition = 0;
        }
        
        long oid = mOIDCache[mOIDCachePosition];
        ++mOIDCachePosition;
        return oid;
    }
    

    // Start of org.odmg.Database interface methods...



    /**
     * {@inheritDoc}
     * @param name The name of the database as a URI. Currently, three forms
     * of URIs are possible:
     * <p>
     * <i>enerj[.subprotocol]://[username[:password]@]hostname[:port]/dbname[?parameters]</i> -- connects to the
     * database server at 'hostname' serving database 'dbname'. If hostname is '-', a server
     * is instantiated in the client's JVM and the database is opened locally. If subprotocol is 
     * not specified, the default Ener-J plug-ins are used. <p> 
     * <i>dbname</i> -- a server
     * is instantiated in the client's JVM using the default Ener-J plug-ins and the database is opened locally.
     * However, if the <code>enerj.dburi</code> system property is set, it is used as the base URI and dbname is
     * appended to it.<p> 
     * <i>dbname@hostname[:port]</i> -- connects to the
     * database server at 'hostname' serving database 'dbname' using the default Ener-J plug-ins.<p> 
     * </code><p>
     * 
     * The system property <code>enerj.plugins</code>, if set, represents a colon-separated list of 
     * plug-in class names which register subprotocols. If set, these classes are loaded and their static initializers 
     * are called to register new subprotocols. This allows new plug-ins to be referenced via the subprotocol on
     * the URI.<p>
     */
    public void open(String name, int accessMode) throws ODMGException 
    {
        if (mIsOpen) {
            throw new DatabaseOpenException("Database is already open");
        }
        
        // Make properties from the URI
        // Copy the system properties as defaults.
        Properties props = new Properties( System.getProperties() );
        String uriString;
        if (name.indexOf("//") < 0) {
            int atIdx = name.indexOf('@'); 
            if (atIdx > 0 && (atIdx + 1) > name.length()) {
                // "dbname@host[:port]"
                String dbname = name.substring(0, atIdx);
                String host = name.substring(atIdx + 1);
                uriString = "enerj://" + host + '/' + dbname;
            }
            else {
                // "dbname"
                uriString = System.getenv("enerj.dburi");
                if (uriString == null) {
                    uriString = "enerj://-";
                }
                
                uriString += '/' + name;
            }
        }
        else {
            // Full URI: "enerj[:subprotocol]://[username[:password]@]hostname[:port]/dbname"
            uriString = name;
        }
        
        URI uri;
        try {
            uri = new URI(uriString);
        }
        catch (URISyntaxException e) {
            throw new ODMGException("Malformed database name URI: " + uriString, e);
        }
        
        String host = uri.getHost();
        int port = uri.getPort();  // -1 if undefined.
        String userinfo = uri.getUserInfo();
        String username = null;
        String password = null;

        if (userinfo != null) {
            // Has password?
            int idx = userinfo.indexOf(':');
            if (idx > 0 && (idx + 1) > userinfo.length()) {
                username = userinfo.substring(0, idx);
                password = userinfo.substring(idx + 1);
            }
            else {
                username = userinfo;
            }
        }
        
        // Path begins with a '/', so strip it.
        String dbname = uri.getPath();
        if (dbname != null && dbname.length() > 1) {
            dbname = dbname.substring(1);
        }

        if (dbname == null || dbname.length() == 0) {
            throw new ODMGException("Malformed URI, must have a database name: " + uriString);
        }
        
        String scheme = uri.getScheme();
        String pluginClassName;
        mIsLocal = false;
        if (scheme.equals("enerj")) {
            if (host == null || host.length() == 0 || host.equals("-")) {
                // Local connection with PagedObjectServer - default.
                host = null;
                pluginClassName = PagedObjectServer.class.getName();
                mIsLocal = true;
            }
            else {
                // TODO --  no remote plug-in yet...
                pluginClassName = PagedObjectServer.class.getName();
            }
        }
        else if (scheme.startsWith("enerj.")) {
            // TODO handle sub-protocols. Use PluginHelper to resolve. PluginHelper registers plugins via system prop enerj.plugins
            String subprotocol = scheme.substring("enerj.".length() );
            throw new ODMGException("Unknown subprotocol '" + subprotocol + "': " + uriString);
        }
        else {
            throw new ODMGException("Malformed URI, must have scheme of 'enerj': " + uriString);
        }

        // Set ObjectServer properties
        props.setProperty(ENERJ_DBNAME_PROP, dbname);
        props.setProperty(ENERJ_ACCESS_MODE_PROP, String.valueOf(accessMode) );

        if (username != null) {
            props.setProperty(ENERJ_USERNAME_PROP, username);
        }
        
        if (password != null) {
            props.setProperty(ENERJ_PASSWORD_PROP, password);
        }
        
        if (host != null) {
            props.setProperty(ENERJ_HOSTNAME_PROP, host);
        }
        
        if (port != -1) {
            props.setProperty(ENERJ_PORT_PROP, String.valueOf(port) );
        }

        // Add query parameters
        String query = uri.getQuery();
        if (query != null) {
            URIUtil.parseQuery(query, false, props);
        }

        props.setProperty(ENERJ_CLIENT_LOCAL, Boolean.toString(mIsLocal));
        
        mObjectServerSession = (ObjectServerSession)PluginHelper.connect(pluginClassName, props);

        // TODO Allow cache size to be set.
        mClientCache = new DefaultPersistableObjectCache(5000);
        mModifiedObjects = new ModifiedPersistableList();
        mSerializedObjectQueue = new ArrayList<SerializedObject>(100);
        mKnownSchemaCIDs = new HashSet<Long>(127);

        // Initialize CID map with known system CIDs.
        // This is so we don't try to update the schema with system CIDs, since they
        // must exist to update it.
        Iterator<Long> iter = SystemCIDMap.getSystemCIDs();
        while (iter.hasNext()) {
            mKnownSchemaCIDs.add( iter.next() );
        }

        //if (mIsLocal) {
            // Server is running locally. We need to run session in another thread. Create a proxy per session.
            // TODO pool these?
        //    mLocalRequestProcessor = new RequestProcessor("Local PagedObjectServer Thread", true);
        //    mObjectServerSession = (ObjectServerSession)RequestProcessorProxy.newInstance(mObjectServerSession, mLocalRequestProcessor);
        //}
        
        // After successful open, check if we need to make it current.
        if (sCurrentProcessDatabase == null) {
            sCurrentProcessDatabase = this;
        }
        
        sCurrentDatabaseForThread.set(this);

        PersisterRegistry.pushPersisterForThread(this);
        mIsOpen = true;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.Database#close()
     */
    public void close() throws ODMGException 
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database is not open");
        }
        
        if (mBoundToTransaction != null) {
            throw new TransactionInProgressException("Cannot close, transaction is in progress");
        }
        
        try {
            mObjectServerSession.disconnect();
        }
        finally {
            mObjectServerSession = null;
            mClientCache = null;
            mModifiedObjects = null;
            mSerializedObjectQueue = null;
            mBoundToTransaction = null;
            mKnownSchemaCIDs = null;
            mIsOpen = false;
            mIsLocal = false;
        
            // Clear any current database.
            if (sCurrentProcessDatabase == this) {
                sCurrentProcessDatabase = null;
            }

            sCurrentDatabaseForThread.remove();
            
            // Pop ourselves as a Persister.
            if (PersisterRegistry.getCurrentPersisterForThread() == this) {
                PersisterRegistry.popPersisterForThread(this);
            }
            else {
                throw new ODMGException("This database was not the active Perister for the thread. Check open/close pairing.");
            }
        } // End finally
    }
    

    public void bind(Object object, String name) throws ObjectNameNotUniqueException 
    {
        checkBoundTransaction();
        
        if ( !(object instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object is not persistable");
        }
        
        mObjectServerSession.bind(getOID(object), name);
    }
    

    public void unbind(String name) throws ObjectNameNotFoundException 
    {
        checkBoundTransaction();
        mObjectServerSession.unbind(name);
    }
    

    public Object lookup(String name) throws ObjectNameNotFoundException 
    {
        checkBoundTransaction(true);

        long oid = mObjectServerSession.lookup(name);
        return getObjectForOID(oid);
    }
    

    /**
     * {@inheritDoc}
     * <p>
     * Note: Only has an effect if the object is new within this transaction.<p>
     *
     * Note: Ener-J implements persistence by reachability, as defined by
     * the ODMG specification. Hence in Ener-J, this method adds the object
     * to its class extent and marks it as not collectable. 
     * To make the object eligible for garbage collection, you must call deletePersistent().
     */
    public void makePersistent(Object object) 
    {
        checkBoundTransaction();
        if ( !(object instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object of class " + object.getClass().getName() + " is not persistable");
        }
        
        Persistable persistable = (Persistable)object;

        if (persistable.enerj_IsNew()) {
            // If this causes an OID to be allocated, the object will
            // be added to the modified list.
            getOID(persistable);
        }
    }
    

    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J implements persistence by reachability, as defined by
     * the ODMG specification. Hence you normally do not need to call this method.
     * Ener-J's instant garbage collection algorithm takes care of removing an
     * object from the database once it is fully dereferenced. This preserves
     * referential integrity and the integrity of any extents defined for the class.
     * However, if the object was explicitly added to the database using makePersistent(), you must call
     * this method to make it eligible for collection.
     */
    public void deletePersistent(Object object) 
    {
        checkBoundTransaction();
        // Force remove from extent, update comment
        if ( !(object instanceof Persistable)) {
            throw new ODMGRuntimeException("Object is not persistable");
        }
        
        Persistable persistable = (Persistable)object;

        //  TODO  decr GC count.- in server
        
        /* // Handle case if it was added then deleted in same transaction.
        if (persistable.enerj_IsNew()) {
            // ??? maybe its new AND referenced elsewhere. Only really delete if GC count has reached zero.
            // ??? maybe GC stuff can just take care of this...
            mBoundToTransaction.removeFromModifiedList(persistable);
        }
        else {
            
        }
        */
        // For now... Just remove from extent. Not quite the correct semantics, but it'll do.
        mObjectServerSession.removeFromExtent( getOID(persistable) );
    }
    
    

    // ...End of org.odmg.Database interface methods.


    /**
     * Gets an Extent for the given class.
     *
     * @param aPersistentCapableClass the class to retrieve the extent for.
     * @param wantSubClassInstances true if subclasses should be included in the extent.
     * 
     * @return the Extent.
     */
    public Extent getExtent(Class aPersistentCapableClass, boolean wantSubClassInstances)
    {
        return new EnerJExtent(this, aPersistentCapableClass, wantSubClassInstances);
    }
    
    /**
     * Get the number of objects in the Extent.
     * 
     * @param aPersistentCapableClass the class to retrieve the extent for.
     * @param wantSubClassInstances true if subclasses should be included in the extent.
     *
     * @return the number of objects in the extent.
     */
    public long getExtentSize(Class aPersistentCapableClass, boolean wantSubClassInstances)
    {
        return mObjectServerSession.getExtentSize(aPersistentCapableClass.getName(), wantSubClassInstances);
    }

    /**
     * Checks if a Persistable is at or above the desired lock level.
     *
     * @param aPersistable the persistable to be checked.
     * @param aDesiredLockLevel the lock level desired.
     *
     * @return true if the object is at or above the level, else false.
     */
    static boolean isAtLockLevel(Persistable aPersistable, int aDesiredLockLevel)
    {
        int currLock = aPersistable.enerj_GetLockLevel();
        switch (currLock) {
        case READ:
            return aDesiredLockLevel == READ || aDesiredLockLevel == UPGRADE || aDesiredLockLevel == WRITE;
    
        case UPGRADE:
            return aDesiredLockLevel == UPGRADE || aDesiredLockLevel == WRITE;
    
        case WRITE:
            return aDesiredLockLevel == WRITE;
    
        case EnerJTransaction.NO_LOCK:
            return true;
            
        default:
            throw new ODMGRuntimeException("Bad lock level on object: " + currLock);
        }
    }
    
    /**
     * Begins a transaction to be associated explicitly with this Database.
     * 
     * @param aTransaction the transaction initiating the begin().
     */
    void begin(EnerJTransaction aTransaction)
    {
        if (isTransactionActive()) {
            throw new TransactionInProgressException("Transaction already started");
        }
        
        if (EnerJTransaction.getCurrentTransaction() != null) {
            throw new TransactionInProgressException("Another Transaction is already in progress on this thread");
        }
        
        if (!isOpen()) {
            throw new DatabaseClosedException("Database is not open yet.");
        }

        // Clear-out any remnants of a previous transaction. 
        clearModifiedList();
        getClientCache().clearPrefetches();

        // On error, this must be cleared.
        // This must be called prior to begin logic because it may throw saying that the
        // Database is already bound to a transaction.
        setTransaction(aTransaction);
        
        try {
            mObjectServerSession.beginTransaction();
        }
        catch (RuntimeException e) {
            setTransaction(null);
            throw e;
        }
    }

    /**
     * Flushes modified and referenced new objects to the server. Does not
     * affect the state of the transaction. The current queue of pending 
     * object modifications is cleared.
     */
    void flush()
    {
        if (mFlushIterator != null) {
            return; // Prevent reentrancy
        }
        
        try {
            flushAndKeepModifiedList();
        }
        finally {
            // Clear out modified objects.
            clearModifiedList();
        }
    }

    /**
     * Flushes modified and referenced new objects to the server. Does not
     * affect the state of the transaction. The modified list is NOT cleared.
     */
    private void flushAndKeepModifiedList()
    {
        try {
            // Note that we start an iterator each item. We do this instead of
            // calling storePersistable() recursively. Such recursion could become
            // very deep. The iterator allows us to add new objects, 
            // essentially flattening the recursion.
            mFlushIterator = getModifiedListIterator();
            while (mFlushIterator.hasNext()) {
                Persistable persistable = mFlushIterator.next();
                int nextIndex = mFlushIterator.nextIndex();
                
                // This can indirectly insert objects into the list due to
                // ObjectSerializer. Note that storePersistable() marks the object as 
                // not new and and not modified now that it will be flushed.
                storePersistable(persistable);

                // Objects could have been inserted into the list before
                // the cursor. We have to back up to the point just after the last
                // object we retrieved to start processing the list there.
                // Note that on the next iteration, more objects could be inserted
                // before these, effectively reproducing recursion.
                for (int i = mFlushIterator.nextIndex() - nextIndex; i > 0; --i) {
                    mFlushIterator.previous();
                }
            }
            
            flushSerializedObjectQueue();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }
        finally {
            mFlushIterator = null;
        }
    }

    /** 
     * @see org.odmg.Transaction#abort()
     */
    void abort() 
    {
        try {
            mObjectServerSession.rollbackTransaction();
    
            // Rollback modified objects and clear new objects.
            boolean restoreValues = getTransaction().getRestoreValues();
            
            for (Iterator<Persistable> iter = getModifiedListIterator(); iter.hasNext(); ) {
                Persistable persistable = iter.next();
                if (restoreValues) {
                    // Restore (rollback) the object
                    restoreAndClearPersistableImage(persistable);
                }

                if (persistable.enerj_IsNew()) {
                    // New objects are evicted from the cache and get their OID cleared.
                    getClientCache().evict( getOID(persistable) );
                    persistable.enerj_SetPrivateOID(ObjectSerializer.NULL_OID);
                }
            }

            clearModifiedList();
            
            // See defined behavior on EnerJTransaction.setRestoreValues.
            if (restoreValues || getTransaction().getRetainValues()) {
                getClientCache().makeObjectsNonTransactional();
            }
            else {
                // Note: hollowObjects() invokes enerj_Hollow() which clears the cache lock state.
                getClientCache().hollowObjects();
            }
            
            getClientCache().clearPrefetches();
        }
        finally {
            setTransaction(null);
        }
    }

    /** 
     * @see org.odmg.Transaction#checkpoint()
     */
    void checkpoint() 
    {
        // Go thru the modified list and store the objects.
        flushAndKeepModifiedList();

        // Go thru the modified list and clear the persistable image. Essentially
        // a rollback after this call rolls back to this point.
        boolean restoreValues = getTransaction().getRestoreValues();
        if (restoreValues) {
            for (Iterator<Persistable> iter = getModifiedListIterator(); iter.hasNext(); ) {
                clearPersistableImage(iter.next());
            }
        }

        clearModifiedList();
        mObjectServerSession.checkpointTransaction();
    }

    /** 
     * @see org.odmg.Transaction#commit()
     */
    void commit() 
    {
        try {
            // Flush pending modified objects out to server.
            flush();
    
            // See defined behavior on setRetainValues().
            if (getTransaction().getRetainValues()) {
                getClientCache().makeObjectsNonTransactional();
            }
            else {
                // Note: hollowObjects() invokes enerj_Hollow() which clears the cache lock state.
                getClientCache().hollowObjects();
            }
    
            getClientCache().clearPrefetches();
            mObjectServerSession.commitTransaction();
        }
        finally {
            setTransaction(null);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J will wait for the lock to become obtainable, or until an
     * error (such as deadlock detected) occurs. Note also that you cannot downgrade
     * a lock (e.g., go from WRITE to READ).
     */
    void lock(Object obj, int lockMode) throws LockNotGrantedException 
    {
        if ( !(obj instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object parameter to lock is not a Persistable object");
        }
        
        Persistable persistable = (Persistable)obj;
        
         // If already at the proper level, just return.
        if (isAtLockLevel(persistable, lockMode)) {
            return;
        }       

        //  TODO  allow lock timeout to be set on database or transaction. -1L means wait til we get it.
        mObjectServerSession.getLock(getOID(persistable), lockMode, -1L);

        persistable.enerj_SetLockLevel(lockMode);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J will <em>not</em> wait for the lock to become obtainable.
     * If the lock is not immediately obtainable, false is returned.
     */
    boolean tryLock(Object obj, int lockMode) 
    {
        if ( !(obj instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object parameter to lock is not a Persistable object");
        }
        
        Persistable persistable = (Persistable)obj;
        
        // If already at the proper level, just return.
        if (isAtLockLevel(persistable, lockMode)) {
            return true;
        }       

        try {
            // Don't wait for lock.
            mObjectServerSession.getLock(getOID(persistable), lockMode, 0L);
            persistable.enerj_SetLockLevel(lockMode);
        }
        catch (LockNotGrantedException e) {
            return false;
        }

        return true;
    }
}
