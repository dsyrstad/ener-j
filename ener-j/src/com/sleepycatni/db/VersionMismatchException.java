/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 1997-2006
 *	Oracle Corporation.  All rights reserved.
 *
 * $Id: VersionMismatchException.java,v 1.5 2006/08/24 14:46:09 bostic Exp $
 */
package com.sleepycatni.db;

import com.sleepycatni.db.internal.DbEnv;

public class VersionMismatchException extends DatabaseException {
    /* package */ VersionMismatchException(final String s,
                                   final int errno,
                                   final DbEnv dbenv) {
        super(s, errno, dbenv);
    }
}
