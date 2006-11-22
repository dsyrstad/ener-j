/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/SelectExprAST.java,v 1.11 2005/11/14 02:55:40 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.EvaluatorContext;
import org.enerj.query.oql.fn.CartesianProductCollectionFunctor;
import org.enerj.query.oql.fn.ExtentFunctor;
import org.enerj.query.oql.fn.SelectCollectionFunctor;
import org.enerj.query.oql.fn.StructureFunctor;
import org.enerj.query.oql.fn.TrackedValueFunctor;



/**
 * The SelectExpr AST. <p>
 * 
 * @version $Id: SelectExprAST.java,v 1.11 2005/11/14 02:55:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SelectExprAST extends BaseAST
{
    private boolean mIsDistinct;
    private ProjectionAttributesAST mProjectionAttributes;
    private FromClauseAST mFromClause;
    private AST mWhereClause;
    private AST mGroupByClause;
    private AST mOrderByClause;
    
    private transient String[] mIteratorNames;
    private transient TrackedValueFunctor[] mIteratorTrackedValueFunctors;
    

    /**
     * Construct a SelectExprAST. 
     *
     * @param isDistinct
     * @param someProjectionAttributes
     * @param aFromClause
     * @param aWhereClause
     * @param aGroupByClause
     * @param anOrderByClause
     */
    public SelectExprAST(boolean isDistinct, ProjectionAttributesAST someProjectionAttributes, 
                    FromClauseAST aFromClause, AST aWhereClause, AST aGroupByClause, AST anOrderByClause)
    {
        mIsDistinct = isDistinct;
        mProjectionAttributes = someProjectionAttributes;
        mFromClause = aFromClause;
        mWhereClause = aWhereClause;
        mGroupByClause = aGroupByClause;
        mOrderByClause = anOrderByClause;
    }


    /**
     * Gets the FromClause.
     *
     * @return a FromClauseAST.
     */
    public FromClauseAST getFromClause()
    {
        return mFromClause;
    }


    /**
     * Gets the GroupByClause.
     *
     * @return an AST.
     */
    public AST getGroupByClause()
    {
        return mGroupByClause;
    }


    /**
     * Gets the IsDistinct.
     *
     * @return a boolean.
     */
    public boolean isDistinct()
    {
        return mIsDistinct;
    }


    /**
     * Gets the OrderByClause.
     *
     * @return an AST.
     */
    public AST getOrderByClause()
    {
        return mOrderByClause;
    }


    /**
     * Gets the ProjectionAttributes.
     *
     * @return a ProjectionAttributesAST.
     */
    public ProjectionAttributesAST getProjectionAttributes()
    {
        return mProjectionAttributes;
    }


    /**
     * Gets the WhereClause.
     *
     * @return an AST.
     */
    public AST getWhereClause()
    {
        return mWhereClause;
    }


    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        // TODO According to ODMG 3.0, this should be a DBag if not distinct, else a DSet.
        // TODO We need collection wrappers so that we can wrap a collection in a DBag/DSet
        return Collection.class;
    }


    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        EvaluatorContext.getContext().pushVariableScope();
        
        // Process 'from' clause.
        UnaryFunctor selectFunctor = resolveFromClause();
        
        // Process 'where' clause.
        selectFunctor = resolveWhereClause(selectFunctor);
        
        // Process projection.
        selectFunctor = resolveProjection(selectFunctor);
        
        EvaluatorContext.getContext().popVariableScope();
        return selectFunctor;
    }

    

    /**
     * Resolves the 'from' clause.
     *
     * @return the functor for the from clause.
     * 
     * @throws QueryException if an error occurs.
     */
    private UnaryFunctor resolveFromClause() throws QueryException
    {
        // Construct iterator collections.
        UnaryFunctor selectFunctor = null;
        List<IteratorDefAST> iteratorDefs = mFromClause.getIteratorDefs();
        mIteratorNames = new String[ iteratorDefs.size() ];
        mIteratorTrackedValueFunctors = new TrackedValueFunctor[mIteratorNames.length];
        int idx = 0;
        for (IteratorDefAST iterDef : iteratorDefs) {
            AST iterExpr = iterDef.getExpr();
            String aliasName = iterDef.getAlias();
            Class iterType = iterExpr.getType();
            Class variableType = Object.class;

            UnaryFunctor iterFunctor;
            if (iterType == Class.class) {
                iterFunctor = new ExtentFunctor().compose( iterExpr.resolve() );
                if (iterExpr instanceof IdentifierExprAST) {
                    IdentifierExprAST identExpr = (IdentifierExprAST)iterExpr;
                    variableType = identExpr.getIdentifierClass();
                    if (variableType == null) {
                        variableType = Object.class;
                    }
                    
                    if (aliasName == null) {
                        aliasName = identExpr.getIdentifier();
                    }
                }
            } else if (Collection.class.isAssignableFrom(iterType)) {
                iterFunctor = iterExpr.resolve();
            } else {
                String aliasMsg = "";
                if (aliasName != null) {
                    aliasMsg = "with alias of '" + aliasName + "' ";
                }
                
                throw new QueryException("Invalid iterator type of " + iterType + aliasMsg + " in 'from'.");
            }
            
            TrackedValueFunctor trackedValueFunctor = new TrackedValueFunctor();
            mIteratorTrackedValueFunctors[idx] = trackedValueFunctor;
            if (aliasName != null) {
                mIteratorNames[idx] = aliasName;
                // We need to track the current value of a iterator on the Collection
                // returned by iterFunctor. SelectCollectionFunctor will wrap the result of iterFunctor in a 
                // FilteredCollection using the given TrackedValueFunctor (which tracks the "current" value).
                iterFunctor = new SelectCollectionFunctor(null, null, trackedValueFunctor).compose(iterFunctor);
                
                EvaluatorContext.getContext().addVariable(aliasName, trackedValueFunctor, variableType);
            }
            else {
                mIteratorNames[idx] = "";
            }
            
            ++idx;
            
            // Apply Cartesian product operator if more than one iterator.
            if (selectFunctor != null) {
                selectFunctor = new CartesianProductCollectionFunctor().compose(selectFunctor, iterFunctor);
            }
            else {
                selectFunctor = iterFunctor;
            }
        }
        
        return selectFunctor;
    }


    /**
     * Resolves the 'where' clause.
     *
     * @param selectFunctor the functor from the 'from' clause.
     * 
     * @return the functor representing a filtered wrapping of the selectFunctor.
     * 
     * @throws QueryException if an error occurs.
     */
    private UnaryFunctor resolveWhereClause(UnaryFunctor selectFunctor) throws QueryException
    {
        if (mWhereClause != null) {
            if ( !Boolean.class.isAssignableFrom( mWhereClause.getType() )) {
                throw new QueryException("'where' clause does not evaluate to a boolean");
            }
            
            if (selectFunctor instanceof SelectCollectionFunctor) {
                ((SelectCollectionFunctor)selectFunctor).setFilterFunctor( mWhereClause.resolve() );
            }
            else {
                selectFunctor = new SelectCollectionFunctor(mWhereClause.resolve(), null, null).compose(selectFunctor);
            }
            
        }
        
        return selectFunctor;
    }


    /**
     * Resolves the projection.
     *
     * @param selectFunctor the functor from the 'where' clause.
     * 
     * @return the functor representing a filtered wrapping of the selectFunctor.
     * 
     * @throws QueryException if an error occurs.
     */
    private UnaryFunctor resolveProjection(UnaryFunctor selectFunctor) throws QueryException
    {
        UnaryFunctor projectionFunctor;
        ProjectionListAST projectListAST = mProjectionAttributes.getProjectionList();
        if (projectListAST == null) {
            // "select *" - Need to check if just one iterator was present. If so, we don't have to do
            // anything extra.
            if (mFromClause.getIteratorDefs().size() == 1) {
                // Just project the iterated object directly.
                return selectFunctor;
            }

            // Need to construct a StructureFunctor of all iterated values. The names of each struct element is 
            // the iterator name.
            projectionFunctor = new StructureFunctor(mIteratorNames, mIteratorTrackedValueFunctors);
        }
        else {
            // TODO - handle aggregates
            List<ProjectionAST> projections = projectListAST.getProjections();
            
            // If we're just projecting one item, we don't create a struct.
            if (projections.size() == 1) {
                projectionFunctor = projections.get(0).getExpr().resolve();
            }
            else {
                String[] names = new String[ projections.size() ];
                UnaryFunctor[] functors = new UnaryFunctor[ names.length ];
                Iterator<ProjectionAST> iter = projections.iterator();
                for (int i = 0; i < functors.length; i++) {
                    ProjectionAST projection = iter.next();
                    names[i] = projection.getAlias();
                    if (names[i] == null) {
                        names[i] = "";
                    }
                    
                    functors[i] = projection.getExpr().resolve();
                }

                projectionFunctor = new StructureFunctor(names, functors);
            }
        }
        
        if (selectFunctor instanceof SelectCollectionFunctor) {
            ((SelectCollectionFunctor)selectFunctor).setProjectionFunctor(projectionFunctor);
        }
        else {
            selectFunctor = new SelectCollectionFunctor(null, projectionFunctor, null).compose(selectFunctor);
        }
        
        return selectFunctor;
    }
}
