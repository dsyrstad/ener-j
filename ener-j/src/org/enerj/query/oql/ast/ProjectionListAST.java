// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ProjectionListAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;


/**
 * The ProjectionList AST. <p>
 * 
 * @version $Id: ProjectionListAST.java,v 1.4 2005/11/14 02:55:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ProjectionListAST extends BaseAST
{
    private List<ProjectionAST> mProjections;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new ProjectionListAST. 
     */
    public ProjectionListAST(List<ProjectionAST> someProjections)
    {
        mProjections = someProjections;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the projections.
     *
     * @return a List of ProjectionAST.
     */
    public List<ProjectionAST> getProjections()
    {
        return mProjections;
    }
}
