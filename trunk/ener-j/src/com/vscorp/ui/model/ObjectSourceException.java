// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/ObjectSourceException.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.model;


/**
 * Generalized exception generated from an ObjectSource.
 * <p>
 *
 * @version $Id: ObjectSourceException.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ObjectSourceException extends Exception
{
    /** The wrapped exception. May be null. */
    private Exception mWrappedException;


    /**
     * Construct with a message. There is no wrapped exception.
     *
     * @param aMessage the exception message
     */
    public ObjectSourceException(String aMessage)
    {
        super(aMessage);
    }


    /**
     * Construct with another exception that is wrapped. The message of anException
     * becomes the message of this exception.
     *
     * @param anException the exception to wrap.
     */
    public ObjectSourceException(Exception anException)
    {
        super(anException.toString());
        mWrappedException = anException;
    }


    /**
     * Gets the wrapped exception.
     *
     * @return the wrapped exception, may be null if there is no wrapped exception.
     */
    public Exception getException()
    {
        return mWrappedException;
    }
}
