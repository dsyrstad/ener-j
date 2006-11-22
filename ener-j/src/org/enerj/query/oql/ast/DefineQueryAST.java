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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/DefineQueryAST.java,v 1.7 2006/05/23 03:36:57 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;

import org.odmg.ODMGException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.QueryException;
import org.enerj.core.EnerJTransaction;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.EvaluatorContext;
import org.enerj.query.oql.fn.InvokeNamedQuery;
import org.enerj.query.oql.fn.ParameterValue;


/**
 * The DefineQuery AST. <p>
 * 
 * @version $Id: DefineQueryAST.java,v 1.7 2006/05/23 03:36:57 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class DefineQueryAST extends BaseAST
{
    public static final String NAMED_QUERY_PREFIX = "NamedQuery:";
    private String mName;
    private ParameterListAST mParams;
    private QueryAST mQuery;
    private boolean mIsPersistent;
    

    /**
     * Construct a new DefineQueryAST. 
     */
    public DefineQueryAST(String aName, ParameterListAST someParams, QueryAST aQuery, boolean isPersistent)
    {
        mName = aName;
        mParams = someParams;
        mQuery = aQuery;
        mIsPersistent = isPersistent;
    }


    /**
     * Gets the name of the query.
     * 
     * @return the query name.
     */
    public String getQueryName()
    {
        return mName;
    }


    /**
     * Gets the Parameter List for the definition.
     * 
     * @return the ParameterListAST for the definition. If no parameters were defined, null is returned.
     */
    public ParameterListAST getParameterList()
    {
        return mParams;
    }


    /**
     * Gets the QueryAST.
     * 
     * @return the QueryAST.
     */
    public QueryAST getQuery()
    {
        return mQuery;
    }
    
    

    /**
     * Determines whether this named query definition should be persistent.
     * 
     * @return true if it should be persistent, or false if it's just defined in memory.
     */
    public boolean isPersistent()
    {
        return mIsPersistent;
    }


    protected Class getType0() throws QueryException
    {
        // resolve0() does all of the work. 
        return null;
    }

    

    protected UnaryFunctor resolve0() throws QueryException
    {
        EvaluatorContext context = EvaluatorContext.getContext(); 
        context.pushVariableScope();
        
        ParameterValue[] paramFunctors;
        if (mParams == null) {
            paramFunctors = new ParameterValue[0];
        }
        else {
            // Add parameters as locally named variables. This needs to be done before we resolve the query.
            List<String> paramNames = mParams.getNames();
            List<TypeAST> paramTypes = mParams.getTypes();
    
            paramFunctors = new ParameterValue[ paramTypes.size() ];
            for (int i = 0; i < paramFunctors.length; i++) {
                Class paramType = paramTypes.get(i).getType();
                paramFunctors[i] = new ParameterValue(paramType);
                context.addVariable(paramNames.get(i), paramFunctors[i], paramType);
            }
        }

        // Resolve the query. This will bind the parameter functors to the query wherever they're used.
        Class resultType = mQuery.getType();
        UnaryFunctor queryFunctor = mQuery.resolve();
        
        context.popVariableScope();
        
        InvokeNamedQuery defFunctor = new InvokeNamedQuery(mName, queryFunctor, paramFunctors, resultType);

        if (mIsPersistent) {
            // Persistent - store it as a named object.
            persistNamedQuery(context, defFunctor);
        }
        else {
            // Locally defined - add as variable def.
            context.addVariable(mName, defFunctor, resultType);
        }
        
        // This AST doesn't actually execute. 
        return null; 
    }

    

    /**
     * Persists the named query. 
     *
     * @param aContext the EvaluatorContext.
     * @param aNamedQueryFunctor an InvokeNamedQuery functor.
     * 
     * @throws QueryException if an error occurs.
     */
    private void persistNamedQuery(EvaluatorContext aContext, InvokeNamedQuery aNamedQueryFunctor) throws QueryException
    {
        boolean ownTxn = false;
        EnerJTransaction txn = aContext.getTransaction();
        // Start our own transaction to store it, if one is not active.
        if (txn == null) {
            txn = new EnerJTransaction();
            txn.begin( aContext.getDatabase() );
            ownTxn = true;
        }
        
        boolean commit = false;
        try {
            String name = NAMED_QUERY_PREFIX + mName;
            try {
                // Try to remove previous definition.
                aContext.getDatabase().unbind(name);
            }
            catch (ObjectNameNotFoundException e) {
                // Ignore.
            }
            
            try {
                aContext.getDatabase().bind(aNamedQueryFunctor, name);
            }
            catch (ODMGException e) {
                // Shouldn't happen, but just in case
                throw new QueryException("Cannot persist named query: " + mName, e);
            }
            
            commit = true;
        }
        finally {
            if (ownTxn) {
                if (commit) {
                    txn.commit();
                }
                else {
                    txn.abort();
                }
            }
        }
    }
}
