// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/SortCriteriaAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.ArrayList;
import java.util.List;

import org.enerj.query.oql.OQLLexer;
import org.enerj.query.oql.OQLTokens;
import org.enerj.query.oql.ParserException;


/**
 * The SortCriteria AST. <p>
 * 
 * @version $Id: SortCriteriaAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SortCriteriaAST extends BaseAST
{
    private List mCriteria;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a SortCriteriaAST. 
     *
     * @param someCriteria
     */
    public SortCriteriaAST(List someCriteria)
    {
        mCriteria = someCriteria;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the criteria.
     *
     * @return a List of SortCriterionAST.
     */
    public List getCriteria()
    {
        return mCriteria;
    }
}
