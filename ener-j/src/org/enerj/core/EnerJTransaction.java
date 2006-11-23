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

import org.odmg.DatabaseClosedException;
import org.odmg.LockNotGrantedException;
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
    
    /** The current database for the Transaction, only while it's open */
    private EnerJDatabase mTransactionDatabase = null;
    
    /** True if values of objects should be retained on commit (instead of hollowing the object). */
    private boolean mRetainValues = false;
    
    /** True if values of objects should be restored to their state at the start of the transaction
     * on an abort.
     */
    private boolean mRestoreValues = false;


    /**
     * Construct a basic transaction.
     */
    public EnerJTransaction()
    {
    }

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
    
    /**
     * Gets the database associated with this transaction.
     * 
     * @return the EnerJDatabase associated with this transaction.
     * 
     * @throws TransactionNotInProgressException if no database is associated with this transaction.
     */
    private EnerJDatabase getTransactionDatabase() throws TransactionNotInProgressException
    {
        if (mTransactionDatabase == null) {
            throw new TransactionNotInProgressException("Transaction is not in progress. No Database is associated with this transaction.");
        }
        
        return mTransactionDatabase;
    }

    /**
     * Alternate form of begin which allows a transaction to be associated
     * explicitly with a Database.
     *
     * @param aDatabase the Database the transaction is being performed on.
     */
    public void begin(EnerJDatabase aDatabase)
    {
        aDatabase.begin(this);

        // After successful begin, store current transaction for thread.
        sCurrentTransactionForThread.set(this);
        mTransactionDatabase = aDatabase;
    }


    /**
     * Gets the database associated with this transaction. 
     *
     * @return a EnerJDatabase, or null if the transaction has not been started.
     */
    public EnerJDatabase getDatabase()
    {
        return mTransactionDatabase;
    }
    

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
    

    /**
     * Closes the current transaction. Assumes checkIsOpenAndOwnedByThread()
     * has already been called.
     */
    private void closeCurrentTransaction()
    {
        sCurrentTransactionForThread.remove();

        if (mTransactionDatabase != null) {
            mTransactionDatabase.setTransaction(null);
        }
        
        mTransactionDatabase = null;
    }
    

    /**
     * Check that the transaction is open and owned by the caller's thread.
     *
     * @throws TransactionNotInProgressException if it's not open or
     * not owned by the caller's thread.
     */
    private void checkIsOpenAndOwnedByThread()
    {
        if (!getTransactionDatabase().isTransactionOpen() || getCurrentTransaction() != this) {
            throw new TransactionNotInProgressException("Transaction not in progress, or you are attempting to use it from the wrong thread");
        }
    }
    

    /**
     * Flushes modified and referenced new objects to the server. Does not
     * affect the state of the transaction. The current queue of pending 
     * object modifications is cleared.
     */
    public void flush()
    {
        checkIsOpenAndOwnedByThread();
        getTransactionDatabase().flush();
    }

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
        getTransactionDatabase().clear();
        closeCurrentTransaction();
    }
    
    // Start of org.odmg.Transaction interface methods...

    /** 
     * {@inheritDoc}
     * @see org.odmg.Transaction#abort()
     */
    public void abort() 
    {
        checkIsOpenAndOwnedByThread();
        try {
            getTransactionDatabase().abort();
        }
        finally {
            // Whether or not the abort succeeded, close the current transaction.
            closeCurrentTransaction();
        }
    }
    

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
        EnerJDatabase database = EnerJDatabase.getCurrentDatabase();
        if (database == null) {
            throw new DatabaseClosedException("Database is not open yet.");
        }

        begin(database);
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.Transaction#checkpoint()
     */
    public void checkpoint() 
    {
        checkIsOpenAndOwnedByThread();
        getTransactionDatabase().checkpoint();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.Transaction#commit()
     */
    public void commit() 
    {
        checkIsOpenAndOwnedByThread();
        try {
            getTransactionDatabase().commit();
        }
        finally {
            closeCurrentTransaction();
        }
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.Transaction#isOpen()
     */
    public boolean isOpen() 
    {
        return mTransactionDatabase != null && mTransactionDatabase.isTransactionOpen();
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.Transaction#join()
     */
    public void join()
    {
        if (!isOpen()) {
            throw new TransactionNotInProgressException("Transaction not in progress");
        }
        
        // ODMG 3.0 - 2.10.3 says that an implicit leave occurs if another Transaction
        // is currently active on the caller's thread.
        EnerJTransaction txn = getCurrentTransaction();
        if (txn != null && txn != this) {
            txn.leave();
        }

        sCurrentTransactionForThread.set(this);
    }
    
    /** 
     * {@inheritDoc}
     * @see org.odmg.Transaction#leave()
     */
    public void leave() 
    {
        checkIsOpenAndOwnedByThread();
        sCurrentTransactionForThread.remove();
    }

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
        getTransactionDatabase().lock(obj, lockMode);
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J will <em>not</em> wait for the lock to become obtainable.
     * If the lock is not immediately obtainable, false is returned.
     */
    public boolean tryLock(Object obj, int lockMode) 
    {
        checkIsOpenAndOwnedByThread();
        return getTransactionDatabase().tryLock(obj, lockMode);
    }
    
    // ...End of org.odmg.Transaction interface methods.
}
