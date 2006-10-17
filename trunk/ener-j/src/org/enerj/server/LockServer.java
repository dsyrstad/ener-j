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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/LockServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import org.odmg.*;

/**
 * Schedules and manages arbitrary locks on arbitrary objects using a two-phase locking
 * protocol. Owners of locks are identified by
 * LockServer Transactions. Lockable items are arbitrary Objects identified by equality
 * (rather than identity).
 * <p>
 * The mode of a lock can be one of the enumerated types in LockMode: READ, UPGRADE, or WRITE. 
 * These modes are defined in 
 * the OMG Concurrency Service 1.0 and are equivalent to the ODMG 3.0 lock modes. 
 * Each successive mode is more exclusive
 * than the previous mode. READ allows multiple owners to READ the same object.
 * WRITE is the most exclusive and only allows one writer per item - no other READ or UPGRADE
 * locks can exist. The UPGRADE mode
 * means "intent to modify". UPGRADE locks conflict with other UPGRADE locks and WRITE locks.
 * So there can be multiple READ locks and one UPGRADE lock
 * on an item. By first obtaining an UPGRADE lock on an item (<em>instead of</em> a READ lock), 
 * followed by a WRITE lock, a
 * form of deadlock can be avoided. Without UPGRADE, this deadlock would occur when two owners 
 * of the same item have it locked for READ, then both try to WRITE lock the item.
 * The first owner will block on the WRITE lock because the second owner has the item READ locked.
 * The second owner will block on its WRITE lock because the first owner also has the item READ locked.
 * Hence, the so-called "deadly embrace", or deadlock, occurs. Instead, if both owners
 * initially attempt to obtain UPGRADE locks, the first owner will get the UPGRADE lock
 * while the second one waits. Then the first owner may obtain a WRITE lock without the forementioned
 * deadlock occuring.
 * <p>
 * Here is a table of granted lock modes and the requested lock modes they conflict with:<br>
 * <table border="1"> <caption>Lock Compatibility (X = conflict, owner will block)</caption> <tbody>
 *  <tr><th></th>              <th>Read Requested</th> <th>Upgrade Requested</th>  <th>Write Requested</th></tr>   
 *  <tr><th>Read Granted</th>  <td>OK</td>             <td>OK</td>                 <td>X</td></tr>
 *  <tr><th>Upgrade Granted</th><td>OK</td>            <td>X</td>                  <td>X</td></tr>
 *  <tr><th>Write Granted</th> <td>X</td>              <td>X</td>                  <td>X</td></tr>
 * </tbody></table>
 * <p>
 * General notes:
 * <ul>
 * <li> This class performs deadlock detection. If deadlock is detected, DeadlockException is thrown.
 * <li> Precedence for those waiting on a lock is First In-First Out (FIFO - first waiter is 
 *      first locker).
 * <li> Locks are reentrant -- a single owner can get the same read or write lock multiple times. 
 * <li> Locks are held in a transaction. Locks are not released until the transaction is
 *      closed. 
 * <li> Lock modes may not be demoted - i.e., you cannot change from a 
 *      write lock to a read lock.
 * </ul>
 *
 * Two flavors of deadlock detection are available. DeadlockAlgorithm.WAITS_FOR is the
 * classic algorithm which checks for cycles in the transaction "waits-for" graph. 
 * It is dead accurate at finding deadlocks,
 * but the detection can be very CPU intensive when many locks are held. The second
 * algorithm is DeadlockAlgorithm.TIMESTAMP. This algorithm is very quick, but less
 * accurate. It will always detect real deadlock, but may also detect deadlock when it 
 * doesn't actually exist. It works by assigning a "timestamp" (really a unique progressing serial number) to each 
 * transaction when the transaction starts - in reality, this enforces a locking protocol. 
 * When the lock method needs to wait for
 * a lock on an item to be released, it first checks its timestamp against all conflicting locks
 * held on the item by other transactions. Let's say T1 and T2 are transactions. T1
 * needs to wait for a lock held by T2. If T1 has an earlier timestamp than T2, T1
 * can wait for T2 to release its lock. If T1 has a later timestamp than T2, the lock
 * method for T1 throws DeadlockException. So, this algorithm can throw DeadlockExceptions
 * when a deadlock doesn't actually exist. <p>
 *
 * The disconnect() method must be used to complete the use of a LockServer.
 *
 * Beyond this interface, the following additional requirements
 * are placed on a LockServer implementation:<p>
 *
 * 1. A static factory method "connect" must be defined. The definition
 *    for the method is:<p>
 *
 * public static LockServer connect(String anLockServerURI) throws ODMGException;<p>
 *
 * Parameters:<br> anLockServerURI - a URI identifying the LockServer to open. By the time 
 *  this method is called, the LockServer has already determined the proper
 *  LockServer implementation to use based on the protocol. The URI has the form: <br>
 * vols://{lock-server-class-name}[,args...]<p>
 *
 * Throws: ODMGException in the event of an error. <p>
 *
 * Returns: a LockServer. <p>
 *
 * 2. The LockServer must be thread-safe.<p>
 *
 * @version $Id: LockServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface LockServer
{
    //----------------------------------------------------------------------
    /**
     * Starts a transaction in the lock server.  A Transaction may only be 
     * used by a single thread at any one time.
     *
     * @return a LockServerTransction.
     */
    public LockServerTransaction startTransaction();

    //----------------------------------------------------------------------
    /**
     * Disconnects from a LockServer.
     * A transaction must not be active.
     *
     * @throws ODMGException in the event of an error. 
     */
    public void disconnect() throws ODMGException;

    //----------------------------------------------------------------------
    /**
     * Gets the DeadlockAlgorithm assigned to this server.
     *
     * @return the DeadlockAlgorithm assigned to this server.
     */
    public DeadlockAlgorithm getDeadlockAlgorithm();

    //----------------------------------------------------------------------
    /**
     * Gets the LockMode for the specified object at the current point in time.
     *
     * @param anObject the Object which is locked.
     *
     * @return the most exclusive LockMode held on anObject, or null if the object is not locked.
     */
    public LockMode getLockMode(Object anObject);

    //----------------------------------------------------------------------
    /**
     * Gets the Total Lock Attempts.
     *
     * @return the Total Lock Attempts.
     */
    public int getTotalLockAttempts();

    //----------------------------------------------------------------------
    /**
     * Gets the Total Locks Granted.
     *
     * @return the Total Locks Granted.
     */
    public int getTotalLocksGranted();

    //----------------------------------------------------------------------
    /**
     * Gets the Total Lock Waits.
     *
     * @return the Total Lock Waits.
     */
    public int getTotalLockWaits();

    //----------------------------------------------------------------------
    /**
     * Gets the total number of Deadlocks.
     *
     * @return the total number of Deadlocks.
     */
    public int getTotalDeadlocks();

    //----------------------------------------------------------------------
    /**
     * Gets the total number of Transactions.
     *
     * @return the total number of Transactions.
     */
    public int getTotalTransactions();

    //----------------------------------------------------------------------
    /**
     * Gets the peak number of Transactions.
     *
     * @return the peak number of Transactions.
     */
    public int getPeakTransactions();

    //----------------------------------------------------------------------
    /**
     * Gets the peak number of locked objects.
     *
     * @return the peak number of locked objects.
     */
    public int getPeakLockedObjects();

    //----------------------------------------------------------------------
    /**
     * Gets the peak number of locks.
     *
     * @return the peak number of locks.
     */
    public int getPeakNumLocks();

    //----------------------------------------------------------------------
    /**
     * Gets the longest wait for a lock.
     *
     * @return the longest wait for a lock in milliseconds.
     */
    public long getLongestWaitForLock();

    //----------------------------------------------------------------------
    /**
     * Gets the longest time for a deadlock check.
     *
     * @return the longest time for a deadlock check in milliseconds.
     */
    public long getLongestDeadlockCheck();

    //----------------------------------------------------------------------
    /**
     * Gets the number of active Transactions.
     *
     * @return the number of active Transactions.
     */
    public int getNumActiveTransactions();

    //----------------------------------------------------------------------
    /**
     * Gets the number of active locked objects.
     *
     * @return the number of active locked objects.
     */
    public int getNumActiveLockedObjects();

    //----------------------------------------------------------------------
    /**
     * Gets the number of active locks.
     *
     * @return the number of active locks.
     */
    public int getNumActiveLocks();

    //----------------------------------------------------------------------
    /**
     * Dumps all of the LockScheduler metrics to a String.
     *
     * @return a String of the metrics.
     */
    public String dumpMetrics();
}
