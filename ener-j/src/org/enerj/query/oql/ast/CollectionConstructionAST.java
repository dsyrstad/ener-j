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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/CollectionConstructionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import static org.enerj.query.oql.OQLTokens.TOK_ARRAY;
import static org.enerj.query.oql.OQLTokens.TOK_BAG;
import static org.enerj.query.oql.OQLTokens.TOK_LIST;
import static org.enerj.query.oql.OQLTokens.TOK_SET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.TokenType;
import org.enerj.query.oql.fn.ConvertToCollection;

/**
 * The CollectionConstruction AST. <p>
 * 
 * @version $Id: CollectionConstructionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CollectionConstructionAST extends BaseAST
{
    private TokenType mType; // TOK_ARRAY, LIST, BAG, SET
    private AST mValues;
    

    /**
     * Construct a CollectionConstructionAST. 
     *
     * @param aType the collection type, one of TOK_ARRAY, TOK_LIST, TOK_BAG, or TOK_SET.
     * @param someValues a ValueListAST. May be null for an empty list.
     */
    public CollectionConstructionAST(TokenType aType, AST someValues)
    {
        assert aType == TOK_ARRAY || aType == TOK_LIST || aType == TOK_BAG || aType == TOK_SET;
        assert someValues == null || someValues instanceof ValueListAST;
        mType = aType;
        mValues = someValues;
        if (mValues == null) {
            mValues = new ValueListAST( new ArrayList(0) );
        }
    }
    

    /**
     * Gets the Type.
     *
     * @return a TokenType representing the collection type (one of TOK_ARRAY, TOK_LIST, TOK_BAG, or TOK_SET).
     */
    public TokenType getTypeAST()
    {
        return mType;
    }
    

    /**
     * Gets the Values.
     *
     * @return an AST.
     */
    public AST getValues()
    {
        return mValues;
    }

    

    /** 
     * 
     * {@inheritDoc}
     * @see org.enerj.query.oql.ast.BaseAST#getType0()
     */
    protected Class getType0() throws QueryException
    {
        if (mType == TOK_ARRAY || mType == TOK_LIST) {
            return List.class;
        }
        
        if (mType == TOK_BAG) {
            return Collection.class;
        }
            
        // if (mType == TOK_SET) 
        return Set.class;
    }

    

    /** 
     * {@inheritDoc}
     * @see org.enerj.query.oql.ast.BaseAST#resolve0()
     */
    protected UnaryFunctor resolve0() throws QueryException
    {
        return new ConvertToCollection( getType(), mValues.resolve() );
    }
}
