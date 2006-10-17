// ============================================================================
// $Id: ComposeUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

public class ComposeUnary<T,F1,F2,R> extends UnaryFunctor<T,R> {

    static final long serialVersionUID = -836030733262754108L;
    
    // the first of two inner functors
    private UnaryFunctor<T,F1> _f;

    // the second of two inner functors
    private UnaryFunctor<T,F2> _g;

    // the outer functor
    private BinaryFunctor<F1,F2,R> _h;

    /**
     * Builds a ComposeUnary functor, given two inner functors <b>f</b> and
     * <b>g</b>, and outer functor <b>h</b>.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public ComposeUnary(UnaryFunctor<T,F1> f, UnaryFunctor<T,F2> g,
                        BinaryFunctor<F1,F2, R> h)
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
    public UnaryFunctor<T,F1> getFirstInnerFunctor() { return _f; }

    /**
     * Returns the second of two inner functors
     * @return the second of two inner functors
     */
    public UnaryFunctor<T,F2> getSecondInnerFunctor() { return _g; }

    /**
     * Returns the outer functor
     * @return the outer functor
     */
    public BinaryFunctor<F1,F2,R> getOuterFunctor() { return _h; }

    // UnaryFunctor interface
    
    /**
     * Given argument <b>x</b>, passes x to both inner functors, and passes the
     * results of those functors to the outer functor.
     * 
     * @return h(f(x), g(x))
     */
    public R fn(T x) {
        return _h.fn(_f.fn(x), _g.fn(x));
    }
    
    /**
     * Calls the Visitor's <code>visit(ComposeUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ComposeUnary.Visitor)
            ((ComposeUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _h+".compose("+_f+","+_g+")";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ComposeUnary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ComposeUnary host);
    }
}
