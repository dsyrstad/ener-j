// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/SortCriterionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The SortCriterion AST. <p>
 * 
 * @version $Id: SortCriterionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SortCriterionAST extends BaseAST
{
    private AST mExpr;
    private boolean mAscending;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a SortCriterionAST. 
     *
     * @param anExpr
     * @param isAscending
     */
    public SortCriterionAST(AST anExpr, boolean isAscending)
    {
        mExpr = anExpr;
        mAscending = isAscending;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the ascending flag.
     *
     * @return true if the criterion should be sorted in ascending order. Otherwise descending order is used.
     */
    public boolean isAscending()
    {
        return mAscending;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the expression.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
}
