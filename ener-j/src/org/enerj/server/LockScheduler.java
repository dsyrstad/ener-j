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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/LockScheduler.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;

import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.enerj.core.DeadlockException;

/**
 * The primary implementation of LockServer. Schedules and manages arbitrary locks on arbitrary objects. 
 * The shutdown() method must be used to complete the use of a LockScheduler.
 *
 * @version $Id: LockScheduler.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see LockServer
 */
public class LockScheduler implements LockServer
{
    /** A map of arbitrary object (the key) to a LockedObject (the value). */
    private HashMap mLockedObjectMap;
    /** The deadlock detection algorithm to be used. */
    private DeadlockAlgorithm mDeadlockAlgorithm;
    /** Indicates that the scheduler should shutdown. */
    private boolean mShutdown = false;
    /** The request queue. This is a list of Request objects. */
    private LinkedList mRequestQueue = new LinkedList();
    /** HashSet that is used during WFG deadlock detection. Set of Transactions
     * whose wait-for subgraph has already been checked.
     */
    private HashSet mWFGCheckedSet = null;

    // Various metrics
    private int mTotalLockAttempts = 0;
    private int mTotalLocksGranted = 0;
    private int mTotalLockWaits = 0;
    private int mTotalDeadlocks = 0;
    private int mTotalTransactions = 0;
    private int mPeakTransactions = 0;
    private int mPeakLockedObjects = 0;
            int mPeakNumLocks = 0;
    private long mLongestWaitForLock = 0;
    private long mLongestDeadlockCheck = 0;
    private int mNumActiveTransactions = 0;
    private int mNumActiveLockedObjects = 0;
            int mNumActiveLocks = 0;
    
    

    /**
     * Construct a LockScheduler with no locks. Use the shutdown() method
     * to terminate the scheduler.
     *
     * @param anInitialLockedObjectCount initially, the LockScheduler will be setup
     *  to support this many locked objects. However, it will dynamically grow
     *  to support more locked objects if necessary.
     * @param aDeadlockAlgorithm One of the enumerated types defined in DeadlockAlgorithm.
     *  This determines the type of deadlock detection to use.
     */
    public LockScheduler(int anInitialLockedObjectCount, DeadlockAlgorithm aDeadlockAlgorithm)
    {
        mLockedObjectMap = new HashMap( (int)(anInitialLockedObjectCount * 1.25), .75F);
        mDeadlockAlgorithm = aDeadlockAlgorithm;
    }
    

    // Start of LockServer interface...



    /**
     * Given a URI, resolve a connected instance of a LockServer.
     *
     * @param someProperties properties which specify the connect parameters.
     * The properties must contain the following keys:<br>
     * <ul>
     * <li><i>LockScheduler.initialNumObjs</i> - The initial number of locked objects to allocate space for. </li>
     * <li><i>LockScheduler.deadlockAlgorithm</i> - Either "Waits-For" or "Timestamp" (as declared in DeadlockAlgorithm
     *      case-insensitive).</li>
     * </ul>
     * 
     * @return a LockServer.
     *
     * @throws ODMGException in the event of an error. <p>
     */
    public static LockServer connect(Properties someProperties) throws ODMGException
    {
        int initialNumLockedObjects;
        String numArg = someProperties.getProperty("LockScheduler.initialNumObjs");
        try {
            initialNumLockedObjects = Integer.parseInt(numArg);
        }
        catch (NumberFormatException e) {
            throw new ODMGException("Invalid number of LockScheduler.initialNumObjs: " + numArg);
        }
        
        String algorithmName = someProperties.getProperty("LockScheduler.deadlockAlgorithm");
        DeadlockAlgorithm algorithm;
        if (algorithmName.equalsIgnoreCase( DeadlockAlgorithm.WAITS_FOR.toString() )) {
            algorithm = DeadlockAlgorithm.WAITS_FOR;
        }
        else if (algorithmName.equalsIgnoreCase( DeadlockAlgorithm.TIMESTAMP.toString() )) {
            algorithm = DeadlockAlgorithm.TIMESTAMP;
        }
        else {
            throw new ODMGException("LockScheduler.deadlockAlgorithm parameter invalid: " + algorithmName);
        }
        
        return new LockScheduler(initialNumLockedObjects, algorithm);
    }
    

