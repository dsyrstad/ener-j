// ============================================================================
// $Id: FindElement.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import java.util.Iterator;
import java.util.Vector;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.FindIterator;

/**
 * Locates values from a given collection in an iteration.
 * <p>
 * To Serialize a FindElement, the generic parameter T must be serializable.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class FindElement<T> extends FindIteratorFunctor<T> {
    
    static final long serialVersionUID = -1746637029942790280L;

    // The predicate used to compare values; can be null
    private BinaryFunctor<T,T,Boolean> _bf;

    // The predicate used to find the next qualifying element in an iteration
    private UnaryFunctor<T,Boolean> _uf;

    // The collection of values that are being sought
    private Collection<? extends T> _elements;

    /**
     * Builds a FindElement functor that locates values in the given collection
     * using the contains() method.
     */
    public FindElement(Collection<? extends T> elements) {
        _elements = (elements == null) ? new Vector<T>() : elements;
        _uf = new ElementOf<T>().bind2nd(elements);
    }

    /**
     * Builds a FindElement functor that locates values in the given collection
     * using the given functor.  The functor is expected to compare two values
     * and return TRUE if they are determined to be equal.
     */
    public FindElement(Collection<? extends T> elements,
                       BinaryFunctor<T,T,Boolean> eq )
    {
        _bf = eq;
        _elements = (elements == null) ? new Vector<T>() : elements;
        _uf = new ElementOf<T>(eq).bind2nd(elements);
    }

    /**
     * Returns the set of values being sought.
     */
    public Collection<? extends T> getElementSet() {
        return Collections.unmodifiableCollection(_elements);
    }

    /**
     * Returns the (possibly null) functor used to compare a value to the
     * contents of the given collection.
     */
    public BinaryFunctor<T,T,Boolean> getComparisonFn() {
        return _bf;
    }

    // UnaryFunctor Interface
    
    /**
     * Finds the first/next element in the iteration that is an element of the
     * given collection.
     * 
     * @return an iterator whose next() [if it hasNext()] points to the next
     * element in the iteration that is an element of the given collection.  If
     * no such element exists, then the returned iterator's hasNext() will be
     * false.
     */
    public FindIterator<T> fn(Iterator<? extends T> iterator) {
        FindIterator<T> finder = wrap(iterator);
        finder.findNext(_uf);
        return finder;
    }

    /**
     * Calls the Visitor's <code>visit(FindElement)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FindElement.Visitor)
            ((FindElement.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "FindElement";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>FindElement</b>
     * functor
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FindElement host);
    }

}
