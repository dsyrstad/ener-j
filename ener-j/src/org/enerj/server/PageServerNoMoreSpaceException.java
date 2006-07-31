// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PageServerNoMoreSpaceException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Exception thrown from a Ener-J Page Server if there is no more space available in the
 * volume.
 *
 * @version $Id: PageServerNoMoreSpaceException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PageServerNoMoreSpaceException extends PageServerException
{
    //----------------------------------------------------------------------
    public PageServerNoMoreSpaceException()
    {
        super();
    }

    //----------------------------------------------------------------------
    public PageServerNoMoreSpaceException(String aMessage)
    {
        super(aMessage);
    }

    //----------------------------------------------------------------------
    public PageServerNoMoreSpaceException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

    //----------------------------------------------------------------------
    public PageServerNoMoreSpaceException(Throwable aCause)
    {
        super(aCause);
    }

}

