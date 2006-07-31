// ============================================================================
// $Id: Construct.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.Arrays;

/**
 * Unary Functor that constructs an object of the given class.  The functor
 * argument is an array containing the arguments to the class' constructor.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Construct<R> extends UnaryFunctor<Object[],R> {

    static final long serialVersionUID = -4682718030847292345L;

    private Class<R> _objclass;
    private Class[] _argclasses;
    
    private transient Constructor<R> _ctor;

    /**
     * Builds a functor that will build an object of class
     * <code>ctorclass</code>, given arguments of classes in
     * <code>argclasses</code>.   
     * @throws IllegalArgumentException if either argument is omitted, or if the
     * constructor cannot be found.
     */
    public Construct(Class[] argclasses, Class<R> ctorclass) {
        if (argclasses == null) {
            String msg = "Argument Classes must be specified";
            throw new IllegalArgumentException(msg);
        }

        if (ctorclass == null) {
            String msg = "Class to be constructed must be specified";
            throw new IllegalArgumentException(msg);
        }

        _objclass = ctorclass;
        _argclasses = argclasses;
        
        try {
            _ctor = ctorclass.getConstructor(argclasses);
        }
        catch (NoSuchMethodException x) {
            IllegalArgumentException x1 = new IllegalArgumentException();
            x1.initCause(x);
            throw x1;
        }
    }

    /**
     * Builds a functor that will build an object using the given constructor.
     * @throws IllegalArgumentException if the constructor is omitted.
     */
    public Construct(Constructor<R> ctor) {
        if (ctor == null) {
            String msg = "constructor must be specified";
            throw new IllegalArgumentException(msg);
        }

        _ctor = ctor;
        _objclass = ctor.getDeclaringClass();
        _argclasses = ctor.getParameterTypes();
    }
    
    // UnaryFunctor interface
    
    /**
     * Builds an object via the designated constructor, passing the given
     * array of argument values.
     * <p>
     * @return the object built by the constructor
     */
    public R fn(Object[] args) {
        try {
            return (R) getConstructor().newInstance(args);
        }
        catch (NoSuchMethodException x) { throw new EvaluationException(x); }
        catch (InstantiationException x) { throw new EvaluationException(x); }
        catch (IllegalAccessException x) { throw new EvaluationException(x); }
        catch (InvocationTargetException x) { throw new EvaluationException(x); }
    }

    /**
     * Lazy loads the constructor (used if the functor is called after
     * deserialization)
     */
    private Constructor<R> getConstructor() throws NoSuchMethodException {
        if (_ctor == null) 
            _ctor = _objclass.getConstructor(_argclasses);

        return _ctor;
    }

    /**
     * Calls the Visitor's <code>visit(Construct)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Construct.Visitor)
            ((Construct.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Construct("+_objclass.getName()+".getConstructor("+Arrays.toString(_argclasses)+"))";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Construct</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Construct host);
    }
}
