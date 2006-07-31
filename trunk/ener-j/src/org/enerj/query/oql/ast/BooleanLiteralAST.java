// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/BooleanLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;



/**
 * The BooleanLiteral AST. <p>
 * 
 * @version $Id: BooleanLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class BooleanLiteralAST extends BaseAST
{
    private boolean mValue;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a BooleanLiteralAST. 
     *
     * @param aValue
     */
    public BooleanLiteralAST(boolean aValue)
    {
        mValue = aValue;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mValue.
     *
     * @return a boolean.
     */
    public boolean isValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Boolean.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        return new ConstantUnary( new Boolean(mValue) );
    }
}
