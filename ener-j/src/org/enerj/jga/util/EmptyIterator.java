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
// $Id: EmptyIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
 * Iterator over an empty set of elements.
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class EmptyIterator<T> implements ListIterator<T>, Iterable<T> {
    
    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<T> iterator() { return this; }
    
    // - - - - - - - - - - - - -
    // ListIterator<T> interface
    // - - - - - - - - - - - - -
    
    /**
     * Returns false: the set of elements is empty by definition
     * @return false
     */
    public boolean hasNext() { return false; }

    /**
     * throws NoSuchElement exception
     *
     * @return nothing
     */
    public T next() { throw new NoSuchElementException(); }

    /**
     * Returns false: the set of elements is empty by definition
     * @return false
     */
    public boolean hasPrevious() { return false; }

    /**
     * throws NoSuchElement exception
     *
     * @return nothing
     */
    public T previous() { throw new NoSuchElementException(); }

    /**
     * returns the size of the list (0, in this case)
     *
     * @return 0
     */
    public int nextIndex() { return 0; }

    /**
     * returns -1, as there is no previous
     *
     * @return -1;
     */
    public int previousIndex() { return -1; }
    
    /**
     * throws UnsupportedOperationException
     */
    public void remove() { throw new UnsupportedOperationException(); }

    /**
     * throws UnsupportedOperationException
     */
    public void set (T arg) { throw new UnsupportedOperationException(); }

    /**
     * throws UnsupportedOperationException
     */
    public void add (T arg) { throw new UnsupportedOperationException(); }

}
