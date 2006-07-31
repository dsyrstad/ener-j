// ============================================================================
// $Id: Bind1st.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.BinaryFunctor;

/**
 * UnaryFunctor that wraps a given BinaryFunctor, passing a constant value
 * as the first argument of the child functor.  The runtime argument is passed
 * as the second argument of the child functor.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Bind1st<T1, T2, R> extends UnaryFunctor<T2, R> {

    static final long serialVersionUID = 135479226987843702L;    
    
    // The functor whose first argument is bound
    private BinaryFunctor<T1, T2, R> _f;

    // The value bound to the first arg
    private T1 _c;

    /**
     * Builds a Bind1st Functor with the given contant and child functor.
     * @throws IllegalArgumentException if the functor is null.
     */
    public Bind1st(T1 constant, BinaryFunctor<T1, T2, R> fn) {
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
    public T1 getConstant() { return _c; }

    // UnaryFunctor interface
    
    /**
     * Given one argument, passes the constant value and the argument to the
     * child functor and returns the result.
     *
     * @return f(c,x)
     */
    public R fn(T2 x) {
        return _f.fn(_c, x);
    }

    /**
     * Calls the Visitor's <code>visit(Bind1st)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Bind1st.Visitor)
            ((Bind1st.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return _f+".bind1st("+_c+")";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Bind1st</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Bind1st bf);
    }
}
