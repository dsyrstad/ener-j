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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/UndefinedExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.query.oql.TokenType;

/**
 * The UndefinedExpr AST. <p>
 * 
 * @version $Id: UndefinedExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class UndefinedExprAST extends BaseAST
{
    private TokenType mOp;
    private AST mExpr;


    /**
     * Construct a UndefinedExprAST. 
     *
     * @param anOp
     * @param anExpr
     */
    public UndefinedExprAST(TokenType anOp, AST anExpr)
    {
        mOp = anOp;
        mExpr = anExpr;
    }


    /**
     * Gets the Expr.
     *
     * @return a AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }


    /**
     * Gets the Op.
     *
     * @return a TokenType.
     */
    public TokenType getOp()
    {
        return mOp;
    }
}
