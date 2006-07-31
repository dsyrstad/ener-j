// ============================================================================
// $Id: FindIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
 * Iterator that provides the ability to skip to the first/next element that
 * meets a particular criteria.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class FindIterator<T> implements Iterator<T>, Iterable<T> {

    //  The next element in the base iterator to be returned
    private T _next;

    // The base iterator
    private Iterator<? extends T> _base;

    // Flag indicating that there is an element ready to be returned.  
    private Boolean _baseHasNext;
    
    /**
     * Builds a FindIterator for the given iterator.
     */
    public FindIterator(Iterator<? extends T> iter) {
        _base = (iter != null) ? iter : new EmptyIterator<T>();
    }

    /**
     * Returns true if at least one instance remaining in the iteration yields
     * true when passed to the filter.  This operation can advance the base
     * iterator.  It will not, however, advance the base iterator if it is
     * called multiple times in succession without having retrieved the value.
     */
    public boolean findNext(UnaryFunctor<T,Boolean> filter) {
        if (filter == null)
            return hasNext();
        
        // test if we've already advanced via hasNext or foundNext
        if (_baseHasNext != null)
        {
            // if there is at least one more element to test
            if (_baseHasNext)
            {
                // if the next value to return passes the filter, then
                // we already have the next item to be found.
                if (filter.fn(_next)) {
                    return true;
                }
                
                // otherwise fall through -- the current 'next' object doesn't
                // pass the selection criteria, so we need to discard it
            }
            else {
                return false;
            }
        }

        // we don't know if there is another element that passes the filter,
        // so we advance the base iterator and test every item until we fall
        // off the end or find an item that passes
        while (_base.hasNext()) {
            T elem = _base.next();
            if (filter.fn(elem)) {
                _next = elem;
                _baseHasNext = Boolean.TRUE;
                return true;
            }
        }
        
        _next = null;
        _baseHasNext = Boolean.FALSE;
        return false;
    }

    
    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - - - -
    // ListIterator<T> interface
    // - - - - - - - - - - - - -
    
    /**
     * Returns true if there is at least one instance remaining in the
     * iteration.  This operation can advance the base iterator.  It will not,
     * however, advance the base iterator if it is called multiple times in
     * succession without having retrieved the value.
     */
    public boolean hasNext(){
        // test if we've already advanced via hasNext or foundNext
        if (_baseHasNext != null) {
            return _baseHasNext;
        }

        // set our internal flag to the value of base.hasNext().
        // since our internal flag is now non-null, further tests of hasNext()
        // will return without consulting the base iterator.
        boolean b = _base.hasNext();
        _baseHasNext = Boolean.valueOf(b);

        // stash the next value returned by the base iterator, if any.
        // User might intermix tests of hasNext() and findNext(): we'll
        // need to stash these the consistently with the way that findNext()
        // does, in order that they may interoperate correctly
        if (b) {
            _next = _base.next();
        }
        
        return b;
    }

    public T next() {
        // If the user hasn't checked, then we will and throw NSE if there;s
        // nothing left.  
        if (_baseHasNext == null) {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
        }
        
        // If he did check and there's nothing left and he called this method
        // anyway, throw NSE
        else {
            if (!_baseHasNext) {
                throw new NoSuchElementException();
            }
        }

        // Return the next value, and reset everything
        T val = _next;
        _next = null;
        _baseHasNext = null;
        return val;
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
