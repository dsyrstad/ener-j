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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/EnerJTransaction.java,v 1.9 2006/05/30 19:05:26 dsyrstad Exp $

package org.enerj.core;

import java.util.LinkedList;
import java.util.ListIterator;

import org.enerj.server.ObjectServer;
import org.odmg.ClassNotPersistenceCapableException;
import org.odmg.Database;
import org.odmg.DatabaseClosedException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.Transaction;
import org.odmg.TransactionInProgressException;
import org.odmg.TransactionNotInProgressException;

/**
 * Ener-J implementation of org.odmg.Transaction.
 *
 * @version $Id: EnerJTransaction.java,v 1.9 2006/05/30 19:05:26 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Transaction
 */
public class EnerJTransaction implements Transaction
{
    /** Augments Transaction's lock levels to indicate no lock is held. */
    public static final int NO_LOCK = 0;

    /** Current transaction for current thread. Set when 
     * a transaction is started (begin). It is unset when a transaction is commited or
     * aborted.
     */
    private static ThreadLocal<EnerJTransaction> sCurrentTransactionForThread = new ThreadLocal<EnerJTransaction>();
    
    /** True if the Transaction has started, but neither commit nor abort have been called. */
    private boolean mIsOpen = false;

    /** The current database for the Transaction, only while it's open */
    private EnerJDatabase mTransactionDatabase = null;
    
    /** List of Persistable objects created or modified during this transaction. 
     */
    private LinkedList<Persistable> mModifiedObjects = new LinkedList<Persistable>();
    
    /** Non-null if the transaction is in the process of flushing objects. This
     * represents the current position in mModifiedObjects. */
    private ListIterator<Persistable> mFlushIterator = null; 
    
    /** True if values of objects should be retained on commit (instead of hollowing the object). */
    private boolean mRetainValues = false;
    
    /** True if values of objects should be restored to their state at the start of the transaction
     * on an abort.
     */
    private boolean mRestoreValues = false;

