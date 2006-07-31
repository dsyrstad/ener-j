// ============================================================================
// $Id: Find.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.util.FindIterator;

/**
 * Locates values in an iteration.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Find<T> extends FindIteratorFunctor<T> {
    
    static final long serialVersionUID = -556722539704270804L;
    
    // the functor used to determine if an element is the one being searched for
    private UnaryFunctor<T,Boolean> _eq;

    /**
     * Builds a Find functor that looks for the given value, using the value's
     * equals() method.
     */
    public Find (T value) {
        this(new EqualTo<T>().bind2nd(value));
    }

    /**
     * Builds a Find functor that looks for the given value, using the given
     * Equality predicate.
     */
    public Find (Equality<T> eq, T value) {
        this(eq.bind2nd(value));
    }

    /**
     * Builds a Find functor that looks for values for which the given predicate
     * returns TRUE.
     */
    public Find (UnaryFunctor<T,Boolean> eq) {
        _eq = eq;
    }

    /**
     * Returns the functor used to determine if an element is the one being
     * searched for.
     */
    public UnaryFunctor<T,Boolean> getComparisonFn() {
        return _eq;
    }

    /**
     * Locates the first/next element that meets the given criteria.
     * @return an Iterator whose next() [if it hasNext()] will return an
     * element that meets the given criteria.  If no such element exists, then
     * the returned iterator's hasNext() is false.
     */
    public FindIterator<T> fn (Iterator<? extends T> iterator) {
        FindIterator<T> finder = wrap(iterator);
        finder.findNext(_eq);
        return finder;
    }

    /**
     * Calls the Visitor's <code>visit(Find)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Find.Visitor)
            ((Find.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Find["+_eq+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Find</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Find host);
    }
}
