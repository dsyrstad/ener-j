/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: LatchImpl.java,v 1.6.2.1 2007/02/01 14:49:46 cwl Exp $
 */

package com.sleepycatje.je.latch;

import java.util.ArrayList;
import java.util.List;

import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.RunRecoveryException;
import com.sleepycatje.je.dbi.EnvironmentImpl;

/**
 * This implementation is used in non-Java5 JVMs.  In Java5 JVMs, the
 * Java5LockWrapperImpl class is used.  The switch hitting is performed in
 * LatchSupport.
 *
 * Simple thread-based non-transactional exclusive non-nestable latch.
 * <p>
 * Latches provide simple exclusive transient locks on objects.  Latches are
 * expected to be held for short, defined periods of time.  No deadlock
 * detection is provided so it is the caller's responsibility to sequence latch
 * acquisition in an ordered fashion to avoid deadlocks.
 * <p>
 * A latch can be acquire in wait or no-wait modes.  In the former, the caller
 * will wait for any conflicting holders to release the latch.  In the latter,
 * if the latch is not available, control returns to the caller immediately.
 */
public class LatchImpl implements Latch {
    private static final String DEFAULT_LATCH_NAME = "LatchImpl";

    private String name = null;
    private List waiters = null;
    private LatchStats stats = new LatchStats();
    /* The object that the latch protects. */
    private EnvironmentImpl env = null;
    private Thread owner = null;

    /**
     * Create a latch.
     */
    public LatchImpl(String name, EnvironmentImpl env) {
	this.name = name;
	this.env = env;
    }

    /**
     * Create a latch with no name, more optimal for shortlived latches.
     */
    public LatchImpl(EnvironmentImpl env) {
	this.env = env;
        this.name = DEFAULT_LATCH_NAME;
    }

    /**
     * Set the latch name, used for latches in objects instantiated from
     * the log.
     */
    synchronized public void setName(String name) {
        this.name = name;
    }

    /**
     * Acquire a latch for exclusive/write access.
     *
     * <p>Wait for the latch if some other thread is holding it.  If there are
     * threads waiting for access, they will be granted the latch on a FIFO
     * basis.  When the method returns, the latch is held for exclusive
     * access.</p>
     *
     * @throws LatchException if the latch is already held by the calling
     * thread.
     *
     * @throws RunRecoveryException if an InterruptedException exception
     * occurs.
     */
    public void acquire()
	throws DatabaseException {

        try {
            Thread thread = Thread.currentThread();
            LatchWaiter waitTarget = null;

	    synchronized (this) {
		if (thread == owner) {
		    stats.nAcquiresSelfOwned++;
		    throw new LatchException(getNameString() +
					     " already held");
		}

		if (owner == null) {
		    stats.nAcquiresNoWaiters++;
		    owner = thread;
		} else {
		    if (waiters == null) {
			waiters = new ArrayList();
		    }
		    waitTarget = new LatchWaiter(thread);
		    waiters.add(waitTarget);
		    stats.nAcquiresWithContention++;
		}
	    }

	    if (waitTarget != null) {
		synchronized (waitTarget) {
		    while (true) {
			if (waitTarget.active) {
			    if (thread == owner) {
				break;
			    } else {
				throw new DatabaseException
				    ("waitTarget.active but not owner");
			    }
			} else {
			    waitTarget.wait();
			    if (thread == owner) {
				break;
			    } else {
				continue;
			    }
			}
		    }
		}
	    }

            assert noteLatch(); // intentional side effect;
	} catch (InterruptedException e) {
	    throw new RunRecoveryException(env, e);
	} finally {
	    assert EnvironmentImpl.maybeForceYield();
	}
    }

