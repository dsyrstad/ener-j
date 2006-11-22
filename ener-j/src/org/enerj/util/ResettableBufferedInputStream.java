/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
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
    

    /**
     * Reset the buffer so that any previously buffered data input is no longer.
     */
    public void resetBuffer()
    {
        this.count = 0;
        this.pos = 0;
    }
}


