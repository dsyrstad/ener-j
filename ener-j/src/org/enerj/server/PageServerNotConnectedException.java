// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PageServerNotConnectedException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Exception thrown from a Ener-J Page Server if it's not currently connected.
 *
 * @version $Id: PageServerNotConnectedException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PageServerNotConnectedException extends PageServerException
{
    //----------------------------------------------------------------------
    public PageServerNotConnectedException()
    {
        super();
    }

    //----------------------------------------------------------------------
    public PageServerNotConnectedException(String aMessage)
    {
        super(aMessage);
    }

    //----------------------------------------------------------------------
    public PageServerNotConnectedException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

    //----------------------------------------------------------------------
    public PageServerNotConnectedException(Throwable aCause)
    {
        super(aCause);
    }

}

