// ============================================================================
// $Id: ArrayIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2005  David A. Hall
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
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Iterates over an array of objects.  Not safe for use by multiple threads.
 * <p>
 * Copyright &copy; 2005  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class ArrayIterator<T> implements ListIterator<T>, Iterable<T> {
    
    // the array being iterated over
    private T[] _array;

    // the current pointer into the array
    private int _idx;

    // Null on initialization, TRUE when next() was the last move called,
    // FALSE when previous() was the last move called.
    private Boolean _goingForward;
    /**
     * Builds an ArrayIterator for the given array
     */
    public ArrayIterator(T[] array) { _array = array; }

    /**
     * 
     */
    public Iterator<T> iterator() { return this; }

    public boolean hasNext() { return _idx < _array.length; }

    public boolean hasPrevious() { return _idx > 0; }

    public int nextIndex() { return _idx; }

    public int previousIndex() { return _idx - 1; }

    public T next() {
        if (_idx >= _array.length )
            throw new NoSuchElementException();

        _goingForward = Boolean.TRUE;
        return _array[_idx++];
    }

    public T previous() {
        if (_idx <= 0)
            throw new NoSuchElementException();

        _goingForward = Boolean.FALSE;
        return _array[--_idx];
    }

    public void set(T value) {
        if (_goingForward == null)
            throw new IllegalStateException();

        if (_goingForward)
            _array[_idx - 1] = value;
        else
            _array[_idx] = value;
    }

    // Can't support either of these, as an array is a fixed size structure
    public void add(T value) {
        _goingForward = null;
        throw new UnsupportedOperationException();
    }
    
    public void remove() {
        _goingForward = null;
        throw new UnsupportedOperationException();
    }

    /**
     * Produce an ArrayIterator over the given array.
     */
    static public <E> ArrayIterator<E> iterate(E[] arr) {
        return new ArrayIterator<E>(arr);
    }
}
