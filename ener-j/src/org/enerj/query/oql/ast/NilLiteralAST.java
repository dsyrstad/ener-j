// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/NilLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;



/**
 * The NilLiteral AST. <p>
 * 
 * @version $Id: NilLiteralAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class NilLiteralAST extends BaseAST
{
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new NilLiteralAST. 
     */
    public NilLiteralAST()
    {
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        return Object.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        return new ConstantUnary(null);
    }
}
