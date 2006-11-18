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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.enerj.core.Persistable;
import org.enerj.core.Persister;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNotPersistentException;

/**
 * Implements common code that can be used by an ObjectServerSession implementation.
 *
 * @version $Id: ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
abstract public class BaseObjectServerSession implements ObjectServerSession, Persister
{
    /** First available user OID. */
    public static final long FIRST_USER_OID = 1000L;
    /** Last available system CID. CIDs from 1 to this value are reserved for pre-enhanced system classes. */
    public static final long LAST_SYSTEM_CID = 10000L;
    /** System OID: the Schema. */
    public static final long SCHEMA_OID = 1L;
    /** System OID: the Bindery. */
    public static final long BINDERY_OID = 2L;
    /** System OID: the Class Extents. */
    public static final long EXTENTS_OID = 3L;

    private ObjectServer mObjectServer;
    private boolean mAllowNontransactionalReads = false;
    /** New OIDs that need to be added to their extents on commit. Key is CID, value is a list of OIDs. */
    private HashMap<Long, TLongArrayList> mPendingNewOIDs = null;
    /** Our shutdown hook. */
    private Thread mShutdownHook = null;
    /** True if session is connected. */
    private boolean mConnected = false;
    /** List of Persistable objects created or modified during this transaction. */
    private LinkedList<Persistable> mModifiedObjects = new LinkedList<Persistable>();

    /**
     * Construct a new BaseObjectServerSession.
     */
    protected BaseObjectServerSession(ObjectServer anObjectServer)
    {
        mObjectServer = anObjectServer;

        // Register a shutdown hook for this session.
        mShutdownHook = new ShutdownHook(this);
        Runtime.getRuntime().addShutdownHook(mShutdownHook);

        mConnected = true;
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
     * Flush pending extent updates out to the extents.
     *
     */
    private void updateExtents()
    {
        /*
        // Add new objects to extents.
        EnerJDatabase db = getClientDatabase();
        // Schema may have been changed, so evict everything at this point.
        db.evictAll();  
        DatabaseRoot root = (DatabaseRoot)db.getDatabaseRoot();
        Schema schema = root.getSchema();
        for (long cid : mPendingNewOIDs.keySet()) {
            TLongArrayList oids = mPendingNewOIDs.get(cid);
            if (oids != null) {
                ClassVersionSchema classVersion = schema.findClassVersion(cid);
                if (classVersion != null) {
                    SparseBitSet extent = classVersion.getLogicalClassSchema().getExtentBitSet();
                    int size = oids.size();
                    for (int i = 0; i < size; i++) {
                        // TODO We need to track the extent size. Do this with a delta that is updated right
                        // TODO at commit time.
                        extent.set(oids.get(i), true);
                    }
                }
            }
        }
        */
        
        mPendingNewOIDs.clear();
    }
    
    
    /**
     * Flushes modified objects using {@link #storeObjects(SerializedObject[])}. 
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    protected void flushModifiedObjects()
    {
        List<Persistable> modifiedObjects = getModifiedList();
        // TODO
    }

    //----------------------------------------------------------------------
    // Start of ObjectServerSession interface methods...
    //----------------------------------------------------------------------

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
        // TODO Check if txn exists.
        Bindery bindery = (Bindery)getObjectForOID(BINDERY_OID);
        bindery.bind(anOID, aName);
        flushModifiedObjects();
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#lookup(java.lang.String)
     */
    public long lookup(String aName) throws ObjectNameNotFoundException
    {
        Bindery bindery = (Bindery)getObjectForOID(BINDERY_OID);
        return bindery.lookup(aName);
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#unbind(java.lang.String)
     */
    public void unbind(String aName) throws ObjectNameNotFoundException
    {
        // TODO Check if txn exists.
        Bindery bindery = (Bindery)getObjectForOID(BINDERY_OID);
        bindery.unbind(aName);
        flushModifiedObjects();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#removeFromExtent(long)
     */
    public void removeFromExtent(long anOID) throws ObjectNotPersistentException
    {
        /*
        EnerJDatabase db = getClientDatabase();
        DatabaseRoot root = (DatabaseRoot)db.getObjectForOID(ObjectSerializer.DATABASE_ROOT_OID);
        ClassVersionSchema classVersion;
        try {
            long cid = getCIDsForOIDs(new long[] { anOID })[0];
            classVersion = root.getSchema().findClassVersion(cid);
        }
        catch (ODMGException e) {
            throw new ObjectNotPersistentException("Object is not persistent", e);
        }

        if (classVersion == null) {
            throw new ObjectNotPersistentException("Object is not persistent");
        }
        
        SparseBitSet extent = classVersion.getLogicalClassSchema().getExtentBitSet();
        extent.set(anOID, false);
        */
        // TODO remove from indexes
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getExtentSize(java.lang.String, boolean)
     */
    public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
    {
        long result = 0;
        /*
        Schema schema = mDatabase.getDatabaseRoot().getSchema();
        LogicalClassSchema candidateClassSchema = schema.findLogicalClass(aClassName);
        if (candidateClassSchema != null) {
            result += candidateClassSchema.getExtentBitSet().getNumBitsSet();
        }

        if (wantSubclasses) {
            Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
            for (ClassVersionSchema classVersion : subclasses) {
                result += classVersion.getLogicalClassSchema().getExtentBitSet().getNumBitsSet();
            }
        }
        */
        return result;
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#createExtentIterator(java.lang.String, boolean)
     */
    public ExtentIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
    {
        /*
        // TODO What about objects added during txn? Flush from client first?
        Schema schema = mDatabase.getDatabaseRoot().getSchema();
        ExtentIterator extentIterator = new DefaultExtentIterator(aClassName, wantSubclasses, schema, this);
        // Proxy iterator if running locally on client.
        if (mObjectServer.mRequestProcessor != null) {
            extentIterator = (ExtentIterator)RequestProcessorProxy.newInstance(extentIterator, mObjectServer.mRequestProcessor);
        }

        return extentIterator;
        */ return null;
    }

    
    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#disconnect()
     */
    public void disconnect() throws ODMGException 
    {
        //  TODO  - write disconnect/session info in log.
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

    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#getAllowNontransactionalReads()
     */
    public boolean getAllowNontransactionalReads() throws ODMGException
    {
        return mAllowNontransactionalReads;
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.server.ObjectServerSession#setAllowNontransactionalReads(boolean)
     */
    public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException
    {
        mAllowNontransactionalReads = isNontransactional;
    }

    /** 
     * {@inheritDoc}.  Subclass should call super.storeObjects() after to doing its work.
     * @see org.enerj.server.ObjectServerSession#storeObjects(org.enerj.server.SerializedObject[])
     */
    public void storeObjects(SerializedObject[] someObjects) throws ODMGException
    {
        // TODO Check if txn exists.

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
     * {@inheritDoc}. Subclass should call super.beginTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#beginTransaction()
     */
    public void beginTransaction() throws ODMGRuntimeException 
    {
        // TODO Check that no txn exists.

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
        // TODO Check if txn exists.
        updateExtents();
    }

    /** 
     * {@inheritDoc}. Subclass should call super.commitTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#commitTransaction()
     */
    public void commitTransaction() throws ODMGRuntimeException 
    {
        // TODO Check if txn exists.
        updateExtents();
    }

    /** 
     * {@inheritDoc}. Subclass should call super.rollbackTransaction() prior to doing its work.
     * @see org.enerj.server.ObjectServerSession#rollbackTransaction()
     */
    public void rollbackTransaction() throws ODMGRuntimeException 
    {
        // TODO Check if txn exists.
        mPendingNewOIDs.clear();
    }

    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#addToModifiedList(org.enerj.core.Persistable)
     */
    public void addToModifiedList(Persistable aPersistable)
    {
        // TODO if no txn exists, ignore.

        mModifiedObjects.addLast(aPersistable);
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#clearModifiedList()
     */
    public void clearModifiedList()
    {
        mModifiedObjects.clear();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getModifiedList()
     */
    public List<Persistable> getModifiedList()
    {
        return mModifiedObjects;
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
        // TODO - Have to check if object is in the modified list. We need a modifiedlist object.

        // TODO Auto-generated method stub
        return null;
    }
    
    /** 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#getOID(java.lang.Object)
     */
    public long getOID(Object anObject)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.core.Persister#loadObject(org.enerj.core.Persistable)
     */
    public void loadObject(Persistable aPersistable)
    {
        // TODO Auto-generated method stub
        
    }

    //----------------------------------------------------------------------
    // ...End of ObjectServerSession interface methods.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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

