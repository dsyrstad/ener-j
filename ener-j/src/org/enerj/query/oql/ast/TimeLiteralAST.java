//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/TimeLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;
import org.odmg.QueryInvalidException;

/**
 * The TimeLiteral AST. <p>
 * 
 * @version $Id: TimeLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TimeLiteralAST extends BaseAST
{
    private String mValue;

    //--------------------------------------------------------------------------------
    /**
     * Construct a TimeLiteralAST. 
     *
     * @param someValue a time string in the format of "hh:mm:ss".
     */
    public TimeLiteralAST(String someValue)
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
        return Time.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        try {
            return new ConstantUnary( Time.valueOf(mValue) );
        }
        catch (Exception e) {
            throw new QueryInvalidException("Invalid time: " + mValue);
        }
    }
}