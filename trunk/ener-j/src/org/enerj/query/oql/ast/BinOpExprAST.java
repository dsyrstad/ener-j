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
// Copyright 2001-2005 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/BinOpExprAST.java,v 1.4 2006/02/16 21:33:44 dsyrstad Exp $

package org.enerj.query.oql.ast;

import static org.enerj.query.oql.OQLTokens.TOK_AND;
import static org.enerj.query.oql.OQLTokens.TOK_ANDTHEN;
import static org.enerj.query.oql.OQLTokens.TOK_ASTERISK;
import static org.enerj.query.oql.OQLTokens.TOK_CONCAT;
import static org.enerj.query.oql.OQLTokens.TOK_DIV;
import static org.enerj.query.oql.OQLTokens.TOK_EXCEPT;
import static org.enerj.query.oql.OQLTokens.TOK_INTERSECT;
import static org.enerj.query.oql.OQLTokens.TOK_MINUS;
import static org.enerj.query.oql.OQLTokens.TOK_MOD;
import static org.enerj.query.oql.OQLTokens.TOK_OR;
import static org.enerj.query.oql.OQLTokens.TOK_ORELSE;
import static org.enerj.query.oql.OQLTokens.TOK_PLUS;
import static org.enerj.query.oql.OQLTokens.TOK_UNION;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.odmg.DBag;
import org.odmg.DSet;
import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ComposeUnary;
import org.enerj.jga.fn.adaptor.ConditionalUnary;
import org.enerj.jga.fn.adaptor.ConstantUnary;
import org.enerj.jga.fn.arithmetic.Divides;
import org.enerj.jga.fn.arithmetic.Minus;
import org.enerj.jga.fn.arithmetic.Modulus;
import org.enerj.jga.fn.arithmetic.Multiplies;
import org.enerj.jga.fn.arithmetic.Plus;
import org.enerj.query.oql.TokenType;
import org.enerj.query.oql.fn.DifferenceFunctor;
import org.enerj.query.oql.fn.FunctorUtil;
import org.enerj.query.oql.fn.IntersectFunctor;
import org.enerj.query.oql.fn.ListConcat;
import org.enerj.query.oql.fn.StringConcat;
import org.enerj.query.oql.fn.StringValueOf;
import org.enerj.query.oql.fn.UnionFunctor;
import org.enerj.util.TypeUtil;

