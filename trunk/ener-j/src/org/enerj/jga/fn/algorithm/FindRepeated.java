// ============================================================================
// $Id: FindRepeated.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.util.EmptyIterator;
import org.enerj.jga.util.LookAheadIterator;

/**
 * Locates runs of repeated values in an iteration.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class FindRepeated<T> extends LookAheadFunctor<T> {
    
    static final long serialVersionUID = 2382887791885942503L;

    // the size of the run sought
    private int _repeatCount;

    // functor used to determine if the element should be included in the run
    private UnaryFunctor<T,Boolean> _eq;

    /**
     * Builds a FindRepeated functor that will look for a run of the given size,
     * using the equals() method.
     */
    public FindRepeated (int count, T value) {
        this(count, new EqualTo<T>().bind2nd(value));
    }

    /**
     * Builds a FindRepeated functor that will look for a run of the given size,
     * using the given equality functor.
     */
    public FindRepeated (int count, T value, Equality<T> eq) {
        this(count, eq.bind2nd(value));
    }

    /**
     * Builds a FindRepeated functor that will look for a run of the given size,
     * using the given functor.  The functor is expected to return TRUE if the
     * element should be included in the run.
     */
    public FindRepeated (int count, UnaryFunctor<T,Boolean> eq){
        if (count < 0)
            throw new IllegalArgumentException("count < 0");
        
        _repeatCount = count;
        _eq = eq;
    }

    /**
     * Returns the length of the run being sought
     */
    public int getRunLength() {
        return _repeatCount;
    }

    /**
     * Returns the functor used to determine if the element should be included
     * in the run
     */
    public UnaryFunctor<T,Boolean> getComparisonFn() {
        return _eq;
    }

    /**
     * Locates the first/next run of the given length containing elements that
     * meet the given criteria.
     * @return an Iterator whose next() [if it hasNext()] points at the first
     * element in the desired run.  If no such run of elements exists, then the
     * returned iterator's hasNext() will be false.
     */
    public LookAheadIterator<T> fn (Iterator<? extends T> iterator) {
        // return early if the input iterator is already finished, 
        if (!iterator.hasNext() || _repeatCount == 0) {
            return new LookAheadIterator<T>(iterator, 1);
        }

        LookAheadIterator<T> lai = wrap(iterator, _repeatCount);
        
    OUTER:
        while (lai.hasNextPlus(_repeatCount)) {

            // ... examine the contents of the look ahead buffer ...
            for (int i = 1; i <= _repeatCount; ++i) {
                T arg = lai.peek(i);
                
                // ... and if we find something in the buffer that isn't
                // 'equal', then we'll advance past that point in the iterator
                // and try again
                if ( ! _eq.fn(arg)) {
                    for (int j = i; j > 0; --j) {
                        lai.next();
                    }

                    continue OUTER;
                }
            }

            // If we safely got off the end of the INNER loop, then we must
            // have found the point we're looking for.
            return lai;
        }

        // didn't find anything, make an iterator that will return false.
        return new LookAheadIterator<T>(new EmptyIterator<T>(), 1);
    }

    /**
     * Calls the Visitor's <code>visit(FindRepeated)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FindRepeated.Visitor)
            ((FindRepeated.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "FindRepeated["+_eq+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>FindRepeated</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FindRepeated host);
    }
}