    public void disconnect() throws ODMGException
    {
        mShutdown = true;
    }


    public synchronized LockServerTransaction startTransaction()
    {
        ++mTotalTransactions;
        ++mNumActiveTransactions;
        if (mNumActiveTransactions > mPeakTransactions) {
            mPeakTransactions = mNumActiveTransactions;
        }
        
        return new Transaction(this);
    }


    public DeadlockAlgorithm getDeadlockAlgorithm()
    {
        return mDeadlockAlgorithm;
    }


    public int getTotalLockAttempts()
    {
        return mTotalLockAttempts;
    }


    public int getTotalLocksGranted()
    {
        return mTotalLocksGranted;
    }


    public int getTotalLockWaits()
    {
        return mTotalLockWaits;
    }


    public int getTotalDeadlocks()
    {
        return mTotalDeadlocks;
    }


    public int getTotalTransactions()
    {
        return mTotalTransactions;
    }


    public int getPeakTransactions()
    {
        return mPeakTransactions;
    }


    public int getPeakLockedObjects()
    {
        return mPeakLockedObjects;
    }


    public int getPeakNumLocks()
    {
        return mPeakNumLocks;
    }


    public long getLongestWaitForLock()
    {
        return mLongestWaitForLock;
    }


    public long getLongestDeadlockCheck()
    {
        return mLongestDeadlockCheck;
    }


    public int getNumActiveTransactions()
    {
        return mNumActiveTransactions;
    }


    public int getNumActiveLockedObjects()
    {
        return mNumActiveLockedObjects;
    }


    public int getNumActiveLocks()
    {
        return mNumActiveLocks;
    }


    public String dumpMetrics()
    {
        return 
            "TotalLockAttempts=" + mTotalLockAttempts + 
            "\nTotalLocksGranted=" + mTotalLocksGranted +
            "\nTotalLockWaits=" + mTotalLockWaits + 
            "\nTotalDeadlocks=" + mTotalDeadlocks +
            "\nTotalTransactions=" + mTotalTransactions +
            "\nPeakTransactions=" + mPeakTransactions + 
            "\nPeakLockedObjects=" + mPeakLockedObjects + 
            "\nPeakNumLocks=" + mPeakNumLocks +
            "\nLongestWaitForLock=" + mLongestWaitForLock + 
            "\nLongestDeadlockCheck=" + mLongestDeadlockCheck + 
            "\nNumActiveTransactions=" + mNumActiveTransactions +
            "\nNumActiveLockedObjects=" + mNumActiveLockedObjects +
            "\nNumActiveLocks=" + mNumActiveLocks + '\n';
    }
    

    public synchronized LockMode getLockMode(Object anObject)
    {
        LockedObject lockedObj = (LockedObject)mLockedObjectMap.get(anObject);
        if (lockedObj == null) {
            return null;
        }

        return lockedObj.getMostExclusiveLockMode();
    }
    

    // End of LockServer interface...



    /**
     * Updates the "longest wait for lock" metric.
     *
     * @param aWaitTime the number of milliseconds waited.
     */
    void updateLongestWaitForLock(long aWaitTime)
    {
        if (aWaitTime > mLongestWaitForLock) {
            mLongestWaitForLock = aWaitTime;
        }
    }
    

    /**
     * Cleans out this request/transaction as a waiter for a locked object.
     *
     * @param aRequest the lock request.
     */
    synchronized void cleanWaiter(LockRequest aRequest)
    {
        if (aRequest.mWaitLock != null) {
            aRequest.mWaitLock.mLockedObject.removeWaiter(aRequest.mWaitLock);
        }
    }
    

