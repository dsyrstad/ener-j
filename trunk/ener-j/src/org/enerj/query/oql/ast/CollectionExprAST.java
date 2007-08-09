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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/CollectionExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import static org.enerj.query.oql.OQLTokens.TOK_EXISTS;
import static org.enerj.query.oql.OQLTokens.TOK_FIRST;
import static org.enerj.query.oql.OQLTokens.TOK_LAST;
import static org.enerj.query.oql.OQLTokens.TOK_UNIQUE;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.TokenType;
import org.enerj.query.oql.fn.ElementExistsPredicate;
import org.enerj.query.oql.fn.FirstLastElementFunctor;
import org.enerj.util.TypeUtil;

/**
 * The CollectionExpr AST. Handles psuedo functions:<br>
 * first(query) - return the first element of a list/array<br>
 * last(query) - return the last element of a list/array<br>
 * unique(query) - returns true if the collection or map (non-OQL) has at least one element<br>
 * exists(query) - returns true if the collection or map (non-OQL) has exactly one element<p>
 * 
 * @version $Id: CollectionExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CollectionExprAST extends BaseAST
{
    private TokenType mOp; // TOK_FIRST, TOK_LAST, TOK_UNIQUE, TOK_EXISTS
    private AST mExpr;


    /**
     * Construct a CollectionExprAST. 
     *
     * @param anOp
     * @param anExpr
     */
    public CollectionExprAST(TokenType anOp, AST anExpr)
    {
        assert anOp == TOK_FIRST || anOp == TOK_LAST || anOp == TOK_UNIQUE || anOp == TOK_EXISTS;
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


    protected Class getType0() throws QueryException
    {
        Class exprType = mExpr.getType();
        if (mOp == TOK_UNIQUE || mOp == TOK_EXISTS) {
            // Must be a collection, map, or array for exists/unique.
            if (exprType.isArray() || Collection.class.isAssignableFrom(exprType) || Map.class.isAssignableFrom(exprType)) {
                return Boolean.class;
            }

            throw new QueryException("Parameter to " + mOp.getName() + "() must be a Collection, Map, or an array."); 
        }

        // TOK_FIRST || TOK_LAST
        // mExpr must be a list or array for first/last
        if (exprType.isArray()) {
            return exprType.getComponentType();
        }

        if (List.class.isAssignableFrom(exprType)) {
            // Component type unknown.
            return Object.class;
        }

        throw new QueryException("Parameter to " + mOp.getName() + "() must be a List or an array."); 
    }

    

    protected UnaryFunctor resolve0() throws QueryException
    {
        UnaryFunctor exprFunctor = mExpr.resolve();
        
        if (mOp == TOK_UNIQUE) {
            return ElementExistsPredicate.UNIQUE_INSTANCE.compose(exprFunctor); 
        }
    
        if (mOp == TOK_EXISTS) {
            return ElementExistsPredicate.EXISTS_INSTANCE.compose(exprFunctor); 
        }

        if (mOp == TOK_FIRST) {
            return FirstLastElementFunctor.FIRST_INSTANCE.compose(exprFunctor);
        }
        
        // TOK_LAST
        return FirstLastElementFunctor.LAST_INSTANCE.compose(exprFunctor);
    }
}
