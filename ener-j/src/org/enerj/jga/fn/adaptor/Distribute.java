// ============================================================================
// $Id: Distribute.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

public class Distribute<T1,T2,F1,F2,R> extends BinaryFunctor<T1,T2,R> {

    static final long serialVersionUID = -8288483375404557210L;

    // The functor to which the first arg is passed
    private UnaryFunctor<T1,F1> _f;

    // The functor to which the second arg is passed
    private UnaryFunctor<T2,F2> _g;

    // The functor that evaluates the results of the two inner functors
    private BinaryFunctor<F1,F2,R> _h;

    /**
     * Builds a Distribute functor, given two inner functors <b>f</b> and
     * <b>g</b>, and outer functor <b>h</b>.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public Distribute(UnaryFunctor<T1,F1> f,
                      UnaryFunctor<T2,F2> g,
                      BinaryFunctor<F1,F2,R> h)
    {
        if (f == null || g == null || h == null) {
            throw new IllegalArgumentException("Three functors are required");
        }
        
        _f = f; _g =g; _h = h;
    }

    /**
     * Returns the first of two inner functors
     * @return the first of two inner functors
     */
    public UnaryFunctor<T1,F1> getFirstInnerFunctor() { return _f; }

    /**
     * Returns the second of two inner functors
     * @return the second of two inner functors
     */
    public UnaryFunctor<T2,F2> getSecondInnerFunctor() { return _g; }

    /**
     * Returns the outer functor
     * @return the outer functor
     */
    public BinaryFunctor<F1,F2,R> getOuterFunctor() { return _h; }

    // BinaryFunctor interface
    
    /**
     * Passes arguments <b>x</b> and <b>y</b> to the corresponding inner
     * functors, and passes the results of those functors to the outer functor.
     * 
     * @return h(f(x), g(y))
     */
    public R fn(T1 x, T2 y) {
        return _h.fn(_f.fn(x), _g.fn(y));
    }
    
    /**
     * Calls the Visitor's <code>visit(Distribute)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Distribute.Visitor)
            ((Distribute.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _h+".distribute("+_f+","+_g+")";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Distribute</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Distribute host);
    }
}
