// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/ResettableBufferedInputStream.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.io.*;

/**
 * An extension of java.io.BufferedInputStream that allows the buffer to be reset 
 * in case the underlying stream has changed.
 *
 * @version $Id: ResettableBufferedInputStream.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class ResettableBufferedInputStream extends BufferedInputStream
{
    //----------------------------------------------------------------------
    /**
     * Constructs a ResettableBufferedInputStream with the specified buffer size.
     *
     * @param anInputStream the underlying InputStream.
     * @param aSize the buffer size.
     *
     * @throws IllegalArgumentException if size <= 0.
     */
    public ResettableBufferedInputStream(InputStream anInputStream, int aBufferSize)
    {
        super(anInputStream, aBufferSize);
    }
    
    //----------------------------------------------------------------------
    /**
     * Reset the buffer so that any previously buffered data input is no longer.
     */
    public void resetBuffer()
    {
        this.count = 0;
        this.pos = 0;
    }
}


