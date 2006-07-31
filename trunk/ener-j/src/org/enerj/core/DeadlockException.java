// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/DeadlockException.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Indicates deadlock could occur if a lock request would be granted.
 *
 * @version $Id: DeadlockException.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class DeadlockException extends org.odmg.LockNotGrantedException
{
    //----------------------------------------------------------------------
    public DeadlockException()
    {
        super();
    }

    //----------------------------------------------------------------------
    public DeadlockException(String aMessage)
    {
        super(aMessage);
    }

    //----------------------------------------------------------------------
    public DeadlockException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

}

