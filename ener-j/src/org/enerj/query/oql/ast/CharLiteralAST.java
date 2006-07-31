// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/CharLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;



/**
 * The CharLiteral AST. <p>
 * 
 * @version $Id: CharLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CharLiteralAST extends BaseAST
{
    private char mValue;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a CharLiteralAST. 
     *
     * @param aValue
     */
    public CharLiteralAST(char aValue)
    {
        mValue = aValue;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Value.
     *
     * @return a char.
     */
    public char getValue()
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Character.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        return new ConstantUnary( new Character(mValue) );
    }
}
