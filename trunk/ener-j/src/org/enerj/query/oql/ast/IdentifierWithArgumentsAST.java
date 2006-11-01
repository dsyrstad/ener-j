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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/IdentifierWithArgumentsAST.java,v 1.4 2006/03/05 03:37:27 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.odmg.ObjectNameNotFoundException;
import org.odmg.QueryException;
import org.enerj.core.EnerJDatabase;
import org.enerj.jga.fn.Generator;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ApplyUnaryReturnArg;
import org.enerj.jga.fn.adaptor.Constant;
import org.enerj.jga.fn.property.Construct;
import org.enerj.jga.fn.property.InvokeMethod;
import org.enerj.query.oql.EvaluatorContext;
import org.enerj.query.oql.EvaluatorContext.VariableDef;
import org.enerj.query.oql.fn.FunctorUtil;
import org.enerj.query.oql.fn.InvokeNamedQuery;
import org.enerj.query.oql.fn.ParameterValue;
import org.enerj.util.ClassUtil;



/**
 * An AST representing either an object construction or a named query call. 
 * When ambiguity exists, we try to resolve a named query first.
 * If that fails, we assume it is an object construction. Note that the query can always
 * force explicit object construction by using the "new" keyword. <p>
 * 
 * @version $Id: IdentifierWithArgumentsAST.java,v 1.4 2006/03/05 03:37:27 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IdentifierWithArgumentsAST extends BaseAST
{
    private static final Constant NULL_CONSTANT_FN = new Constant(null);
    private static final Class[] NO_ARGS = new Class[0];

    private String mIdent;
    private FieldListAST mFieldList = null;
    private ValueListAST mValueList = null;
    
    /** True if we've determined that this is an object construction, false if it is 
     * a declared query call, or null if it's still ambiguous. */
    private Boolean mIsObjectConstruction = null;
    
    /** If this is a named query, this is its functor. */
    transient private InvokeNamedQuery mNamedQueryFunctor = null;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IdentifierWithArgumentsAST. Because the arguments are a FieldListAST,
     * it is assumed that this is an object construction. 
     *
     * @param anIdent the name of the class to construct.
     * @param aFieldList a FieldListAST.
     */
    public IdentifierWithArgumentsAST(String anIdent, FieldListAST aFieldList)
    {
        mIdent = anIdent;
        mFieldList = aFieldList;
        mIsObjectConstruction = true;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IdentifierWithArgumentsAST. Could be a named query
     * call or object construction, we don't know yet. Semantic checks will resolve this. 
     *
     * @param anIdent the name of the class to construct.
     * @param aValueList a ValueListAST.
     */
    public IdentifierWithArgumentsAST(String anIdent, ValueListAST aValueList)
    {
        mIdent = anIdent;
        mValueList = aValueList;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IdentifierWithArgumentsAST. This is used when we know for sure that
     * the invocation is either an object constructor or named query call.
     *
     * @param anIdent the name of the class to construct.
     * @param aValueList a ValueListAST.
     * @param isObjectConstructor true if this is a object constructor, otherwise it is
     *  a named query call.
     */
    public IdentifierWithArgumentsAST(String anIdent, ValueListAST aValueList, boolean isObjectConstructor)
    {
        mIdent = anIdent;
        mValueList = aValueList;
        mIsObjectConstruction = isObjectConstructor;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Identifier.
     *
     * @return an identifier String.
     */
    public String getIdent()
    {
        return mIdent;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Field List.
     *
     * @return the FieldListAST or null if not set.
     */
    public AST getFieldList()
    {
        return mFieldList;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Value List.
     *
     * @return the ValueListAST or null if not set.
     */
    public AST getValueList()
    {
        return mValueList;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        boolean ambiguous = (mIsObjectConstruction == null);
        
        // Is this AST ambiguous at this point? I.e., we haven't determined if this is an object construction or named query call yet.
        // We alway attempt to resolve it as a named query call first, then fallback to 
        // object construction. Also handle the case where we know it is a named query.
        // Must have a value list for a named query.
        if ((mIsObjectConstruction == null || !mIsObjectConstruction) && mValueList != null) {
            // See if the query is locally defined (i.e., non-persistent).
            EvaluatorContext context = EvaluatorContext.getContext();
            VariableDef var = context.getVariable(mIdent);
            if (var != null) {
                mIsObjectConstruction = false;
                mNamedQueryFunctor = (InvokeNamedQuery)var.getValueFunctor();
                return var.getType();
            }
            
            // Try Persistent Named Query
            EnerJDatabase db = context.getDatabase();
            try {
                mNamedQueryFunctor = (InvokeNamedQuery)db.lookup(DefineQueryAST.NAMED_QUERY_PREFIX + mIdent);
                mIsObjectConstruction = false;
                return mNamedQueryFunctor.getResultType();
            }
            catch (ObjectNameNotFoundException e) {
                // Not found - Ignore -- keep going.
            }
            
            if (mIsObjectConstruction != null) {
                throw new QueryException("Unable to find named query '" + mIdent + '\'');
            }
            // else fall through and treat as object construction.
        }

        // Lookup the class for mIdent
        mIsObjectConstruction = true;
        Class type = EvaluatorContext.getContext().resolveClass(mIdent);
        if (type == null) {
            if (ambiguous) {
                throw new QueryException("Unable to find a class or named query '" + mIdent + '\'');
            }
            
            throw new QueryException("Class not found: " + mIdent);
        }
        
        return type;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        // Make sure ambiguity is resolved.
        Class resultType = getType();

        // Named query?
        if (!mIsObjectConstruction) {
            // Coerece the parameters to the proper types, if necessary.
            ParameterValue[] paramValues = mNamedQueryFunctor.getParameterValues();
            Class[] paramTypes = new Class[ paramValues.length ];
            for (int i = 0; i < paramValues.length; i++) {
                paramTypes[i] = paramValues[i].getType();
            }
            
            return mNamedQueryFunctor.compose( mValueList.resolveAgainstTypes(paramTypes) );
        }
        
        // It's object construction.
        // Get the constructor parameter types. 
        if (mFieldList instanceof FieldListAST) {
            // Named Property construction. Find no-arg constructor, then set properties.
            List<FieldAST> fields = mFieldList.getFields();
            UnaryFunctor result;
            // Want to combine the result of xtorFunctor and send it once to each SetProperty, and 
            // chain it all together so xtor is executed first, then each setProperty.
            UnaryFunctor[] propFunctors = new UnaryFunctor[ fields.size() ];
            int i = 0;
            for (FieldAST field : fields) {
                // Create a SetProperty that is bound to the specified field value functor as the second argument.
                // The SetProperty is reduced to a UnaryFunctor whose first arg will be passed to the first arg of SetProperty.fn(x, y).
                // The field value functor doesn't need an argument, so it gets null.
                String fieldName = field.getFieldName();
                String setName = "set" + fieldName.substring(0, 1).toUpperCase() + (fieldName.length() > 1 ? fieldName.substring(1) : "");
                Method setterMethod = ClassUtil.findMethod(resultType, fieldName, new Class[] { field.getType() }, setName, null);
                if (setterMethod == null) {
                    throw new QueryException("Cannot find a property setter named '" + fieldName + "' or '" + setName + "' in object construction for " + resultType);
                }

                // Convert the result of the field value to a type that matches the method.
                Generator fieldValueGen = FunctorUtil.resolveAgainstTypes(Collections.singletonList(field), setterMethod.getParameterTypes()).generate(NULL_CONSTANT_FN);
                propFunctors[i++] = new InvokeMethod(resultType, setterMethod).generate2nd(fieldValueGen); 
            }
            
            // Now apply (calculate) the xtor once and send it to all of the propFunctors.
            UnaryFunctor xtorFunctor;
            try {
                xtorFunctor = new Construct(NO_ARGS, resultType);
            }
            catch (IllegalArgumentException e) {
                throw new QueryException("Class " + mIdent + " does not have a default constructor, which is required for named parameter construction.");
            }
            
            // Apply result of xtor to each property setter and then return the result of the constructor.
            return new ApplyUnaryReturnArg(propFunctors).compose(xtorFunctor);
        }

        // Straight value-based construction.
        List<AST> argList = mValueList.getValues();
        Class[] paramTypes = new Class[ argList.size() ];
        Iterator<AST> iter = argList.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            AST ast = iter.next();
            paramTypes[i] = ast.getType();
        }
        
        Constructor xtor = ClassUtil.findMostSpecificConstructor(resultType, paramTypes);
        if (xtor == null) {
            String msg = "Cannot find a constructor matching " + resultType.getName() + '(';
            String delim = "";
            for (Class paramType : paramTypes) {
                msg += delim + paramType.getName();
                delim = ", ";
            }
            
            msg += ')';
            
            throw new QueryException(msg);
        }
        
        // Resolve arg list specially because we may need to convert argument types.
        return new Construct(xtor).compose( mValueList.resolveAgainstTypes(xtor.getParameterTypes() ) );
    }
}
