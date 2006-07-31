// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PageServerException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Base Exception thrown from a Ener-J Page Server.
 *
 * @version $Id: PageServerException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PageServerException extends ODMGException
{
    //----------------------------------------------------------------------
    public PageServerException()
    {
        super();
    }

    //----------------------------------------------------------------------
    public PageServerException(String aMessage)
    {
        super(aMessage);
    }

    //----------------------------------------------------------------------
    public PageServerException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

    //----------------------------------------------------------------------
    public PageServerException(Throwable aCause)
    {
        super(aCause.getMessage(), aCause);
    }

}

