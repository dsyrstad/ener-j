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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/QuantifierExprAST.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The Universal or Existential QuantifierExpr AST. <p>
 * 
 * @version $Id: QuantifierExprAST.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QuantifierExprAST extends BaseAST
{
    private AST mInClause;
    private AST mRightExpr;
    private boolean mIsUniversal;

    //--------------------------------------------------------------------------------
    /**
     * Construct a OrExprAST. 
     *
     * @param anInClause
     * @param aRightExpr
     * @param isUniversal
     */
    public QuantifierExprAST(AST anInClause, AST aRightExpr, boolean isUniversal)
    {
        mInClause = anInClause;
        mRightExpr = aRightExpr;
        mIsUniversal = isUniversal;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mInClause.
     *
     * @return a AST.
     */
    public AST getInClause()
    {
        return mInClause;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mRightExpr.
     *
     * @return a AST.
     */
    public AST getRightExpr()
    {
        return mRightExpr;
    }
    }
