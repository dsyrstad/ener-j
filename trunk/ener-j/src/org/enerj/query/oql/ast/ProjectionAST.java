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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ProjectionAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The Projection AST. <p>
 * 
 * @version $Id: ProjectionAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ProjectionAST extends BaseAST
{
    private AST mExpr;
    private String mAlias;
    

    /**
     * Construct a ProjectionAST. 
     *
     * @param anExpr the expression to project.
     * @param anAlias the alias name for the projection. May be null.
     */
    public ProjectionAST(AST anExpr, String anAlias)
    {
        mExpr = anExpr;
        mAlias = anAlias;
    }


    /**
     * Gets the mAlias.
     *
     * @return a String.
     */
    public String getAlias()
    {
        return mAlias;
    }


    /**
     * Gets the mExpr.
     *
     * @return a ExprAST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
}
