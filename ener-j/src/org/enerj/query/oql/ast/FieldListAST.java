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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/FieldListAST.java,v 1.5 2006/02/16 21:33:44 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.fn.FunctorUtil;


/**
 * The FieldList AST. <p>
 * 
 * @version $Id: FieldListAST.java,v 1.5 2006/02/16 21:33:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FieldListAST extends BaseAST
{
    private List<FieldAST> mFields;


    /**
     * Construct a FieldListAST. 
     *
     * @param someFields
     */
    public FieldListAST(List<FieldAST> someFields)
    {
        mFields = someFields;
    }
    

    /**
     * Gets the fields.
     *
     * @return a List of FieldAST.
     */
    public List<FieldAST> getFields()
    {
        return mFields;
    }

    

    /**
     * Like resolve(), only adds additional conversion functors so that the values
     * evaluate to the types specified by someTargetTypes. 
     *
     * @param someTargetTypes the desired value types.
     * 
     * @return the resolved UnaryFunctor.
     * 
     * @throws QueryException if an error occurs.
     */
    public UnaryFunctor resolveAgainstTypes(Class[] someTargetTypes) throws QueryException
    {
        return FunctorUtil.resolveAgainstTypes(mFields, someTargetTypes);
    }
}
