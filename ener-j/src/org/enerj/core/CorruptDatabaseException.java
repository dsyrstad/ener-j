// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/CorruptDatabaseException.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Indicates that Ener-J has detected a corrupted database.
 *
 * @version $Id: CorruptDatabaseException.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CorruptDatabaseException extends ODMGRuntimeException {
    //--------------------------------------------------------------------------
    /**
     * Construct an instance of the exception.
     */
    public CorruptDatabaseException() 
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Construct an instance of the exception with a descriptive message.
     * @param	msg	A description of the exception.
     */
    public CorruptDatabaseException(String msg) 
    {
        super(msg);
    }

    //--------------------------------------------------------------------------
    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public CorruptDatabaseException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}