    /**
     * Acquire a latch for exclusive/write access, but do not block if it's not
     * available.
     *
     * @return true if the latch was acquired, false if it is not available.
     *
     * @throws LatchException if the latch is already held by the calling
     * thread.
     */
    public synchronized boolean acquireNoWait()
	throws LatchException {

        try {
            Thread thread = Thread.currentThread();
            if (thread == owner) {
                stats.nAcquiresSelfOwned++;
                throw new LatchException(getNameString() +
                                         " already held");
            }
            if (owner == null) {
                owner = thread;
                stats.nAcquireNoWaitSuccessful++;
                assert noteLatch(); // intentional side effect;
                return true;
            } else {
                stats.nAcquireNoWaitUnsuccessful++;
                return false;
            }
	} finally {
	    assert EnvironmentImpl.maybeForceYield();
	}
    }

    /**
     * Release the latch.  If there are other thread(s) waiting for the latch,
     * one is woken up and granted the latch. If the latch was not owned by 
     * the caller, just return;
     */
    public void releaseIfOwner() {
        doRelease(false);
    }

    /**
     * Release the latch.  If there are other thread(s) waiting for the latch,
     * they are woken up and granted the latch.
     *
     * @throws LatchNotHeldException if the latch is not currently held.
     */
    public void release()
        throws LatchNotHeldException {

        if (doRelease(true)) {
            throw new LatchNotHeldException
                (getNameString() + " not held");
        }
    }

    /**
     * Do the work of releasing the latch. Wake up any waiters.
     *
     * @returns true if this latch was not owned by the caller.
     */
    private boolean doRelease(boolean checkHeld) {

        LatchWaiter newOwner = null;

        try {
            synchronized (this) {
		Thread thread = Thread.currentThread();
		if (thread != owner) {
                    return true;
		}

		if (waiters != null && waiters.size() > 0) {
		    newOwner = (LatchWaiter) waiters.remove(0);
		    owner = (Thread) newOwner.thread;

		} else {
		    owner = null;
		}
		stats.nReleases++;
		assert unNoteLatch(checkHeld); // intentional side effect.
            }
	} finally {
	    assert EnvironmentImpl.maybeForceYield();
	}

	if (newOwner != null) {
	    synchronized (newOwner) {
		newOwner.active = true;
		newOwner.notifyAll();
	    }
	}
        return false;
    }

    /**
     * Return true if the current thread holds this latch.
     *
     * @return true if we hold this latch.  False otherwise.
     */
    public boolean isOwner() {

        return Thread.currentThread() == owner;
    }

    /**
     * Used only for unit tests.
     *
     * @return the thread that currently holds the latch for exclusive access.
     */
    public Thread owner() {

	return owner;
    }

    /**
     * Return the number of threads waiting.
     *
     * @return the number of threads waiting for the latch.
     */
    public synchronized int nWaiters() {

        return (waiters != null) ? waiters.size() : 0;
    }

    /**
     * @return a LatchStats object with information about this latch.
     */
    public LatchStats getLatchStats() {
	LatchStats s = null;
	try {
	    s = (LatchStats) stats.clone();
	} catch (CloneNotSupportedException e) {
	    /* Klockwork - ok */
	}
	return s;
    }

    /**
     * Formats a latch owner and waiters.
     */
    public synchronized String toString() {

        return LatchSupport.latchTable.toString(name, owner, waiters, 0);
    }

    /**
     * For concocting exception messages
     */
    private String getNameString() {

        return LatchSupport.latchTable.getNameString(name);
    }

    /**
     * Only call under the assert system. This records latching by thread.
     */
    private boolean noteLatch()
	throws LatchException {

        return LatchSupport.latchTable.noteLatch(this);
    }

    /**
     * Only call under the assert system. This records latching by thread.
     */
    private boolean unNoteLatch(boolean checkHeld) {
        
        /* Only return a false status if we are checking for latch ownership.*/
        if (checkHeld) {
            return LatchSupport.latchTable.unNoteLatch(this, name);
        } else {
            LatchSupport.latchTable.unNoteLatch(this, name);
            return true;
        }
    }
    
    /**
     * Simple class that encapsulates a Thread to be 'notify()ed'.
     */
    static private class LatchWaiter {
	boolean active;
	Thread thread;

	LatchWaiter(Thread thread) {
	    this.thread = thread;
	    active = false;
	}

	public String toString() {
	    return "<LatchWaiter: " + thread + ">";
	}
    }
}
