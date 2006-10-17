// ============================================================================
// $Id: InvokeNoArgMethod.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

public class InvokeNoArgMethod<T,R> extends UnaryFunctor<T,R> {
    
    static final long serialVersionUID = -2651164047444243205L;

    private String _methName;

    private Class<T> _objclass;

    private transient Method _meth;

    /**
     * Builds a InvokeMethod for the given method, which takes no arguments.
     * @throws IllegalArgumentException if the method name is null or empty
     */
    
    public InvokeNoArgMethod(Class<T> objclass, String methName) {
        if (methName == null || methName.length() == 0) {
            String msg = "Must supply method name";
            throw new IllegalArgumentException(msg);
        }

        if (objclass == null) {
            String msg = "Must supply object class";
            throw new IllegalArgumentException(msg);
        }

        _methName = methName;
        _objclass = objclass;
        
        try {
            _meth = objclass.getMethod(_methName, new Class[0]);
        }
        catch (NoSuchMethodException x) {
            String msg = "No such method: "+x.getMessage();
            IllegalArgumentException iax = new IllegalArgumentException(msg);
            iax.initCause(x);
            throw iax;
        }
    }

    /**
     * Builds a InvokeMethod for the given method, which takes no arguments.
     * @throws IllegalArgumentException if the method name is null or empty,
     * or if it is not defined for the given class, or if it takes arguments
     */
    
    public InvokeNoArgMethod(Class<T> objClass, Method method) {
        if (method == null) {
            String msg = "Must supply method";
            throw new IllegalArgumentException(msg);
        }

        if (objClass == null) {
            String msg = "Must supply object class";
            throw new IllegalArgumentException(msg);
        }

        if (method.getParameterTypes().length != 0) {
            String msg = "Method {0} takes arguments";
            Object[] args = { method.getName() };
            throw new IllegalArgumentException(MessageFormat.format(msg, args));
        }
        
        if (!method.getDeclaringClass().isAssignableFrom(objClass)) {
            String msg = "Method {0} not defined for class {1}";
            Object[] args = { method.getName(), objClass.getName() };
            throw new IllegalArgumentException(MessageFormat.format(msg, args));
        }
        
        _objclass = objClass;
        _meth     = method;
        _methName = method.getName();
    }

    /**
     * Returns the name of the property that this functor retrieves.
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

    // Unary Functor interface

    /**
     * Invokes the method on the given object, and returns the result.
     * @return the result of the designated method of the object
     * @throws EvaluationException if the argument does not have the designated
     *         public property, or if it is not of the correct type. 
     */
    public R fn(T obj) {
        try {
            // @SuppressWarnings
            // There's nothing we can do about this other than warn the users
            // to make sure that they don't use an inappropriate return type
            return (R) getMethod().invoke(obj, new Object[0]);
        }
        catch (ClassCastException x)        {throw new EvaluationException(x);}
        catch (IllegalAccessException x)    {throw new EvaluationException(x);}
        catch (InvocationTargetException x) {throw new EvaluationException(x);}
    }


    /**
     * Lazy loads the method (used if the functor is called after
     * deserialization)
     */
    private Method getMethod() {
        if (_meth == null) {
            try {
                _meth = _objclass.getMethod(_methName, new Class[0]);
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
        if (v instanceof InvokeNoArgMethod.Visitor)
            ((InvokeNoArgMethod.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "InvokeNoArgMethod("+_meth+")";
    }
    
   // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>InvokeMethod</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(InvokeNoArgMethod host);
    }
}
