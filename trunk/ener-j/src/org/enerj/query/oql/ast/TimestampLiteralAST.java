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
//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/TimestampLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.sql.Timestamp;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;
import org.odmg.QueryInvalidException;

/**
 * The TimestampLiteral AST. <p>
 * 
 * @version $Id: TimestampLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TimestampLiteralAST extends BaseAST
{
    private String mValue;

    //--------------------------------------------------------------------------------
    /**
     * Construct a TimestampLiteralAST. 
     *
     * @param someValue a timestamp string in the format "yyyy-mm-dd HH:MM:SS[.fffffffff]". 
     *  The ".fffffffff" represents nanoseconds and is optional.
     */
    public TimestampLiteralAST(String someValue)
    {
        mValue = someValue;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Value.
     *
     * @return a String.
     */
    public String getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Timestamp.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        try {
            return new ConstantUnary( Timestamp.valueOf(mValue) );
        }
        catch (IllegalArgumentException e) {
            throw new QueryInvalidException("Invalid timestamp: " + mValue);
        }
    }
}
