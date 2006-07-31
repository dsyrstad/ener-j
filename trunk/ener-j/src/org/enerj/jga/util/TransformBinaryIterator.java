// ============================================================================
// $Id: TransformBinaryIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.BinaryFunctor;

/**
 * Iterator that returns the results of applying the given functor to
 * corresponding elements of two given iterators.  When either of the two
 * base iterators has been exhausted, this iterator's hasNext() will return
 * false.  
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class TransformBinaryIterator<T1,T2,R> implements Iterator<R>,Iterable<R>
{
    // The two base iterators
    private Iterator<? extends T1> _i1;
    private Iterator<? extends T2> _i2;
    
    // Functor applied to corresponding elements of the base iterator
    private BinaryFunctor<T1,T2,R> _bf;
        
    /**
     * Builds a TransformBinaryIterator that applies the given functor to
     * corresponding elements of the given base iterators.
     */
    public TransformBinaryIterator(Iterator<? extends T1> i1,
                                   Iterator<? extends T2> i2,
                                   BinaryFunctor<T1,T2,R> bf)
    {
        _i1 = i1;
        _i2 = i2;
        _bf = bf;
    }

    // - - - - - - - - - - -
    // Iterable<R> interface
    // - - - - - - - - - - -

    public Iterator<R> iterator() { return this; }
    
    // - - - - - - - - - - -
    // Iterator<R> interface
    // - - - - - - - - - - -
    
    public boolean hasNext() {
        return _i1.hasNext() && _i2.hasNext();
    }

    public R next() {
        return _bf.fn(_i1.next(), _i2.next());
    }
        
    public void remove() { throw new UnsupportedOperationException(); }
}
