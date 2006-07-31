// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/LongLiteralAST.java,v 1.4 2005/10/29 21:21:34 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;



/**
 * The LongLiteral AST. <p>
 * 
 * @version $Id: LongLiteralAST.java,v 1.4 2005/10/29 21:21:34 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class LongLiteralAST extends BaseAST
{
    private long mValue;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a LongLiteralAST. 
     *
     * @param aValue
     */
    public LongLiteralAST(long aValue)
    {
        mValue = aValue;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mValue.
     *
     * @return a long.
     */
    public long getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
    	if (mValue >= Integer.MIN_VALUE && mValue <= Integer.MAX_VALUE) {
    		return Integer.class;
    	}
    	
        return Long.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
    	if (mValue >= Integer.MIN_VALUE && mValue <= Integer.MAX_VALUE) {
            return new ConstantUnary( new Integer((int)mValue) );
    	}
    	
        return new ConstantUnary( new Long(mValue) );
    }
}
