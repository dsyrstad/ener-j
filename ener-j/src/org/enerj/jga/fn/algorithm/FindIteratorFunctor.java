// ============================================================================
// $Id: FindIteratorFunctor.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
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
package org.enerj.jga.fn.algorithm;

import java.util.Iterator;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.FindIterator;

/**
 * Abstract base class for functors that take an input iterator and return a
 * FindIterator.  This class provides the ability to prevent excessive
 * iterator wrapping by tracking the identity of the functor that has produced
 * the wrapped iterator, and not re-wrapping an iterator if it was produced by
 * the same object that is currently requesting a wrapped iterator.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

abstract public class FindIteratorFunctor<T>
    extends UnaryFunctor<Iterator<? extends T>, FindIterator<T>>
{
    /**
     * Conditionally wraps the input iterator in a FindIterator for return.
     * Uses a Wrapper to determine if the input iterator is one that this
     * object has already built and returned: if so, then there is no need
     * to wrap it again.
     */
    protected FindIterator<T> wrap(Iterator<? extends T> iterator) {
        if (iterator instanceof Wrapper && ((Wrapper)iterator).owner == this)
            // @SuppressWarnings
            // The preceding condition can only be true if the iterator argument was
            // created by the constructor call three lines hence, and will always be
            // true in that case.
           return (FindIterator<T>) iterator;
  
        return new Wrapper<T>(this, iterator);

    }

    /**
     * Wrapper class used to prevent excessive creation of wrapper interators.
     * This class stores a reference to its caller (which is an instance of the
     * enclosing class).  
     */
    static private class Wrapper<T> extends FindIterator<T> {
        private FindIteratorFunctor<T> owner;
        public Wrapper(FindIteratorFunctor<T> owner, Iterator<? extends T> iter) {
            super(iter); this.owner = owner;
        }
    }
}
