// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/FromClauseAST.java,v 1.4 2005/11/05 21:26:24 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;


/**
 * The FromClause AST. <p>
 * 
 * @version $Id: FromClauseAST.java,v 1.4 2005/11/05 21:26:24 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FromClauseAST extends BaseAST
{
    private List<IteratorDefAST> mIteratorDefs;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a FromClauseAST. 
     *
     * @param someIteratorDefs a List of IterationDefAST. Must contain at least one element.
     */
    public FromClauseAST(List<IteratorDefAST> someIteratorDefs)
    {
        assert someIteratorDefs.size() > 0;
        
        mIteratorDefs = someIteratorDefs;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the IteratorDefs.
     *
     * @return a List of IteratorDefAST, guaranteed to have at least one element.
     */
    public List<IteratorDefAST> getIteratorDefs()
    {
        return mIteratorDefs;
    }
}
