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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ParameterListAST.java,v 1.4 2006/02/24 03:00:42 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.ArrayList;
import java.util.List;

import org.enerj.query.oql.OQLLexer;
import org.enerj.query.oql.OQLTokens;
import org.enerj.query.oql.ParserException;

/**
 * The Parmeter List AST. <p>
 * 
 * @version $Id: ParameterListAST.java,v 1.4 2006/02/24 03:00:42 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ParameterListAST extends BaseAST
{
    /** List of TypeAST - parameter type. One-to-one correspondence with mNames. */ 
    private List<TypeAST> mTypes;
    /** Parameter names - List of String. One-to-one correspondence with mTypes. */
    private List<String> mNames;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new ParameterListAST.
     * 
     * @param someTypes a List of TypeAST with a one-to-one correspondence to someNames.
     * @param someNames a List of parameter name Strings with a one-to-one correspondence to someTypes. 
     */
    public ParameterListAST(List<TypeAST> someTypes, List<String> someNames)
    {
        mTypes = someTypes;
        mNames = someNames;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the parameter names.
     * 
     * @return a List of parameter name Strings with a one-to-one correspondence to getTypes().
     */
    public List<String> getNames()
    {
        return mNames;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the parameter types.
     * 
     * @return a List of TypeAST with a one-to-one correspondence to getNames().
     */
    public List<TypeAST> getTypes()
    {
        return mTypes;
    }
}
