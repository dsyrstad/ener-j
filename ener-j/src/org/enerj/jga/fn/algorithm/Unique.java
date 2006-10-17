// ============================================================================
// $Id: Unique.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.util.UniqueIterator;

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

public class Unique<T>
    extends UnaryFunctor<Iterator<? extends T>, UniqueIterator<T>>
{
    static final long serialVersionUID = 603897787127100783L;
    
    private BinaryFunctor<T,T,Boolean> _fn;

    /**
     * Builds an Unique functor that will use EqualTo to compare successive
     * elements.
     * @throws IllegalArgumentException if the test is null
     */
    public Unique() {
        this(new EqualTo<T>());
    }

    /**
     * Builds an Unique functor that will use the given functor to compare
     * successive elements.  The functor is required to return TRUE when its
     * arguments are the same.
     * @throws IllegalArgumentException if the test is null
     */
    public Unique(BinaryFunctor<T,T,Boolean> test) {
        if (test == null)
            throw new IllegalArgumentException();
        
        _fn = test;
    }

    /**
     * Returns the functor used to process elements in an iteration.
     */
    public BinaryFunctor<T,T,Boolean> getFunction() {
        return _fn;
    }

    /**
     * Apply the functor to each element in the iteration and return an iterator
     * over the results
     *
     * @return an iterator over the results of the transformation
     */
    public UniqueIterator<T> fn(Iterator<? extends T> iterator) {
        return new UniqueIterator<T>(iterator, _fn);
    }
    
    /**
     * Calls the Visitor's <code>visit(Unique)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Unique.Visitor)
            ((Unique.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Unique["+_fn+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Unique</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Unique host);
    }
}
