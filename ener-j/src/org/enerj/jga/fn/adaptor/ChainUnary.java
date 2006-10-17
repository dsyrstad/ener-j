// ============================================================================
// $Id: ChainUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.fn.UnaryFunctor;
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

public class ChainUnary<T,F,R> extends UnaryFunctor<T,R> {

    static final long serialVersionUID = -6690274081307420708L;

    // the outer fn
    private UnaryFunctor<F,R> _f;

    // the inner fn
    private UnaryFunctor<T,F> _g;
    
    /**
     * Builds a BinaryCompose functor, given the outer functor <b>f</b> the
     * inner functor <b>g</b>.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public ChainUnary(UnaryFunctor<F,R> f, UnaryFunctor<T,F> g)
    {
        if (f == null || g == null) {
            throw new IllegalArgumentException("Two functors are required");
        }
        
        _f = f;
        _g = g;
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
    public UnaryFunctor<T,F> getInnerFunctor() { return _g; }

    // UnaryFunctor interface
    
    /**
     * Given argument <b>x</b>, passes x to the inner functor, and passes the
     * result to the outer functor.
     * 
     * @return f(g(x))
     */
    public R fn(T x) {
        return _f.fn(_g.fn(x));
    }

    // Object overrides

    public String toString() {
        return _f+".compose("+_g+")";
    }
    
    /**
     * Calls the Visitor's <code>visit(BinaryCompose)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ChainUnary.Visitor)
            ((ChainUnary.Visitor)v).visit(this);
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>ChainUnary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ChainUnary bf);
    }
}
