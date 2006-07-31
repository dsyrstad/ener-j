// ============================================================================
// $Id: Bind.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.fn.Generator;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Generator that wraps a given UnaryFunctor, passing a constant value
 * as the argument of the child functor.  
 * <p>
 * Copyright &copy; 2004  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Bind<T, R> extends Generator<R> {

    static final long serialVersionUID = -7970887561245835912L;
    
    // The functor whose argument is bound
    private UnaryFunctor<T, R> _f;

    // The value bound to the arg
    private T _c;

    /**
     * Builds a Bind Functor with the given constant and child functor.
     * @throws IllegalArgumentException if the functor is null.
     */
    public Bind(T constant, UnaryFunctor<T, R> fn) {
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
    public UnaryFunctor<T, R> getFunctor() { return _f; }

    /**
     * Returns the constant value
     * @return the constant value
     */
    public T getConstant() { return _c; }

    // UnaryFunctor interface
    
    /**
     * Given one argument, passes the constant value and the argument to the
     * child functor and returns the result.
     *
     * @return f(c,x)
     */
    public R gen() {
        return _f.fn(_c);
    }

    /**
     * Calls the Visitor's <code>visit(Bind)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Bind.Visitor)
            ((Bind.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return _f+".bind("+_c+")";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Bind</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Bind bf);
    }
}
