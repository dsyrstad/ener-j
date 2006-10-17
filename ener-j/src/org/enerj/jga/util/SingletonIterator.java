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
// ============================================================================
// $Id: SingletonIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * Iterates over a single item.  The iterator is structured as a list iterator,
 * but the list is a fixed size (1).  The value may be changed after it has
 * been retrieved at least one time.
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class SingletonIterator<T> implements ListIterator<T>, Iterable<T> {
    // The value returned by the iterator
    private T _value;

    // Flag indicating that the 'pointer' is set to the beginning of the
    // imaginary list.
    private boolean _beforeValue = true;

    // Flag indicating if the current value can be changed.
    private boolean _canSetValue = false;

    /**
     * Builds a SingletonIterator that will return the given value.
     */
    public SingletonIterator(T value) {
        _value = value;        
    }

    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - - - -
    // ListIterator<T> interface
    // - - - - - - - - - - - - -
    
    /**
     * Returns true if the item has not yet been returned.
     * @return true if the item has not yet been returned
     */
    public boolean hasNext() {
        return _beforeValue;
    }

    /**
     * Returns the single item
     * @return the single item
     * @throws NoSuchElementException if the item has already been returned
     */
    public T next() {
        if (!_beforeValue)
            throw new NoSuchElementException();

        _beforeValue = false;
        _canSetValue = true;
        return _value;
    }

    /**
     * Returns true if the item has not yet been returned.
     * @return true if the item has not yet been returned
     */
    public boolean hasPrevious() {
        return !_beforeValue;
    }

    /**
     * Returns the single item
     * @return the single item
     * @throws NoSuchElementException if the item has already been returned
     */
    public T previous() {
        if (_beforeValue)
            throw new NoSuchElementException();

        _beforeValue = true;
        _canSetValue = true;
        return _value;
    }

    public int nextIndex() {
        return _beforeValue ? 0 : 1;
    }

    public int previousIndex() {
        return _beforeValue ? -1 : 0;
    }
        
    public void add(T arg)
    {
        throw new UnsupportedOperationException();
    }

    public void set(T value) {
        if (!_canSetValue)
            throw new IllegalStateException();

        _value = value;
        _canSetValue = false;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