/**
 * The BinOpExpr AST. <p>
 * 
 * @version $Id: BinOpExprAST.java,v 1.4 2006/02/16 21:33:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class BinOpExprAST extends BaseAST
{
    /** Maps an operator's TokenType to a UnaryFunctor class. */
    private static final Map<TokenType, Class<?>> FUNCTOR_MAP = new HashMap<TokenType, Class<?>>();
    
    static {
        FUNCTOR_MAP.put(TOK_PLUS, Plus.class);
        FUNCTOR_MAP.put(TOK_MINUS, Minus.class);
        FUNCTOR_MAP.put(TOK_ASTERISK, Multiplies.class);
        FUNCTOR_MAP.put(TOK_DIV, Divides.class);
        FUNCTOR_MAP.put(TOK_MOD, Modulus.class);

        FUNCTOR_MAP.put(TOK_UNION, null);
        FUNCTOR_MAP.put(TOK_EXCEPT, null);
        FUNCTOR_MAP.put(TOK_INTERSECT, null);
    }

    private AST mLeftExpr;
    private AST mRightExpr;
    private TokenType mOp;
    
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a BinOpExprAST. 
     *
     * @param aLeftExpr
     * @param aRightExpr
     * @param anOp the operator, represented as a token type.
     */
    public BinOpExprAST(AST aLeftExpr, AST aRightExpr, TokenType anOp)
    {
        assert  anOp == TOK_OR || anOp == TOK_ORELSE || anOp == TOK_AND || anOp == TOK_ANDTHEN || 
                anOp == TOK_PLUS || anOp == TOK_MINUS || anOp == TOK_UNION || anOp == TOK_EXCEPT || 
                anOp == TOK_CONCAT || anOp == TOK_ASTERISK || anOp == TOK_DIV || anOp == TOK_MOD || 
                anOp == TOK_INTERSECT; 

        mLeftExpr = aLeftExpr;
        mRightExpr = aRightExpr;
        mOp = anOp;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Left Expr.
     *
     * @return an AST.
     */
    public AST getLeftExpr()
    {
        return mLeftExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Op.
     *
     * @return a TokenType.
     */
    public TokenType getOp()
    {
        return mOp;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Right Expr.
     *
     * @return an AST.
     */
    public AST getRightExpr()
    {
        return mRightExpr;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        Class leftType = mLeftExpr.getType();
        Class rightType = mRightExpr.getType();
        
        if ((leftType == String.class || leftType == Character.class) && 
            (mOp == TOK_PLUS || mOp == TOK_CONCAT)) {
            return String.class;
        }
        
        if (TypeUtil.isNumericType(leftType) && TypeUtil.isNumericType(rightType) &&
            (mOp == TOK_PLUS || mOp == TOK_MINUS || mOp == TOK_ASTERISK || 
             mOp == TOK_DIV || mOp == TOK_MOD)) {
            // Numeric operation
            // Determine the rank of the type to determine promotion.
            int leftRank = TypeUtil.getRank(leftType);
            int rightRank = TypeUtil.getRank(rightType);
            return (leftRank > rightRank) ? leftType : rightType; 
        }
        
        // '+' is valid on two Lists or arrays.
        if (mOp == TOK_PLUS && 
            ((List.class.isAssignableFrom(leftType) && List.class.isAssignableFrom(rightType) ) ||
             (leftType.isArray() && rightType.isArray()) ) ) {
            return leftType;
        }
        
        if (mOp == TOK_UNION || mOp == TOK_INTERSECT || mOp == TOK_EXCEPT) {
            if (Set.class.isAssignableFrom(leftType) && Set.class.isAssignableFrom(rightType)) {
                return DSet.class;
            }
            
            if (Collection.class.isAssignableFrom(leftType) && Collection.class.isAssignableFrom(rightType)) {
                return DBag.class;
            }
            
            // else Fall through to exception. 
        }
        
        if (leftType == Boolean.class && rightType == Boolean.class &&
            (mOp == TOK_AND || mOp == TOK_OR || mOp == TOK_ORELSE || mOp == TOK_ANDTHEN)) {
            return Boolean.class;
        }

        throw new QueryException("Invalid operands to operator '" + mOp.getName() + "': left operand is " + leftType.getName() + ", right operand is " + rightType.getName());
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        Class resultType = getType();
        
        UnaryFunctor leftFunctor = mLeftExpr.resolve();
        UnaryFunctor rightFunctor = mRightExpr.resolve();

        if (resultType == String.class) {
            if (mLeftExpr.getType() != String.class) {
                leftFunctor = new StringValueOf().compose(rightFunctor);
            }
            
            // String Concatenation. ODMG 4.10.6.3 only allows String + String, we allow String + {Object|null}.
            if (mRightExpr.getType() != String.class) {
                rightFunctor = new StringValueOf().compose(rightFunctor);
            }

            return new ComposeUnary(leftFunctor, rightFunctor, new StringConcat()); 
        }

        if (mOp == TOK_PLUS && (List.class.isAssignableFrom(resultType) || resultType.isArray())) {
            return new ComposeUnary(leftFunctor, rightFunctor, ListConcat.INSTANCE);
        }
        
        if (TypeUtil.isNumericType(resultType)) {
            // Numeric operation.
            if (mLeftExpr.getType() != resultType) {
                leftFunctor = TypeUtil.getNumberPromotionFunctor(resultType).compose(leftFunctor);
            }

            if (mRightExpr.getType() != resultType) {
                rightFunctor = TypeUtil.getNumberPromotionFunctor(resultType).compose(rightFunctor);
            }
            
            Class<?> functorClass = FUNCTOR_MAP.get(mOp);
            assert functorClass != null;
            try {
                return new ComposeUnary(leftFunctor, rightFunctor, 
                                FunctorUtil.createBinaryFunctor(functorClass, resultType) );
            }
            catch (IllegalArgumentException e) {
                throw new QueryException("Error creating functor for " + mOp, e);
            }
        }
        
        if (resultType == Boolean.class) {
            // Boolean operation.
            // For ANDTHEN and ORELSE, ComposeUnary cannot be used because it will evaluate both sides before the 
            // operation is performed. So we just treat "and" and "andthen" the same.
            if (mOp == TOK_AND || mOp == TOK_ANDTHEN) {
                return new ConditionalUnary(leftFunctor, rightFunctor, new ConstantUnary(Boolean.FALSE) );
            }

            // else TOK_OR || TOK_ORELSE
            return new ConditionalUnary(leftFunctor, new ConstantUnary(Boolean.TRUE), rightFunctor);
        }

        if (mOp == TOK_UNION) {
            return new ComposeUnary(leftFunctor, rightFunctor, UnionFunctor.INSTANCE);
        }
        
        if (mOp == TOK_INTERSECT) {
            return new ComposeUnary(leftFunctor, rightFunctor, IntersectFunctor.INSTANCE);
        }
        
        if (mOp == TOK_EXCEPT) {
            return new ComposeUnary(leftFunctor, rightFunctor, DifferenceFunctor.INSTANCE);
        }

        throw new QueryException("Invalid operands to operator."); // Shouldn't really get here if getType0() does it's job.
    }
}
