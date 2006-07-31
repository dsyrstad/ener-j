// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ProjectionAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The Projection AST. <p>
 * 
 * @version $Id: ProjectionAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ProjectionAST extends BaseAST
{
    private AST mExpr;
    private String mAlias;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ProjectionAST. 
     *
     * @param anExpr the expression to project.
     * @param anAlias the alias name for the projection. May be null.
     */
    public ProjectionAST(AST anExpr, String anAlias)
    {
        mExpr = anExpr;
        mAlias = anAlias;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the mAlias.
     *
     * @return a String.
     */
    public String getAlias()
    {
        return mAlias;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the mExpr.
     *
     * @return a ExprAST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
}
