// ============================================================================
// $Id: Count.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

import java.util.Iterator;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.fn.comparison.EqualTo;
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

public class Count<T> extends UnaryFunctor<Iterator<? extends T>,Long> {

    static final long serialVersionUID = 5477750093695096889L;    
    
    // the predicate used to determine if an element should be counted
    private UnaryFunctor<T,Boolean> _eq;

    /**
     * Builds a Count functor that counts instances of a specific value in an
     * iteration, using the value's equals() method.
     */
    public Count (T value) {
        this(new EqualTo<T>().bind2nd(value));
    }
    
    /**
     * Builds a Count functor that counts instances of a specific value in an
     * iteration, using the given Equality predicate.
     */
    public Count (Equality<T> eq, T value) {
        this(eq.bind2nd(value));
    }

    /**
     * Builds a Count functor that counts instances in an iteration for which
     * the given predicate is true
     */
    public Count (UnaryFunctor<T,Boolean> eq) {
        _eq = eq;
    }

    /**
     * Returns the predicate used to determine if an element should be counted.
     */
    public UnaryFunctor<T,Boolean> getComparisonFn() {
        return _eq;
    }

    /**
     * Returns the number of elements in the iteration that meet the given
     * criteria.
     */
    public Long fn (Iterator<? extends T> iterator) {
        long count = 0;
        FindIterator<T> finder = new FindIterator<T>(iterator);
        
        while (finder.findNext(_eq)) {
            ++count;
            finder.next();
        }
     
        return new Long(count);
    }

    /**
     * Calls the Visitor's <code>visit(Count)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Count.Visitor)
            ((Count.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Count["+_eq+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Count</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Count host);
    }
}