    /**
     * Acquires a lock on the item identified by the given object, or promotes the lock mode if 
     * the transaction currently owns a lock on the object. See the description on
     * Transaction.lock for more information.
     *
     * @param aRequest the LockRequest. On return, aRequest.mRequestComplete is
     * set to true if the locking request is complete (but not necessarily successful).
     * If mRequestComplete is true, then mRequestException may be set to indicate some
     * exception that occurred. If mRequestException is null, then mResult indicates 
     * the status of the lock request - true if it was granted, false if it timed out.
     * These stored statuses are required because the lock request may be completed
     * by another transaction's endTransaction request in another thread.
     *
     * @see Transaction#lock
     */
    synchronized void lock(LockRequest aRequest)
    {
        ++mTotalLockAttempts;
        Transaction transaction = aRequest.mTransaction;
        LockMode lockMode = aRequest.mLockMode;

        // Clear this now because we're not currently waiting.
        transaction.mOutstandingLockRequest = null;
        transaction.mWaitForLockedObject = null;
        
        LockedObject lockedObj = (LockedObject)mLockedObjectMap.get(aRequest.mObject);
        try {
            // See if the object is currently locked.
            if (lockedObj == null) {
                // Object not currently locked. Simply add a new LockedObject and Lock.
                lockedObj = new LockedObject(aRequest.mObject);
                lockedObj.addNewLock(transaction, lockMode);
                mLockedObjectMap.put(aRequest.mObject, lockedObj);

                ++mNumActiveLockedObjects;
                if (mNumActiveLockedObjects > mPeakLockedObjects) {
                    mPeakLockedObjects = mNumActiveLockedObjects;
                }

                ++mTotalLocksGranted;
                aRequest.complete(true);
                return;
            }

            Lock acquiredListHead = lockedObj.getAcquiredLockListHead();
            if (acquiredListHead == null && lockedObj.getWaitQueueHead() == null) {
                // No current locks and no waiters. Just lock it.
                lockedObj.addNewLock(transaction, lockMode);
                ++mTotalLocksGranted;
                aRequest.complete(true);
                return;
            }

            // A lock exists on the object - is one of the locks owned by the Transaction?
            Lock currentLock;
            if (aRequest.mRetries == 0) {
                currentLock = acquiredListHead;
                for (; currentLock != null && currentLock.mTransaction != transaction; 
                        currentLock = currentLock.mNextLock) {
                    // Empty
                }

                if (currentLock != null) {
                    // Transaction has a lock. If the same mode is requested as currently
                    // held, just return.
                    if (currentLock.mLockMode == lockMode) {
                        ++mTotalLocksGranted;
                        aRequest.complete(true);
                        return;
                    }

                    // Attempting to demote/downgrade? Error.
                    if ( currentLock.mLockMode.isMoreExclusiveThan(lockMode) ) {
                        // Just ignore the request.
                        aRequest.complete(true);
                        return;
                    }

                    // Promoting lock mode. Treat this like a new lock request. However, 
                    // remember this lock, we'll have to remove it after the new lock is granted.
                    aRequest.mCurrentLock = currentLock;
                }
            }
            else {
                currentLock = aRequest.mCurrentLock;
            }

            // If the requested mode is compatible with the most exclusive mode or the lock list is empty (or
            // this transaction is the only locker on the list),
            // we'll just grant the lock because no conflicts exist. Add a new lock or
            // promote current lock.
            if (lockedObj.getMostExclusiveLockMode().isCompatible(lockMode) || acquiredListHead == null ||
                (currentLock != null && acquiredListHead == currentLock && acquiredListHead.mNextLock == null) ) {
                if (currentLock != null) {
                    // Transaction has a lock and is promoting it. 
                    lockedObj.promoteLock(currentLock, lockMode);
                }
                else {
                    // Add a new lock for this transaction.
                    lockedObj.addNewLock(transaction, lockMode);
                }

                ++mTotalLocksGranted;
                aRequest.complete(true);
                return;
            }


            // We must wait. 
            
            // If the lock wait time was zero, we shouldn't wait at all. Just fail the request.
            if (aRequest.mWaitTime == 0) {
                aRequest.complete(false);
                return;
            }

            // Sanity check. If this condition is true, we'd wait forever because there is no one
            // to wake us, which isn't good.
            // What the condition says is: We shouldn't wait if there's no one on the acquired
            // list, other than this transaction, and there's no one on the wait queue, other
            // than this transaction. 
            Lock waitQueueHead = lockedObj.getWaitQueueHead();
            assert !((acquiredListHead == null || 
                      (acquiredListHead.mTransaction == transaction && acquiredListHead.mNextLock == null)) &&
                     (waitQueueHead == null || 
                      (waitQueueHead.mTransaction == transaction && waitQueueHead.mNextLock == null)) );

            // Check if deadlock would occur if we waited.
            if (checkForDeadlock(transaction, lockedObj)) {
                aRequest.complete( new DeadlockException("Deadlock would occur.") );
            }

            // Queue the wait lock if this is the first wait.
            if (aRequest.mWaitLock == null) {
                aRequest.mWaitLock = new Lock(lockedObj, transaction, lockMode);
                lockedObj.queueWaiter(aRequest.mWaitLock);
                ++mTotalLockWaits;
            }

            // Set the LockedObject this Transaction will be waiting on.
            transaction.mWaitForLockedObject = lockedObj;
            ++aRequest.mRetries;

            // Request is in the wait queue now. We'll retry the lock when another
            // transaction unlocks.
            transaction.mOutstandingLockRequest = aRequest;
            return;
        }
        finally {
            // If we're not waiting, cleanup the wait lock if we installed one.
            if (transaction.mWaitForLockedObject == null) {
                cleanWaiter(aRequest);
            }
        }
    }


