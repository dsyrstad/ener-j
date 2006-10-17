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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/util/MROWLockTest.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $

package org.enerj.util;

import junit.framework.*;

/**
 * Tests MROWLock. <p>
 *
 * @version $Id: MROWLockTest.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class MROWLockTest extends TestCase
{
    
    //----------------------------------------------------------------------
    public MROWLockTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(MROWLockTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(MROWLockTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs local-only read lock assertions on the given aLock.
     */
    private void assertOnlyLocallyReadLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isReadLocked() );
        assertTrue( aLock.isLocallyLocked() );
        assertTrue( aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isWriteLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
        assertTrue( !aLock.isExternallyLocked() );
        assertTrue( !aLock.isExternallyReadLocked() );
        assertTrue( !aLock.isExternallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs local-only write lock assertions on the given aLock.
     */
    private void assertOnlyLocallyWriteLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isLocallyLocked() );
        assertTrue( aLock.isWriteLocked() );
        assertTrue( aLock.isLocallyWriteLocked() );
        assertTrue( !aLock.isReadLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isExternallyLocked() );
        assertTrue( !aLock.isExternallyReadLocked() );
        assertTrue( !aLock.isExternallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs local-only read and write lock assertions on the given aLock.
     */
    private void assertOnlyLocallyReadAndWriteLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isReadLocked() );
        assertTrue( aLock.isWriteLocked() );
        assertTrue( aLock.isLocallyLocked() );
        assertTrue( aLock.isLocallyWriteLocked() );
        assertTrue( aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isExternallyLocked() );
        assertTrue( !aLock.isExternallyReadLocked() );
        assertTrue( !aLock.isExternallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs local read lock assertions on the given aLock. External locks are not checked.
     */
    private void assertLocallyReadLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isReadLocked() );
        assertTrue( aLock.isLocallyLocked() );
        assertTrue( aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isWriteLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs local write lock assertions on the given aLock. External locks are not checked.
     */
    private void assertLocallyWriteLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isLocallyLocked() );
        assertTrue( aLock.isWriteLocked() );
        assertTrue( aLock.isLocallyWriteLocked() );
        assertTrue( !aLock.isReadLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs local read and write lock assertions on the given aLock. External locks are not checked.
     */
    private void assertLocallyReadAndWriteLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isReadLocked() );
        assertTrue( aLock.isWriteLocked() );
        assertTrue( aLock.isLocallyLocked() );
        assertTrue( aLock.isLocallyWriteLocked() );
        assertTrue( aLock.isLocallyReadLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs external-only read lock assertions on the given aLock.
     */
    private void assertOnlyExternallyReadLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isReadLocked() );
        assertTrue( !aLock.isWriteLocked() );
        assertTrue( !aLock.isLocallyLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
        assertTrue( aLock.isExternallyLocked() );
        assertTrue( aLock.isExternallyReadLocked() );
        assertTrue( !aLock.isExternallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs external-only write lock assertions on the given aLock.
     */
    private void assertOnlyExternallyWriteLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( !aLock.isReadLocked() );
        assertTrue( aLock.isWriteLocked() );
        assertTrue( !aLock.isLocallyLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
        assertTrue( aLock.isExternallyLocked() );
        assertTrue( !aLock.isExternallyReadLocked() );
        assertTrue( aLock.isExternallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Performs external-only read and write lock assertions on the given aLock.
     */
    private void assertOnlyExternallyReadAndWriteLocked(MROWLock aLock) throws Exception
    {
        assertTrue( aLock.isLocked() );
        assertTrue( aLock.isReadLocked() );
        assertTrue( aLock.isWriteLocked() );
        assertTrue( !aLock.isLocallyLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
        assertTrue( aLock.isExternallyLocked() );
        assertTrue( aLock.isExternallyReadLocked() );
        assertTrue( aLock.isExternallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Asserts that a lock is not locked.
     */
    private void assertNotLocked(MROWLock aLock) throws Exception
    {
        assertTrue( !aLock.isLocked() );
        assertTrue( !aLock.isReadLocked() );
        assertTrue( !aLock.isWriteLocked() );
        assertTrue( !aLock.isLocallyLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
        assertTrue( !aLock.isExternallyLocked() );
        assertTrue( !aLock.isExternallyReadLocked() );
        assertTrue( !aLock.isExternallyWriteLocked() );
    }

    //----------------------------------------------------------------------
    /**
     * Asserts that no local locks are held. External locks are not checked.
     */
    private void assertNotLocallyLocked(MROWLock aLock) throws Exception
    {
        assertTrue( !aLock.isLocallyLocked() );
        assertTrue( !aLock.isLocallyReadLocked() );
        assertTrue( !aLock.isLocallyWriteLocked() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Acquire read locks on multiple threads.
     */
    private ReaderThread[] acquireReadLocks(MROWLock aLock) throws Exception
    {
        ReaderThread[] threads = new ReaderThread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ReaderThread(aLock);
            threads[i].start();
        }

        // Wait until all threads acquire lock
        int acquireCount;
        System.out.println("Waiting for threads to acquire locks");
        do {
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            acquireCount = 0;
            for (int i = 0; i < threads.length; i++) {
                if (threads[i].mAcquired) {
                    ++acquireCount;
                }

                // Make sure that no thread got an exception - this would prevent acquistion.
                if (threads[i].mException != null) {
                    throw threads[i].mException;
                }
            }
        } while (acquireCount != threads.length);
        
        System.out.println("Acquired.");
        return threads;
    }
    
    //----------------------------------------------------------------------
    /** 
     * Release and terminate read locking threads created by acquireReadLocks.
     */
    private void releaseReadLocks(ReaderThread[] someThreads) throws Exception
    {
        // Tell all threads that they can now release their locks.
        for (int i = 0; i < someThreads.length; i++) {
            someThreads[i].mRelease = true;
        }

        // Wait for each thread to finish and check for exceptions.
        System.out.println("Waiting for release.");
        for (int i = 0; i < someThreads.length; i++) {
            someThreads[i].join();
            if (someThreads[i].mException != null) {
                throw someThreads[i].mException;
            }
        }

        System.out.println("Released.");
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests that read locks are reentrant and "is" methods work properly for read locks.
     */
    public void testReentrantRead() throws Exception
    {
        MROWLock lock = new MROWLock();
        
        //-------------------------------------------------------------
        // Test one level
        assertNotLocked(lock);

        lock.acquireRead(); // Level 1
        assertOnlyLocallyReadLocked(lock);

        lock.releaseRead(); // Back to unlocked
        assertNotLocked(lock);

        //-------------------------------------------------------------
        // Test two level
        assertNotLocked(lock);

        lock.acquireRead(); // Level 1
        assertOnlyLocallyReadLocked(lock);

        lock.acquireRead(); // Level 2
        assertOnlyLocallyReadLocked(lock);
        
        lock.releaseRead(); // Back to level 1
        assertOnlyLocallyReadLocked(lock);
        
        lock.releaseRead(); // Back to unlocked
        assertNotLocked(lock);

        //-------------------------------------------------------------
        // Test three level
        assertNotLocked(lock);

        lock.acquireRead(); // Level 1
        assertOnlyLocallyReadLocked(lock);

        lock.acquireRead(); // Level 2
        assertOnlyLocallyReadLocked(lock);
        
        lock.acquireRead(); // Level 3
        assertOnlyLocallyReadLocked(lock);
        
        lock.releaseRead(); // Back to level 2
        assertOnlyLocallyReadLocked(lock);
        
        lock.releaseRead(); // Back to level 1
        assertOnlyLocallyReadLocked(lock);
        
        lock.releaseRead(); // Back to unlocked
        assertNotLocked(lock);
        
        //-------------------------------------------------------------
        // Test exception
        assertNotLocked(lock);
        try {
            lock.releaseRead();
            fail("Should have thrown a RuntimeException");
        }
        catch (RuntimeException e) {
            // Expected
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests that write locks are reentrant and "is" methods work properly for write locks.
     */
    public void testReentrantWrite() throws Exception
    {
        MROWLock lock = new MROWLock();
        
        //-------------------------------------------------------------
        // Test one level
        assertNotLocked(lock);

        lock.acquireWrite(); // Level 1
        assertOnlyLocallyWriteLocked(lock);

        lock.releaseWrite(); // Back to unlocked
        assertNotLocked(lock);

        //-------------------------------------------------------------
        // Test two level
        assertNotLocked(lock);

        lock.acquireWrite(); // Level 1
        assertOnlyLocallyWriteLocked(lock);

        lock.acquireWrite(); // Level 2
        assertOnlyLocallyWriteLocked(lock);
        
        lock.releaseWrite(); // Back to level 1
        assertOnlyLocallyWriteLocked(lock);
        
        lock.releaseWrite(); // Back to unlocked
        assertNotLocked(lock);

        //-------------------------------------------------------------
        // Test three level
        assertNotLocked(lock);

        lock.acquireWrite(); // Level 1
        assertOnlyLocallyWriteLocked(lock);

        lock.acquireWrite(); // Level 2
        assertOnlyLocallyWriteLocked(lock);
        
        lock.acquireWrite(); // Level 3
        assertOnlyLocallyWriteLocked(lock);
        
        lock.releaseWrite(); // Back to level 2
        assertOnlyLocallyWriteLocked(lock);
        
        lock.releaseWrite(); // Back to level 1
        assertOnlyLocallyWriteLocked(lock);
        
        lock.releaseWrite(); // Back to unlocked
        assertNotLocked(lock);
        
        //-------------------------------------------------------------
        // Test exception
        assertNotLocked(lock);
        try {
            lock.releaseWrite();
            fail("Should have thrown a RuntimeException");
        }
        catch (RuntimeException e) {
            // Expected
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests that a read lock can be followed by a write lock and that after
     * releasing a write lock, the read lock is retained.
     */
    public void testReadFollowedByWrite() throws Exception
    {
        MROWLock lock = new MROWLock();

        //-------------------------------------------------------------
        // Lock order: read/write/release write/release read
        assertNotLocked(lock);

        lock.acquireRead();
        assertOnlyLocallyReadLocked(lock);
        
        lock.acquireWrite();
        assertOnlyLocallyReadAndWriteLocked(lock);
        
        lock.releaseWrite();
        assertOnlyLocallyReadLocked(lock);

        lock.releaseRead();
        assertNotLocked(lock);


        //-------------------------------------------------------------
        // Lock order: read/write/release read/release write
        assertNotLocked(lock);

        lock.acquireRead();
        assertOnlyLocallyReadLocked(lock);
        
        lock.acquireWrite();
        assertOnlyLocallyReadAndWriteLocked(lock);
        
        lock.releaseRead();
        assertOnlyLocallyWriteLocked(lock);

        lock.releaseWrite();
        assertNotLocked(lock);
    }

    //----------------------------------------------------------------------
    /**
     * Tests multiple readers from multiple threads.
     */
    public void testMultipleReaders() throws Exception
    {
        System.out.println("---> testMultipleReaders");
        MROWLock lock = new MROWLock();

        //-------------------------------------------------------------
        assertNotLocked(lock);

        ReaderThread[] threads = acquireReadLocks(lock);
        
        assertOnlyExternallyReadLocked(lock);

        releaseReadLocks(threads);

        assertNotLocked(lock);
    }

    //----------------------------------------------------------------------
    /**
     * Tests multiple readers blocking writer request.
     */
    public void testMultipleReadersBlockingWriter() throws Exception
    {
        System.out.println("---> testMultipleReadersBlockingWriter");
        MROWLock lock = new MROWLock();

        //-------------------------------------------------------------
        assertNotLocked(lock);

        ReaderThread[] readerThreads = acquireReadLocks(lock);
        
        assertOnlyExternallyReadLocked(lock);
        
        WriterThread writerThread = new WriterThread(lock);
        writerThread.start();
        
        // Wait until writer is blocking in acquireWrite
        System.out.println("Waiting for writer to block.");
        while (!writerThread.mInAcquire) {
            assertTrue( !writerThread.mAcquired );
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            if (writerThread.mException != null) {
                throw writerThread.mException;
            }
        }
        
        // acquireWrite is a synchronized method on lock. So by entering a synchronized block
        // here on lock, we can safely test if mInAcquire is set and we are ensured that the writer blocked.
        // (The blocking on lock via the wait() in acquire temporarily releases the lock.)
        synchronized (lock) {
            assertTrue( writerThread.mInAcquire );
            assertTrue( !writerThread.mAcquired );
        }

        assertOnlyExternallyReadLocked(lock);

        System.out.println("Writer blocked. Telling readers to release...");
        // Tell readers to release. Writer should unblock and lock.
        releaseReadLocks(readerThreads);
        
        System.out.println("Waiting for writer to acquire...");
        while (!writerThread.mAcquired) {
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            if (writerThread.mException != null) {
                throw writerThread.mException;
            }
        }
        
        assertOnlyExternallyWriteLocked(lock);
        System.out.println("Write acquired.");
        
        // Tell writer to release
        writerThread.mRelease = true;
        System.out.println("Waiting for writer to finish.");
        writerThread.join();

        if (writerThread.mException != null) {
            throw writerThread.mException;
        }

        assertNotLocked(lock);
        System.out.println("Finished.");
    }

    //----------------------------------------------------------------------
    /**
     * Tests writer lock blocking multiple readers.
     */
    public void testWriterBlockingMultipleReaders() throws Exception
    {
        System.out.println("---> testWriterBlockingMultipleReaders");
        MROWLock lock = new MROWLock();

        //-------------------------------------------------------------
        assertNotLocked(lock);
        
        // This thread will be the writer...
        lock.acquireWrite();
        assertOnlyLocallyWriteLocked(lock);

        ReaderThread[] threads = new ReaderThread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ReaderThread(lock);
            threads[i].start();
        }

        // Wait until all reader threads block on acquire.
        int enterAcquireCount;
        System.out.println("Waiting for readers to block.");
        do {
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            enterAcquireCount = 0;
            for (int i = 0; i < threads.length; i++) {
                synchronized (lock) {
                    if (threads[i].mInAcquire) {
                        ++enterAcquireCount;
                    }

                    // Make sure we didn't acquire it yet.
                    assertTrue( !threads[i].mAcquired );
                }

                // Make sure that no thread got an exception - this would prevent acquistion.
                if (threads[i].mException != null) {
                    throw threads[i].mException;
                }
            }
        } while (enterAcquireCount != threads.length);
        
        System.out.println("Readers are blocked.");
        
        // Release writer - readers should continue
        lock.releaseWrite();
        assertNotLocallyLocked(lock);

        // Wait for readers to acquire
        System.out.println("Waiting for readers to acquire...");
        int acquireCount;
        do {
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            acquireCount = 0;
            for (int i = 0; i < threads.length; i++) {
                if (threads[i].mAcquired) {
                    ++acquireCount;
                }

                // Make sure that no thread got an exception - this would prevent acquistion.
                if (threads[i].mException != null) {
                    throw threads[i].mException;
                }
            }
        } while (acquireCount != threads.length);
        
        System.out.println("Read locks acquired.");

        assertOnlyExternallyReadLocked(lock);

        System.out.println("Telling readers to release...");
        // Tell readers to release. Writer should unblock and lock.
        releaseReadLocks(threads);

        assertNotLocked(lock);
        System.out.println("Finished.");
    }

    //----------------------------------------------------------------------
    /**
     * Tests writer lock blocking another writer.
     */
    public void testWriterBlockingWriter() throws Exception
    {
        System.out.println("---> testWriterBlockingWriter");
        MROWLock lock = new MROWLock();

        //-------------------------------------------------------------
        assertNotLocked(lock);

        // This main thread will be the locking writer...
        lock.acquireWrite();
        assertOnlyLocallyWriteLocked(lock);

        // This thread will be the one blocked.
        WriterThread writerThread = new WriterThread(lock);
        writerThread.start();
        
        // Wait until writer is blocking in acquireWrite
        System.out.println("Waiting for second writer to block.");
        while (!writerThread.mInAcquire) {
            assertTrue( !writerThread.mAcquired );
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            if (writerThread.mException != null) {
                throw writerThread.mException;
            }
        }
        
        // acquireWrite is a synchronized method on lock. So by entering a synchronized block
        // here on lock, we can safely test if mInAcquire is set and we are ensured that the writer blocked.
        // (The blocking on lock via the wait() in acquire temporarily releases the lock.)
        synchronized (lock) {
            assertTrue( writerThread.mInAcquire );
            assertTrue( !writerThread.mAcquired );
        }

        assertOnlyLocallyWriteLocked(lock);

        System.out.println("Second writer blocked. Releasing first writer...");
        lock.releaseWrite();

        System.out.println("Waiting for second writer to acquire...");
        while (!writerThread.mAcquired) {
            try { Thread.sleep(10L); } catch (InterruptedException e) { }
            if (writerThread.mException != null) {
                throw writerThread.mException;
            }
        }
        
        assertOnlyExternallyWriteLocked(lock);
        System.out.println("Second writer acquired.");
        
        // Tell writer to release
        writerThread.mRelease = true;
        System.out.println("Waiting for second writer to finish.");
        writerThread.join();

        if (writerThread.mException != null) {
            throw writerThread.mException;
        }

        assertNotLocked(lock);
        System.out.println("Finished.");
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private final class ReaderThread extends Thread
    {
        private MROWLock mLock;
        volatile boolean mInAcquire = false;
        volatile boolean mAcquired = false;
        volatile boolean mRelease = false;
        volatile Exception mException = null;
        
        //----------------------------------------------------------------------
        ReaderThread(MROWLock aLock)
        {
            mLock = aLock;
        }
        
        //----------------------------------------------------------------------
        public void run() 
        {
            mAcquired = false;
            try {
                assertNotLocallyLocked(mLock);

                mInAcquire = true;
                mLock.acquireRead();
                // Synchonize on mLock to set these, because main thread is synced on it too.
                synchronized (mLock) {
                    mInAcquire = false;
                    mAcquired = true;
                }

                assertLocallyReadLocked(mLock);
                // Hold lock until told to release
                while (!mRelease) {
                    try { Thread.sleep(10L); } catch (InterruptedException e) { }
                }
                
                mLock.releaseRead();
                assertNotLocallyLocked(mLock);
                // Terminate
            }
            catch (Exception e) {
                mException = e;
                if (mLock.isLocallyLocked()) {
                    mLock.releaseRead();
                }
            }
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private final class WriterThread extends Thread
    {
        private MROWLock mLock;
        volatile boolean mInAcquire = false;
        volatile boolean mAcquired = false;
        volatile boolean mRelease = false;
        volatile Exception mException = null;
        
        //----------------------------------------------------------------------
        WriterThread(MROWLock aLock)
        {
            mLock = aLock;
        }
        
        //----------------------------------------------------------------------
        public void run() 
        {
            mAcquired = false;
            try {
                mInAcquire = true;
                mLock.acquireWrite();
                // Synchonize on mLock to set these, because main thread is synced on it too.
                synchronized (mLock) {
                    mInAcquire = false;
                    mAcquired = true;
                }

                assertLocallyWriteLocked(mLock);

                // Hold lock until told to release
                while (!mRelease) {
                    try { Thread.sleep(10L); } catch (InterruptedException e) { }
                }
                
                mLock.releaseWrite();
                assertNotLocallyLocked(mLock);
                // Terminate
            }
            catch (Exception e) {
                mException = e;
                if (mLock.isLocallyLocked()) {
                    mLock.releaseWrite();
                }
            }
        }
    }


}
