// ============================================================================
// $Id: TransformIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.NoSuchElementException;
import org.enerj.jga.fn.UnaryFunctor;
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

public class TransformIterator<T,R> implements Iterator<R>, Iterable<R> {

    // The base iterator
    private Iterator<? extends T> _base;

    // Functor applied to each element of the base iterator
    private UnaryFunctor<T,R> _fn;

    /**
     * Builds a TransformIterator that applies the given functor to each element
     * of the given base iterator.
     */
    public TransformIterator(Iterator<? extends T> iter, UnaryFunctor<T,R> fn) {
        _base = iter;
        _fn = fn;
    }

    // - - - - - - - - - - -
    // Iterable<R> interface
    // - - - - - - - - - - -

    public Iterator<R> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<R> interface
    // - - - - - - - - - - -
    
    public boolean hasNext(){
        return _base.hasNext();
    }

    public R next() {
        return _fn.fn(_base.next());
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
