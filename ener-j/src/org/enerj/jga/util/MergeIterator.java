// ============================================================================
// $Id: MergeIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.comparison.LessEqual;

/**
 * Iterator that merges the contents of two input iterators.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class MergeIterator<T> implements Iterator<T>, Iterable<T> {
    // The base iterators
    private LookAheadIterator<? extends T> _base1;
    private LookAheadIterator<? extends T> _base2;

    // The test
    private BinaryFunctor<T,T,Boolean> _pred;

    /**
     * Builds a MergeIterator for the given base iterators that uses the given
     * Comparator to compare corresponding elements.  The Comparator will be
     * used with a LessEqualComp predicate.
     * @throws IllegalArgumentException if either argument is null
     */
      
    public MergeIterator (Iterator<? extends T> base1,
                          Iterator<? extends T> base2,
                          Comparator<T> comp)
    {
        this(base1, base2, new LessEqual<T>(comp));
    }

    /**
     * Builds a MergeIterator for the given base iterators that uses the given
     * predicate to compare corresponding elements.  The predicate should return
     * TRUE if its first argument is less than or equal to the second.
     * @throws IllegalArgumentException if either argument is null
     */

    public MergeIterator (Iterator<? extends T> base1,
                          Iterator<? extends T> base2,
                          BinaryFunctor<T,T,Boolean> pred)
    {
        if (base1 == null || base2 == null) {
            String msg = "two base iterators are required";
            throw new IllegalArgumentException(msg);
        }
        if (pred == null) {
            String msg = "functor is required";
            throw new IllegalArgumentException(msg);
        }

        _base1 = new LookAheadIterator<T>(base1, 1);
        _base2 = new LookAheadIterator<T>(base2, 1);
        _pred = pred;
    }

    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<T> interface
    // - - - - - - - - - - -
    
    public boolean hasNext() {
        return _base1.hasNextPlus(1) || _base2.hasNextPlus(1);
    }

    public T next() {
        if (_base1.hasNextPlus(1)) 
             if (_base2.hasNextPlus(1)) 
                 if (_pred.fn(_base1.peek(1), _base2.peek(1)))
                     return _base1.next();
                 else
                     return _base2.next();
             else
                 return _base1.next();
        else
            if (_base2.hasNextPlus(1)) 
                return _base2.next();
            else
                throw new NoSuchElementException();
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
