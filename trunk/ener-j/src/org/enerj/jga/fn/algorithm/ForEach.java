// ============================================================================
// $Id: ForEach.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Applies a UnaryFunctor to each element in an iteration, and returns the
 * final result.  Each element in the iteration is passed to the functor in
 * turn.  The result of the final call to the functor is returned.  If the
 * iteration was empty, then the result of this function is null.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class ForEach<T,R> extends UnaryFunctor<Iterator<? extends T>, R> {
    static final long serialVersionUID = -2342252375909337974L;

    // The functor to be applied
    private UnaryFunctor<T,R> _fn;

    /**
     * Builds a ForEach functor that will use the given functor to process
     * elements in an iteration.
     * @throws IllegalArgumentException if the functor is null
     */
    public ForEach(UnaryFunctor<T,R> fn) {
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
     * Apply the functor to each element in the iteration and return the final
     * result.
     * @return the result of the last execution of the functor, or null if the
     * functor is not executed.
     */
    public R fn(Iterator<? extends T> iterator) {
        R value = null;
        while (iterator.hasNext()) {
            value = _fn.fn(iterator.next());
        }

        return value;
    }
    
    /**
     * Calls the Visitor's <code>visit(ForEach)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ForEach.Visitor)
            ((ForEach.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ForEach";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>ForEach</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ForEach host);
    }
}
