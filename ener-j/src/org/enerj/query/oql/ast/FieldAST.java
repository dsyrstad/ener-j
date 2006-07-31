// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/FieldAST.java,v 1.4 2006/02/19 01:20:33 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;



/**
 * The Field AST. <p>
 * 
 * @version $Id: FieldAST.java,v 1.4 2006/02/19 01:20:33 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FieldAST extends BaseAST
{
    private String mFieldName;
    private AST mExpr;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a FieldAST. 
     *
     * @param aFieldName
     * @param anExpr
     */
    public FieldAST(String aFieldName, AST anExpr)
    {
        mFieldName = aFieldName;
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
     * Gets the FieldName.
     *
     * @return a String.
     */
    public String getFieldName()
    {
        return mFieldName;
    }

    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return mExpr.getType();
    }

    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException
    {
        return mExpr.resolve();
    }
}
