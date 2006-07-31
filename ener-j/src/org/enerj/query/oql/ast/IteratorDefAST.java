// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/IteratorDefAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The IteratorDef AST. <p>
 * 
 * @version $Id: IteratorDefAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IteratorDefAST extends BaseAST
{
    private AST mExpr;
    private String mAlias;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IteratorDefAST. 
     *
     * @param anExpr
     * @param anAlias the alias, may be null.
     */
    public IteratorDefAST(AST anExpr, String anAlias)
    {
        mExpr = anExpr;
        mAlias = anAlias;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the alias.
     *
     * @return a String. May be null if no alias was defined.
     */
    public String getAlias()
    {
        return mAlias;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the expression.
     *
     * @return an ExprAST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
}
