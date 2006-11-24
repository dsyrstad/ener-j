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

package org.enerj.server;

import gnu.trove.TLongArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.enerj.core.ClassVersionSchema;
import org.enerj.core.DefaultPersistableObjectCache;
import org.enerj.core.ModifiedPersistableList;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.PersistableObjectCache;
import org.enerj.core.Persister;
import org.enerj.core.Schema;
import org.enerj.core.SparseBitSet;
import org.enerj.util.RequestProcessor;
import org.enerj.util.RequestProcessorProxy;
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
    private static Logger mLogger = Logger.getLogger( BaseObjectServerSession.class.getName() ); 
    
    private BaseObjectServer mObjectServer;
    private boolean mAllowNontransactionalReads = false;
    /** New OIDs that need to be added to their extents on commit. Key is CID, value is a list of OIDs. */
    private HashMap<Long, TLongArrayList> mPendingNewOIDs = null;
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
    /** If we're running locally within the client JVM, will have a RequestProcessor setup to proxy requests.
     * Otherwise this will be null. */
    private RequestProcessor mRequestProcessor = null;
    

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
        mLogger.info("Session " + this + " is connected.");
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
     * @return the current RequestProcessor if the session is running in the client's JVM, else null.
     */
    RequestProcessor getRequestProcessor()
    {
        return mRequestProcessor;
    }
    
    /**
     * Sets the RequestProcessor that is the proxy to this session.
     * 
     * @param aRequestProcessor the RequestProcessor if the session is running in the client's JVM, else null.
     */
    void setRequestProcessor(RequestProcessor aRequestProcessor)
    {
        mRequestProcessor = aRequestProcessor;
    }
    
    /**
     * Flush pending extent updates out to the extents.
     */
    private void updateExtents()
    {
        Schema schema;
        try {
            schema = getSchema();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }

        ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
        for (long cid : mPendingNewOIDs.keySet()) {
            TLongArrayList oids = mPendingNewOIDs.get(cid);
            if (oids != null) {
                ClassVersionSchema version = schema.findClassVersion(cid);
                if (version != null) {
                    SparseBitSet extent = extentMap.getExtent( version.getLogicalClassSchema().getClassName() );
                    if (extent != null) {
                        int size = oids.size();
                        for (int i = 0; i < size; i++) {
                            // TODO We need to track the extent size. Do this with a delta that is updated right
                            // TODO at commit time.
                            extent.set(oids.get(i), true);
                        }
                    }
                }
            }
        }
        
        flushModifiedObjects();
        mPendingNewOIDs.clear();
    }
    
    
    /**
     * Flushes modified objects using {@link #storeObjects(SerializedObject[])}. 
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    protected void flushModifiedObjects()
    {
        List<SerializedObject> images = new ArrayList<SerializedObject>(1000); 
        for (Iterator<Persistable> iter = getModifiedListIterator(); iter.hasNext(); ) {
            Persistable persistable = iter.next();
            byte[] image = PersistableHelper.createSerializedImage(persistable);
            images.add( new SerializedObject(persistable.enerj_GetPrivateOID(), persistable.enerj_GetClassId(), 
                            image, persistable.enerj_IsNew()) );
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
        Bindery bindery = (Bindery)getObjectForOID(BaseObjectServer.BINDERY_OID);
        bindery.bind(anOID, aName);
        flushModifiedObjects();
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#lookup(java.lang.String)
     */
    public long lookup(String aName) throws ObjectNameNotFoundException
    {
        Bindery bindery = (Bindery)getObjectForOID(BaseObjectServer.BINDERY_OID);
        return bindery.lookup(aName);
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#unbind(java.lang.String)
     */
    public void unbind(String aName) throws ObjectNameNotFoundException
    {
        checkTransactionActive();
        Bindery bindery = (Bindery)getObjectForOID(BaseObjectServer.BINDERY_OID);
        bindery.unbind(aName);
        flushModifiedObjects();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#removeFromExtent(long)
     */
    public void removeFromExtent(long anOID) throws ObjectNotPersistentException
    {
        checkTransactionActive();
        
        ClassInfo classInfo;
        Schema schema; 
        try {
            classInfo = getClassInfoForOIDs(new long[] { anOID })[0];
            schema = getSchema();
        }
        catch (ODMGException e) {
            throw new ObjectNotPersistentException("Object is not persistent", e);
        }

        if (classInfo == null) {
            throw new ObjectNotPersistentException("Object is not persistent");
        }
        
        ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
        ClassVersionSchema version = schema.findClassVersion( classInfo.getCID() );
        if (version != null) {
            SparseBitSet extent = extentMap.getExtent( version.getLogicalClassSchema().getClassName() );
            if (extent != null) {
                extent.set(anOID, false);
            }
        }

        // TODO remove from indexes

        flushModifiedObjects();
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

        ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
        SparseBitSet extent = extentMap.getExtent(aClassName);
        long result = 0;
        if (extent != null) {
            result += extent.getNumBitsSet();
        }

        if (wantSubclasses) {
            Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
            for (ClassVersionSchema classVersion : subclasses) {
                extent = extentMap.getExtent( classVersion.getLogicalClassSchema().getClassName() );
                if (extent != null) {
                    result += extent.getNumBitsSet();
                }
            }
        }

        return result;
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#createExtentIterator(java.lang.String, boolean)
     */
    public ExtentIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
    {
        // TODO What about objects added during txn? Flush from client first? I think we're OK. Flush updates extents.
        Schema schema;
        try {
            schema = getSchema();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }

        List<SparseBitSet> extents = new ArrayList<SparseBitSet>();
        ExtentMap extentMap = (ExtentMap)getObjectForOID(BaseObjectServer.EXTENTS_OID);
        SparseBitSet extent = extentMap.getExtent(aClassName);
        if (extent != null) {
            extents.add(extent);
        }

        if (wantSubclasses) {
            Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
            for (ClassVersionSchema classVersion : subclasses) {
                extent = extentMap.getExtent( classVersion.getLogicalClassSchema().getClassName() );
                if (extent != null) {
                    extents.add(extent);
                }
            }
        }

        ExtentIterator extentIterator = new DefaultExtentIterator(extents);
        // Proxy iterator using Session's RequestProcessor if running locally in the client's JVM.
        if (mRequestProcessor != null) {
            extentIterator = (ExtentIterator)RequestProcessorProxy.newInstance(extentIterator, mRequestProcessor);
        }

        return extentIterator;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getSchema()
     */
    public Schema getSchema() throws ODMGException
    {
        return mObjectServer.getSchema();
    }
    
    
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#addClassVersionToSchema(java.lang.String, long, java.lang.String[], byte[], java.lang.String[], java.lang.String[])
     */
    public void addClassVersionToSchema(String aClassName, long aCID, String[] someSuperTypeNames, byte[] anOriginalByteCodeDef, String[] somePersistentFieldNames, String[] someTransientFieldNames) throws ODMGException
    {
        Schema schema = getSchema();
        
        // CID already in schema?
        ClassVersionSchema version = schema.findClassVersion(aCID);
        if (version != null) {
            String existingClassName = version.getLogicalClassSchema().getClassName();
            if (!existingClassName.equals(aClassName)) {
                throw new ODMGException("Given class name " + aClassName + " does not match existing class name " + existingClassName
                                + " for CID " + aCID);
            }

            // Already exists, just return.
            return;
        }
        
        mObjectServer.addClassVersionToSchema(aClassName, aCID, someSuperTypeNames, anOriginalByteCodeDef,
                        somePersistentFieldNames, someTransientFieldNames);
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#disconnect()
     */
    public void disconnect() throws ODMGException 
    {
        mLogger.info("Session " + this + " is disconnecting.");
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
     * {@inheritDoc}.  Subclass should call super.storeObjects() after to doing its work.
     * @see org.enerj.server.ObjectServerSession#storeObjects(org.enerj.server.SerializedObject[])
     */
    public void storeObjects(SerializedObject[] someObjects) throws ODMGException
    {
        checkTransactionActive();

        for (SerializedObject object : someObjects) {
            long oid = object.getOID();
            long cid = object.getCID();

            if (object.isNew()) {
                // Queue this object to be added to its extent on commit - only after delegate stores successfully.
                TLongArrayList oids = mPendingNewOIDs.get(cid);
                if (oids == null) {
                    // First instance of the CID to be stored in this txn, create new list.
                    oids = new TLongArrayList(1000);
                    mPendingNewOIDs.put(cid, oids);
                }
                
                oids.add(oid);
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

        mObjectCache.evictAll();
        if (mPendingNewOIDs == null) {
            mPendingNewOIDs = new HashMap<Long, TLongArrayList>(128);
        }
        else {
            mPendingNewOIDs.clear();
        }
    }

    /** 
     * {@inheritDoc}.  Subclass should call super.checkpointTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#checkpointTransaction()
     */
    public void checkpointTransaction() throws ODMGRuntimeException 
    {
        checkTransactionActive();
        updateExtents();
    }

    /** 
     * {@inheritDoc}. Subclass should call super.commitTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#commitTransaction()
     */
    public void commitTransaction() throws ODMGRuntimeException 
    {
        checkTransactionActive();
        updateExtents();
        mObjectCache.evictAll();
    }

    /** 
     * {@inheritDoc}. Subclass should call super.rollbackTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#rollbackTransaction()
     */
    public void rollbackTransaction() throws ODMGRuntimeException 
    {
        checkTransactionActive();
        mPendingNewOIDs.clear();
        mObjectCache.evictAll();
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

        mModifiedObjects.addToModifiedList(aPersistable);
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
    public Iterator<Persistable> getModifiedListIterator()
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
        mLogger.info("anObject = " + anObject.getClass());
        if ( !(anObject instanceof Persistable)) {
            return ObjectSerializer.NULL_OID;
        }

        Persistable persistable = (Persistable)anObject;
        long oid = persistable.enerj_GetPrivateOID();
        if (oid != ObjectSerializer.NULL_OID) {
            return oid;
        }
        
        if (persistable.enerj_IsNew()) {
            try {
                // Allocate one new oid.
                oid = getNewOIDBlock(1)[0];
            }
            catch (ODMGException e) {
                throw new ODMGRuntimeException(e);
            }
            
            mLogger.info("New Object " + persistable.getClass() + " with oid " + oid);
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
}

