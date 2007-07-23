/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 1997-2006
 *	Oracle Corporation.  All rights reserved.
 *
 * $Id: RunRecoveryException.java,v 12.4 2006/08/24 14:46:09 bostic Exp $
 */
package com.sleepycatni.db;

import com.sleepycatni.db.internal.DbEnv;

public class RunRecoveryException extends DatabaseException {
    /* package */ RunRecoveryException(final String s,
                                   final int errno,
                                   final DbEnv dbenv) {
        super(s, errno, dbenv);
    }
}