    /**
     * Checks for deadlock based on the chosen algorithm.
     *
     * @param aTransaction the Transaction.
     * @param aLockedObject the object with existing locks which the Transaction
     *  intends on waiting for.
     *
     * @return true if deadlock would occur.
     */
    private boolean checkForDeadlock(Transaction aTransaction, LockedObject aLockedObject)
    {
        long start = System.currentTimeMillis();
        boolean result;
        if (mDeadlockAlgorithm == DeadlockAlgorithm.WAITS_FOR) {
            result = checkForWFGDeadlock(aTransaction, aLockedObject);
        }
        else {
            // DeadlockAlgorithm.TIMESTAMP...
            result = checkForTimestampDeadlock(aTransaction, aLockedObject);
        }
        
        long duration = System.currentTimeMillis() - start;
        if (duration > mLongestDeadlockCheck) {
            mLongestDeadlockCheck = duration;
        }
        
        return result;
    }


    /**
     * Checks for deadlock based on the waits-for graph (WFG) algorithm.
     *
     * @param aTransaction the Transaction.
     * @param aLockedObject the object whose graph is being examined.
     *
     * @return true if deadlock would occur.
     */
    private boolean checkForWFGDeadlock(Transaction aTransaction, LockedObject aLockedObject)
    {
        if (mWFGCheckedSet == null) {
            mWFGCheckedSet = new HashSet(mNumActiveTransactions);
        }
        
        // Stack of LockedObjects to recursively examine.
        LinkedList stack = new LinkedList();
        
        // Push the initial LockedObject to get started.
        stack.addFirst(aLockedObject);
        for (int branchesExamined = 0; !stack.isEmpty(); ++branchesExamined) {
            LockedObject lockedObj = (LockedObject)stack.removeFirst();
            // For each lock on this LockedObject...
            for (Lock lock = lockedObj.getAcquiredLockListHead(); lock != null; lock = lock.mNextLock) {
                // If we're back to the this transaction after traversing at least one wait-for branch,
                // it's a cycle.
                if (lock.mTransaction == aTransaction && branchesExamined > 0) {
                    ++mTotalDeadlocks;
                    return true;
                }

                // Recursively check for the wait-for branch. Don't recurse if we've already checked this subgraph.
                if (lock.mTransaction.mWaitForLockedObject != null && ! mWFGCheckedSet.contains(lock.mTransaction)) {
                    mWFGCheckedSet.add(lock.mTransaction);
                    stack.addFirst(lock.mTransaction.mWaitForLockedObject);
                }
            }
        }
        
        mWFGCheckedSet.clear();
        return false;
    }


    /**
     * Checks for deadlock based using the timestamp algorithm.
     *
     * @param aTransaction the Transaction.
     * @param aLockedObject the object with existing locks which the Transaction
     *  intends on waiting for.
     *
     * @return true if deadlock would occur.
     */
    private boolean checkForTimestampDeadlock(Transaction aTransaction, LockedObject aLockedObject)
    {
        for (Lock lock = aLockedObject.getAcquiredLockListHead(); lock != null; lock = lock.mNextLock) {
            // If our transaction is younger, throw exception.
            if (aTransaction.mDeadlockTimestamp > lock.mTransaction.mDeadlockTimestamp) {
                ++mTotalDeadlocks;
                return true;
            }
        }
        
        return false;
    }


