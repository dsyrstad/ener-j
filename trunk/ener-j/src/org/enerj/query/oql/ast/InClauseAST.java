// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/InClauseAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The InClause AST. <p>
 * 
 * @version $Id: InClauseAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class InClauseAST extends BaseAST
{
    private String mIdent;
    private AST mExpr;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a InClauseAST. 
     *
     * @param anIdent
     * @param anExpr
     */
    public InClauseAST(String anIdent, AST anExpr)
    {
        mIdent = anIdent;
        mExpr = anExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Expr.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Ident.
     *
     * @return a String.
     */
    public String getIdent()
    {
        return mIdent;
    }
}
