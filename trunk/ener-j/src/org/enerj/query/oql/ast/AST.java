// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/AST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.jga.fn.UnaryFunctor;

import org.odmg.QueryException;


/**
 * Abstract Syntax Tree interface. All ASTs must implement this interface.
 * 
 * @version $Id: AST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public interface AST
{
    //--------------------------------------------------------------------------------
    /**
     * Gets the the type that this AST will evaluate to.
     * 
     * @return the Class representing the type of this AST. May be null if the AST
     *  is purely declarative. 
     * 
     * @throws QueryException if the query is not valid.
     */
    public Class getType() throws QueryException;

    //--------------------------------------------------------------------------------
    /**
     * Recursively resolves references and validates non-synctactical rules. Also creates
     * a unary functor that can be used to execute the expression.
     * 
     * @return a UnaryFunctor representing the expression. May be null if the AST
     *  is purely declarative. 
     * 
     * @throws QueryException if the query is not valid.
     */
    public UnaryFunctor resolve() throws QueryException;

}