    /**
     * Ends the transaction and unlocks all objects the Transaction locked with the lock method.
     *
     * @param aTransaction the transaction to end.
     */
    synchronized void endTransaction(Transaction aTransaction)
    {
        Lock nextLock = null;
        for (Lock lock = aTransaction.mLockListHead; lock != null; lock = nextLock) {
            nextLock = lock.mNextTransactionLock;
            LockedObject lockedObj = lock.mLockedObject;
            lockedObj.dropLock(lock);
            
            Lock waitQueueHead = lockedObj.getWaitQueueHead();
            if (waitQueueHead != null) {
                // Wake up all waiters on this LockedObject that can immediately gain the lock.
                // We want to dequeue as many waiters as possible that can
                // immediately get a lock. This logic enforces the FIFO behavior.
                Lock nextWaitLock;
                // Don't break out of the loop when we can't dequeue more waiters because
                // there may be waiters that have timed out and need to be cleaned up.
                boolean processWaiters = true;
                for (Lock waiter = waitQueueHead; waiter != null; waiter = nextWaitLock) {
                    // Set nextWaitLock now because if this waiter is removed, mNextLock will be cleared.
                    nextWaitLock = waiter.mNextLock;

                    // Attempt the lock request again on behalf of the waiter.
                    // If we the lock request completes (granted or exception thrown), we
                    // keep processing waiters.
                    LockRequest waiterRequest = waiter.mTransaction.mOutstandingLockRequest;
                    if (processWaiters || waiterRequest.mRequestComplete) {
                        lock(waiterRequest);
                    }
                    
                    // If the request completed, wake the waiting transactino.
                    if (waiterRequest.mRequestComplete) {
                        synchronized (waiterRequest.mTransaction) {
                            waiterRequest.mTransaction.notify();
                        }
                    }
                    else {
                        // The request didn't complete, we cannot process more of the FIFO queue.
                        processWaiters = false;
                    }
                }
            }

            if (lockedObj.getAcquiredLockListHead() == null && lockedObj.getWaitQueueHead() == null) {
                // No waiters and no other locks, we can remove the lock.
                mLockedObjectMap.remove( lockedObj.getClientObject() );
                --mNumActiveLockedObjects;
            }
        } // ...end for.

        aTransaction.mLockListHead = null;
        --mNumActiveTransactions;
    }
    


    /**
     * Transaction context used in LockScheduler. A Transaction may only be 
     * used by a single thread at any one time. Transactions should not be
     * reused unless deadlock occurs.
     *
     * @version $Id: LockScheduler.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     * @see LockScheduler
     */
    private static final class Transaction implements LockServerTransaction
    {
        private static long sNextDeadlockTimestamp = 1L;

        /** Timestamp used to check for deadlocks. */
        private long mDeadlockTimestamp;
       /** Reference back to the scheduler that this transaction is contained in. */
        private LockScheduler mLockScheduler;
        /** True if the transaction is active, otherwise it's ended. */
        private boolean mActive = true;

        // The following attrributes are manipulated by LockScheduler on behalf of the Transaction. 
        /** Head of the list of locks currently held by this transaction. */
        Lock mLockListHead;
        /** The LockedObject we're currently waiting for, or null if none. Used in deadlock detection. */
        LockedObject mWaitForLockedObject = null;
        /** Outstanding lock request that we're waiting for, if any. */
        private LockRequest mOutstandingLockRequest = null;
        

        /** 
         * Internal use - only this package can construct a transaction.
         * 
         * @param aLockScheduler the LockScheduler associated with the transaction.
         */
        Transaction(LockScheduler aLockScheduler)
        {
            synchronized (Transaction.class) {
                mDeadlockTimestamp = sNextDeadlockTimestamp++;
            }
            
            mLockScheduler = aLockScheduler;
        }


        /**
         * Adds a lock to the transaction's lock list.
         *
         * @param aLock the lock to be added.
         */
        void addToLockList(Lock aLock)
        {
            aLock.mNextTransactionLock = mLockListHead;
            mLockListHead = aLock;
        }


        // Start of LockServerTransaction interface...



        public LockServer getLockServer()
        {
            return mLockScheduler;
        }
        

