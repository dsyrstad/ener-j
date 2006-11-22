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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/MethodCallExprAST.java,v 1.8 2005/11/08 02:47:03 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.property.GetField;
import org.enerj.jga.fn.property.InvokeMethod;
import org.enerj.jga.fn.property.InvokeNoArgMethod;
import org.enerj.util.ClassUtil;




/**
 * The MethodCallExpr AST. In reality, this may be a public member variable reference.
 * 
 * @version $Id: MethodCallExprAST.java,v 1.8 2005/11/08 02:47:03 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class MethodCallExprAST extends BaseAST
{
    private AST mExpr;
    private String mMethodName;
    private ValueListAST mArgList;
    
    transient private Method mMethod;
    transient private Class[] mParamTypes;
    transient private Field mField;
    

    /**
     * Construct a MethodCallExprAST. The aMethodName argument is actually treated like
     * a JavaBean property when anArgList is null. In the case of a property name, a JavaBean
     * property may be used, or an explicit public member variable name. 
     *
     * @param anExpr an expression evaluating to an object whose method will be called. 
     * @param aMethodName a method or JavaBean property name.
     * @param anArgList the argument list. May be null if no args were specified.
     */
    public MethodCallExprAST(AST anExpr, String aMethodName, ValueListAST anArgList)
    {
        assert anExpr != null && aMethodName != null;
        mExpr = anExpr;
        mMethodName = aMethodName;
        mArgList = anArgList;
    }
    

    /**
     * Gets the ArgList.
     *
     * @return an AST, may be null if no arg list was specified.
     */
    public AST getArgList()
    {
        return mArgList;
    }
    

    /**
     * Gets the left-hand Expr evaluating to an object.
     *
     * @return an AST.  May be null for non-static method call.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    

    /**
     * Gets the method name.
     *
     * @return a method name.
     */
    public String getMethodName()
    {
        return mMethodName;
    }

    

    /**
     * Gets the type of mExpr. Handles the case where mExpr may be a identifier representing a 
     * Class, in which case we have a static method call.
     *
     * @return the Class representing the type.
     * 
     * @throws QueryException if an error occurs.
     */
    private Class getExprType() throws QueryException
    {
        Class exprType = mExpr.getType();
        
        // If exprType is Class.class and mExpr is an IdentifierExprAST, then we have a static
        // method call.
        if (exprType == Class.class && mExpr instanceof IdentifierExprAST) {
            Class c = ((IdentifierExprAST)mExpr).getIdentifierClass();
            if (c != null) {
                exprType = c;
            }
        }

        return exprType;
    }
    

    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
    	Class exprType = getExprType();
        
    	List<AST> argList = (mArgList == null ? Collections.EMPTY_LIST : mArgList.getValues());
    	mParamTypes = new Class[ argList.size() ];
    	for (int i = 0; i < mParamTypes.length; i++) {
    		mParamTypes[i] = argList.get(i).getType();
    	}

    	// Find a method.
    	// Also try a JavaBean-style "is" or "get" method name if no parameters.
    	String getMethodName = null;
    	String isMethodName = null;
    	if (mParamTypes.length == 0) {
    		String capFirstCharMethodName = Character.toUpperCase( mMethodName.charAt(0) ) +
    			(mMethodName.length() > 1 ? mMethodName.substring(1) : "");
    		getMethodName = "get" + capFirstCharMethodName;
    		isMethodName = "is" + capFirstCharMethodName;
    	}
    	
    	mMethod = ClassUtil.findMethod(exprType, mMethodName, mParamTypes, getMethodName, isMethodName);
    	if (mMethod != null) {
    		return ClassUtil.mapFromPrimitiveType( mMethod.getReturnType() );
    	}
    	
    	if (argList.isEmpty()) {
	    	// Couldn't find method, try public member variable - only if no parameters.
    		mField = ClassUtil.findField(exprType, mMethodName);
    		if (mField != null) {
    			return ClassUtil.mapFromPrimitiveType( mField.getType() );
    		}
    	}
    	
        String msg = "Cannot find a method " + 
            (mParamTypes.length > 0 ? "" : "or field ") + "called '" + mMethodName + 
            (getMethodName == null ? "" : ("', '" + getMethodName)) + 
            (isMethodName == null ? "" : ("', or '" + isMethodName)) + 
            "' on " + exprType;
        
        if (mParamTypes.length > 0) {
            msg += " matching parameters " + mMethodName + "(";
            String delim = "";
            for (Class paramType : mParamTypes) {
                msg += delim + paramType.getName();
                delim = ", ";
            }
            
            msg += ')';
        }

        throw new QueryException(msg);
    }


    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
    	Class exprType = getExprType();
    	
    	// This ensures that mMethod/mField are resolved.
        Class resultType = getType();
        
        if (mMethod != null) {
        	if (mParamTypes.length == 0) {
        		return new InvokeNoArgMethod(exprType, mMethod).compose( mExpr.resolve() );
        	}
        	else {
        		// Resolve arg list specially because we may need to convert argument types.
        		return new InvokeMethod(exprType, mMethod).compose( mExpr.resolve(), 
                                mArgList.resolveAgainstTypes( mMethod.getParameterTypes() ) );
        	}
        }
        
        if (mField != null) {
        	return new GetField(exprType, mField).compose( mExpr.resolve() );
        }

        throw new QueryException("Cannot resolve identifier:" + mMethodName); // Shouldn't really get here if getType0() does it's job.
    }
}
