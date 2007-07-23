/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: LogException.java,v 1.14.2.1 2007/02/01 14:49:47 cwl Exp $
 */

package com.sleepycatje.je.log;

import com.sleepycatje.je.DatabaseException;

/**
 * Configuration related exceptions.
 */
public class LogException extends DatabaseException {
    public LogException(String message) {
	super(message);
    }

    public LogException(String message, Exception e) {
	super(message, e);
    }
}

