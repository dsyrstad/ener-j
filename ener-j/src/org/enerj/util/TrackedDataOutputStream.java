// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/TrackedDataOutputStream.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.io.*;

/**
 * An extension of java.io.DataOutputStream that allows the buffered position of the underlying
 * stream to be tracked. The position is tracked as a long, whereas DataOutputStream's size()
 * method only tracks as an int.
 *
 * @version $Id: TrackedDataOutputStream.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TrackedDataOutputStream extends DataOutputStream
{
    private long mPosition;
    
    //----------------------------------------------------------------------
    /**
     * Constructs a TrackedDataOutputStream with the specified buffer size.
     *
     * @param anOutputStream the underlying OutputStream.
     * @param aPosition the current position of anOutputStream where this stream will
     *  start writing.
     *
     * @throws IllegalArgumentException if size <= 0.
     */
    public TrackedDataOutputStream(OutputStream anOutputStream, long aPosition)
    {
        super(anOutputStream);
        mPosition = aPosition;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the current position. The position must be retrieved for at least
     * every 2GB written to the stream, otherwise it will be incorrect.
     *
     * @return the position in the underlying output stream.
     */
    public synchronized long getPosition()
    {
        mPosition += written;
        written = 0;
        return mPosition;
    }
}


