//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/DoubleLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;

/**
 * The DoubleLiteral AST. <p>
 * 
 * @version $Id: DoubleLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class DoubleLiteralAST extends BaseAST
{
    private double mValue;

    //--------------------------------------------------------------------------------
    /**
     * Construct a DoubleLiteralAST. 
     *
     * @param aValue
     */
    public DoubleLiteralAST(double aValue)
    {
        mValue = aValue;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the mValue.
     *
     * @return a double.
     */
    public double getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Double.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        return new ConstantUnary( new Double(mValue) );
    }
}