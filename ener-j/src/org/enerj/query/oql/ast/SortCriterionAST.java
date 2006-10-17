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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/SortCriterionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The SortCriterion AST. <p>
 * 
 * @version $Id: SortCriterionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SortCriterionAST extends BaseAST
{
    private AST mExpr;
    private boolean mAscending;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a SortCriterionAST. 
     *
     * @param anExpr
     * @param isAscending
     */
    public SortCriterionAST(AST anExpr, boolean isAscending)
    {
        mExpr = anExpr;
        mAscending = isAscending;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the ascending flag.
     *
     * @return true if the criterion should be sorted in ascending order. Otherwise descending order is used.
     */
    public boolean isAscending()
    {
        return mAscending;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the expression.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
}
