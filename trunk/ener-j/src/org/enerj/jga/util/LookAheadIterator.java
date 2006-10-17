// ============================================================================
// $Id: LookAheadIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

public class LookAheadIterator<T> implements Iterator<T>, Iterable<T> {
    // The base iterator
    private Iterator<? extends T> _base;

    // ring buffer, to hold the elements that have been read ahead of the
    // current position.
    private Object[] _buffer;

    // index of the next element to read from the buffer
    private int _readptr = 0;

    // index of the next element to write to the buffer
    private int _writeptr = 0;

    // number of elements in the buffer
    private int _cnt = 0;

    // size of the buffer
    private int _size;

    /**
     * Builds a LookAheadIterator that can look ahead 1 element.
     */
      
    public LookAheadIterator (Iterator<? extends T> base) {
        this(base, 1);
    }

    /**
     * Builds a LookAheadIterator that can look ahead the given number of
     * elements.
     * 
     * @throws IllegalArgumentException if max <= 0.
     */
      
    public LookAheadIterator (Iterator<? extends T> base, int max) {
        if (max <= 0)
            throw new IllegalArgumentException();
        
        _base = (base != null) ? base : new EmptyIterator<T>();
        _size = max;

        _buffer = new Object[_size];
    }

    /**
     * Returns true if there is an element at the Nth position.  Put another
     * way, returns true if there are enough elements remaining in the iterator
     * that next() could be called N times without having a
     * NoSuchElementException thrown.
     *
     * @return true if there is an element at the Nth position
     * @throws IllegalArgumentException if n < 0 or n > max lookahead
     */
    
    public boolean hasNextPlus(int n) {
        if (n < 0 || n > _size)
            throw new IllegalArgumentException();

        if (n == 0)
            return hasNext();

        return readAhead(n);
    }

    /**
     * Returns the element at the Nth position.  Put another way, returns the
     * element that the Nth call to next() would return.  The current position
     * of the iteration is not modified.
     *
     * @return the element at the Nth position
     * @throws IllegalArgumentException if n < 0 or n > max lookahead
     * @throws NoSuchElementException if the Nth position is off the end of
     *         the iteration
     */
    
    public T peek(int n) {
        if (n < 0 || n > _size)
            throw new IllegalArgumentException();

        if (!readAhead(n)) {
            throw new NoSuchElementException();
        }
            
        int offset = (_readptr + n - 1) % _size;
        
        // @SuppressWarnings
        // This generates an unchecked cast warning: it's ok since the only
        // thing that can get into the buffer came from an
        // Iterator<? extends T>, we know that the cast is always valid
        
        return (T) _buffer[offset];
    }
    
    /**
     * Returns the maximum offset that may be peeked.
     */
    public int getMaxPeekSize() {
        return _size;
    }

    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<T> interface
    // - - - - - - - - - - -
    
    public boolean hasNext() {
        if (_cnt > 0)
            return true;
        else 
            return _base.hasNext();
    }

    public T next() {
        if (_cnt == 0)
            return _base.next();

        // @SuppressWarnings
        // This generates an unchecked cast warning: it's ok since the only
        // thing that can get into the buffer came from an
        // Iterator<? extends T>, we know that the cast is always valid
        
        T value = (T) _buffer[_readptr];
        _readptr = advance(_readptr);
        _cnt--;

        return value;
    }

    // private implementation
    
    public void remove() { throw new UnsupportedOperationException(); }

        private boolean readAhead(int n) {
        if (n <= _cnt)
            return true;
        
        while (n > _cnt &&_base.hasNext()) {
            // this line generates an unchecked cast warning, although it should
            // be safe in this case: it would be difficult to find a way to
            // corrupt the private array
            _buffer[_writeptr] = (T) _base.next();
            
            _cnt++;
            _writeptr = advance(_writeptr);
        }

        return n == _cnt;
    }
    
    private int advance(int n) {
        return ++n % _size;
    }
}
