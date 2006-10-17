// ============================================================================
// $Id: TransformAdjacent.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
package org.enerj.jga.fn.algorithm;

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.TransformAdjacentIterator;
import java.util.Iterator;

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

public class TransformAdjacent<T,R>
    extends UnaryFunctor<Iterator<? extends T>, TransformAdjacentIterator<T,R>>
{
    static final long serialVersionUID = -3208147943658111386L;
    
    private BinaryFunctor<T,T,R> _fn;

    /**
     * Builds an TransformAdjacent functor that will apply the given functor to
     * elements in an iteration.
     * @throws IllegalArgumentException if the functor is null
     */
    public TransformAdjacent(BinaryFunctor<T,T,R> fn) {
        if (fn == null)
            throw new IllegalArgumentException();
        
        _fn = fn;
    }

    /**
     * Returns the functor used to process elements in an iteration.
     */
    public BinaryFunctor<T,T,R> getFunction() {
        return _fn;
    }

    /**
     * Apply the functor to each element in the iteration and return an iterator
     * over the results
     *
     * @return an iterator over the results of the transformation
     */
    public TransformAdjacentIterator<T,R> fn(Iterator<? extends T> iterator) {
        return new TransformAdjacentIterator<T,R>(iterator, _fn);
    }
    
    /**
     * Calls the Visitor's <code>visit(TransformAdjacent)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof TransformAdjacent.Visitor)
            ((TransformAdjacent.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "TransformAdjacent["+_fn+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>TransformAdjacent</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(TransformAdjacent host);
    }
}
