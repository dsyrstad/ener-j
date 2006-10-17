// ============================================================================
// $Id: InvokeMethod.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
// ============================================================================
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ChainBinary;
import org.enerj.jga.fn.adaptor.ChainUnary;
import org.enerj.jga.util.Arrays;

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

public class InvokeMethod<T,R> extends BinaryFunctor<T,Object[],R> {

    static final long serialVersionUID = -4632096384509133504L;

    private Class<T> _objClass;

    private String _methName;

    private Class[] _argtypes;
    
    private transient Method _meth;

    /**
     * Builds a InvokeMethod for a given method, using the given class
     * to find the desired method.
     * @throws IllegalArgumentException if the method name is null or empty, or
     *     if the argument type array is null.
     */
    public InvokeMethod(Class<T> objClass, Method method) {
        if (method == null) {
            String msg = "Must supply method";
            throw new IllegalArgumentException(msg);
        }

        if (objClass == null) {
            String msg = "Must supply object class";
            throw new IllegalArgumentException(msg);
        }

        if (!method.getDeclaringClass().isAssignableFrom(objClass)) {
            String msg = "Method {0} not defined for class {1}";
            Object[] args = { method.getName(), objClass.getName() };
            throw new IllegalArgumentException(MessageFormat.format(msg, args));
        }
        
        _objClass = objClass;
        _meth     = method;
        _methName = method.getName();
        _argtypes = method.getParameterTypes();
    }

    /**
     * Builds a InvokeMethod for a given method, using the given class
     * to find the desired method.  Note that this is a convenience constructor
     * for a common case: the case where the method takes a single argument.
     * It is still necessary for the argument passed to the fn method to be an
     * array, in this case it must be an array of length 1 containing a value of
     * of type argtype.  A ClassCastException may be thrown if this functor is
     * called incorrectly: if the line number in the associated stack dump points
     * to the class statement (on or about line 45), then the argument was likely
     * passed without being wrapped in an array.
     * @throws IllegalArgumentException if the method name is null or empty, or
     *     if the argument type array is null.
     */
    public InvokeMethod(Class<T> objClass, String methName, Class argtype) {
        this(objClass, methName, new Class[]{argtype});
    }

    /**
     * Builds a InvokeMethod for the given method, using the given class array
     * to find the desired method.
     * @throws IllegalArgumentException if the method name is null or empty, or
     *     if the argument type array is null.
     */
    public InvokeMethod(Class<T> objClass, String methName, Class[] argtypes) {
        if (methName == null || methName.length() == 0) {
            String msg = "Must supply method name";
            throw new IllegalArgumentException(msg);
        }

        if (objClass == null) {
            String msg = "Must supply object class";
            throw new IllegalArgumentException(msg);
        }

        _methName = methName;
        _objClass = objClass;
        _argtypes = argtypes;
        
        try {
            _meth = objClass.getMethod(_methName, argtypes);
        }
        catch (NoSuchMethodException x) {
            String msg = "No such method: "+x.getMessage();
            IllegalArgumentException iax = new IllegalArgumentException(msg);
            iax.initCause(x);
            throw iax;
        }
    }

    /**
     * Returns the name of the method that this functor invokes.
     */
    public String getMethodName() {
        return _methName;
    }


    /**
     * Returns the type of the method 
     */
    public Class<R> getReturnType() {
        return (Class<R>) _meth.getReturnType();
    }

    // BinaryFunctor interface
    
    public R fn(T obj, Object[] args) {
        try {
            // @SuppressWarnings
            // There's nothing we can do about this other than warn the users
            // to make sure that they don't use an inappropriate return type


            return (R) getMethod(_argtypes).invoke(obj, args);
        }
        catch (ClassCastException x) {
            String msg = "ClassCastException: " +_objClass +"." +_methName
                +"("+Arrays.toString(args)+")";
            throw new EvaluationException(msg, x);
        }
        catch (IllegalAccessException x) {
            String msg = _objClass +"." +_methName +" is not accessible";
            throw new EvaluationException(msg, x);
        }
        catch (InvocationTargetException x) {
            String xmsg = x.getMessage();
            String msg = "InvocationException: " +_objClass +"." +_methName
                +"("+Arrays.toString(args)+")"+(xmsg != null ? (":"+xmsg) : "");
            throw new EvaluationException(msg, x);
        }
    }

    /**
     * Lazy loads the method (used if the functor is called after
     * deserialization)
     */
    private Method getMethod(Class[] argtypes) {
        if (_meth == null) {
            try {
                _meth = _objClass.getMethod(_methName, argtypes);
            }
            catch (NoSuchMethodException x) {
                throw new EvaluationException(x);
            }
        }

        return _meth;
    }

    /**
     * Calls the Visitor's <code>visit(InvokeMethod)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof InvokeMethod.Visitor)
            ((InvokeMethod.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "InvokeMethod("+_meth+")";
    }
    
   // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>InvokeMethod</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(InvokeMethod host);
    }
}
