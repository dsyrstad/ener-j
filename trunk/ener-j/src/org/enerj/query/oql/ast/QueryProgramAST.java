// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/QueryProgramAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ApplyUnary;
import org.enerj.jga.fn.adaptor.ConstantUnary;

import org.odmg.QueryException;

/**
 * The QueryProgram AST. <p>
 * 
 * @version $Id: QueryProgramAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QueryProgramAST extends BaseAST
{
    /** List of QueryAST and/or DeclarationAST. */ 
    private List mQueriesAndDeclarations = new ArrayList(10);
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new QueryProgramAST. 
     */
    public QueryProgramAST(List someQueriesAndDeclarations)
    {
        mQueriesAndDeclarations = someQueriesAndDeclarations;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the list of queries and declarations for this query program.
     * 
     * @return  a List of QueryAST and/or DeclartionAST. Returns an empty list if 
     *  none exist.
     */
    public List getQueriesAndDeclartions()
    {
        return mQueriesAndDeclarations;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        // This always returns a List of results.
        return List.class;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        List<UnaryFunctor> functors = new ArrayList<UnaryFunctor>( mQueriesAndDeclarations.size() );
        int i = 0;
        for (Iterator iter = mQueriesAndDeclarations.iterator(); iter.hasNext(); ++i) { 
            AST ast = (AST)iter.next();
            UnaryFunctor functor = ast.resolve();
            if (functor != null) {
                functors.add(functor);
            }
        }

        if (functors.isEmpty()) {
            return null;
        }
        
        UnaryFunctor[] fns = new UnaryFunctor[ functors.size() ];
        fns = functors.toArray(fns);
        return new ApplyUnary(fns);
    }
}
