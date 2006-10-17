// ============================================================================
// $Id: Less.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

package org.enerj.jga.fn.comparison;

import java.util.Comparator;
import org.enerj.jga.fn.BinaryPredicate;
import org.enerj.jga.util.ComparableComparator;

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

public class Less<T> extends BinaryPredicate<T,T> {
    
    static final long serialVersionUID = 4250101315339441676L;

    // The Comparator used to compare elements.
    private Comparator/*@*/<? super T>/*@*/ _comp;
    
    /**
     * Builds a Less predicate using the given Comparator
     * @throws IllegalArgumentException if the argument is null
     */
    public Less(Comparator<? super T> comp) {
        if (comp == null) {
            throw new IllegalArgumentException("Comparator may not be null");
        }
        
        _comp = comp;
    }

    /**
     * Returns the comparator in use by this functor
     * @return the comparator in use by this functor
     */
    public Comparator<? super T> getComparator() { return _comp; }
    
    // BinaryPredicate interface
    
    /**
     * Given Comparable arguments <b>x</b> and <b>y</b>, returns x &lt; y.
     *
     * @return x &lt; y. Returns false if either is null.
     */
    public Boolean fn(T x, T y) {
        if (x == null || y == null) {
            return Boolean.FALSE;
        }

        return Boolean.valueOf(_comp.compare(x, y) < 0);
    }

    /**
     * Calls the Visitor's <code>visit(Less)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Less.Visitor)
            ((Less.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Less";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Less</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Less host);
    }

    /**
     * Less predicate for use with Comparable arguments.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a Less functor.
     */

    static public class Comparable<T extends java.lang.Comparable/*@*/<? super T>/*@*/>
            extends Less<T>
    {
        static final long serialVersionUID = -2207237027980811852L;
        public Comparable() { super(new ComparableComparator<T>()); }
    }
}

