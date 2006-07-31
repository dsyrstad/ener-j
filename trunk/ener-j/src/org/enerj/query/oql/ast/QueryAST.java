// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/QueryAST.java,v 1.6 2005/11/05 21:26:24 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.Collection;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.fn.ExtentFunctor;

/**
 * The Query AST. <p>
 * 
 * @version $Id: QueryAST.java,v 1.6 2005/11/05 21:26:24 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QueryAST extends BaseAST
{
    private AST mChildAST;
    
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new QueryAST.
     * 
     * @param aChildAST the child AST. 
     */
    public QueryAST(AST aChildAST)
    {
        mChildAST = aChildAST;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the child AST.
     * 
     * @return the child AST.
     */
    public AST getChildAST()
    {
        return mChildAST;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        Class type = mChildAST.getType();
        
        if (type == Class.class) {
            // Querying on a Class type means querying on an entire extent.
            return Collection.class;
        }
        
        return type;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        Class type = getType();
        
        if (mChildAST.getType() == Class.class) {
            // Querying on a Class type means querying on an entire extent.
            return new ExtentFunctor().compose( mChildAST.resolve() );
        }

        return mChildAST.resolve();
    }
}
