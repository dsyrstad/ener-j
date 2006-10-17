// ============================================================================
// $Id: FindMismatch.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

package org.enerj.jga.fn.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.comparison.NotEqualTo;
import org.enerj.jga.util.EmptyIterator;
import org.enerj.jga.util.FindIterator;
import org.enerj.jga.util.LookAheadIterator;
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

public class FindMismatch<T> extends LookAheadFunctor<T> {
    
    static final long serialVersionUID = -5406536818520367561L;
    
    // The collection of elements against which an iteration will be compared
    private Collection<? extends T> _elements;

    // functor used to compare an element in the iteration and the collection
    private BinaryFunctor<T,T,Boolean> _bf;
    
    // The iterator used for a given call to fn 
    private LookAheadIterator<T> _elemIter;

    // The element that was not matched
    private T _mismatch;
    
    /**
     * Builds a FindMismatch functor that uses &#33;equals() to compare elements.
     * If the collection is null, an empty collection will be used for
     * comparison.
     */
    public FindMismatch(Collection<? extends T> elements) {
        this(elements, new NotEqualTo<T>());
    }

    /**
     * Builds a FindMismatch functor that uses the given functor to compare
     * elements.  The functor is expected to compare two arguments and return
     * TRUE if they are <b>not</b> equal.  If the collection is null, an empty
     * collection will be used for comparison.
     */
    public FindMismatch(Collection<? extends T> elements,
                        BinaryFunctor<T,T,Boolean> neq)
    {
        _elements = (elements == null) ? new Vector<T>() : elements;
        _bf = neq;
    }

    /**
     * Builds one-time use FindMismatch finder that will test against the
     * contents of an iteration rather than a collection.  The functor is
     * expected to compare two arguments and return TRUE if they are <b>not</b>
     * equal.
     * <p>
     * A FindMismatch built this way cannot be used more than once as the
     * contents of the iteration will be consumed in the first execution.
     */
    public FindMismatch(Iterator<? extends T> iter,
                        BinaryFunctor<T,T,Boolean> neq)
    {
        if (iter == null)
            iter = new EmptyIterator<T>();

        _elemIter = new LookAheadIterator<T>(iter);
        _bf = neq;
    }

    /**
     * Returns the collection against which the argument will be compared.  When
     * called on a FindMismatch built with an iteration rather than a collection,
     * this method will return null
     */
    public Collection<? extends T> getElements() {
        return (_elements == null) ? null :
            Collections.unmodifiableCollection(_elements);
    }
    
    /**
     * Returns the functor that is used to test matching elements
     */
    
    public BinaryFunctor<T,T,Boolean> getComparisonFn() {
        return _bf;
    }

    /**
     * Returns the mismatched element in the given collection/iteration on the
     * last call to fn().  If the iterator passed to the last call to fn() had
     * no mismatch for the length of the collection, then this method returns
     * null.
     * @throws IllegalStateException if called before a call to fn.
     */

    // NOTE: the signature changed to fix bug 919269.  The old signature was
    // public LookAheadIterator<T> getMismatchedElement
    
    public T getMismatchedElement() {
        if (_elemIter == null)
            throw new IllegalStateException();

        return _mismatch;
    }


    // UnaryFunctor Interface

    /**
     * Locates the first/next element in an iteration that is not the same as
     * the corresponding element in the given collection/iteration.
     * @return an iterator whose next() [if it hasNext()] points to the next
     * element in the iteration that does not match the corresponding element in
     * the given collection.  If no such element exists and the iteration is
     * longer than the the given collection, then the returned iterator's next()
     * points to the first element that does not exist in the given collection.
     * If no mismatched element exists and the collection is at least as long as
     * the iteration, then the returned iterator's hasNext() will be false.
     */
    public LookAheadIterator<T> fn(Iterator<? extends T> iterator) {
        if (_elemIter == null)
            _elemIter = new LookAheadIterator<T>(_elements.iterator());
        
        LookAheadIterator<T> lai = wrap(iterator, 1);
        while (lai.hasNextPlus(1) && _elemIter.hasNextPlus(1)) {
            T arg1 = lai.peek(1);
            T arg2 = _elemIter.peek(1);
            if (_bf.fn(arg1,arg2)) {
                _mismatch = _elemIter.next();
                return lai;
            }
            lai.next();
            _elemIter.next();
        }

        return lai;
    }

    /**
     * Calls the Visitor's <code>visit(FindMismatch)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FindMismatch.Visitor)
            ((FindMismatch.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "FindMismatch";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>FindMismatch</b>
     * functor
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FindMismatch host);
    }

}
