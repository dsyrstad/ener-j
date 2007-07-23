/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: InternalException.java,v 1.14.2.1 2007/02/01 14:49:54 cwl Exp $
 */

package com.sleepycatje.je.utilint;

import com.sleepycatje.je.DatabaseException;

/**
 * Some internal inconsistency exception.
 */
public class InternalException extends DatabaseException {
    
    public InternalException() {
	super();
    }

    public InternalException(String message) {
	super(message);
    }
}
