// ============================================================================
// $Id: Merge.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

import java.util.Comparator;
import java.util.Iterator;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.util.MergeIterator;

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

public class Merge<T>
    extends BinaryFunctor<Iterator<? extends T>, Iterator<? extends T>,
                                  MergeIterator<T>>
{
    static final long serialVersionUID = 1680420131592467899L;

    private Comparator<T> _comp;
    
    /**
     * Builds an Merge functor that will use the given comparator to compare
     * corresponding elements of two input iterators..
     * @throws IllegalArgumentException if the test is null
     */
    public Merge(Comparator<T> comp) {
        if (comp == null)
            throw new IllegalArgumentException();

        _comp = comp;
    }

    /**
     * Returns the functor used to process elements in an iteration.
     */
    public Comparator<T> getComparator() {
        return _comp;
    }

    /**
     * Apply the functor to each element in the iteration and return an iterator
     * over the results
     *
     * @return an iterator over the results of the transformation
     */
    public MergeIterator<T> fn(Iterator<? extends T> iter1,
                               Iterator<? extends T> iter2)
    {
        return new MergeIterator<T>(iter1, iter2, _comp);
    }
    
    /**
     * Calls the Visitor's <code>visit(Merge)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Merge.Visitor)
            ((Merge.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Merge";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Merge</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Merge host);
    }
}
