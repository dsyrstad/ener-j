// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/LockServerTransaction.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import org.odmg.*;

import org.enerj.core.*;

/**
 * Transaction context used by LockServer. A Transaction may only be 
 * used by a single thread at any one time. Transactions should not be
 * reused unless deadlock occurs.
 *
 * @version $Id: LockServerTransaction.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see LockServer
 */
public interface LockServerTransaction
{
    //----------------------------------------------------------------------
    /**
     * Gets the LockServer for this transaction.
     *
     * @return the LockServer.
     */
    public LockServer getLockServer();

    //----------------------------------------------------------------------
    /**
     * Acquires a lock on the item identified by the given object, or promotes the lock mode if 
     * the transaction currently owns a lock on the object. If the requested lock
     * mode is the same as the current lock mode held by the transaction, the
     * method immediately returns true. If the requested lock mode would result in
     * a demotion (i.e., downgrade), a RuntimeException is thrown. If the lock
     * cannot be immediately obtained, the calling thread blocks (depending on
     * the value of aWaitTime). Note that objects
     * are locked by their equality, not their identity. So as long as two
     * objects are equal (via equals()), they both refer to the same item being locked.
     *
     * @param anObject the Object which identifies the item to lock.
     * @param aLockMode the locking mode. This is one of the static modes
     *  defined in LockServer.LockMode.
     * @param aWaitTime a timeout value in milliseconds. If a lock is not acquired
     *  within this time, the lock is not obtained and false is returned. A value
     *  greater than zero indicates the time to wait. Zero indicates the
     *  lock should be attempted, but if not immediately available, return false.
     *  A -1 (negative one) indicates the thread should block forever waiting for
     *  the lock, or until deadlock occurs.
     *
     * @return true if the lock was acquired, or false if the lock was not 
     *  acquired and aWaitTime was exceeded.
     *
     * @throws LockNotGrantedException if a lock mode demotion/downgrade is attempted or
     *  the transaction is not active. Throws DeadlockException (type of LockNotGrantedException)
     *  if deadlock would occur by obtaining the lock.
     */
    public boolean lock(Object anObject, LockMode aLockMode, long aWaitTime) throws LockNotGrantedException;

    //----------------------------------------------------------------------
    /**
     * Ends the transaction and unlocks all objects the Transaction locked with the lock method.
     *
     */
    public void end();
}
