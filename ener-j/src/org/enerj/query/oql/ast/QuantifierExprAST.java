// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/QuantifierExprAST.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The Universal or Existential QuantifierExpr AST. <p>
 * 
 * @version $Id: QuantifierExprAST.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QuantifierExprAST extends BaseAST
{
    private AST mInClause;
    private AST mRightExpr;
    private boolean mIsUniversal;

    //--------------------------------------------------------------------------------
    /**
     * Construct a OrExprAST. 
     *
     * @param anInClause
     * @param aRightExpr
     * @param isUniversal
     */
    public QuantifierExprAST(AST anInClause, AST aRightExpr, boolean isUniversal)
    {
        mInClause = anInClause;
        mRightExpr = aRightExpr;
        mIsUniversal = isUniversal;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mInClause.
     *
     * @return a AST.
     */
    public AST getInClause()
    {
        return mInClause;
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
