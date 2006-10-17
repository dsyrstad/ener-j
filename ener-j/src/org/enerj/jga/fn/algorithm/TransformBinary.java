// ============================================================================
// $Id: TransformBinary.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.util.TransformBinaryIterator;

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

public class TransformBinary<T1,T2,R>
    extends BinaryFunctor<Iterator<? extends T1>, Iterator<? extends T2>,
                          Iterator<R>>
{
    static final long serialVersionUID = -6353149079946565288L;
    
    // the functor used to transform two iterators
    private BinaryFunctor<T1,T2,R> _fn;

    /**
     * Builds an TransformBinary functor that will use the given functor to
     * process corresponding elements in a pair of iterations.
     * @throws IllegalArgumentException if the functor is null
     */
    public TransformBinary(BinaryFunctor<T1,T2,R> fn) {
        if (fn == null)
            throw new IllegalArgumentException();
        
        _fn = fn;
    }

    /**
     * Returns the functor used to process elements in a pair of iterations.
     */
    public BinaryFunctor<T1,T2,R> getFunction() {
        return _fn;
    }

    /**
     * Apply the functor to corresponding elements in the iterations and return
     * an iterator over the results.  The resulting iterator will contain the
     * same number of elements as the shorter of the two input iterators (if
     * one of the input iterators is empty, then the resulting iterator will
     * also be empty).
     *
     * @return an iterator over the results of the transformation
     */
    public Iterator<R>
    fn(Iterator<? extends T1> i1, Iterator<? extends T2> i2)
    {
        return new TransformBinaryIterator<T1,T2,R>(i1,i2,_fn);
    }
    
    /**
     * Calls the Visitor's <code>visit(Transform)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof TransformBinary.Visitor)
            ((TransformBinary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "TransformBinary("+_fn+")";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Transform</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(TransformBinary host);
    }
}
