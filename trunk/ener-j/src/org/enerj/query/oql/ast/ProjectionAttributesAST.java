// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ProjectionAttributesAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $

package org.enerj.query.oql.ast;



/**
 * The ProjectionAttributes AST. <p>
 * 
 * @version $Id: ProjectionAttributesAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ProjectionAttributesAST extends BaseAST
{
    private ProjectionListAST mProjectionList;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new ProjectionAttributesAST. 
     * 
     * @param aProjectionListAST a ProjectionListAST. May be null if all attributes should be projected.
     */
    public ProjectionAttributesAST(ProjectionListAST aProjectionListAST)
    {
        mProjectionList = aProjectionListAST;
    }
    //--------------------------------------------------------------------------------
    /**
     * Gets the ProjectionListAST.
     *
     * @return a ProjectionListAST. May be null if all attributes should be projected.
     */
    public ProjectionListAST getProjectionList()
    {
        return mProjectionList;
    }
}
