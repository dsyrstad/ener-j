// ============================================================================
// $Id: MaxValue.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import java.util.Collections;
import java.util.Comparator;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Identifies the largest value in a collection.
 * <p>
 * To serialize a MaxValue, the comparator passed at construction must be
 * Serializable.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class MaxValue<T> extends UnaryFunctor<Collection<? extends T>, T> {
    
    static final long serialVersionUID = -832437021573535619L;

    private Comparator<T> _comp;

    /**
     * Builds a MaxValue functor that will use the given comparator to
     * compare elements in the collection.  Typically, the functor would compare
     * its two arguments and return the greater value.
     * @throws IllegalArgumentException if the Comparator is null
     */
    public MaxValue(Comparator<T> comp) {
        if (comp == null)
            throw new IllegalArgumentException();
        
        _comp = comp;
    }

    /**
     * Returns the comparator used to order values in the collection.
     */
    public Comparator<T> getComparator() {
        return _comp;
    }

    /**
     * Returns the largest value in the collection
     * @throws NoSuchElementException if the collection is empty
     */
    public T fn(Collection<? extends T> collection) {
        return Collections.max(collection, _comp);
    }
    
    /**
     * Calls the Visitor's <code>visit(MaxValue)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof MaxValue.Visitor)
            ((MaxValue.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "MaxValue";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>MaxValue</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(MaxValue host);
    }
}
