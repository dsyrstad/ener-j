// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/UnaryExprAST.java,v 1.4 2006/02/14 21:13:17 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ChainUnary;
import org.enerj.jga.fn.adaptor.ConstantUnary;
import org.enerj.jga.fn.arithmetic.Negate;
import org.enerj.jga.fn.logical.LogicalNot;

import org.odmg.QueryException;
import org.odmg.QueryInvalidException;
import org.enerj.query.oql.OQLTokens;
import org.enerj.query.oql.TokenType;
import org.enerj.query.oql.fn.AbsoluteValueOf;
import org.enerj.util.TypeUtil;


/**
 * The UnaryExpr AST. <p>
 * 
 * @version $Id: UnaryExprAST.java,v 1.4 2006/02/14 21:13:17 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class UnaryExprAST extends BaseAST
{
    private AST mExpr;
    private TokenType mOp;  // TOK_PLUS, MINUS, ABS, NOT
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a UnaryExprAST. 
     *
     * @param anExpr
     * @param anOp
     */
    public UnaryExprAST(AST anExpr, TokenType anOp)
    {
        assert anOp == OQLTokens.TOK_PLUS || anOp == OQLTokens.TOK_MINUS || anOp == OQLTokens.TOK_ABS || anOp == OQLTokens.TOK_NOT;
        mExpr = anExpr;
        mOp = anOp;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mExpr.
     *
     * @return a AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mOp.
     *
     * @return a TokenType.
     */
    public TokenType getOp()
    {
        return mOp;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        Class type = mExpr.getType();
        
        if (TypeUtil.isNumericType(type) && 
            (mOp == OQLTokens.TOK_PLUS || mOp == OQLTokens.TOK_MINUS || mOp == OQLTokens.TOK_ABS)) {
            // Numeric operation
            return type;
        }
        
        if (type == Boolean.class && mOp == OQLTokens.TOK_NOT) {
            return Boolean.class;
        }

        throw new QueryException("Invalid operand to operator '" + mOp.getName() + "': operand is " + type.getName());
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        Class resultType = getType();
        
        UnaryFunctor functor = mExpr.resolve();

        if (TypeUtil.isNumericType(resultType)) {
            // Numeric operation.
            if (mOp == OQLTokens.TOK_PLUS) {
                return functor; // Nothing more to do.
            }
            
            if (mOp == OQLTokens.TOK_MINUS) {
                return new Negate(resultType).compose(functor);
            }
            
            if (mOp == OQLTokens.TOK_ABS) {
                return new AbsoluteValueOf(resultType).compose(functor) ;
            }
            else {
                throw new QueryInvalidException("Unknown numeric unary operator: " + mOp);
            }
        }
        
        if (resultType == Boolean.class) {
            // Boolean "NOT" operation.
            return new ChainUnary( new LogicalNot(), functor);
        }

        throw new QueryException("Invalid operand to operator."); // Shouldn't really get here if getType0() does it's job.
    }
}
