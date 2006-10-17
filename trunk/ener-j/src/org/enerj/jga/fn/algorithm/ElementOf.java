// ============================================================================
// $Id: ElementOf.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
package org.enerj.jga.fn.algorithm;

import java.util.Collection;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.BinaryPredicate;
import org.enerj.jga.util.FindIterator;

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

public class ElementOf<T> extends BinaryPredicate<T, Collection<? extends T>> {
    
    static final long serialVersionUID = 4639100512962835854L;
    
    // The functor used to compare the element to the items in the collection
    private BinaryFunctor<T,T,Boolean> _eq;

    /**
     * Builds an ElementOf predicate that will use the collection's built in
     * contains() method.  This form is potentially more efficient than the
     * other constructed form, if the collection passed at evaluation
     * implements contains() using an algorithm more efficient than a linear
     * search.
     */
    public ElementOf() {}
    
    /**
     * Builds an ElementOf predicate that will use the given functor to
     * determine collection membership.  The collection will be searched
     * sequentially for the first element for which the functor returns true.
     */
    public ElementOf(BinaryFunctor<T,T,Boolean> eq) {
        _eq = eq;
    }

    /**
     * Returns the (possibly null) functor used to compare a value to the
     * contents of a collection.
     */
    public BinaryFunctor<T,T,Boolean> getComparisonFn() {
        return _eq;
    }

    /**
     * Return true if the given value is an element of the collection
     */
    public Boolean fn(T value, Collection<? extends T> collection) {
        if (_eq == null)
            return collection.contains(value);
        else {
            FindIterator<T> finder =
                new FindIterator<T>(collection.iterator());
            UnaryFunctor<T,Boolean> uf = _eq.bind2nd(value);
            return finder.findNext(uf);
        }
    }
    
    /**
     * Calls the Visitor's <code>visit(ElementOf)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ElementOf.Visitor)
            ((ElementOf.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ElementOf";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>ElementOf</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ElementOf host);
    }
}
