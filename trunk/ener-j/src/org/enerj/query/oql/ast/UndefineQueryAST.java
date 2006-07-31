// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/UndefineQueryAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.LinkedList;
import java.util.List;

import org.enerj.query.oql.OQLLexer;
import org.enerj.query.oql.OQLTokens;
import org.enerj.query.oql.ParserException;

/**
 * The UndefineQuery AST. <p>
 * 
 * @version $Id: UndefineQueryAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class UndefineQueryAST extends BaseAST
{
    private String mQueryName;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new UndefineQueryAST.
     * 
     * @param aQueryName the name of the query to be undefined. 
     */
    public UndefineQueryAST(String aQueryName)
    {
        mQueryName = aQueryName;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the query name to be undefined.
     * 
     * @return the query name.
     */
    public String getQueryName()
    {
        return mQueryName;
    }
}