        public boolean lock(Object anObject, LockMode aLockMode, long aWaitTime) throws LockNotGrantedException
        {
            if (!mActive) {
                throw new RuntimeException("Transaction not active");
            }
            
            if (anObject == null) {
                throw new IllegalArgumentException("anObject is null");
            }

            LockRequest request = new LockRequest(this, anObject, aLockMode, aWaitTime);
            long startTime = System.currentTimeMillis();
            do {
                // Try the lock.
                mLockScheduler.lock(request);

                if ( request.mRequestComplete ) {
                    // Request is complete - don't need to wait.
                    break;
                }
                
                // If a timeout set, check it now.
                long remainingTime = 0;
                if (request.mWaitTime > 0) {
                    remainingTime = request.mWaitTime - (System.currentTimeMillis() - startTime);
                    if (remainingTime <= 0) {
                        // Clean out any waiter we may have added.
                        mLockScheduler.cleanWaiter(request);
                        request.complete(false);   // Timeout.
                        break;
                    }
                }

                // Wait for another transaction to release locks. The other transaction's
                // call to endTransaction() will wake us. At that point, our lock should
                // have been completed by the ending transaction (on our behalf), 
                // but not necessarily granted. We can also wake here if the
                // timeout expires, and then we loop to check the remaining time.
                synchronized (this) {
                    // We have to re-check the request complete flag after gaining the lock.
                    if (request.mRequestComplete) {
                        break;
                    }
                    
                    try {
                        this.wait(remainingTime);
                    }
                    catch (InterruptedException e) {
                        // Ignore
                    }
                }
                
            }
            while ( ! request.mRequestComplete );

            // Wrap up. 
            mLockScheduler.updateLongestWaitForLock( System.currentTimeMillis() - startTime );
            
            if (request.mRequestException != null) {
                // Throw a new exception to keep this thread's stack context.
                if (request.mRequestException instanceof DeadlockException) {
                    throw new DeadlockException(request.mRequestException.getMessage(), request.mRequestException);
                }
                else {
                    throw new LockNotGrantedException(request.mRequestException.getMessage(), request.mRequestException);
                }
            }

            return request.mResult;
        }


        public void end()
        {
            if (!mActive) {
                throw new RuntimeException("Transaction not active");
            }

            mLockScheduler.endTransaction(this);
            mActive = false;
        }

        public boolean isActive()
        {
            return mActive;
        }

        // ...End of LockServerTransaction interface.


    }



    /**
     * Repesents the locking information for a locked object.
     *
     * @version $Id: LockScheduler.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     * @see LockScheduler
     */
    private static final class LockedObject
    {
        /** Out of the locks in the acquired lock list, this is the most exclusive.
         * Basically if the lock list length is greater than one, this can only be
         * READ or UPGRADE. A lock list length of one could contain a lock of
         * READ, UPGRADE, or WRITE.
         */
        private LockMode mMostExclusiveLockMode = LockMode.READ;
        /** The head of the queue of transactions waiting to lock this item. If this is
         * null, no transaction is waiting for the lock. The Lock at the head of the
         * list is the next one which will acquire a lock (FIFO behavior).
         */
        private Lock mWaitQueueHead = null;
        /** The tail of the wait queue. New requests are pushed on the tail of the queue. */
        private Lock mWaitQueueTail = null;
        /** The head of the acquired lock list. This is a list of all locks held on this object. */
        private Lock mAcquiredListHead = null;
        private Object mClientObject;
        

        /**
         * Construct a LockedObject with no locks. 
         *
         * @param aClientObject the client object actually being locked.
         */
        LockedObject(Object aClientObject)
        {
            mClientObject = aClientObject;
        }


        /**
         * Gets the most exclusive lock mode on this object.
         * Logically, this is the most exclusive mode in the acquired lock list.
         *
         * @return the most exclusive LockMode.
         */
        LockMode getMostExclusiveLockMode()
        {
            return mMostExclusiveLockMode;
        }


        /**
         * Gets the client object that is actually being locked.
         *
         * @return the client object.
         */
        Object getClientObject()
        {
            return mClientObject;
        }
        

        /** 
         * Gets the head of the wait queue. This should only be used for
         * iteration purposes. The queue is linked by Lock.mNextLock.
         *
         * @return the head of the wait queue.
         */
        Lock getWaitQueueHead()
        {
            return mWaitQueueHead;
        }
        

