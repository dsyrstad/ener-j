// ============================================================================
// $Id: Bind2nd.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * UnaryFunctor that wraps a given BinaryFunctor, passing a constant value
 * as the second argument of the child functor.  The runtime argument is passed
 * as the first argument of the child functor.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Bind2nd<T1, T2, R> extends UnaryFunctor<T1, R> {

    static final long serialVersionUID = -5977756270123944716L;
    
    // The functor whose second argument is bound
    private BinaryFunctor<T1, T2, R> _f;
    
    // The value bound to the second arg
    private T2 _c;

    /**
     * Builds a Bind2nd Functor with the given contant and child functor.
     * @throws IllegalArgumentException if the functor is null.
     */
    public Bind2nd(T2 constant, BinaryFunctor<T1, T2, R> fn) {
        if (fn == null) {
            throw new IllegalArgumentException("Must supply a function");
        }
        _f = fn;
        _c = constant;
    }


    /**
     * Returns the child functor for this functor
     * @return the child functor for this functor
     */
    public BinaryFunctor<T1, T2, R> getFunctor() { return _f; }
    
    /**
     * Returns the constant value
     * @return the constant value
     */
    public T2 getConstant() { return _c; }

    // UnaryFunctor interface
    
    /**
     * Given one argument, passes the argument and the constant value to the
     * child functor and returns the result.
     *
     * @return f(x,c)
     */
    public R fn(T1 x) {
        return _f.fn(x, _c);
    }

    /**
     * Calls the Visitor's <code>visit(Bind2nd)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Bind2nd.Visitor)
            ((Bind2nd.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return _f+".bind2nd("+_c+")";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Bind2nd</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Bind2nd bf);
    }
}
