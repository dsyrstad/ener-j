// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PageServerNotFoundException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Exception thrown from a Ener-J Page Server if the page volume/configuration
 * cannot be found.
 *
 * @version $Id: PageServerNotFoundException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PageServerNotFoundException extends PageServerException
{
    //----------------------------------------------------------------------
    public PageServerNotFoundException()
    {
        super();
    }

    //----------------------------------------------------------------------
    public PageServerNotFoundException(String aMessage)
    {
        super(aMessage);
    }

    //----------------------------------------------------------------------
    public PageServerNotFoundException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

    //----------------------------------------------------------------------
    public PageServerNotFoundException(Throwable aCause)
    {
        super(aCause);
    }

}

