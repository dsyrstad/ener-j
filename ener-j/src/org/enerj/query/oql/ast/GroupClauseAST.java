// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/GroupClauseAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The GroupClause AST. <p>
 * 
 * @version $Id: GroupClauseAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class GroupClauseAST extends BaseAST
{
    private AST mFieldList;
    private AST mHaving;

    //--------------------------------------------------------------------------------
    /**
     * Construct a GroupClauseAST. 
     *
     * @param aFieldList
     * @param aHaving
     */
    public GroupClauseAST(AST aFieldList, AST aHaving)
    {
        mFieldList = aFieldList;
        mHaving = aHaving;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the FieldList.
     *
     * @return an AST.
     */
    public AST getFieldList()
    {
        return mFieldList;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the having clause.
     *
     * @return an AST.
     */
    public AST getHaving()
    {
        return mHaving;
    }
}
