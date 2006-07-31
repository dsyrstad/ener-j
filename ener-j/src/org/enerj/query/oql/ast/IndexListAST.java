// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/IndexListAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;


/**
 * The IndexList AST. <p>
 * 
 * @version $Id: IndexListAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IndexListAST extends BaseAST
{
    private List mIndicies;
    
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IndexListAST. 
     *
     * @param someIndicies
     */
    public IndexListAST(List someIndicies)
    {
        mIndicies = someIndicies;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Indicies.
     *
     * @return a List of AST.
     */
    public List getIndicies()
    {
        return mIndicies;
    }
}
