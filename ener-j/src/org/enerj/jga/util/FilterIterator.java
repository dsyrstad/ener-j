// ============================================================================
// $Id: FilterIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
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

package org.enerj.jga.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Iterator that only returns elements that meet the given selection criteria.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class FilterIterator<T> implements Iterator<T>, Iterable<T> {

    // base iterator
    private FindIterator<T> _base;

    // predicate used to test elements of the base iterator.
    private UnaryFunctor<T,Boolean> _filter;

    // flag indicating that the base iterator has been advanced to the next
    // qualifying element (or off the end if no remaining element qualifies)
    private boolean _testedNext;

    /**
     * Builds a FilterIterator that will return only qualifying elements of
     * the given iterator.
     */
    public FilterIterator(Iterator<? extends T> iter,
                          UnaryFunctor<T,Boolean> pred)
    {
        _base = new FindIterator<T>(iter);
        _filter = pred;
    }

    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - - - -
    // ListIterator<T> interface
    // - - - - - - - - - - - - -
    
    public boolean hasNext(){
        _testedNext = true;
        return _base.findNext(_filter);
    }

    public T next() {
        if (!_testedNext) {
            hasNext();
        }
        
        _testedNext = false;
        return _base.next();
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
