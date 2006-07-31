// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ListRangeAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The ListRange AST. <p>
 * 
 * @version $Id: ListRangeAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ListRangeAST extends BaseAST
{
    private AST mStartExpr;
    private AST mEndExpr;

    //--------------------------------------------------------------------------------
    /**
     * Construct a ListRangeAST. 
     *
     * @param aStartExpr
     * @param anEndExpr
     */
    public ListRangeAST(AST aStartExpr, AST anEndExpr)
    {
        mStartExpr = aStartExpr;
        mEndExpr = anEndExpr;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the EndExpr.
     *
     * @return an AST.
     */
    public AST getEndExpr()
    {
        return mEndExpr;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the StartExpr.
     *
     * @return an AST.
     */
    public AST getStartExpr()
    {
        return mStartExpr;
    }
}
