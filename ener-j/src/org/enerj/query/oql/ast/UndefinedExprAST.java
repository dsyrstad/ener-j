//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/UndefinedExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.query.oql.TokenType;

/**
 * The UndefinedExpr AST. <p>
 * 
 * @version $Id: UndefinedExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class UndefinedExprAST extends BaseAST
{
    private TokenType mOp;
    private AST mExpr;

    //--------------------------------------------------------------------------------
    /**
     * Construct a UndefinedExprAST. 
     *
     * @param anOp
     * @param anExpr
     */
    public UndefinedExprAST(TokenType anOp, AST anExpr)
    {
        mOp = anOp;
        mExpr = anExpr;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Expr.
     *
     * @return a AST.
     */
    public AST getExpr()
    {
        return mExpr;
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
}