// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/InExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The InExpr AST. <p>
 * 
 * @version $Id: InExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class InExprAST extends BaseAST
{
    private AST mLeftExpr;
    private AST mRightExpr;

    //--------------------------------------------------------------------------------
    /**
     * Construct a OrExprAST. 
     *
     * @param aLeftExpr
     * @param aRightExpr
     */
    public InExprAST(AST aLeftExpr, AST aRightExpr)
    {
        mLeftExpr = aLeftExpr;
        mRightExpr = aRightExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mLeftExpr.
     *
     * @return a AST.
     */
    public AST getLeftExpr()
    {
        return mLeftExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mRightExpr.
     *
     * @return a AST.
     */
    public AST getRightExpr()
    {
        return mRightExpr;
    }
    
}
