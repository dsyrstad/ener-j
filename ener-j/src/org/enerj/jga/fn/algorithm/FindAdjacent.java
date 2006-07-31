// ============================================================================
// $Id: FindAdjacent.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.util.LookAheadIterator;
import java.util.Iterator;


/**
 * Locates pairs of adjacent values in an iteration.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class FindAdjacent<T> extends LookAheadFunctor<T> {

    static final long serialVersionUID = 6357244961374202731L;
    
    // The functor used to determine if two adjacent values are the same
    private BinaryFunctor<T,T,Boolean> _eq;

    /**
     * Builds a FindAdjacent functor that uses the equals() method to determine
     * if adjacent values are the same.
     */
    public FindAdjacent () {
        this(new EqualTo<T>());
    }

    /**
     * Builds a FindAdjacent functor that uses the given predicate to determine
     * if adjacent values are the same.  The functor argument is expected to
     * compare two values and return TRUE if they are considered to be equal..
     */
    public FindAdjacent (BinaryFunctor<T,T,Boolean> eq) {
        _eq = eq;
    }

    /**
     * Returns the functor used to determine if two adjacent values are the same
     */
    public BinaryFunctor<T,T,Boolean> getComparisonFn() {
        return _eq;
    }

    /**
     * Locates the first/next pair of adjacent elements in an iteration that
     * are the same value.
     * @return an iterator whose next() [if it hasNext()] points to the first of
     * a pair of adjacent equivalent values.  If no such pair exists, then the
     * iterator's hasNext() will be false.
     */
    public LookAheadIterator<T> fn (Iterator<? extends T> iterator) {
        // return early If the input iterator is finished, 
        if (!iterator.hasNext()) {
            return new LookAheadIterator<T>(iterator, 1);
        }
        
        LookAheadIterator<T> lai = wrap(iterator, 2);
        while (lai.hasNextPlus(2)) {
            T arg1 = lai.peek(1);
            T arg2 = lai.peek(2);
            if (_eq.fn(arg1, arg2)) {
                return lai;
            }

            lai.next();
        }

        // didn't find anything, so we advance our working iterator off the end
        // and return it.
        lai.next();
        return lai;
    }


    /**
     * Calls the Visitor's <code>visit(FindAdjacent)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FindAdjacent.Visitor)
            ((FindAdjacent.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "FindAdjacent["+_eq+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>FindAdjacent</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FindAdjacent host);
    }
}
