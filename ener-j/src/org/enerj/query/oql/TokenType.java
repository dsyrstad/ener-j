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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/TokenType.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;

/**
 * A type of Token returned from a lexer. Serves as an enumerated type.
 *
 * @version $Id: TokenType.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TokenType {
    private String mName;
    

    public TokenType(String aName) 
    {
        mName = aName;
    }
    

    public String getName()
    {
        return mName;
    }


    public String toString()
    {
        return mName;
    }


    public boolean equals(Object anObject)
    {
        if (!(anObject instanceof TokenType)) {
            return false;
        }
        
        return ((TokenType)anObject).mName.equals(mName);
    }


    public int hashCode()
    {
        return mName.hashCode();
    }
}
