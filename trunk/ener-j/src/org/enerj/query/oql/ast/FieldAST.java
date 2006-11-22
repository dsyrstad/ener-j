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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/FieldAST.java,v 1.4 2006/02/19 01:20:33 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;



/**
 * The Field AST. <p>
 * 
 * @version $Id: FieldAST.java,v 1.4 2006/02/19 01:20:33 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FieldAST extends BaseAST
{
    private String mFieldName;
    private AST mExpr;
    

    /**
     * Construct a FieldAST. 
     *
     * @param aFieldName
     * @param anExpr
     */
    public FieldAST(String aFieldName, AST anExpr)
    {
        mFieldName = aFieldName;
        mExpr = anExpr;
    }
    

    /**
     * Gets the Expr.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    

    /**
     * Gets the FieldName.
     *
     * @return a String.
     */
    public String getFieldName()
    {
        return mFieldName;
    }

    

    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return mExpr.getType();
    }

    

    /** 
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException
    {
        return mExpr.resolve();
    }
}
