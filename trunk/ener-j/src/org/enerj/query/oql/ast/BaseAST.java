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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/BaseAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;
import org.enerj.query.oql.TokenType;
import org.enerj.util.StringUtil;



/**
* The Base AST. Implements common functionality for all ASTs. <p>
* 
* @version $Id: BaseAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
* @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
*/
abstract public class BaseAST implements AST
{
    /** Cached expression functor returned by resolve0(). */
    private UnaryFunctor mASTExprFunctor = null;
    /** Cached type returned by getType0(). */
    private Class mASTType = null;
    

    /** 
     * {@inheritDoc}
     * Calls sub-class' resolve0() method if this AST has not been resolved yet.
     * 
     * @see org.enerj.query.oql.ast.AST#resolve()
     */
    public UnaryFunctor resolve() throws QueryException
    {
        if (mASTExprFunctor == null) {
            mASTExprFunctor = resolve0();
        }
        
        return mASTExprFunctor;
    }
    

    /**
     * @see #resolve().
     */
    /* TODO abstract */ protected UnaryFunctor resolve0() throws QueryException 
    {
        return new ConstantUnary("resolve0 not implemented for AST: " + this.getClass() );
    }
    

    /** 
     * {@inheritDoc}
     * Calls sub-class' getType0() method if it hasn't been called yet.
     * 
     * @see org.enerj.query.oql.ast.AST#getType()
     */
    public Class getType() throws QueryException
    {
        if (mASTType == null) {
            mASTType = getType0();
        }
        
        return mASTType;
    }


    /** 
     * @see #getType().
     */
    /* TODO abstract*/ protected Class getType0() throws QueryException
    {
        return String.class;
    }


    /**
     * Returns a recusive String representation of this AST. 
     */
    public String toString() 
    {
        String result = StringUtil.toString(this, false, false);
        // Clean out package names that we don't need to see.
        result = result.replaceAll('(' + this.getClass().getPackage().getName() + "\\.)" +
                "|(" + TokenType.class.getPackage().getName() + "\\.)" +
                "|(java\\.util\\.ArrayList)" +
                "|(,? *\\.mASTExprFunctor=[^,}]* *)" +
                "|(,? *\\.mASTType=[^,}]* *)" +
                "|(TextPosition\\{mFileName[^\\}]*\\})", "");
        
        result = result.replaceAll("\\{, *", "{");
        return result;
    }
}
