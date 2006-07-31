// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/StructConstructionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The StructConstruction AST. <p>
 * 
 * @version $Id: StructConstructionAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class StructConstructionAST extends BaseAST
{
    private AST mFieldList;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a StructConstructionAST. 
     *
     * @param aFieldList
     */
    public StructConstructionAST(AST aFieldList)
    {
        mFieldList = aFieldList;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the FieldList.
     *
     * @return a AST.
     */
    public AST getFieldList()
    {
        return mFieldList;
    }
}
