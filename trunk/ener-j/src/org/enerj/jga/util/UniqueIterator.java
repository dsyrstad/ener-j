// ============================================================================
// $Id: UniqueIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.comparison.EqualTo;

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

public class UniqueIterator<T> implements Iterator<T>, Iterable<T> {
    // The base iterator
    private Iterator<? extends T> _base;

    // The test for equality
    private BinaryFunctor<T,T,Boolean> _eq;

    // null at startup, TRUE if hasNext() has been called since last next()
    private Boolean _testedNext;

    // flag indicating that there is at least new element remaining
    private boolean _hasNext;
    
    // previous item returned
    private T _lastValue;

    // next item to return
    private T _nextValue;

    /**
     * Builds a UniqueIterator for the given base iterator, using a test based
     * on the equals() method of type T.
     */
      
    public UniqueIterator (Iterator<? extends T> base) {
        this(base, new EqualTo<T>());
    }

    /**
     * Builds a UniqueIterator for the given base iterator that uses the given
     * predicate to compare adjacent elements.  The predicate should return true
     * if the adjacent elements are the same.
     * @throws IllegalArgumentException if either argument is null
     */

    public UniqueIterator (Iterator<? extends T> base,
                           BinaryFunctor<T,T,Boolean> eq)
    {
        if (base == null) {
            throw new IllegalArgumentException("base iterator is required");
        }
        if (eq == null) {
            throw new IllegalArgumentException("functor is required");
        }

        _base = base;
        _eq = eq;
    }

    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<T> interface
    // - - - - - - - - - - -
    
    public boolean hasNext() {
        // make sure this isn't the first time through
        if (_testedNext != null) {

            // make sure that we haven't already tested hasNext()
            if (!_testedNext) {
                
                // first test since next() called
                _testedNext = Boolean.TRUE;
                _hasNext = false; // in case we walk off the end
                while (_base.hasNext()) {
                    _nextValue = _base.next();
                    if (_eq.fn(_lastValue, _nextValue)) {
                        continue; // skip this value, try the next one
                    }
                    
                    _hasNext = true;
                    break;
                }
            }
        }
        else {
            // this is the first time through -- can't call the functor with the
            // first value (the first value may be null, so we can't take the
            // chance that it equals the not yet initialized _lastValue member);
            
            _testedNext = Boolean.TRUE;
            _hasNext = _base.hasNext();
            if (_hasNext) {
                _nextValue = _base.next();
            }
        }
        
        return _hasNext;

    }

    public T next() {
        // make sure that hasNext() has been called
        if (_testedNext == null || !_testedNext) {
            hasNext();
        }

        // just in case the result of hasNext() has been ignored
        if (!_hasNext)
            throw new NoSuchElementException();

        //stash off the value being returned for future testing
        _testedNext = Boolean.FALSE;
        _lastValue = _nextValue;
        return _nextValue;
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
