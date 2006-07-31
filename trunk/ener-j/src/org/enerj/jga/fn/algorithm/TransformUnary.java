// ============================================================================
// $Id: TransformUnary.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.TransformIterator;
import java.util.Iterator;

/**
 * Applies a UnaryFunctor to every element in an iteration, and iterates
 * over the results.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class TransformUnary<T,R>
    extends UnaryFunctor<Iterator<? extends T>, TransformIterator<T,R>>
{
    static final long serialVersionUID = -5437939894267586472L;
    
    private UnaryFunctor<T,R> _fn;

    /**
     * Builds an TransformUnary functor that will apply the given functor to
     * elements in an iteration.
     * @throws IllegalArgumentException if the functor is null
     */
    public TransformUnary(UnaryFunctor<T,R> fn) {
        if (fn == null)
            throw new IllegalArgumentException();
        
        _fn = fn;
    }

    /**
     * Returns the functor used to process elements in an iteration.
     */
    public UnaryFunctor<T,R> getFunction() {
        return _fn;
    }

    /**
     * Apply the functor to each element in the iteration and return an iterator
     * over the results
     *
     * @return an iterator over the results of the transformation
     */
    public TransformIterator<T,R> fn(Iterator<? extends T> iterator) {
        return new TransformIterator<T,R>(iterator, _fn);
    }
    
    /**
     * Calls the Visitor's <code>visit(TransformUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof TransformUnary.Visitor)
            ((TransformUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "TransformUnary";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>TransformUnary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(TransformUnary host);
    }
}
