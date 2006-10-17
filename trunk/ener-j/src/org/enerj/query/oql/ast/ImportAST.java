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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ImportAST.java,v 1.5 2006/02/21 02:37:47 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.EvaluatorContext;

/**
 * The Import AST. <p/>
 * NOTE: We allow wildcards at the end of the import, e.g. "import java.util.*". <p/>
 * 
 * @version $Id: ImportAST.java,v 1.5 2006/02/21 02:37:47 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ImportAST extends BaseAST
{
    /** LinkedList of QueryAST and/or DeclarationAST. */ 
    private QualifiedNameAST mQualifiedName;
    private String mAlias;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new ImportAST. 
     */
    public ImportAST(QualifiedNameAST aQualifiedName, String anAlias)
    {
        mQualifiedName = aQualifiedName;
        mAlias = anAlias;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the qualified name of this import.
     * 
     * @return a QualifiedNameAST.
     */
    public QualifiedNameAST getQualifiedNameAST()
    {
        return mQualifiedName;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the import alias, if defined.
     * 
     * @return the import alias name, or null if not defined.
     */
    public String getAlias()
    {
        return mAlias;
    }

    //--------------------------------------------------------------------------------
    protected Class getType0() throws QueryException
    {
        // resolve0() does all of the work. 
        return null;
    }

    
    //--------------------------------------------------------------------------------
    protected UnaryFunctor resolve0() throws QueryException
    {
        EvaluatorContext.getContext().addImport(mQualifiedName.getQualifiedName(), mAlias);
        return null; 
    }
}
