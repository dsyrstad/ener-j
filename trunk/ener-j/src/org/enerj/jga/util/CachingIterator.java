// ============================================================================
// $Id: CachingIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Iterator;

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

public class CachingIterator<T> implements Iterator<T>, Iterable<T> {
    // The base iterator
    private Iterator<? extends T> _base;

    // ring buffer, to hold the elements that have been consumed
    private Object[] _cache;

    // index of the buffer's base element.  Once the ring buffer loops around,
    // the base pointer indicates the logical 
    private int _baseptr = 0;

    // number of elements in the buffer
    private int _cnt = 0;

    // size of the buffer
    private int _size;

    /**
     * Builds a CachingIterator that can retain 1 element.
     */
      
    public CachingIterator (Iterator<? extends T> base) {
        this(base, 1);
    }

    /**
     * Builds a CachingIterator that can retain the given number of elements.
     * 
     * @throws IllegalArgumentException if max <= 0.
     */
      
    public CachingIterator (Iterator<? extends T> base, int max) {
        if (max <= 0)
            throw new IllegalArgumentException();
        
        _base = (base != null) ? base : new EmptyIterator<T>();
        _size = max;

        _cache = new Object[_size];
    }

    /**
     * Returns true if there have been at least N elements consumed from the
     * underlying iterator.
     */

    public boolean hasCached(int n) {
        return n > 0 && n <= _cnt;
    }
    
    /**
     * Returns the Nth previous element consumed from the underlying iterator.
     */

    public T cached(int n) {
        if (n <= 0 || n > _cnt || n > _size)
            throw new NoSuchElementException();

        // @SuppressWarnings
        // The buffer array is private, and the only things that we ever put
        // into it are known to be T objects because they are read from an
        // Iterator<? extends T>, so we know that the cast is always valid

        return (T) _cache[(_baseptr +_size - n) % _size];
    }
    
    /**
     * Returns the cache size
     */
    public int getCacheSize() {
        return _size;
    }
    
    /**
     * Returns the number of elements in the cache 
     */
    public int getCacheCount() {
        return _cnt;
    }
    
    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<T> interface
    // - - - - - - - - - - -
    
    public boolean hasNext() {
        return _base.hasNext();
    }

    public T next() {
        if (_cnt < _size)
            ++_cnt;
        
        int n = _baseptr;
        _baseptr = ++_baseptr % _size;
        _cache[n] =_base.next();

        // @SuppressWarnings
        // The buffer array is private, and the only things that we ever put
        // into it are known to be T objects because they are read from an
        // Iterator<? extends T>, so we know that the cast is always valid

        return (T) _cache[n];
    }


    /**
     * Removes the last item returned from the base iterator, if the base
     * iterator supports the remove operation.  The item is not removed from
     * the cache.
     * @throws UnsupportedOperationException if the base iterator does not
     * support the remove() operation
     */
    public void remove() {
        _base.remove();
    }
}
