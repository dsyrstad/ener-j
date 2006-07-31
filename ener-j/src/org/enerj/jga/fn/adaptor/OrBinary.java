// ============================================================================
// $Id: OrBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.fn.BinaryPredicate;

/**
 * Binary that performs a shortcircuit or operation using a given pair of
 * Boolean BinaryFunctors.  When the first functor returns true, the second
 * is not evaluated.
 **/

public class OrBinary<T1,T2> extends BinaryPredicate<T1,T2> {

    static final long serialVersionUID = 9139956045465862762L;
    
    // The two binarys that are tested
    private BinaryFunctor<T1,T2,Boolean> _first;
    private BinaryFunctor<T1,T2,Boolean> _second;
    
    /**
     * Builds a OrBinary functor, given the two functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public OrBinary(BinaryFunctor<T1,T2,Boolean> first, BinaryFunctor<T1,T2,Boolean> second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Two functors are required");
        }
        
        _first = first;
        _second = second;
    }


    public BinaryFunctor<T1,T2,Boolean> getFirstFunctor() { return _first; }                                                 

    public BinaryFunctor<T1,T2,Boolean> getSecondFunctor() { return _second; }
    
    
    public Boolean fn(T1 arg1, T2 arg2) {
        return _first.fn(arg1,arg2) || _second.fn(arg1,arg2);
    }

    /**
     * Calls the Visitor's <code>visit(OrBinary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof OrBinary.Visitor)
            ((OrBinary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _first +" || " +_second;
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>OrBinaryFunctor</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(OrBinary host);
    }
}
