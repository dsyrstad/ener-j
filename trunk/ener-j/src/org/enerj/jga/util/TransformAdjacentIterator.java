// ============================================================================
// $Id: TransformAdjacentIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.NoSuchElementException;
import org.enerj.jga.fn.BinaryFunctor;
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

public class TransformAdjacentIterator<T,R> implements Iterator<R>,Iterable<R> {

    // The base iterator
    private LookAheadIterator<? extends T> _base;

    // Functor applied to adjacent values in the base iterator
    private BinaryFunctor<T,T,R> _fn;

    /**
     * Builds a TransormAdjacentIterator that applies the given functor to
     * adjacent elements of the given base iterator.
     */
    public TransformAdjacentIterator(Iterator<? extends T> iter,
                                     BinaryFunctor<T,T,R> fn)
    {
        _base = new LookAheadIterator<T>(iter, 2);
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
        return _base.hasNextPlus(2);
    }

    public R next() {
        T next = _base.next();
        return _fn.fn(next,_base.peek(1));
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
