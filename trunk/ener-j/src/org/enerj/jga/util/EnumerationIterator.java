// ============================================================================
// $Id: EnumerationIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * Adapts an Enumeration to the Iterator and Iterable interfaces.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class EnumerationIterator<T> implements Iterator<T>, Iterable<T> {

    // The base enumeration (as if that wasn't self-evident)
    private Enumeration<T> _base;

    /**
     * Builds an EnumerationIterator that adapts the given enumeration to the
     * Iterator and Iterable interfaces.
     * @throws IllegalArgumentException if the given enumeration is null.
     */
    
    public EnumerationIterator(Enumeration<T> enumeration)
    {
        if (enumeration == null)
            throw new IllegalArgumentException();
        
        _base = enumeration;
    }

    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<T> interface
    // - - - - - - - - - - -
    
    /**
     * Returns true if the base enumeration has elements remaining.
     * @return true if the base enumeration has elements remaining.
     */

    public boolean hasNext(){
        return _base.hasMoreElements();
    }

    /**
     * Returns the next element in the base enumeration.
     * @return the next element in the base enumeration.
     * @throws NoSuchElementException if the base enumeration is at its end.
     */

    public T next() {
        return _base.nextElement();
    }

    /**
     * throws UnsupportedOperationException: Enumerations do not support the
     * removal of elements.
     * @throws UnsupportedOperationException when called.
     */

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
