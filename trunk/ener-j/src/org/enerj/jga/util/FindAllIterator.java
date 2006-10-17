// ============================================================================
// $Id: FindAllIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
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

public class FindAllIterator<T>
    implements Iterable<Iterator<? extends T>>, Iterator<Iterator<? extends T>>
{

    // the base iterator
    private Iterator<? extends T> _iter;

    // the functor
    private UnaryFunctor<Iterator<? extends T>, ? extends Iterator<T>> _fn;

    // three state flag indicating that the test was done and its results
    private Boolean _tested;
    
    /**
     * Builds a FindAllIterator that will apply the given functor to the given
     * iterator.
     */
    public FindAllIterator(Iterator<? extends T> it,
                   UnaryFunctor<Iterator<? extends T>,? extends Iterator<T>> fn)
    {
        _iter = it;
        _fn = fn;
    }
 
    // - - - - - - - - - - -
    // Iterable<T> interface
    // - - - - - - - - - - -

    public Iterator<Iterator<? extends T>> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<T> interface
    // - - - - - - - - - - -

    public boolean hasNext() {
        _iter = _fn.fn(_iter);
        _tested = Boolean.valueOf(_iter.hasNext());
        return _tested;
    }

    public Iterator<? extends T> next() {
        if (_tested == null)
            hasNext();

        if (!_tested)
            throw new NoSuchElementException();
        
        _tested = null;
        return /*(Iterator<T>)*/ _iter;
    }

    public void remove() {
        _iter.remove();
    }
}
