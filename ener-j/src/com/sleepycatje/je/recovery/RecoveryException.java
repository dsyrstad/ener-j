/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: RecoveryException.java,v 1.14.2.1 2007/02/01 14:49:49 cwl Exp $
 */

package com.sleepycatje.je.recovery;

import com.sleepycatje.je.RunRecoveryException;
import com.sleepycatje.je.dbi.EnvironmentImpl;

/**
 * Recovery related exceptions
 */
public class RecoveryException extends RunRecoveryException {

    public RecoveryException(EnvironmentImpl env,
                             String message,
                             Throwable t) {
	super(env, message, t);
    }
    public RecoveryException(EnvironmentImpl env,
                             String message) {
	super(env, message);
    }
}
