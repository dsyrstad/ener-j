/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: ReadCommittedLocker.java,v 1.6.2.1 2007/02/01 14:49:53 cwl Exp $
 */

package com.sleepycatje.je.txn;

import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.dbi.CursorImpl;
import com.sleepycatje.je.dbi.DatabaseImpl;
import com.sleepycatje.je.dbi.EnvironmentImpl;
import com.sleepycatje.je.tree.BIN;
import com.sleepycatje.je.tree.Key;

/**
 * Extends BuddyLocker to acquire write locks using the buddy locker (the
 * transaction locker).  This is used for ReadCommitted (Degree 2) isolation.
 */
public class ReadCommittedLocker extends BuddyLocker {

    /**
     * Creates a ReadCommittedLocker.
     * @param buddy is a transactional locker that will be used for acquiring
     * write locks.
     */
    public ReadCommittedLocker(EnvironmentImpl env, Locker buddy)
        throws DatabaseException {

        /*
         * If the buddy param is a read-committed locker, reach in to get its
         * transactional buddy locker.
         */
        super(env,
              (buddy instanceof ReadCommittedLocker) ?
              ((ReadCommittedLocker) buddy).getBuddy() : buddy);

        assert getBuddy().isTransactional();
    }

    /**
     * Creates a new instance of this txn for the same environment.  No
     * transactional locks are held by this object, so no locks are retained.
     * newNonTxnLocker is also called for the BuddyLocker.
     */
    public Locker newNonTxnLocker()
        throws DatabaseException {

        return new ReadCommittedLocker(envImpl, getBuddy().newNonTxnLocker());
    }

    /**
     * Forwards write locks to the buddy locker (the transaction locker).
     *
     * @see Locker#lockInternal
     * @Override
     */
    LockResult lockInternal(long nodeId,
			    LockType lockType,
                            boolean noWait,
                            DatabaseImpl database)
        throws DatabaseException {

        if (lockType.isWriteLock()) {
            return getBuddy().lockInternal(nodeId, lockType, noWait, database);
        } else {
            return super.lockInternal(nodeId, lockType, noWait, database);
        }
    }

    /**
     * Releases the lock from this locker, or if not owned by this locker then
     * releases it from the buddy locker.
     */
    public void releaseLock(long nodeId)
        throws DatabaseException {

        if (!lockManager.release(nodeId, this)) {
            lockManager.release(nodeId, getBuddy());
        }
    }

    /**
     * Forwards this method to the transactional buddy.  Since the buddy
     * handles write locks, it knows whether this transaction created the node.
     */
    public boolean createdNode(long nodeId) 
        throws DatabaseException {

        return getBuddy().createdNode(nodeId);
    }

    /**
     * Forwards this method to the transactional buddy.  The buddy handles
     * write locks and therefore handles abort information.
     */
    public long getAbortLsn(long nodeId) 
        throws DatabaseException {

        return getBuddy().getAbortLsn(nodeId);
    }

    /**
     * @return the WriteLockInfo for this node.
     */
    public WriteLockInfo getWriteLockInfo(long nodeId)
	throws DatabaseException {

	return getBuddy().getWriteLockInfo(nodeId);
    }

    /**
     * Forwards this method to the transactional buddy.  The buddy handles
     * write locks and therefore handles delete information.
     */
    public void addDeleteInfo(BIN bin, Key deletedKey)
        throws DatabaseException {

        getBuddy().addDeleteInfo(bin, deletedKey);
    }

    /**
     * Forwards this method to the transactional buddy.  The buddy Txn tracks
     * cursors.
     */
    public void registerCursor(CursorImpl cursor)
        throws DatabaseException {

        getBuddy().registerCursor(cursor);
    }

    /**
     * Forwards this method to the transactional buddy.  The buddy Txn tracks
     * cursors.
     */
    public void unRegisterCursor(CursorImpl cursor)
        throws DatabaseException {

        getBuddy().unRegisterCursor(cursor);
    }

    /**
     * Is always transactional because the buddy locker is transactional.
     */
    public boolean isTransactional() {
        return true;
    }

    /**
     * Is always read-committed isolation.
     */
    public boolean isReadCommittedIsolation() {
        return true;
    }
}
