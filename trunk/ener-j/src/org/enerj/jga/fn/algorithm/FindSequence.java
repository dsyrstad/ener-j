// ============================================================================
// $Id: FindSequence.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

package org.enerj.jga.fn.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.util.EmptyIterator;
import org.enerj.jga.util.LookAheadIterator;
import java.util.Iterator;

/**
 * Locates a sequence that matches the given pattern.
 * <p>
 * To Serialize a FindSequence, the generic parameter T must be serializable.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class FindSequence<T> extends LookAheadFunctor<T> {

    static final long serialVersionUID = 5277671793270812331L;

    // the pattern to be located
    private Collection<? extends T> _pattern;

    // functor used to compare elements in the iteration and the pattern
    private BinaryFunctor<T,T,Boolean> _eq;

    // the length of the pattern 
    private int _length;

    /**
     * Builds a FindSequence functor that locates the given pattern using the
     * equals() method to compare elements.
     */
    public FindSequence(Collection<? extends T> pattern) {
        this(pattern, new EqualTo<T>());
    }

    /**
     * Builds a FindSequence functor that locates the given pattern using
     * given functor to compare elements.  If the pattern is null, then an
     * arbitrary empty collection will be used.
     * @throws IllegalArgumentException if the functor is null.
     */
    public FindSequence(Collection<? extends T> pattern,
                        BinaryFunctor<T,T,Boolean> eq )
    {
        if (eq == null)
            throw new IllegalArgumentException();
        
        _eq = eq;
        _pattern = (pattern != null) ? pattern : new ArrayList<T>();

        // can't be 0, as the minimum size of a LookAhead is 1
        _length = Math.max(pattern.size(), 1);
    }

    /**
     * Returns the pattern being sought
     */
    public Collection<? extends T> getPattern() {
        return Collections.unmodifiableCollection(_pattern);
    }

    /**
     * Returns the functor used to compare elements in the iteration and
     * the pattern.
     */
    public BinaryFunctor<T,T,Boolean> getComparisonFn() {
        return _eq;
    }

    // UnaryFunctor Interface
    
    /**
     * Locates a sequence that matches the given pattern.
     * @return an iterator whose next() [if it hasNext()] points to the
     * beginning of a sequence in the iteration that matches the given pattern.
     * If no such sequence exists, then the returned interator's hasNext() will
     * be false.
     */
    public LookAheadIterator<T> fn(Iterator<? extends T> iterator) {
        // return early if the input iterator is already finished, 
        if (!iterator.hasNext() || _length == 0) {
            return wrap(iterator, 1);
        }
        
        LookAheadIterator<T> lai = wrap(iterator, _length);
        
        // So long as the LookAhead has enough room for the repeat count to
        // possibly fit, ...
        
    OUTER:
        while (lai.hasNextPlus(_length)) {
            int idx = 1;
            
            // ... examine the contents of the look ahead buffer ...
            for (T obj : _pattern) {
//             Iterator<? extends T> patternIter = _pattern.iterator();
//             while (patternIter.hasNext()) {
//                 T obj = patternIter.next();
                // ... and if we find something in the buffer that isn't
                // 'equal', then advance the look ahead
                if (!_eq.fn(obj, lai.peek(idx))) {
                    lai.next();
                    continue OUTER;
                }

                ++idx;
            }
            
            // If we safely got off the end of the INNER loop, then we must
            // have found the point we're looking for.
            return lai;
        }

        // didn't find anything, make an iterator that will return false.
        return new LookAheadIterator<T>(new EmptyIterator<T>(), 1);
    }

    /**
     * Calls the Visitor's <code>visit(FindSequence)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FindSequence.Visitor)
            ((FindSequence.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "FindSequence";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>FindSequence</b>
     * functor
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FindSequence host);
    }

}
