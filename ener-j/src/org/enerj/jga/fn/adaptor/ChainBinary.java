// ============================================================================
// $Id: ChainBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
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
 * Binary Functor that passes the results of a Binary Functor as the argument
 * to a Unary Functor.  This allows for the construction of compound
 * functors from the primitives found in the arithmetic, logical, property, and
 * comparison packages.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class ChainBinary<T1,T2,F,R> extends BinaryFunctor<T1,T2,R> {
    
    static final long serialVersionUID = -8161448545088932320L;

    private UnaryFunctor<F,R> _f;
    
    private BinaryFunctor<T1,T2,F> _g;

    /**
     * Builds a ChainBinary functor, given outer functor <b>f</b> and inner
     * functor <b>g</b>.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public ChainBinary(UnaryFunctor<F,R> f, BinaryFunctor<T1,T2,F> g) {
        if (f == null || g == null ) {
            String msg = "Two functors are required";
            throw new IllegalArgumentException(msg);
        }
        
        _f = f; _g =g;;
    }

    /**
     * Returns the outer functor
     * @return the outer functor
     */
    public UnaryFunctor<F,R> getOuterFunctor() { return _f; }

   /**
     * Returns the inner functor
     * @return the inner functor
     */
    public BinaryFunctor<T1,T2,F> getInnerFunctor() { return _g; }

    // BinaryFunctor interface
    
    /**
     * Passes arguments <b>x</b> and <b>y</b> to the inner functor, and passes
     * the result to the outer functor.
     * 
     * @return f(g(x,y))
     */
    public R fn(T1 x, T2 y) {
        return _f.fn(_g.fn(x,y));
    }
    
    /**
     * Calls the Visitor's <code>visit(ChainBinary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ChainBinary.Visitor)
            ((ChainBinary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _f+".compose("+_g+")";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ChainBinary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ChainBinary host);
    }
}
