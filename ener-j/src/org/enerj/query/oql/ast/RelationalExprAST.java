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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/RelationalExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import static org.enerj.query.oql.OQLTokens.TOK_ALL;
import static org.enerj.query.oql.OQLTokens.TOK_ANY;
import static org.enerj.query.oql.OQLTokens.TOK_EQ;
import static org.enerj.query.oql.OQLTokens.TOK_GE;
import static org.enerj.query.oql.OQLTokens.TOK_GT;
import static org.enerj.query.oql.OQLTokens.TOK_LE;
import static org.enerj.query.oql.OQLTokens.TOK_LIKE;
import static org.enerj.query.oql.OQLTokens.TOK_LT;
import static org.enerj.query.oql.OQLTokens.TOK_NE;
import static org.enerj.query.oql.OQLTokens.TOK_SOME;

import java.util.HashMap;
import java.util.Map;

import org.odmg.QueryException;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ComposeUnary;
import org.enerj.jga.fn.comparison.Equals;
import org.enerj.jga.fn.comparison.Greater;
import org.enerj.jga.fn.comparison.GreaterEqual;
import org.enerj.jga.fn.comparison.Less;
import org.enerj.jga.fn.comparison.LessEqual;
import org.enerj.jga.fn.comparison.Like;
import org.enerj.jga.fn.comparison.NotEquals;
import org.enerj.query.oql.OQLTokens;
import org.enerj.query.oql.TokenType;
import org.enerj.query.oql.fn.CollectionPredicate;
import org.enerj.query.oql.fn.StringValueOf;
import org.enerj.util.TypeUtil;


/**
 * The RealtionalExpr AST. <p>
 * 
 * @version $Id: RelationalExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class RelationalExprAST extends BaseAST
{
    /** Maps an operator's TokenType to a BinaryFunctor class. */
    private static final Map<TokenType, BinaryFunctor> FUNCTOR_MAP = new HashMap<TokenType, BinaryFunctor>();
    
    static {
        FUNCTOR_MAP.put(OQLTokens.TOK_LT, new Less.Comparable() );
        FUNCTOR_MAP.put(OQLTokens.TOK_LE, new LessEqual.Comparable() );
        FUNCTOR_MAP.put(OQLTokens.TOK_GT, new Greater.Comparable() );
        FUNCTOR_MAP.put(OQLTokens.TOK_GE, new GreaterEqual.Comparable() );
        FUNCTOR_MAP.put(OQLTokens.TOK_EQ, new Equals.Comparable() );
        FUNCTOR_MAP.put(OQLTokens.TOK_NE, new NotEquals.Comparable() );
        FUNCTOR_MAP.put(OQLTokens.TOK_LIKE, new Like() );
    }

    private AST mLeftExpr;
    private AST mRightExpr;
    private TokenType mCompositePredicate; // null, TOK_ANY (TOK_SOME is changed to TOK_ANY) or TOK_ALL
    private TokenType mOp;  // TOK_LT, LE, GT, GE, EQ, NE, LIKE
    
    

    /**
     * Construct a RelationalExprAST. 
     *
     * @param aLeftExpr
     * @param aRightExpr
     * @param aCompositePredicate one of null, TOK_SOME, TOK_ANY, or TOK_ALL.
     * @param anOp one of TOK_LT, TOK_LE, TOK_GT, TOK_GE, TOK_EQ, TOK_NE, or TOK_LIKE. If
     *  anOp is TOK_LIKE, aCompositePredicate must be null.
     */
    public RelationalExprAST(AST aLeftExpr, AST aRightExpr, TokenType aCompositePredicate, TokenType anOp)
    {
        assert (anOp == TOK_LT || anOp == TOK_LE || anOp == TOK_GT || anOp == TOK_GE || anOp == TOK_EQ || anOp == TOK_NE || anOp == TOK_LIKE) &&
               (aCompositePredicate == null || aCompositePredicate == TOK_SOME || aCompositePredicate == TOK_ANY ||aCompositePredicate == TOK_ALL) &&
               (anOp != TOK_LIKE || aCompositePredicate == null);
        
        
        if (aCompositePredicate == TOK_SOME) {
            aCompositePredicate = TOK_ANY;
        }
        
        mLeftExpr = aLeftExpr;
        mRightExpr = aRightExpr;
        mCompositePredicate = aCompositePredicate;
        mOp = anOp;
    }
    

    /**
     * Gets the mCompositePredicate.
     *
     * @return a TokenType.
     */
    public TokenType getCompositePredicate()
    {
        return mCompositePredicate;
    }
    

    /**
     * Gets the mLeftExpr.
     *
     * @return a AST.
     */
    public AST getLeftExpr()
    {
        return mLeftExpr;
    }
    

    /**
     * Gets the mOp.
     *
     * @return a TokenType.
     */
    public TokenType getOp()
    {
        return mOp;
    }
    

    /**
     * Gets the mRightExpr.
     *
     * @return a AST.
     */
    public AST getRightExpr()
    {
        return mRightExpr;
    }


    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Boolean.class;
    }


    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        Class leftType = mLeftExpr.getType();
        Class rightType = mRightExpr.getType();
        UnaryFunctor leftFunctor = mLeftExpr.resolve();
        UnaryFunctor rightFunctor = mRightExpr.resolve();

        BinaryFunctor opFunctor = FUNCTOR_MAP.get(mOp); 
        if (opFunctor == null) {
            throw new QueryException("Error creating functor for " + mOp, null);
        }

        // ANY or ALL composite predicate?
        if (TypeUtil.isScalarType(leftType) && TypeUtil.isCollectionType(rightType) && mCompositePredicate != null) {
            return new CollectionPredicate(leftFunctor, opFunctor, mCompositePredicate == TOK_ANY, rightFunctor); 
        }
        
        if (mCompositePredicate != null) {
            throw new QueryException("The " + mOp.getName() + ' ' + mCompositePredicate.getName() + " operator must have a scalar on the left-hand side and a Collection on the right hand side."); 
        }

        if (TypeUtil.isStringType(leftType) || TypeUtil.isStringType(rightType)) {
            if (mLeftExpr.getType() != String.class) {
                leftFunctor = new StringValueOf().compose(leftFunctor);
            }
            
            if (mRightExpr.getType() != String.class) {
                rightFunctor = new StringValueOf().compose(rightFunctor);
            }

            return new ComposeUnary(leftFunctor, rightFunctor, opFunctor); 
        }

        if (TypeUtil.isNumericType(leftType) && TypeUtil.isNumericType(rightType)) {
            // Numeric operation
            // Determine the rank of the type to determine promotion.
            int leftRank = TypeUtil.getRank(leftType);
            int rightRank = TypeUtil.getRank(rightType);
            Class<?> promotionType = (leftRank > rightRank) ? leftType : rightType;
            
            if (mLeftExpr.getType() != promotionType) {
                leftFunctor = TypeUtil.getNumberPromotionFunctor(promotionType).compose(leftFunctor);
            }

            if (mRightExpr.getType() != promotionType) {
                rightFunctor = TypeUtil.getNumberPromotionFunctor(promotionType).compose(rightFunctor);
            }

            return new ComposeUnary(leftFunctor, rightFunctor, opFunctor); 
        }
        

        throw new QueryException("Invalid operands to operator '" + mOp.getName() + "': left operand is " + leftType.getName() + ", right operand is " + rightType.getName());
    }
}
