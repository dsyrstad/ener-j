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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ProjectionAttributesAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The ProjectionAttributes AST. <p>
 * 
 * @version $Id: ProjectionAttributesAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ProjectionAttributesAST extends BaseAST
{
    private ProjectionListAST mProjectionList;
    

    /**
     * Construct a new ProjectionAttributesAST. 
     * 
     * @param aProjectionListAST a ProjectionListAST. May be null if all attributes should be projected.
     */
    public ProjectionAttributesAST(ProjectionListAST aProjectionListAST)
    {
        mProjectionList = aProjectionListAST;
    }

    /**
     * Gets the ProjectionListAST.
     *
     * @return a ProjectionListAST. May be null if all attributes should be projected.
     */
    public ProjectionListAST getProjectionList()
    {
        return mProjectionList;
    }
}
