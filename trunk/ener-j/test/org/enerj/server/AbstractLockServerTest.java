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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/AbstractLockServerTest.java,v 1.1 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.server;

import java.util.Properties;

import junit.framework.TestCase;

import org.enerj.core.DeadlockException;


/**
 * Tests LockServer. <p>
 *
 * @version $Id: AbstractLockServerTest.java,v 1.1 2006/01/12 04:39:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
abstract public class AbstractLockServerTest extends TestCase
{
    

    public AbstractLockServerTest(String aTestName) 
    {
        super(aTestName);
    }
    

    abstract protected String getLockServerClassName();
    

    /**
     * Gets properties that specify a server that uses waits-for deadlock detection.
     */
    abstract protected Properties getWaitsForProperties();
        

    /**
     * Gets properties that specify a server that uses timestamp deadlock detection.
     */
    abstract protected Properties getTimestampProperties();
        

    /**
     * Tests that an empty transaction works and that locks cannot be gained after
     * the txn ends.
     */
    public void testEmptyTxn() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );
        LockServerTransaction txn = lockServer.startTransaction();
        txn.end();
        try {
            txn.lock(new Object(), LockMode.READ, -1L);
            fail("Expected RuntimeException");
        }
        catch (RuntimeException e) {
            // Expected
        }
    }


    /**
     * Tests single transaction gaining multiple locks. Also tests that locks
     * are dropped after ending the txn.
     */
    public void testSingleTxnMultipleLocks() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );
        LockServerTransaction txn = lockServer.startTransaction();
        Object[] objs = new Object[100];
        LockMode[] modes = new LockMode[objs.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new String("Obj" + i);
            
            switch (i % 3) {
            case 0:
                modes[i] = LockMode.READ;
                break;
            case 1:
                modes[i] = LockMode.UPGRADE;
                break;
            case 2:
                modes[i] = LockMode.WRITE;
                break;
            }
            
            assertTrue( txn.lock(objs[i], modes[i], -1L) );
        }

        for (int i = 0; i < objs.length; i++) {
            assertTrue( lockServer.getLockMode(objs[i]) == modes[i] );
        }
        
        txn.end();

        for (int i = 0; i < objs.length; i++) {
            assertNull( lockServer.getLockMode(objs[i]) );
        }

        lockServer.disconnect();
    }


    /**
     * Tests single transaction lock promotions. Also tests that demotions are invalid.
     */
    public void testSingleTxnLockPromotion() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );


        // READ -> WRITE
        LockServerTransaction txn = lockServer.startTransaction();

        java.util.Date obj = new java.util.Date();

        assertTrue( txn.lock(obj, LockMode.READ, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        assertTrue( txn.lock(obj, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        txn.end();

        assertNull( lockServer.getLockMode(obj) );


        // READ -> UPGRADE
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.READ, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        assertTrue( txn.lock(obj, LockMode.UPGRADE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        txn.end();

        assertNull( lockServer.getLockMode(obj) );


        // READ -> READ (no promotion)
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.READ, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        assertTrue( txn.lock(obj, LockMode.READ, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        txn.end();

        assertNull( lockServer.getLockMode(obj) );


        // UPGRADE -> WRITE
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.UPGRADE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        assertTrue( txn.lock(obj, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        txn.end();

        assertNull( lockServer.getLockMode(obj) );


        // UPGRADE -> UPGRADE (no promotion)
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.UPGRADE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        assertTrue( txn.lock(obj, LockMode.UPGRADE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        txn.end();

        assertNull( lockServer.getLockMode(obj) );


        // WRITE -> WRITE (no promotion)
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        assertTrue( txn.lock(obj, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        txn.end();

        assertNull( lockServer.getLockMode(obj) );


        // UPGRADE -> READ (demotion - OK - lock should stay UPGRADE)
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.UPGRADE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        txn.lock(obj, LockMode.READ, -1L);

        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);
        txn.end();
        assertNull( lockServer.getLockMode(obj) );


        // WRITE -> READ (demotion - OK - lock should stay WRITE)
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        txn.lock(obj, LockMode.READ, -1L);
        
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);
        txn.end();
        assertNull( lockServer.getLockMode(obj) );


        // WRITE -> UPGRADE (demotion - OK - lock should stay WRITE)
        txn = lockServer.startTransaction();

        assertTrue( txn.lock(obj, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        txn.lock(obj, LockMode.UPGRADE, -1L);
        
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);
        txn.end();
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests multiple readers.
     */
    public void testMultipleReaders() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        java.util.Date obj = new java.util.Date();
        LockServerTransaction[] txns = new LockServerTransaction[10];

        for (int i = 0; i < txns.length; i++) {
            txns[i] = lockServer.startTransaction();
        }

        for (int i = 0; i < txns.length; i++) {
            // Don't wait for lock.
            assertTrue( txns[i].lock(obj, LockMode.READ, 0L) );
        }

        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        // End all but the last txn
        for (int i = 0; i < (txns.length - 1); i++) {
            txns[i].end();
            assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);
        }

        // End the last one.
        txns[ txns.length - 1 ].end();
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests multiple readers with single UPGRADE. A second upgrade request
     * should wait. Also tests wait timeouts.
     */
    public void testMultipleReadersSingleUpgrade() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        java.util.Date obj = new java.util.Date();
        LockServerTransaction[] txns = new LockServerTransaction[10];

        for (int i = 0; i < txns.length; i++) {
            txns[i] = lockServer.startTransaction();
        }

        for (int i = 0; i < txns.length; i++) {
            // Don't wait for lock.
            assertTrue( txns[i].lock(obj, LockMode.READ, 0L) );
        }

        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        LockServerTransaction upgradeTxn = lockServer.startTransaction();
        assertTrue( upgradeTxn.lock(obj, LockMode.UPGRADE, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // Second upgrade transaction should wait
        LockServerTransaction upgradeTxn2 = lockServer.startTransaction();

        // Zero timeout should not wait.
        assertTrue( ! upgradeTxn2.lock(obj, LockMode.UPGRADE, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // 100ms wait.
        long startTime = System.currentTimeMillis();
        assertTrue( ! upgradeTxn2.lock(obj, LockMode.UPGRADE, 100L) );
        // We give the timeout some leeway by checking that we waited at least 90ms.
        assertTrue( (System.currentTimeMillis() - startTime) >= 90L );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        upgradeTxn2.end();
        
        // Lock mode should still be upgrade.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // Wait in another thread until first upgrade released.
        LockWaiter lockWaiter = new LockWaiter(lockServer, obj, LockMode.UPGRADE);
        lockWaiter.start();
        // Allow thread to actually queue the request.
        lockWaiter.waitToRequest();
        
        assertTrue( lockWaiter.mRequested );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);
        // Shouldn't be acquired yet.
        assertTrue( ! lockWaiter.mAcquired );

        upgradeTxn.end();
        
        // Wait for waiter thread to acquire and end.
        try {
            lockWaiter.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (lockWaiter.mException != null) {
            throw new Exception("Waiter thread threw Exception", lockWaiter.mException);
        }
        
        // Should be acquired.
        assertTrue( lockWaiter.mAcquired );

        // Most exclusive lock mode should change to read now.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        // End all but the last txn
        for (int i = 0; i < (txns.length - 1); i++) {
            txns[i].end();
            assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);
        }

        // End the last one.
        txns[ txns.length - 1 ].end();
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests single upgrader, adds multiple readers with WRITE waiting.
     */
    public void testSingleUpgraderMultipleReadersWriteWaiting() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        java.util.Date obj = new java.util.Date();

        // Get UPGRADE lock
        LockServerTransaction upgradeTxn = lockServer.startTransaction();
        assertTrue( upgradeTxn.lock(obj, LockMode.UPGRADE, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        LockServerTransaction[] txns = new LockServerTransaction[10];

        for (int i = 0; i < txns.length; i++) {
            txns[i] = lockServer.startTransaction();
        }

        // Get READ locks
        for (int i = 0; i < txns.length; i++) {
            // Don't wait for lock.
            assertTrue( txns[i].lock(obj, LockMode.READ, 0L) );
        }

        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // Writer transaction should wait
        LockServerTransaction writerTxn = lockServer.startTransaction();

        // Zero timeout should not wait.
        assertTrue( ! writerTxn.lock(obj, LockMode.WRITE, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // 100ms wait.
        long startTime = System.currentTimeMillis();
        assertTrue( ! writerTxn.lock(obj, LockMode.WRITE, 100L) );
        // We give the timeout some leeway by checking that we waited at least 90ms.
        assertTrue( (System.currentTimeMillis() - startTime) >= 90L );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        writerTxn.end();
        
        // Lock mode should still be UPGRADE.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // Wait in another thread until all of the read locks are released.
        LockWaiter lockWaiter = new LockWaiter(lockServer, obj, LockMode.WRITE);
        lockWaiter.start();
        // Allow thread to actually queue the request.
        lockWaiter.waitToRequest();
        
        assertTrue( lockWaiter.mRequested );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);
        // Shouldn't be acquired yet.
        assertTrue( ! lockWaiter.mAcquired );

        // End UPGRADE txn
        upgradeTxn.end();
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);
        
        // End all of the reader txns
        for (int i = 0; i < txns.length; i++) {
            txns[i].end();
        }
        
        // Wait for waiter thread to acquire and end.
        try {
            lockWaiter.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (lockWaiter.mException != null) {
            throw new Exception("Waiter thread threw Exception", lockWaiter.mException);
        }
        
        // Should have been acquired.
        assertTrue( lockWaiter.mAcquired );

        // All locks should be released at this point.
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests multiple readers with WRITE waiting.
     */
    public void testMultipleReadersWriteWaiting() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        java.util.Date obj = new java.util.Date();
        LockServerTransaction[] txns = new LockServerTransaction[10];

        for (int i = 0; i < txns.length; i++) {
            txns[i] = lockServer.startTransaction();
        }

        for (int i = 0; i < txns.length; i++) {
            // Don't wait for lock.
            assertTrue( txns[i].lock(obj, LockMode.READ, 0L) );
        }

        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        // Writer transaction should wait
        LockServerTransaction writerTxn = lockServer.startTransaction();

        // Zero timeout should not wait.
        assertTrue( ! writerTxn.lock(obj, LockMode.WRITE, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        // 100ms wait.
        long startTime = System.currentTimeMillis();
        assertTrue( ! writerTxn.lock(obj, LockMode.WRITE, 100L) );
        // We give the timeout some leeway by checking that we waited at least 90ms.
        assertTrue( (System.currentTimeMillis() - startTime) >= 90L );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        writerTxn.end();
        
        // Lock mode should still be READ.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        // Wait in another thread until all of the read locks are released.
        LockWaiter lockWaiter = new LockWaiter(lockServer, obj, LockMode.WRITE);
        lockWaiter.start();
        // Allow thread to actually queue the request.
        lockWaiter.waitToRequest();
        
        assertTrue( lockWaiter.mRequested );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);
        // Shouldn't be acquired yet.
        assertTrue( ! lockWaiter.mAcquired );

        // End all of the reader txns
        for (int i = 0; i < txns.length; i++) {
            txns[i].end();
        }
        
        // Wait for waiter thread to acquire and end.
        try {
            lockWaiter.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (lockWaiter.mException != null) {
            throw new Exception("Waiter thread threw Exception", lockWaiter.mException);
        }
        
        // Should have been acquired.
        assertTrue( lockWaiter.mAcquired );

        // All locks should be released at this point.
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests single writer with multiple readers waiting.
     */
    public void testSingleWriterMultipleReadersWaiting() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        java.util.Date obj = new java.util.Date();
        LockServerTransaction writerTxn = lockServer.startTransaction();

        // WRITE lock and test non-zero timeout returning true.
        assertTrue( writerTxn.lock(obj, LockMode.WRITE, 10L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        // A Reader transaction should wait
        LockServerTransaction readerTxn = lockServer.startTransaction();

        // Zero timeout should not wait.
        assertTrue( ! readerTxn.lock(obj, LockMode.READ, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        // 100ms wait.
        long startTime = System.currentTimeMillis();
        assertTrue( ! readerTxn.lock(obj, LockMode.READ, 100L) );
        // We give the timeout some leeway by checking that we waited at least 90ms.
        assertTrue( (System.currentTimeMillis() - startTime) >= 90L );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        readerTxn.end();
        
        // Lock mode should still be WRITE.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        // Queue up a bunch of READ locks to wait on the WRITE lock.
        LockWaiter[] lockWaiters = new LockWaiter[10];
        for (int i = 0; i < lockWaiters.length; i++) {
            lockWaiters[i] = new LockWaiter(lockServer, obj, LockMode.READ);
            lockWaiters[i].mReleaseLock = false; // We'll tell thread when to release.
            lockWaiters[i].start();
        }
        
        // Allow threads to actually queue the request.
        for (int i = 0; i < lockWaiters.length; i++) {
            lockWaiters[i].waitToRequest();
        
            assertTrue( lockWaiters[i].mRequested );
            assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);
            // Shouldn't be acquired yet.
            assertTrue( ! lockWaiters[i].mAcquired );
        }

        // End the WRITE txn. Allows readers to gain locks.
        writerTxn.end();
        
        // Wait for waiter threads to acquire.
        for (int i = 0; i < lockWaiters.length; i++) {
            lockWaiters[i].waitToAcquire();
            // Should have been acquired.
            assertTrue( lockWaiters[i].mAcquired );
        }
        
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.READ);

        // All threads should still be running and holding the READ locks
        for (int i = 0; i < lockWaiters.length; i++) {
            assertTrue( lockWaiters[i].isAlive() );
            assertTrue( ! lockWaiters[i].mFinished );
        }
        
        // Tell threads to release locks and terminate.
        for (int i = 0; i < lockWaiters.length; i++) {
            lockWaiters[i].mReleaseLock = true;
        }

        // Wait for them to finish.
        for (int i = 0; i < lockWaiters.length; i++) {
            try {
                lockWaiters[i].join(5000L);
            }
            catch (InterruptedException e) {  }

            if (lockWaiters[i].mException != null) {
                throw new Exception("Waiter thread threw Exception", lockWaiters[i].mException);
            }
        }

        // All locks should be released at this point.
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests single writer with an upgrader and multiple readers waiting, followed
     * by another upgrader waiting - in this order in the FIFO queue.
     */
    public void testSingleWriterUpgraderAndMultipleReadersWaiting() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        java.util.Date obj = new java.util.Date();
        LockServerTransaction writerTxn = lockServer.startTransaction();

        // WRITE lock and test non-zero timeout returning true.
        assertTrue( writerTxn.lock(obj, LockMode.WRITE, 10L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        // An UPGRADE transaction should wait
        LockServerTransaction upgraderTxn = lockServer.startTransaction();

        // Zero timeout should not wait.
        assertTrue( ! upgraderTxn.lock(obj, LockMode.UPGRADE, 0L) );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        // 100ms wait.
        long startTime = System.currentTimeMillis();
        assertTrue( ! upgraderTxn.lock(obj, LockMode.UPGRADE, 100L) );
        // We give the timeout some leeway by checking that we waited at least 90ms.
        assertTrue( (System.currentTimeMillis() - startTime) >= 90L );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        upgraderTxn.end();
        
        // Lock mode should still be WRITE.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);

        // Queue up an UPGRADE lock, a bunch of READ locks, and another UPGRADE lock to wait on the WRITE lock.
        LockWaiter upgradeWaiter = new LockWaiter(lockServer, obj, LockMode.UPGRADE);
        upgradeWaiter.mReleaseLock = false;
        upgradeWaiter.start();
        upgradeWaiter.waitToRequest();
        assertTrue( upgradeWaiter.mRequested );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);
        // Shouldn't be acquired yet.
        assertTrue( ! upgradeWaiter.mAcquired );
        
        // Queue up the READ locks
        LockWaiter[] readLockWaiters = new LockWaiter[10];
        for (int i = 0; i < readLockWaiters.length; i++) {
            readLockWaiters[i] = new LockWaiter(lockServer, obj, LockMode.READ);
            readLockWaiters[i].mReleaseLock = false; // We'll tell thread when to release.
            readLockWaiters[i].start();

            // Allow threads to actually queue the request.
            readLockWaiters[i].waitToRequest();
        
            assertTrue( readLockWaiters[i].mRequested );
            assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);
            // Shouldn't be acquired yet.
            assertTrue( ! readLockWaiters[i].mAcquired );
        }
        
        // Queue up the final UPGRADE lock. This one will wait until the WRITE locks,
        // the first UPGRADE lock, and the READ locks are done.
        LockWaiter upgradeWaiter2 = new LockWaiter(lockServer, obj, LockMode.UPGRADE);
        upgradeWaiter2.mReleaseLock = false;
        upgradeWaiter2.start();
        upgradeWaiter2.waitToRequest();
        assertTrue( upgradeWaiter2.mRequested );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.WRITE);
        // Shouldn't be acquired yet.
        assertTrue( ! upgradeWaiter2.mAcquired );

        // End the WRITE txn. Allows first upgrader and readers to gain locks.
        writerTxn.end();

        // Wait for first upgrader to acquire
        upgradeWaiter.waitToAcquire();
        assertTrue( upgradeWaiter.mAcquired );

        // Wait for read waiter threads to acquire.
        for (int i = 0; i < readLockWaiters.length; i++) {
            readLockWaiters[i].waitToAcquire();
            // Should have been acquired.
            assertTrue( readLockWaiters[i].mAcquired );
        }
        
        // First UPGRADE and READ locks should be acquired now, but not the second UPGRADE.
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);
        assertTrue( ! upgradeWaiter2.mAcquired );

        // All threads should still be running
        assertTrue( upgradeWaiter.isAlive() );
        assertTrue( upgradeWaiter2.isAlive() );
        assertTrue( ! upgradeWaiter.mFinished );
        assertTrue( ! upgradeWaiter2.mFinished );
        for (int i = 0; i < readLockWaiters.length; i++) {
            assertTrue( readLockWaiters[i].isAlive() );
            assertTrue( ! readLockWaiters[i].mFinished );
        }
        
        // Tell first upgrader and readers to release locks and terminate.
        upgradeWaiter.mReleaseLock = true;
        for (int i = 0; i < readLockWaiters.length; i++) {
            readLockWaiters[i].mReleaseLock = true;
        }

        // Wait for them to finish.
        try {
            upgradeWaiter.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (upgradeWaiter.mException != null) {
            throw new Exception("Waiter thread threw Exception", upgradeWaiter.mException);
        }
        
        upgradeWaiter = null; // Just so we don't try to use it again.

        for (int i = 0; i < readLockWaiters.length; i++) {
            try {
                readLockWaiters[i].join(5000L);
            }
            catch (InterruptedException e) {  }

            if (readLockWaiters[i].mException != null) {
                throw new Exception("Waiter thread threw Exception", readLockWaiters[i].mException);
            }
        }
        
        // Now second upgrader should have obtained the lock.
        upgradeWaiter2.waitToAcquire();
        assertTrue( upgradeWaiter2.mAcquired );
        assertTrue( lockServer.getLockMode(obj) ==  LockMode.UPGRADE);

        // Now we can release the last upgrade lock.
        upgradeWaiter2.mReleaseLock = true;

        // Wait for them to finish.
        try {
            upgradeWaiter2.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (upgradeWaiter2.mException != null) {
            throw new Exception("Waiter thread threw Exception", upgradeWaiter2.mException);
        }

        // All locks should be released at this point.
        assertNull( lockServer.getLockMode(obj) );

        lockServer.disconnect();
    }


    /**
     * Tests simple case of waits-for deadlock.
     */
    public void testSimpleWaitsForDeadlock() throws Exception
    {
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );

        String objA = "Object A";
        String objB = "Object B";


        // Single depth cycle: T1 wants to wait on T2, which is waiting on T1.
        LockServerTransaction txn1 = lockServer.startTransaction();
        
        assertTrue( txn1.lock(objA, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.WRITE);

        LockServerTransaction txn2 = lockServer.startTransaction();
        
        assertTrue( txn2.lock(objB, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(objB) ==  LockMode.WRITE);

        DeadlockWaiter deadlockWaiter = new DeadlockWaiter(txn2, objA, LockMode.READ, "txn2");
        deadlockWaiter.start();
        deadlockWaiter.waitToRequest();
        
        try {
            txn1.lock(objB, LockMode.READ, 5000L);
            fail("Expected DeadlockException");
        }
        catch (DeadlockException e) {
            // Expected.
        }
        
        assertNull( deadlockWaiter.mException );
        txn1.end();

        lockServer.disconnect();
    }


    /**
     * Tests complex waits-for deadlock with longer cycle.
     */
    public void testComplexWaitsForDeadlock() throws Exception
    {

        // Four depth cycle. "T1" means Transaction 1. "R(X)" means Read lock on resource X. 
        // "W(X) wait T4" means waiting on Transaction 4 for write lock on resource X.
        //  T0          T1          T2          T3          T4
        //  R(0)        R(1)        R(2)        R(3)        R(4)
        //                                                  W(3) wait on T3
        //                          W(4) wait on T4
        //              W(2) wait on T2
        //                                      W(0) wait on T0
        //  W(1) wait on T1 - deadlock.
        //
        // Some other transactions are also created with read locks on the above 
        // objects, but they shouldn't affect the deadlock cycles.
        
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );
        String[] objs = new String[5];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = "Object " + i;
        }

        // These are some miscellaneous transactions which are not part of the deadlock cycle.
        LockServerTransaction[] miscTxns = new LockServerTransaction[5];
        for (int i = 0; i < miscTxns.length; i++) {
            miscTxns[i] = lockServer.startTransaction();
            assertTrue( miscTxns[i].lock(objs[i], LockMode.READ, -1L) );
            assertTrue( lockServer.getLockMode(objs[i]) ==  LockMode.READ);
        }

        // These are the the transactions that form the deadlock cycle.
        LockServerTransaction[] txns = new LockServerTransaction[5];
        for (int i = 0; i < txns.length; i++) {
            txns[i] = lockServer.startTransaction();
            assertTrue( txns[i].lock(objs[i], LockMode.READ, -1L) );
            assertTrue( lockServer.getLockMode(objs[i]) ==  LockMode.READ);
        }

        // These are some more miscellaneous transactions which are not part of the deadlock cycle.
        LockServerTransaction[] misc2Txns = new LockServerTransaction[5];
        for (int i = 0; i < miscTxns.length; i++) {
            misc2Txns[i] = lockServer.startTransaction();
            assertTrue( misc2Txns[i].lock(objs[i], LockMode.READ, -1L) );
            assertTrue( lockServer.getLockMode(objs[i]) ==  LockMode.READ);
        }
        
        DeadlockWaiter[] deadlockWaiters = new DeadlockWaiter[5];
        deadlockWaiters[4] = new DeadlockWaiter(txns[4], objs[3], LockMode.WRITE, "Txn4");
        deadlockWaiters[4].start();
        deadlockWaiters[4].waitToRequest();

        deadlockWaiters[2] = new DeadlockWaiter(txns[2], objs[4], LockMode.WRITE, "Txn2");
        deadlockWaiters[2].start();
        deadlockWaiters[2].waitToRequest();

        deadlockWaiters[1] = new DeadlockWaiter(txns[1], objs[2], LockMode.WRITE, "Txn1");
        deadlockWaiters[1].start();
        deadlockWaiters[1].waitToRequest();

        deadlockWaiters[3] = new DeadlockWaiter(txns[3], objs[0], LockMode.WRITE, "Txn3");
        deadlockWaiters[3].start();
        deadlockWaiters[3].waitToRequest();

        // Should still be read lock on each of the objects, not WRITE.
        for (int i = 0; i < 5; i++) {
            assertTrue( lockServer.getLockMode(objs[i]) ==  LockMode.READ);
        }
        
        // Attempt T0's write lock - should deadlock.
        try {
            txns[0].lock(objs[1], LockMode.WRITE, 5000L);
            fail("Expected DeadlockException");
        }
        catch (DeadlockException e) {
            // Expected.
        }
        
        for (int i = 1; i < 5; i++) {
            assertNull( deadlockWaiters[i].mException );
        }
        
        txns[0].end();

        for (int i = 0; i < miscTxns.length; i++) {
            miscTxns[i].end();
            misc2Txns[i].end();
        }

        lockServer.disconnect();
        //System.out.println("-------------------------------");
        //System.out.println( lockServer.dumpMetrics() );
    }
        

    /**
     * Tests single resource waits-for deadlock.
     */
    public void testSingleResourceWaitsForDeadlock() throws Exception
    {

        // Test the single resource deadlock scenario:
        // T0                   T1
        // R(A)                 R(A)
        // W(A) waits on T1     W(A) would wait on T0 - deadlock

        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );
        LockServerTransaction txn1 = lockServer.startTransaction();
        String objA = "Object A";
        
        assertTrue( txn1.lock(objA, LockMode.READ, -1L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.READ);

        LockServerTransaction txn2 = lockServer.startTransaction();
        
        assertTrue( txn2.lock(objA, LockMode.READ, -1L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.READ);

        DeadlockWaiter deadlockWaiter = new DeadlockWaiter(txn1, objA, LockMode.WRITE, "SingleResource");
        deadlockWaiter.start();
        deadlockWaiter.waitToRequest();
        
        try {
            txn2.lock(objA, LockMode.WRITE, 5000L);
            fail("Expected DeadlockException");
        }
        catch (DeadlockException e) {
            // Expected.
        }
        
        assertNull( deadlockWaiter.mException );
        txn2.end();

        lockServer.disconnect();
    }
    
        

    /**
     * Tests that no waits-for deadlock occurs on a single resource when UPGRADE is used.
     */
    public void testNoSingleResourceWaitsForDeadlockWithUpgrade() throws Exception
    {

        // Test that no single resource deadlock occurs with UPGRADE locks. Scenario:
        // T0                   T1
        // U(A)                 U(A) waits on T0
        // W(A)
        // end transaction      -upgrade granted
        //                      W(A) (not attemtped in test)

        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getWaitsForProperties() );
        LockServerTransaction txn1 = lockServer.startTransaction();
        String objA = "Object A";
        
        assertTrue( txn1.lock(objA, LockMode.UPGRADE, -1L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.UPGRADE);

        LockWaiter lockWaiter = new LockWaiter(lockServer, objA, LockMode.UPGRADE);
        lockWaiter.mReleaseLock = false;
        lockWaiter.start();
        lockWaiter.waitToRequest();
        
        assertTrue( lockWaiter.mRequested );
        // Shouldn't be acquired yet.
        assertTrue( ! lockWaiter.mAcquired );

        assertTrue( txn1.lock(objA, LockMode.WRITE, 5000L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.WRITE);

        txn1.end();
        
        // lockWaiter should have gotten upgrade now.
        lockWaiter.waitToAcquire();
        assertTrue( lockWaiter.mAcquired );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.UPGRADE);
        
        // It can release now...
        lockWaiter.mReleaseLock = true;
        
        // Wait for waiter thread to release and end.
        try {
            lockWaiter.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (lockWaiter.mException != null) {
            throw new Exception("Waiter thread threw Exception", lockWaiter.mException);
        }
        
        lockServer.disconnect();
    }


    /**
     * Tests simple case of timestamp deadlock.
     */
    public void testSimpleTimestampDeadlock() throws Exception
    {

        // Scenario:
        // T1               T2 (T2 created after T1)
        // W(A)
        //                  R(A) - would wait on T1. T2's timestamp later than T1, so it dies.
        LockServer lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getTimestampProperties() );

        String objA = "Object A";

        LockServerTransaction txn1 = lockServer.startTransaction();
        
        assertTrue( txn1.lock(objA, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.WRITE);

        LockServerTransaction txn2 = lockServer.startTransaction();
        
        try {
            txn2.lock(objA, LockMode.READ, 5000L);
            fail("Expected DeadlockException");
        }
        catch (DeadlockException e) {
            // Expected.
        }
        
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.WRITE);
        txn1.end();
        txn2.end();

        lockServer.disconnect();


        // This case shouldn't deadlock:
        // T1               T2 (T2 created after T1)
        //                  W(A)
        // R(A) - wait on T2. T1's timestamp is earlier than T1, so it waits.

        lockServer = (LockServer)PluginHelper.connect(getLockServerClassName(), getTimestampProperties() );

        txn1 = lockServer.startTransaction();
        txn2 = lockServer.startTransaction();
        
        assertTrue( txn2.lock(objA, LockMode.WRITE, -1L) );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.WRITE);

        LockWaiter lockWaiter = new LockWaiter(lockServer, txn1, objA, LockMode.READ);
        lockWaiter.mReleaseLock = false;
        lockWaiter.start();
        lockWaiter.waitToRequest();
        
        assertTrue( lockWaiter.mRequested );
        // Shouldn't be acquired yet.
        assertTrue( ! lockWaiter.mAcquired );

        txn2.end();
        
        // lockWaiter should have gotten upgrade now.
        lockWaiter.waitToAcquire();
        assertTrue( lockWaiter.mAcquired );
        assertTrue( lockServer.getLockMode(objA) ==  LockMode.READ);
        
        // It can release now...
        lockWaiter.mReleaseLock = true;
        
        // Wait for waiter thread to release and end.
        try {
            lockWaiter.join(5000L);
        }
        catch (InterruptedException e) {  }

        if (lockWaiter.mException != null) {
            throw new Exception("Waiter thread threw Exception", lockWaiter.mException);
        }
        
        lockServer.disconnect();
    }



    private static final class LockWaiter extends Thread
    {
        private LockServer mLockServer;
        private Object mObject;
        private LockMode mMode;
        private LockServerTransaction mAlternateTransaction = null;
        
        Exception mException = null;
        boolean mAcquired = false;
        boolean mRequested = false;
        boolean mFinished = false;
        boolean mReleaseLock = true;
        

        LockWaiter(LockServer aLockServer, Object anObject, LockMode aMode)
        {
            mLockServer = aLockServer;
            mObject = anObject;
            mMode = aMode;
        }
        

        LockWaiter(LockServer aLockServer, LockServerTransaction aTransaction, 
                    Object anObject, LockMode aMode)
        {
            mLockServer = aLockServer;
            mObject = anObject;
            mMode = aMode;
            mAlternateTransaction = aTransaction;
        }
        

        public void run() 
        {
            LockServerTransaction txn = null;
            try {
                if (mAlternateTransaction == null) {
                    txn = mLockServer.startTransaction();
                }
                else {
                    txn = mAlternateTransaction;
                }
                
                mRequested = true;
                if ( ! txn.lock(mObject, mMode, -1L)) {
                    throw new Exception("Lock wait didn't return true.");
                }

                mAcquired = true;
                // Wait until test tells us to finish.
                for (int i = 0; i < 5000 && !mReleaseLock; i++) {
                    try { Thread.sleep(10L); } catch (InterruptedException e) { }
                }
            }
            catch (Exception e) {
                mException = e;
                System.err.println("Exception in LockWaiter waiting for lock mode: " + mMode);
                e.printStackTrace(System.err);
            }
            finally {
                mFinished = true;
                if (txn != null) {
                    txn.end();
                }
            }
        }


        void waitToRequest()
        {
            for (int w = 0; w < 5000 && !mRequested && mException == null; ++w) {
                try { Thread.sleep(1L); } catch (InterruptedException e) { }
            }
        }


        void waitToAcquire()
        {
            for (int w = 0; w < 5000 && !mAcquired && mException == null; ++w) {
                try { Thread.sleep(1L); } catch (InterruptedException e) { }
            }
        }
    }



    private static final class DeadlockWaiter extends Thread
    {
        private LockServerTransaction mTransaction;
        private Object mObject;
        private LockMode mMode;
        private String mName;
        
        Exception mException = null;
        boolean mAcquired = false;
        boolean mRequested = false;
        boolean mFinished = false;
        

        DeadlockWaiter(LockServerTransaction aTransaction, Object anObject, LockMode aMode, String aName)
        {
            mTransaction = aTransaction;
            mObject = anObject;
            mMode = aMode;
            mName = aName;
        }
        

        public void run() 
        {
            try {
                mRequested = true;
                // Wait five seconds for lock. This will be enough to to go into deadlock.
                if ( ! mTransaction.lock(mObject, mMode, 5000L)) {
                    return;
                }

                mAcquired = true;
            }
            catch (Exception e) {
                mException = e;
                System.err.println("Exception in DeadlockWaiter " + mName + " waiting for lock mode: " + mMode);
                e.printStackTrace(System.err);
            }
            finally {
                mFinished = true;
                mTransaction.end();
            }
        }


        void waitToRequest()
        {
            for (int w = 0; w < 5000 && !mRequested && mException == null; ++w) {
                try { Thread.sleep(1L); } catch (InterruptedException e) { }
            }
        }

    }
}