        /** 
         * Gets the head of the acquired lock list. This should only be used for
         * iteration purposes. The queue is linked by Lock.mNextLock/mPrevLock.
         *
         * @return the head of the acquired lock list.
         */
        Lock getAcquiredLockListHead()
        {
            return mAcquiredListHead;
        }
        

        /** 
         * Queues a wait lock onto the tail of the wait queue.
         *
         * @param aLock the Lock to add. aLock.mNextLock is set to null.
         */
        void queueWaiter(Lock aLock)
        {
            // First entry?
            if (mWaitQueueTail == null) {
                mWaitQueueHead = aLock;
            }
            else {
                mWaitQueueTail.mNextLock = aLock;
            }
            
            aLock.mPrevLock = mWaitQueueTail;
            aLock.mNextLock = null;
            mWaitQueueTail = aLock;
        }
        

        /** 
         * Removes a lock from the wait queue.
         */
        void removeWaiter(Lock aLock)
        {
            // Unlink.
            if (aLock.mPrevLock != null) {
                aLock.mPrevLock.mNextLock = aLock.mNextLock;
            }
            else {
                // Unlink from the head.
                mWaitQueueHead = aLock.mNextLock;
            }
            
            if (aLock.mNextLock != null) {
                aLock.mNextLock.mPrevLock = aLock.mPrevLock;
            }
            else {
                // Unlink from the tail.
                mWaitQueueTail = aLock.mPrevLock;
            }
            
            aLock.mNextLock = null;
            aLock.mPrevLock = null;
        }
        

        /** 
         * Adds a new lock to this LockedObject, the specified transaction, and
         * the lock scheduler. The most exclusive lock mode is 
         * updated if necessary.
         *
         * @param aTransaction the transaction which is locking anObject.
         * @param aLockMode the locking mode. This is one of the static modes
         *  defined in LockMode.
         */
        void addNewLock(Transaction aTransaction, LockMode aLockMode)
        {
            LockScheduler lockScheduler = (LockScheduler)aTransaction.getLockServer();
            ++lockScheduler.mNumActiveLocks;
            if (lockScheduler.mNumActiveLocks > lockScheduler.mPeakNumLocks) {
                lockScheduler.mPeakNumLocks = lockScheduler.mNumActiveLocks;
            }
            
            Lock lock = new Lock(this, aTransaction, aLockMode);
            if ( lock.mLockMode.isMoreExclusiveThan(mMostExclusiveLockMode) ) {
                mMostExclusiveLockMode = lock.mLockMode;
            }
            
            // Add it to the head of the list.
            lock.mNextLock = mAcquiredListHead;
            lock.mPrevLock = null;
            if (mAcquiredListHead != null) {
                mAcquiredListHead.mPrevLock = lock;
            }
            
            mAcquiredListHead = lock;
            aTransaction.addToLockList(lock);
        }


        /** 
         * Promotes a lock already in the acquired list to aLockMode. The most exclusive lock mode is 
         * updated if necessary.
         *
         * @param aLock the Lock to promote.
         * @param aLockMode the new lock mode.
         */
        void promoteLock(Lock aLock, LockMode aLockMode)
        {
            if ( aLockMode.isMoreExclusiveThan(mMostExclusiveLockMode) ) {
                mMostExclusiveLockMode = aLockMode;
            }
            
            aLock.mLockMode = aLockMode;
        }


