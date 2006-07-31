//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/BigDecimalLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.math.BigDecimal;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;

/**
 * The BigDecimalLiteral AST. <p>
 * 
 * @version $Id: BigDecimalLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class BigDecimalLiteralAST extends BaseAST
{
    private BigDecimal mValue;

    //--------------------------------------------------------------------------------
    /**
     * Construct a BigDecimalLiteralAST. 
     *
     * @param aValue
     */
    public BigDecimalLiteralAST(BigDecimal aValue)
    {
        mValue = aValue;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the value.
     *
     * @return a BigDecimal.
     */
    public BigDecimal getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return BigDecimal.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        return new ConstantUnary(mValue);
    }
}