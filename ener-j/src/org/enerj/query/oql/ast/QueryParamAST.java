// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/QueryParamAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;




/**
 * The QueryParam AST. <p>
 * 
 * @version $Id: QueryParamAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QueryParamAST extends BaseAST
{
    private AST mIndex;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a QueryParamAST. 
     *
     * @param anIndex the parameter number as an AST.
     */
    public QueryParamAST(AST anIndex)
    {
        mIndex = anIndex;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Index.
     *
     * @return an AST representing the parameter number.
     */
    public AST getIndex()
    {
        return mIndex;
    }
}