    //----------------------------------------------------------------------
    /**
     * Construct a basic transaction.
     */
    public EnerJTransaction()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Ener-J Server use only. Construct a EnerJTransaction that is connected to
     * an MetaObjectServer. Exists primarly for servers
     * that want to use the API and participate in the client's transaction.
     *
     * @param aDatabase a EnerJDatabase whose transaction has already been started.
     */
    public EnerJTransaction(EnerJDatabase aDatabase)
    {
        begin(aDatabase, false);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the current transaction for the caller's thread. There can be at most
     * one Transaction open per thread at any given time.
     *
     * @return a EnerJTransaction, or null if there is no open Transaction for the 
     * caller's thread.
     */
    public static EnerJTransaction getCurrentTransaction()
    {
        return sCurrentTransactionForThread.get();
    }
    
    //----------------------------------------------------------------------
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

        case NO_LOCK:
            return true;
            
        default:
            throw new ODMGRuntimeException("Bad lock level on object: " + currLock);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Alternate form of begin which allows a transaction to be associated
     * explicityly with a Database.
     *
     * @param aDatabase the Database the transaction is being performed on.
     */
    public void begin(Database aDatabase)
    {
        begin(aDatabase, true);
    }
    
    //----------------------------------------------------------------------
    /**
     * Alternate form of begin which allows a transaction to be associated
     * explicitly with a Database.
     *
     * @param aDatabase the Database the transaction is being performed on.
     * @param clientSideTxn if true, the transaction will be started on the server.
     *  Use false if the transaction is already started on the server and you just
     *  need to initialize the this EnerJTransaction. 
     */
    private void begin(Database aDatabase, boolean clientSideTxn)
    {
        if (mIsOpen) {
            throw new TransactionInProgressException("Transaction already started");
        }
        
        if (getCurrentTransaction() != null) {
            throw new TransactionInProgressException("Another Transaction is already in progress on this thread");
        }
        
        EnerJDatabase voDatabase = (EnerJDatabase)aDatabase;
        if (aDatabase == null || !voDatabase.isOpen()) {
            throw new DatabaseClosedException("Database is not open yet.");
        }

        // On error, this must be cleared.
        // This must be called prior to begin logic because it may throw saying that the
        // Database is already bound to a transaction.
        voDatabase.setTransaction(this);
        
        if (clientSideTxn) {
            try {
                voDatabase.getMetaObjectServerSession().beginTransaction();
            }
            catch (RuntimeException e) {
                voDatabase.setTransaction(null);
                throw e;
            }

        }

        // After successful begin, store current transaction for thread.
        sCurrentTransactionForThread.set(this);
        
        mIsOpen = true;
        mTransactionDatabase = voDatabase;
        mTransactionDatabase.getClientCache().setTransaction(this);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the database associated with this transaction. 
     *
     * @return a EnerJDatabase, or null if the transaction has not been started.
     */
    public EnerJDatabase getDatabase()
    {
        return mTransactionDatabase;
    }
    
    //----------------------------------------------------------------------
    /**
     * Sets whether object values are retained on transaction commit.
     * This may only be called if a transaction is not active.
     * The default setting for a transaction is false.
     *
     * @param shouldRetainValues if true, on transaction commit object values
     *  are preserved, the object transitions to a non-transactional state, and
     *  the object remains in the local cache.
     *  If false, on transaction commit the cached objects are hollowed.
     *
     * @throws TransactionInProgressException if a transaction is in progress.
     */
    public void setRetainValues(boolean shouldRetainValues)
    {
        if (isOpen()) {
            throw new TransactionInProgressException("setRetainValues can only be called on an inactive transaction");
        }
        
        mRetainValues = shouldRetainValues;
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines whether object values are retained on transaction commit.
     * The default setting for a transaction is false.
     *
     * @return true if, on transaction commit, object values
     *  are preserved, the object transitions to a non-transactional state, and
     *  the object remains in the local cache.
     *  Returns false if, on transaction commit, the object is hollowed.
     */
    public boolean getRetainValues()
    {
        return mRetainValues;
    }
    
    //----------------------------------------------------------------------
    /**
     * Sets whether object values are restored on transaction abort (rollback).
     * This may only be called if a transaction is not active.
     * The default setting for a transaction is false.
     *
     * @param shouldRestoreValues if true, on transaction abort object values
     *  are restored to their state at the time the transaction started, 
     *  the object transitions to a non-transactional state, and
     *  the object, if not created during the transaction, remains in the local cache.
     *  If false, on transaction abort the object is hollowed and, if it is new, evicted from
     *  the cache.
     *
     * @throws TransactionInProgressException if a transaction is in progress.
     */
    public void setRestoreValues(boolean shouldRestoreValues)
    {
        if (isOpen()) {
            throw new TransactionInProgressException("setRestoreValues can only be called on an inactive transaction");
        }
        
        mRestoreValues = shouldRestoreValues;
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines whether object values are retained on transaction commit.
     * The default setting for a transaction is false.
     *
     * @return true if, on transaction commit, object values
     *  are restored to their state at the time the transaction started, 
     * the object transitions to a non-transactional state, and
     *  the object remains in the local cache.
     *  Returns false if, on transaction commit, the object is hollowed.
     */
    public boolean getRestoreValues()
    {
        return mRestoreValues;
    }
    
    //----------------------------------------------------------------------
    /**
     * Add a Persistable to the list of modified objects. The object's lock
     * is upgraded to WRITE. 
     *
     * @param aPersistable the object to be added.
     * @param shouldSaveImage if true and getRestoreValues() is true, 
     *  a serialzed image of aPersistable is saved in the local cache for later
     *  restoration on rollback. Otherwise, a serialized image is not made.
     *  If this is true, aPersistable must NOT be modified yet (modification
     *  should occur after this call).
     */
    void addToModifiedList(Persistable aPersistable, boolean shouldSaveImage)
    {
        checkIsOpenAndOwnedByThread();
        
        // Make sure object is WRITE-locked.
        lock(aPersistable, WRITE);

        // If we're in the process of flushing, insert this right at the cursor.
        // Note that if we were to just call storePersistable() here, we could get
        // into a very deep recursion. See flushAndKeepModifiedList() for more details. 
        if (mFlushIterator != null) {
            mFlushIterator.add(aPersistable);
        }
        else {
            // Otherwise add it to the end of the modified list. 
            mModifiedObjects.addLast(aPersistable);
        }
        
        if (shouldSaveImage && mRestoreValues) {
            mTransactionDatabase.savePersistableImage(aPersistable);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Closes the current transaction. Assumes checkIsOpenAndOwnedByThread()
     * has already been called.
     */
    private void closeCurrentTransaction()
    {
        sCurrentTransactionForThread.remove();

        mIsOpen = false;
        if (mTransactionDatabase != null) {
            mTransactionDatabase.setTransaction(null);
        }
        
        mTransactionDatabase = null;
    }
    
    //----------------------------------------------------------------------
    /**
     * Check that the transaction is open and owned by the caller's thread.
     *
     * @throws TransactionNotInProgressException if it's not open or
     * not owned by the caller's thread.
     */
    private void checkIsOpenAndOwnedByThread()
    {
        if (!mIsOpen || getCurrentTransaction() != this) {
            throw new TransactionNotInProgressException("Transaction not in progress, or you are attempting to use it from the wrong thread");
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Flushes modified and referenced new objects to the server. Does not
     * affect the state of the transaction. The current queue of pending 
     * object modifications is cleared.
     */
    public void flush()
    {
        try {
            flushAndKeepModifiedList();
        }
        finally {
            // Clear out modified objects.
            mModifiedObjects.clear();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Flushes modified and referenced new objects to the server. Does not
     * affect the state of the transaction. The modified list is NOT cleared.
     */
    private void flushAndKeepModifiedList()
    {
        if (mFlushIterator != null) {
            return; // Prevent reentrancy
        }
        
        checkIsOpenAndOwnedByThread();
        
        try {
            // Note that we start an iterator each item. We do this instead of
            // calling storePersistable() recursively. Such recursion could become
            // very deep. The iterator allows us to insert new objects at the current
            // cursor of the iteration, essentially flattening the recursion.
            mFlushIterator = mModifiedObjects.listIterator();
            while (mFlushIterator.hasNext()) {
                Persistable persistable = mFlushIterator.next();
                int nextIndex = mFlushIterator.nextIndex();
                
                // This can indirectly insert objects into the list due to
                // ObjectSerializer.
                mTransactionDatabase.storePersistable(persistable);
                
                // Objects could have been inserted into the list before
                // the cursor. We have to back up to the point just after the last
                // object we retrieved to start processing the list there.
                // Note that on the next iteration, more objects could be inserted
                // before these, effectively reproducing recursion.
                for (int i = mFlushIterator.nextIndex() - nextIndex; i > 0; --i) {
                    mFlushIterator.previous();
                }
            }
            
            mTransactionDatabase.flushSerializedObjectQueue();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }
        finally {
            mFlushIterator = null;
        }
    }

    //----------------------------------------------------------------------
    /**
     * Clears the transaction list of modified and referenced new objects. Similar to a
     * abort(), but only on the client-side.
     * These objects will not be flushed to the server. Modified objects are essentially rolled
     * back in memory if RestoreValues was set, otherwise they are hollowed. The
     * client cache is evicted. The transaction is closed.
     */
    public void clear()
    {
        checkIsOpenAndOwnedByThread();
        
        for (Persistable persistable : mModifiedObjects) {
            if (mRestoreValues) {
                // Restore (rollback) the object
                mTransactionDatabase.restoreAndClearPersistableImage(persistable);
            }

            if (persistable.enerj_IsNew()) {
                // New objects are evicted from the cache and get their OID cleared.
                mTransactionDatabase.getClientCache().evict( mTransactionDatabase.getOID(persistable) );
                persistable.enerj_SetPrivateOID(ObjectServer.NULL_OID);
            }
        }

        mModifiedObjects.clear();
        
        // See defined behavior on setRestoreValues.
        if (mRestoreValues) {
            mTransactionDatabase.getClientCache().makeObjectsNonTransactional();
        }
        else {
            // Note: hollowObjects() invokes enerj_Hollow() which clears the cache lock state.
            mTransactionDatabase.getClientCache().hollowObjects();
        }
        
        mTransactionDatabase.getClientCache().evictAll();
        closeCurrentTransaction();
    }
    
    //----------------------------------------------------------------------
    // Start of org.odmg.Transaction interface methods...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public void abort() 
    {
        checkIsOpenAndOwnedByThread();
        try {
            if (mTransactionDatabase != null && mTransactionDatabase.getMetaObjectServerSession() != null) {
                mTransactionDatabase.getMetaObjectServerSession().rollbackTransaction();
            }

            // Rollback modified objects and clear new objects.
            clear();
        }
        finally {
            // Whether or not the abort succeeded, close the current transaction.
            closeCurrentTransaction();
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * <p>
     * The transaction will be bound to the "current" database. The current database
     * is either the first open database for the thread, or the first open database
     * for the process. A DatabaseClosedException will be thrown if there is
     * no "current" database, or it has not been opened.
     *
     * @see EnerJDatabase#getCurrentDatabase
     */
    public void begin()
    {
        begin( EnerJDatabase.getCurrentDatabase() );
    }
    
    //----------------------------------------------------------------------
    public void checkpoint() 
    {
        checkIsOpenAndOwnedByThread();

        // Go thru the modified list and store the objects.
        flushAndKeepModifiedList();

        // Go thru the modified list and clear the persistable image. Essentially
        // a rollback after this call rolls back to this point.
        if (mRestoreValues) {
            for (Persistable persistable : mModifiedObjects) {
                mTransactionDatabase.clearPersistableImage(persistable);
            }
        }

        mModifiedObjects.clear();
        mTransactionDatabase.getMetaObjectServerSession().checkpointTransaction();
    }
    
    //----------------------------------------------------------------------
    public void commit() 
    {
        checkIsOpenAndOwnedByThread();

        try {
            // Flush pending modified objects out to server.
            flush();

            // See defined behavior on setRetainValues().
            if (mRetainValues) {
                mTransactionDatabase.getClientCache().makeObjectsNonTransactional();
            }
            else {
                // Note: hollowObjects() invokes enerj_Hollow() which clears the cache lock state.
                mTransactionDatabase.getClientCache().hollowObjects();
            }

            mTransactionDatabase.getMetaObjectServerSession().commitTransaction();
        }
        finally {
            closeCurrentTransaction();
        }
    }
    
    //----------------------------------------------------------------------
    public boolean isOpen() 
    {
        return mIsOpen;
    }
    
    //----------------------------------------------------------------------
    public void join()
    {
        if (!mIsOpen) {
            throw new TransactionNotInProgressException("Transaction not in progress, or you are attempting to use it from the wrong thread");
        }
        
        // ODMG 3.0 - 2.10.3 says that an implicit leave occurs if another Transaction
        // is currently active on the caller's thread.
        EnerJTransaction voTransaction = getCurrentTransaction();
        if (voTransaction != null && voTransaction != this) {
            voTransaction.leave();
        }

        sCurrentTransactionForThread.set(this);
    }
    
    //----------------------------------------------------------------------
    public void leave() 
    {
        checkIsOpenAndOwnedByThread();

        sCurrentTransactionForThread.remove();
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J will wait for the lock to become obtainable, or until an
     * error (such as deadlock detected) occurs. Note also that you cannot downgrade
     * a lock (e.g., go from WRITE to READ).
     */
    public void lock(Object obj, int lockMode) throws LockNotGrantedException 
    {
        checkIsOpenAndOwnedByThread();

        if ( !(obj instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object parameter to lock is not a Persistable object");
        }
        
        Persistable persistable = (Persistable)obj;
        
         // If already at the proper level, just return.
        if (isAtLockLevel(persistable, lockMode)) {
            return;
        }       

        //  TODO  allow lock timeout to be set on database or transaction. -1L means wait til we get it.
        mTransactionDatabase.getMetaObjectServerSession().getLock(mTransactionDatabase.getOID(persistable), lockMode, -1L);

        persistable.enerj_SetLockLevel(lockMode);
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J will <em>not</em> wait for the lock to become obtainable.
     * If the lock is not immediately obtainable, false is returned.
     */
    public boolean tryLock(Object obj, int lockMode) 
    {
        checkIsOpenAndOwnedByThread();

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
            mTransactionDatabase.getMetaObjectServerSession().getLock(mTransactionDatabase.getOID(persistable), lockMode, 0L);
            persistable.enerj_SetLockLevel(lockMode);
        }
        catch (LockNotGrantedException e) {
            return false;
        }

        return true;
    }
    
    //----------------------------------------------------------------------
    // ...End of org.odmg.Transaction interface methods.
    //----------------------------------------------------------------------
}

