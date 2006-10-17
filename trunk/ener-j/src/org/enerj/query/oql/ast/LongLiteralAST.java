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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/LongLiteralAST.java,v 1.4 2005/10/29 21:21:34 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;



/**
 * The LongLiteral AST. <p>
 * 
 * @version $Id: LongLiteralAST.java,v 1.4 2005/10/29 21:21:34 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class LongLiteralAST extends BaseAST
{
    private long mValue;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a LongLiteralAST. 
     *
     * @param aValue
     */
    public LongLiteralAST(long aValue)
    {
        mValue = aValue;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mValue.
     *
     * @return a long.
     */
    public long getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
    	if (mValue >= Integer.MIN_VALUE && mValue <= Integer.MAX_VALUE) {
    		return Integer.class;
    	}
    	
        return Long.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
    	if (mValue >= Integer.MIN_VALUE && mValue <= Integer.MAX_VALUE) {
            return new ConstantUnary( new Integer((int)mValue) );
    	}
    	
        return new ConstantUnary( new Long(mValue) );
    }
}
