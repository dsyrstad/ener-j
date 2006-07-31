//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/TimestampLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.sql.Timestamp;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;
import org.odmg.QueryInvalidException;

/**
 * The TimestampLiteral AST. <p>
 * 
 * @version $Id: TimestampLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TimestampLiteralAST extends BaseAST
{
    private String mValue;

    //--------------------------------------------------------------------------------
    /**
     * Construct a TimestampLiteralAST. 
     *
     * @param someValue a timestamp string in the format "yyyy-mm-dd HH:MM:SS[.fffffffff]". 
     *  The ".fffffffff" represents nanoseconds and is optional.
     */
    public TimestampLiteralAST(String someValue)
    {
        mValue = someValue;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Value.
     *
     * @return a String.
     */
    public String getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Timestamp.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        try {
            return new ConstantUnary( Timestamp.valueOf(mValue) );
        }
        catch (IllegalArgumentException e) {
            throw new QueryInvalidException("Invalid timestamp: " + mValue);
        }
    }
}