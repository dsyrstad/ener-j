// ============================================================================
// $Id: ConstructUnary.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.UnaryFunctor;

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

public class ConstructUnary<T,R> extends UnaryFunctor<T,R> {
    
    static final long serialVersionUID = -6046849006655934333L;

    private Class<T> _argclass;
    private Class<R> _ctorclass;
    private transient Constructor<R> _ctor;

    /**
     * Builds a functor that will build an object of class
     * <code>ctorclass</code>, given an argument of class <code>argclass</code>.
     * The classes passed to the constructor must be assignable to the generic
     * arguments (when generics are in use):
     * <code>
     *    ConstructUnary<String,Integer> ctor =
     *         new ConstructUnary<String,Integer>(String.class,Integer.class);
     * </code>
     * @throws IllegalArgumentException if either argument is omitted, or if the
     * constructor cannot be found.
     */
    
    public ConstructUnary(Class<T> argclass, Class<R> ctorclass) {
        if (argclass == null) {
            String msg = "Argument Class must be specified";
            throw new IllegalArgumentException(msg);
        }

        if (ctorclass == null) {
            String msg = "Class to be constructed must be specified";
            throw new IllegalArgumentException(msg);
        }
        
        try {
            _argclass = argclass;
            _ctorclass = ctorclass;
            _ctor = getConstructor();
        }
        catch (NoSuchMethodException x) {
            String msg = "No constructor found for class {0} that takes an argument of class {1}";
            Object args[] = new Object[] { ctorclass, argclass };
            
            IllegalArgumentException x1 =
                new IllegalArgumentException(MessageFormat.format(msg, args));
            x1.initCause(x);
            throw x1;
        }
    }
    
    // UnaryFunctor interface
    
    /**
     * Builds an object via a one-arg constructor, passing the given value.
     * <p>
     * @return the object built by the constructor
     */
    public R fn(T arg) {
        
        try {
            R val = (R) getConstructor().newInstance(new Object[]{arg});
            return val;
        }
        catch (NoSuchMethodException x) {
            String msg = "No constructor for class {0} that takes an argument of type {1}";
            Object[] args = new Object[]{_ctorclass.getName(), _argclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (InstantiationException x) {
            String msg = "class {0} is abstract: cannot be constructed";
            Object[] args = new Object[]{_ctorclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalArgumentException x) {
            String msg = "class {0} ctor({1}) cannot be called with {2}";
            Object[] args = new Object[]{_ctorclass.getName(), _argclass.getName(), arg};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalAccessException x) {
            String msg = "class {0} ctor({1}) is not accessible";
            Object[] args = new Object[]{_ctorclass.getName(), _argclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (InvocationTargetException x) { 
            String msg = "class {0} ctor({1}) failed: "+x.getMessage();
            Object[] args = new Object[]{_ctorclass.getName(), _argclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }
    
    /**
     * Lazy loads the constructor (used if the functor is called after
     * deserialization)
     */
    private Constructor<R> getConstructor() throws NoSuchMethodException {
        if (_ctor == null) 
            _ctor = _ctorclass.getConstructor(new Class[]{_argclass});

        return _ctor;
    }

    /**
     * Calls the Visitor's <code>visit(ConstructUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConstructUnary.Visitor)
            ((ConstructUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "new "+_ctorclass.getName()+"(["+_argclass.getName()+"])";
//         return "ConstructUnary["+_ctorclass.getName()+"("+_argclass.getName()+")]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ConstructUnary</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConstructUnary host);
    }
}
