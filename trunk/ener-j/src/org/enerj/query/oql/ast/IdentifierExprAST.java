// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/IdentifierExprAST.java,v 1.10 2006/05/23 03:36:57 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.odmg.ObjectNameNotFoundException;
import org.odmg.QueryException;
import org.enerj.core.EnerJDatabase;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantUnary;
import org.enerj.query.oql.EvaluatorContext;
import org.enerj.query.oql.EvaluatorContext.VariableDef;
import org.enerj.query.oql.fn.InvokeNamedQuery;




/**
 * The IdentifierExpr AST. <p>
 * 
 * @version $Id: IdentifierExprAST.java,v 1.10 2006/05/23 03:36:57 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IdentifierExprAST extends BaseAST
{
    private String mIdentifier;
    
    // Set if mIdentifier evaluates to a Class.
    private transient Class mIdentifierClass = null;
    // Set if mIdentifier evaluates to a variable.
    private transient VariableDef mVariableDef = null;
    // Set if mIdentifier evaluates to a named query.
    private transient InvokeNamedQuery mNamedQueryFunctor = null;
    // Set if mIdentifier evaluates to a named object.
    private transient Object mNamedObject = null;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IdentifierExprAST. It is unknown at this point what the identifier 
     * represents. It may be a defined query call with no arguments, it may be a reference
     * to a named object (bind), it may be an extent name, or it may be a local variable/alias name.  
     *
     * @param anIdentifier an identifier.
     */
    public IdentifierExprAST(String anIdentifier)
    {
        mIdentifier = anIdentifier;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the identifier.
     *
     * @return an identifier.
     */
    public String getIdentifier()
    {
        return mIdentifier;
    }

    //--------------------------------------------------------------------------------
    /**
     * If this identifier refers to a class, get the resolved Class. Otherwise null is returned.
     * 
     * @throws QueryException if an error occurs.
     */
    public Class getIdentifierClass() throws QueryException
    {
        if (mIdentifierClass == null) {
            getType();
        }
        
        return mIdentifierClass;
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        EvaluatorContext context = EvaluatorContext.getContext();

        // Local variables take precedence over everything else.
        mVariableDef = context.getVariable(mIdentifier);
        if (mVariableDef != null) {
            return mVariableDef.getType();
        }
        
        // Try Persistent Named Query
        EnerJDatabase db = context.getDatabase();
        try {
            mNamedQueryFunctor = (InvokeNamedQuery)db.lookup(DefineQueryAST.NAMED_QUERY_PREFIX + mIdentifier);
            return mNamedQueryFunctor.getResultType();
        }
        catch (ObjectNameNotFoundException e) {
            // Not found - Ignore -- keep going.
        }
    	
        // Named object
        try {
            mNamedObject = db.lookup(mIdentifier);
            if (mNamedObject != null) {
                return mNamedObject.getClass();
            }
            
            return Object.class; // For nulls.
        }
        catch (ObjectNameNotFoundException e) {
            // Not found - Ignore -- keep going.
        }

        // Class name?
        mIdentifierClass = context.resolveClass(mIdentifier);
        if (mIdentifierClass != null) {
            return Class.class;
        }
        
        throw new QueryException("Cannot resolve identifier: " + mIdentifier);
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        // Make sure resolution has been done.
        Class resultType = getType();

        if (mVariableDef != null) {
            return mVariableDef.getValueFunctor();
        }
        
        if (mNamedQueryFunctor != null) {
            return mNamedQueryFunctor;
        }
        
        if (mNamedObject != null) {
            return new ConstantUnary(mNamedObject);
        }
        
        if (mIdentifierClass != null) {
            return new ConstantUnary(mIdentifierClass);
        }

        throw new QueryException("Cannot resolve identifier:" + mIdentifier); // Shouldn't really get here if getType0() does it's job.
    }
}
