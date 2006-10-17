// ============================================================================
// $Id: Accumulate.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
package org.enerj.jga.fn.algorithm;

import java.util.Iterator;
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

public class Accumulate<T> extends UnaryFunctor<Iterator<? extends T>, T> {
    
    static final long serialVersionUID = 4611344190624502921L;
    
    // Functor applied to each element in an iteration in turn
    private BinaryFunctor<T,T,T> _fn;

    // The start value
    private T _value;

    // Flag indicating that the start value was given
    private boolean _givenValue = false;

    /**
     * Builds an Accumulate functor that will use the given functor to process
     * elements in an iteration.  The first element in the iteration will be
     * used as the start value.
     */
    public Accumulate(BinaryFunctor<T,T,T> fn) {
        if (fn == null)
            throw new IllegalArgumentException();
        
        _fn = fn;
    }

    /**
     * Builds an Accumulate functor that will use the given start value and
     * functor to process elements in an iteration.  The first element in the
     * iteration will be used as the start value.
     */
    public Accumulate(T startValue,
                      BinaryFunctor<T,T,T> fn){
        this(fn);
        _value = startValue;
        _givenValue = true;
    }

    /**
     * Returns the functor used to process elements in the iteration.
     */
    public BinaryFunctor<T,T,T> getFunction() {
        return _fn;
    }

    /**
     * Returns the start value, or null if no start value was given.
     */

    public T getStartValue() { return _value; }

    /**
     * Returns true if a start value was passed at construction.
     */

    public boolean hasStartValue() { return _givenValue; }

    /**
     * Apply the functor to the elements of the iteration and return the final
     * result.  Results do not accumulate from one invocation to the next: each
     * time this method is called, the accumulation starts over with the given
     * start value.
     */
    public T fn(Iterator<? extends T> iterator) {
        T value = _givenValue ? _value :
            iterator.hasNext() ? iterator.next() : null;
        
        while (iterator.hasNext()) {
            value = _fn.fn(value, iterator.next());
        }

        return value;
    }
    
    /**
     * Calls the Visitor's <code>visit(Accumulate)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Accumulate.Visitor)
            ((Accumulate.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Accumulate";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Accumulate</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Accumulate host);
    }
}
