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
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $

package org.enerj.server.pageserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.enerj.core.ClassSchema;
import org.enerj.core.ClassVersionSchema;
import org.enerj.core.DefaultPersistableObjectCache;
import org.enerj.core.GenericKey;
import org.enerj.core.IndexAlreadyExistsException;
import org.enerj.core.IndexSchema;
import org.enerj.core.ModifiedPersistableList;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.PersistableObjectCache;
import org.enerj.core.PersistentBxTree;
import org.enerj.core.Persister;
import org.enerj.core.PersisterRegistry;
import org.enerj.core.Schema;
import org.enerj.core.SparseBitSet;
import org.enerj.core.SystemCIDMap;
import org.enerj.server.ClassInfo;
import org.enerj.server.DBIterator;
import org.enerj.server.ObjectServer;
import org.enerj.server.ObjectServerSession;
import org.enerj.server.SerializedObject;
import org.enerj.util.OIDUtil;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNotPersistentException;
import org.odmg.TransactionInProgressException;
import org.odmg.TransactionNotInProgressException;

/**
 * Implements common code that can be used by an ObjectServerSession implementation.
 *
 * @version $Id: ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
abstract public class BaseObjectServerSession implements ObjectServerSession, Persister
{
    private static Logger sLogger = Logger.getLogger( BaseObjectServerSession.class.getName() );
    
    private BaseObjectServer mObjectServer;
    private boolean mAllowNontransactionalReads = false;
    /** Our shutdown hook. */
    private Thread mShutdownHook = null;
    /** True if session is connected. */
    private boolean mConnected = false;
    /** True if a transaction is active. */
    private boolean mTransactionActive = false;
    /** Cache of loaded objects. This cache lasts only during the duration of a transaction. */
    private PersistableObjectCache mObjectCache = new DefaultPersistableObjectCache(1000);
    /** List of Persistable objects created or modified during this transaction. */
    private ModifiedPersistableList mModifiedObjects = new ModifiedPersistableList();
    /** Iterator that is active while we're in {@link #flushModifiedObjects()} objects. */
    private ListIterator<Persistable> mFlushIterator = null;
    private boolean mInSchemaInit = false;

    /** New OIDs that need to be added to their extents on commit. Key is CIDX, value is a list of OIDs. */
    protected Map<Integer, Set<StoredOID>> mStoredOIDs = null;

    /**
     * Construct a new BaseObjectServerSession.
     */
    protected BaseObjectServerSession(BaseObjectServer anObjectServer)
    {
        mObjectServer = anObjectServer;

        // Register a shutdown hook for this session.
        mShutdownHook = new ShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(mShutdownHook);

        mConnected = true;
        sLogger.fine("Session " + this + " is connected.");
    }

    /**
     * Determines whether the session is connected.
     *
     * @return true if it is, false if not.
     */
    boolean isConnected()
    {
        return mConnected;
    }
    
    /**
     * Marks this session as disconnected.
     */
    void setDisconnected()
    {
        mConnected = false;
    }
    
    /**
     * @return true if schema initialization is in progress.
     */
    boolean isInSchemaInit()
    {
        return mInSchemaInit;
    }

    /**
     * Sets whether schema initialization is in progress. 
     *
     * @param inSchemaInit true if schema initialization is in progress.
     */
    void setInSchemaInit(boolean someInSchemaInit)
    {
        mInSchemaInit = someInSchemaInit;
    }

    /**
     * Flushes modified objects using {@link #storeObjects(SerializedObject[])}. 
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    protected void flushModifiedObjects()
    {
        // Are we already flushing? If so, we can just return.
        if (mFlushIterator != null) {
            return; 
        }
        
        List<SerializedObject> images = new ArrayList<SerializedObject>(1000);
        mFlushIterator = getModifiedListIterator();
        try {
            while (mFlushIterator.hasNext() ) {
                Persistable persistable = mFlushIterator.next();
                int nextIndex = mFlushIterator.nextIndex();

                byte[] image = PersistableHelper.createSerializedImage(persistable);
                images.add( new SerializedObject(persistable.enerj_GetPrivateOID(), persistable.enerj_GetClassId(),
                                image, persistable.enerj_IsNew()) );
                
                // Mark object as not new and and not modified now that it will be flushed.
                persistable.enerj_SetModified(false);
                persistable.enerj_SetNew(false);
                
                // Objects could have been inserted into the list before
                // the cursor. We have to back up to the point just after the last
                // object we retrieved to start processing the list there.
                // Note that on the next iteration, more objects could be inserted
                // before these, effectively reproducing recursion.
                for (int i = mFlushIterator.nextIndex() - nextIndex; i > 0; --i) {
                    mFlushIterator.previous();
                }
            }
            
            if (!images.isEmpty()) {
                SerializedObject[] objs = new SerializedObject[images.size()];
                objs = images.toArray(objs);
                try {
                    storeObjects(objs);
                }
                catch (ODMGException e) {
                    throw new ODMGRuntimeException(e);
                }
            }
        }
        finally {
            mFlushIterator = null;
            clearModifiedList();
        }
    }

    /**
     * Pushes this session as the Persister and clears the Persistable state.
     * This should be used instead of a direct call to {@link PersisterRegistry#pushPersisterForThread(Persister)}. 
     */
    void pushAsPersister()
    {
        PersisterRegistry.pushPersisterForThread(this);
        resetPersistables();
    }
    
    /**
     * Pops this session as the Persister and clears the Persistable state.
     * This should be used instead of a direct call to {@link PersisterRegistry#popPersisterForThread(Persister)}. 
     */
    void popAsPersister()
    {
        resetPersistables();
        PersisterRegistry.popPersisterForThread(this);
    }
    
    /**
     * Resets the persistable cache and modified list of the Persister.
     */
    private void resetPersistables()
    {
        mObjectCache.reset();
        clearModifiedList();
    }

    /**
     * Gets the schema for the database.
     *
     * @return the Schema or null if it doesn't exist.
     * 
     * @throws ODMGException if an error occurs.
     */
    protected Schema getSchemaOrNull() throws ODMGException
    {
        return mObjectServer.getSchema();
    }
    
    // Start of ObjectServerSession interface methods...


    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getObjectServer()
     */
    public ObjectServer getObjectServer()
    {
        return mObjectServer;
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#bind(long, java.lang.String)
     */
    public void bind(long anOID, String aName) throws ObjectNameNotUniqueException
    {
        checkTransactionActive();
        pushAsPersister();
        try {
            Bindery bindery = (Bindery)getObjectForOID(BaseObjectServer.BINDERY_OID);
            bindery.bind(anOID, aName);
            flushModifiedObjects();
        }
        finally {
            popAsPersister();
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#lookup(java.lang.String)
     */
    public long lookup(String aName) throws ObjectNameNotFoundException
    {
        pushAsPersister();
        try {
            Bindery bindery = (Bindery)getObjectForOID(BaseObjectServer.BINDERY_OID);
            return bindery.lookup(aName);
        }
        finally {
            popAsPersister();
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#unbind(java.lang.String)
     */
    public void unbind(String aName) throws ObjectNameNotFoundException
    {
        checkTransactionActive();
        pushAsPersister();
        try {
            Bindery bindery = (Bindery)getObjectForOID(BaseObjectServer.BINDERY_OID);
            bindery.unbind(aName);
            flushModifiedObjects();
        }
        finally {
            popAsPersister();
        }
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#removeFromExtent(long)
     */
    public void removeFromExtent(long anOID) throws ObjectNotPersistentException
    {
        checkTransactionActive();
        
        ClassInfo classInfo;
        try {
            classInfo = getClassInfoForOIDs(new long[] { anOID })[0];
        }
        catch (ODMGException e) {
            throw new ObjectNotPersistentException("Object is not persistent", e);
        }

        if (classInfo == null) {
            throw new ObjectNotPersistentException("Object is not persistent");
        }
        
        pushAsPersister();
        try {
            ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
            int cidx = OIDUtil.getCIDX(anOID);
            SparseBitSet extent = extentMap.getExtent(cidx);
            if (extent != null) {
                extent.set(anOID, false);
            }
    
            // TODO remove from indexes
    
            flushModifiedObjects();
        }
        finally {
            popAsPersister();
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getExtentSize(java.lang.String, boolean)
     */
    public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
    {
        Schema schema;
        try {
            schema = getSchema();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }

        pushAsPersister();
        try {
            ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
            ClassSchema classSchema = schema.findClassSchema(aClassName);
            long result = 0;
            if (classSchema != null) {
                int cidx = classSchema.getClassIndex(); 
                SparseBitSet extent = extentMap.getExtent(cidx);
                if (extent != null) {
                    result += extent.getNumBitsSet();
                }
            }
    
            if (wantSubclasses) {
                Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
                for (ClassVersionSchema classVersion : subclasses) {
                    SparseBitSet extent = extentMap.getExtent( classVersion.getClassSchema().getClassIndex() );
                    if (extent != null) {
                        result += extent.getNumBitsSet();
                    }
                }
            }
    
            return result;
        }
        finally {
            popAsPersister();
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#createExtentIterator(java.lang.String, boolean)
     */
    public DBIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
    {
        // TODO What about objects added during txn? Flush from client first? I think we're OK. Flush updates extents.
        Schema schema;
        try {
            schema = getSchema();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }

        pushAsPersister();
        try {
            List<SparseBitSet> extents = new ArrayList<SparseBitSet>();
            ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
            ClassSchema classSchema = schema.findClassSchema(aClassName);
            if (classSchema != null) {
                SparseBitSet extent = extentMap.getExtent(classSchema.getClassIndex());
                if (extent != null) {
                    extents.add(extent);
                }
            }
    
            if (wantSubclasses) {
                Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
                for (ClassVersionSchema classVersion : subclasses) {
                    SparseBitSet extent = extentMap.getExtent( classVersion.getClassSchema().getClassIndex() );
                    if (extent != null) {
                        extents.add(extent);
                    }
                }
            }
    
            DBIterator extentIterator = new DefaultExtentIterator(extents);
            return extentIterator;
        }
        finally {
            popAsPersister();
        }
    }
    

    /**
     * Flush pending updates out to the extents and indexes.
     */
    private void updateExtentsAndIndexes()
    {
        // Is Schema being built?
        if (isInSchemaInit()) {
            mStoredOIDs.clear();
            return;
        }

        Schema schema;
        try {
            schema = getSchemaOrNull();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }

        pushAsPersister();
        try {
            ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
            IndexMap indexMap = (IndexMap)getObjectForOID(BaseObjectServer.INDEXES_OID);
            for (int cidx : mStoredOIDs.keySet()) {
                Set<StoredOID> oids = mStoredOIDs.get(cidx);
                if (oids != null) {
                    ClassSchema classSchema = schema.findClassSchema(cidx);
                    if (classSchema == null) {
                        if (SystemCIDMap.isSystemCID((long)cidx)) {
                            continue; // Ignore.
                        }

                        throw new ODMGRuntimeException("Cannot find class schema for CIDX " + cidx);
                    }

                    SparseBitSet extent = extentMap.getExtent(cidx);
                    boolean hasExtent = extent != null;
                    
                    // TODO In the future, we should spin this off to a separate thread that handles
                    // index updates. To check that the index is in sync, the index will post a txn id
                    // when the transaction's index updates are complete.
                    
                    // For indexes, we have to go all of the way up the class hierarchy and
                    // find parent indexes too.
                    List<IndexInfo> indexes = new ArrayList<IndexInfo>();
                    buildIndexList(schema, indexMap, classSchema, indexes);
                    ClassVersionSchema version = classSchema.getLatestVersion();
                    String[] superTypeNames = version.getSuperTypeNames();
                    for (String superTypeName : superTypeNames) {
                        ClassSchema superClassSchema = schema.findClassSchema(superTypeName);
                        buildIndexList(schema, indexMap, superClassSchema, indexes);
                    }
                    
                    boolean hasIndexes = !indexes.isEmpty();

                    for (StoredOID storedOID : oids) {
                        long oid = storedOID.getOID();
                        if (hasExtent && storedOID.isNew()) {
                            extent.set(oid, true);
                        }
                        
                        if (hasIndexes) {
                            // TODO We need to handle replace somehow, which means we need the key
                            // TODO that existed prior to update. For dupl keys, must match OID too.
                            // TODO session method to retrieve stored object, (not updated) but
                            // TODO what about second update.
                            // TODO Add method to tree to get current key for oid.
                            ClassInfo classInfo = getClassInfoForOIDs(new long[] { oid } )[0];
                            Persistable obj = PersistableHelper.createHollowPersistable(classInfo, oid, this);
                            for (IndexInfo indexInfo : indexes) {
                                Object key = GenericKey.createKey(indexInfo.indexSchema, obj);
                                Map index = indexInfo.index;
                                if (index instanceof PersistentBxTree) {
                                    ((PersistentBxTree)index).insert(key, obj);
                                }
                                else {
                                    index.put(key, obj);
                                }
                            }
                        }
                    }
                }
            }
            
            flushModifiedObjects();
            mStoredOIDs.clear();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }
        finally {
            popAsPersister();
        }
    }

    /**
     * Builds a list of IndexInfo for the given class. Adds the IndexInfos to indexes. 
     */
    private void buildIndexList(Schema schema, IndexMap indexMap, ClassSchema classSchema, List<IndexInfo> indexes)
    {
        if (classSchema == null) {
            return;
        }
        
        List<IndexSchema> indexSchemas = classSchema.getIndexes();
        for (IndexSchema indexSchema : indexSchemas) {
            indexes.add( new IndexInfo(indexMap.getIndex(classSchema, indexSchema), indexSchema) );
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getSchema()
     */
    public Schema getSchema() throws ODMGException
    {
        Schema schema = getSchemaOrNull();
        if (schema == null) {
            sLogger.severe("Cannot find Schema");
            throw new ODMGException("Internal Error: Schema does not exist.");
        }
        
        return schema;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#addClassVersionToSchema(java.lang.String, long, java.lang.String[], byte[], java.lang.String[], java.lang.String[])
     */
    public void addClassVersionToSchema(String aClassName, long aCID, String[] someSuperTypeNames, byte[] anOriginalByteCodeDef, String[] somePersistentFieldNames, String[] someTransientFieldNames) throws ODMGException
    {
        Schema schema = getSchema();
        
        // CID already in schema?
        if (!schema.doesCIDExist(aCID)) {
            mObjectServer.addClassVersionToSchema(aClassName, aCID, someSuperTypeNames, anOriginalByteCodeDef,
                            somePersistentFieldNames, someTransientFieldNames);
        }
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#addIndex(java.lang.String, org.enerj.core.IndexSchema)
     */
    public void addIndex(String aClassName, IndexSchema anIndexSchema) throws ODMGException, IndexAlreadyExistsException
    {
        mObjectServer.addIndex(aClassName, anIndexSchema);
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#disconnect()
     */
    public void disconnect() throws ODMGException 
    {
        sLogger.fine("Session " + this + " is disconnected.");
        setDisconnected();
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#shutdown()
     */
    public void shutdown() throws ODMGException
    {
        if (isConnected()) {
            disconnect();
        }

        try {
            Runtime.getRuntime().removeShutdownHook(mShutdownHook);
        }
        catch (Exception e) {
            // Ignore - shutdown may be in progress.
        }

        getObjectServer().shutdown();
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#isTransactionActive()
     */
    public boolean isTransactionActive()
    {
        return mTransactionActive;
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getAllowNontransactionalReads()
     */
    public boolean getAllowNontransactionalReads()
    {
        return mAllowNontransactionalReads;
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#setAllowNontransactionalReads(boolean)
     */
    public void setAllowNontransactionalReads(boolean isNontransactional)
    {
        mAllowNontransactionalReads = isNontransactional;
    }

    /** 
     * {@inheritDoc}.  Subclass should call super.storeObjects() after successfully storing the objects.
     * @see org.enerj.server.ObjectServerSession#storeObjects(org.enerj.server.SerializedObject[])
     */
    public void storeObjects(SerializedObject[] someObjects) throws ODMGException
    {
        checkTransactionActive();

        for (SerializedObject object : someObjects) {
            long oid = object.getOID();
            int cidx = OIDUtil.getCIDX(oid);

            // Queue this object to be added to its extent and any indexes on commit - 
            // only after delegate stores successfully.
            Set<StoredOID> oids = mStoredOIDs.get(cidx);
            if (oids == null) {
                // First instance of the CID to be stored in this txn, create new list.
                oids = new HashSet<StoredOID>(1000);
                mStoredOIDs.put(cidx, oids);
            }
            
            // Only add to set if it doesn't exist.
            StoredOID storedOID = new StoredOID(oid, object.isNew());
            if (!oids.contains(storedOID)) {
                oids.add(storedOID);
            }
        }
    }
    
    /**
     * Sets whether the transaction is active.
     * 
     * @param isTransactionActive true if a transaction is active.
     */
    public void setTransactionActive(boolean isTransactionActive)
    {
        mTransactionActive = isTransactionActive;
    }
    
    /**
     * Checks that a transaction is in progress.
     * 
     * @throws TransactionNotInProgressException if no transaction is in progress.
     */
    public void checkTransactionActive() throws TransactionNotInProgressException
    {
        if (!mTransactionActive) {
            throw new TransactionNotInProgressException("Transaction not in progress.");
        }
    }

    /** 
     * {@inheritDoc}. Subclass should call super.beginTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#beginTransaction()
     */
    public void beginTransaction() throws ODMGRuntimeException 
    {
        if (mTransactionActive) { 
            throw new TransactionInProgressException("Transaction already in progress.");
        }

        mObjectCache.reset();
        if (mStoredOIDs == null) {
            mStoredOIDs = new HashMap<Integer, Set<StoredOID>>(128);
        }
        else {
            mStoredOIDs.clear();
        }
    }

    /** 
     * {@inheritDoc}.  Subclass should call super.checkpointTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#checkpointTransaction()
     */
    public void checkpointTransaction() throws ODMGRuntimeException 
    {
        checkTransactionActive();
        updateExtentsAndIndexes();
    }

    /** 
     * {@inheritDoc}. Subclass should call super.commitTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#commitTransaction()
     */
    public void commitTransaction() throws ODMGRuntimeException 
    {
        checkTransactionActive();
        updateExtentsAndIndexes();
        mObjectCache.reset();
        clearModifiedList();
    }

    /** 
     * {@inheritDoc}. Subclass should call super.rollbackTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#rollbackTransaction()
     */
    public void rollbackTransaction() throws ODMGRuntimeException 
    {
        checkTransactionActive();
        mStoredOIDs.clear();
        mObjectCache.reset();
        clearModifiedList();
    }

    /** 
     * {@inheritDoc} Also ensures that the object in in the cache.
     * @see org.enerj.core.Persister#addToModifiedList(org.enerj.core.Persistable)
     */
    public void addToModifiedList(Persistable aPersistable)
    {
        // If no txn exists, ignore.
        if (!mTransactionActive) {
            return;
        }

        // If we're iterating (flushing), add to the iterator rather than the list directly.
        if (mFlushIterator != null) {
            mFlushIterator.add(aPersistable);
        }
        else {
            mModifiedObjects.addToModifiedList(aPersistable);
        }
        
        // Make sure that it's cached.
        mObjectCache.add(aPersistable.enerj_GetPrivateOID(), aPersistable);
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#clearModifiedList()
     */
    public void clearModifiedList()
    {
        mModifiedObjects.clearModifiedList();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getModifiedListIterator()
     */
    public ListIterator<Persistable> getModifiedListIterator()
    {
        return mModifiedObjects.getIterator();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getObjectForOID(long)
     */
    public Persistable getObjectForOID(long anOID)
    {
        return getObjectsForOIDs(new long[] { anOID } )[0];
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getObjectsForOIDs(long[])
     */
    public Persistable[] getObjectsForOIDs(long[] someOIDs)
    {
        // TODO We could cache these as in EnerJDatabase.
        ClassInfo[] classInfo;
        try {
            classInfo = getClassInfoForOIDs(someOIDs);
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }
        
        Persistable[] objects = new Persistable[someOIDs.length];
        int idx = 0;
        for (long oid : someOIDs) {
            // Is object in the cache?
            Persistable persistable = mObjectCache.get(oid);
            // If not, check if object is in the modified list. If not, let server load it.
            if (persistable == null) {
                persistable = mModifiedObjects.getModifiedObjectByOID(oid);
            }
            
            if (persistable == null && classInfo[idx] != null) {
                persistable = PersistableHelper.createHollowPersistable(classInfo[idx], oid, this);
                // Cache the object.
                mObjectCache.add(oid, persistable);
            }

            objects[idx++] = persistable;
        }

        return objects;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getOID(java.lang.Object)
     */
    public long getOID(Object anObject)
    {
        if ( !(anObject instanceof Persistable)) {
            return ObjectSerializer.NULL_OID;
        }

        Persistable persistable = (Persistable)anObject;
        long oid = persistable.enerj_GetPrivateOID();
        if (oid != ObjectSerializer.NULL_OID) {
            return oid;
        }
        
        if (persistable.enerj_IsNew()) {
            long oidx;
            try {
                // Allocate one new oid.
                oidx = getNewOIDXBlock(1)[0];
            }
            catch (ODMGException e) {
                throw new ODMGRuntimeException(e);
            }

            long cid = persistable.enerj_GetClassId();
            int cidx;
            if (SystemCIDMap.isSystemCID(cid)) {
                cidx = (int)cid;
            }
            else {
                throw new ODMGRuntimeException("I found myself trying to create a new none System object: " + persistable.getClass().getName());
            }
            
            oid = OIDUtil.createOID(cidx, oidx);
            PersistableHelper.setOID(this, oid, persistable);
            // This call adds the object to the cache too.
            addToModifiedList(persistable);
        }
        
        return oid;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#loadObject(org.enerj.core.Persistable)
     */
    public void loadObject(Persistable aPersistable)
    {
        long oid = getOID(aPersistable);
        // Ensure the object is in the cache.
        mObjectCache.add(oid, aPersistable);
        
        try {
            byte[] image = loadObjects( new long[] { oid } )[0];
            PersistableHelper.loadSerializedImage(this, aPersistable, image);
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }
    }


    // ...End of ObjectServerSession interface methods.
    /**
     * Our JVM ShutdownHook thread.
     */
    private static final class ShutdownHook extends Thread
    {
        private ObjectServerSession mSession;
        
        ShutdownHook(ObjectServerSession aSession)
        {
            mSession = aSession;
        }
        
        public void run()
        {
            // TODO log shutdown hook invoked.
            //System.err.println("ObjectServer Shutdown hook invoked.");
            try {
                mSession.shutdown();
            }
            catch (ODMGException e) {
                // TODO log...
                System.err.println("Shutdown problem:");
                e.printStackTrace();
            }
        }
    }
    
    protected static final class StoredOID
    {
        private long mOID;
        private boolean mIsNew;
        
        public StoredOID(long anOID, boolean isNew)
        {
            mOID = anOID;
            mIsNew = isNew;
        }

        public boolean isNew()
        {
            return mIsNew;
        }

        public long getOID()
        {
            return mOID;
        }
        
        public int hashCode()
        {
            return (int)mOID;
        }
        
        public boolean equals(Object anObject)
        {
            if (anObject == null || !(anObject instanceof StoredOID)) {
                return false;
            }
            
            return mOID == ((StoredOID)anObject).mOID;
        }
    }
    

    /**
     * Holder for an association between the index and its IndexSchema.
     */
    private static final class IndexInfo
    {
        Map index;
        IndexSchema indexSchema;

        public IndexInfo(Map someIndex, IndexSchema someIndexSchema)
        {
            index = someIndex;
            indexSchema = someIndexSchema;
        }
    }
}

