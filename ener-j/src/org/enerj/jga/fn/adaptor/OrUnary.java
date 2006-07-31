// ============================================================================
// $Id: OrUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryPredicate;

/**
 * Unary that performs a shortcircuit or operation using a given pair of
 * Boolean UnaryFunctors.  When the first functor returns true, the second
 * is not evaluated.
 **/

public class OrUnary<T> extends UnaryPredicate<T> {

    static final long serialVersionUID = -3172663271637391544L;
    
    // The two unarys that are tested
    private UnaryFunctor<T,Boolean> _first;
    private UnaryFunctor<T,Boolean> _second;
    
    /**
     * Builds a OrUnary functor, given the two functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public OrUnary(UnaryFunctor<T,Boolean> first, UnaryFunctor<T,Boolean> second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Two functors are required");
        }
        
        _first = first;
        _second = second;
    }


    public UnaryFunctor<T,Boolean> getFirstFunctor() { return _first; }                                                 

    public UnaryFunctor<T,Boolean> getSecondFunctor() { return _second; }
    
    
    public Boolean fn(T arg) {
        return _first.fn(arg) || _second.fn(arg);
    }

    /**
     * Calls the Visitor's <code>visit(OrUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof OrUnary.Visitor)
            ((OrUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _first +" || " +_second;
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>OrUnaryFunctor</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(OrUnary host);
    }
}
