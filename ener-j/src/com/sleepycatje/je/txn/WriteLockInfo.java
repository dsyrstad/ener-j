/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: WriteLockInfo.java,v 1.14.2.1 2007/02/01 14:49:53 cwl Exp $
 */

package com.sleepycatje.je.txn;

import com.sleepycatje.je.utilint.DbLsn;

/*
 * Lock and abort LSN kept for each write locked node. Allows us to log with
 * the correct abort LSN.
 */
public class WriteLockInfo {
    /* Write lock for node. */
    Lock lock;            

    /*
     * The original LSN. This is stored in the LN log entry.  May be null if
     * the node was created by this transaction.
     */
    long abortLsn = DbLsn.NULL_LSN;

    /*
     * The original setting of the knownDeleted flag.  It parallels abortLsn.
     */
    boolean abortKnownDeleted;

    /*
     * Size of the original log entry, or zero if abortLsn is NULL_LSN or if
     * the size is not known.
     */
    int abortLogSize;

    /*
     * True if the node has never been locked before. Used so we can determine
     * when to set abortLsn.
     */
    boolean neverLocked;

    /*
     * True if the node was created this transaction.
     */
    boolean createdThisTxn;

    static final WriteLockInfo basicWriteLockInfo =
	new WriteLockInfo();

    WriteLockInfo(Lock lock) {
	this.lock = lock;
	abortLsn = DbLsn.NULL_LSN;
	abortKnownDeleted = false;
	neverLocked = true;
	createdThisTxn = false;
    }

    /* public for Sizeof program. */
    public WriteLockInfo() {
	this.lock = null;
	abortLsn = DbLsn.NULL_LSN;
	abortKnownDeleted = true;
	neverLocked = true;
	createdThisTxn = false;
    }

    public boolean getAbortKnownDeleted() {
	return abortKnownDeleted;
    }

    public long getAbortLsn() {
	return abortLsn;
    }

    public void setAbortLogSize(int logSize) {
        abortLogSize = logSize;
    }
}
