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
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/TrackedPositionReader.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.io.PushbackReader;


/**
 * Used by the lexer to track the TextPosition during input.
 *
 * @version $Id: TrackedPositionReader.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TrackedPositionReader extends BufferedReader
{
    private String mFileName;
    private int mLineNumber = 0;
    private int mColumn;
    private char[] mLineTextBuf = new char[1000];
    private int mLineLength;


    /**
     *
     * @param aFileName the file name for the TextPosition context.
     */
    public TrackedPositionReader(String aFileName, Reader aDelegateReader, int aBufSize)  throws IOException
    {
        super(aDelegateReader, aBufSize);
        mFileName = aFileName;
        startNextLine();
    }


    private void startNextLine() throws IOException
    {
        ++mLineNumber;
        mColumn = 0;
        mark(mLineTextBuf.length + 1);
        for (mLineLength = 0; mLineLength < mLineTextBuf.length; mLineLength++) {
            int c = super.read();
            if (c == -1 || c == '\r' || c == '\n') {
                break;
            }
            
            mLineTextBuf[mLineLength] = (char)c;
        }

        reset();
    }


    /**
     * "\r\n" is returned as '\n' since it is treated as a single newline. However,
     * a single '\r' not followed by '\n' is returned as itself.
     */
    public int read() throws IOException
    {
        int c = super.read();
        if (c == '\n') {
            startNextLine();
        }
        else if (c == '\r') {
            if (lookAhead() == '\n') {
                c = super.read();
            }
            
            startNextLine();
        }
        else {
            ++mColumn;
        }
        
        return c;
    }
    

    /**
     * Looks ahead one character in the stream without removing it from the stream.
     *
     * @return the next character to be read from the stream, or -1 on EOF.
     */
    public int lookAhead() throws IOException
    {
        mark(1);
        int c = super.read();
        reset();
        return c;
    }


    /**
     * Gets the TextPosition of the last character read from the stream.
     */
    public TextPosition getTextPosition()
    {
        return new TextPosition(mFileName, mLineNumber, mColumn, new String(mLineTextBuf, 0, mLineLength) );
    }
}