        /** 
         * Drops a lock from the acquired list. The most exclusive lock mode is 
         * updated if necessary.
         *
         * @param aLock the Lock to add. aLock.mNextLock is set to null.
         */
        void dropLock(Lock aLock)
        {
            LockScheduler lockScheduler = (LockScheduler)aLock.mTransaction.getLockServer();
            --lockScheduler.mNumActiveLocks;

            // Unlink.
            if (aLock.mPrevLock != null) {
                aLock.mPrevLock.mNextLock = aLock.mNextLock;
            }
            
            if (aLock.mNextLock != null) {
                aLock.mNextLock.mPrevLock = aLock.mPrevLock;
            }
            
            if (aLock == mAcquiredListHead) {
                // Unlink from the head.
                mAcquiredListHead = aLock.mNextLock;
            }
            
            aLock.mNextLock = null;
            aLock.mPrevLock = null;

            // If aLock.mLockMode and mMostExclusiveLockMode are the same (note 
            // that aLock.mLockMode can't be greater), we have to check the list 
            // for the next most exclusive lock mode. 
            // - If aLock.mLockMode and mMostExclusiveLockMode are both WRITE, 
            //   we'll won't find another lock (READ, UPGRADE, or WRITE) because there can only be one 
            //   lock when a WRITE lock exists (the one being dropped).
            // - If mMostExclusiveLockMode is READ, we don't have to search
            //   the list for UPGRADE because it won't be in the list (UPGRADE 
            //   is more exclusive than READ, so when mMostExclusiveLockMode is 
            //   READ, there can't be an UPGRADE lock).
            // Hence, we only need to change mMostExclusiveLockMode if
            // aLock.mLockMode and mMostExclusiveLockMode are both UPGRADE. In this case, 
            // there can only be one UPGRADE lock (the one being dropped), so
            // the rest of the list is either empty or READ locks. Simply downgrade
            // mMostExclusiveLockMode to READ if aLock.mLockMode and 
            // mMostExclusiveLockMode are both UPGRADE.
            // If list is empty, always set mMostExclusiveLockMode to READ.
            if (mAcquiredListHead == null || 
                (aLock.mLockMode == LockMode.UPGRADE && mMostExclusiveLockMode == LockMode.UPGRADE) ) {
                mMostExclusiveLockMode = LockMode.READ;
            }
        }
    }



    /**
     * Repesents the information for a single Lock on a object.
     *
     * @version $Id: LockScheduler.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     * @see LockScheduler
     */
    private static final class Lock
    {
        /** The object being locked. */
        LockedObject mLockedObject;
        /** Transaction which has or is waiting for the lock. */
        Transaction mTransaction;
        /** The lock mode. */
        LockMode mLockMode;
        /** The next lock in either the LockedObject wait list or acquired list
         * depending on the lock's state. 
         */
        Lock mNextLock = null;
        /** The previous lock in the LockedObject wait list or acquired list.   */
        Lock mPrevLock = null;
        /** The next lock in the Transaction's lock list. */
        Lock mNextTransactionLock = null;


        /** Construct a new Lock.
         *
         * @param aLockedObject the object being locked.
         * @param aTransaction the transaction which owns (or will own) the lock.
         * @param aLockMode the lock mode.
         */
        Lock(LockedObject aLockedObject, Transaction aTransaction, LockMode aLockMode)
        {
            mLockedObject = aLockedObject;
            mTransaction = aTransaction;
            mLockMode = aLockMode;
        }
    }



    /**
     * Represents a the state of a LockRequest.
     *
     * @version $Id: LockScheduler.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     * @see LockScheduler
     */
    private static final class LockRequest
    {
        Transaction mTransaction;
        Object mObject;
        LockMode mLockMode;
        long mWaitTime;
        
        /** Cached lock that the transaction currently holds on mObject, if any. 
         * Only valid if mRetries > 0. 
         */
        Lock mCurrentLock = null;
        /** The number of wait/retries done on the lock request. */
        int mRetries = 0;
        /** Wait lock if we're in the wait queue. */
        Lock mWaitLock = null;

        /** This is set to true when the request processing is complete - regardless 
         * of whether an exception was thrown. 
         */
        boolean mRequestComplete = false;
        /** The result of the lock request, if an exception was not thrown. True on success,
         * false on timeout.
         */
        boolean mResult = false;
        /** If non-null, an exception that was thrown while processing the request. */
        Exception mRequestException = null;


        /**
         * Constructs a lock request.
         */
        LockRequest(Transaction aTransaction, Object anObject, LockMode aLockMode, long aWaitTime)
        {
            mTransaction = aTransaction;
            mObject = anObject;
            mLockMode = aLockMode;
            mWaitTime = aWaitTime;
        }


        /**
         * Completes a request.
         */
        void complete(boolean aResult)
        {
            mRequestComplete = true;
            mResult = aResult;
        }


        /**
         * Completes a request with an Exception.
         */
        void complete(Exception anException)
        {
            mRequestComplete = true;
            mRequestException = anException;
        }
    }
}
