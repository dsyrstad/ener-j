/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 1997-2006
 *	Oracle Corporation.  All rights reserved.
 *
 * $Id: ReplicationHandleDeadException.java,v 12.4 2006/08/24 14:46:08 bostic Exp $
 */
package com.sleepycatni.db;

import com.sleepycatni.db.internal.DbEnv;

public class ReplicationHandleDeadException extends DatabaseException {
    /* package */ ReplicationHandleDeadException(final String s,
                                   final int errno,
                                   final DbEnv dbenv) {
        super(s, errno, dbenv);
    }
}
