// ============================================================================
// $Id: ComparatorFn.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryFunctor;
import java.io.Serializable;
import java.util.Comparator;

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

public class ComparatorFn<T> extends BinaryFunctor<T,T,Integer> implements Comparator<T> {

    static final long serialVersionUID = -7851342943467256913L;

    private Comparator<T> _comp;

    /**
     * Builds the ComparatorFn wrapped around the given Comparator.
     *
     * @throws NullPointerException if no Comparator is passed.
     */
    public ComparatorFn(Comparator<T> comp) {
        if (comp == null) {
            throw new IllegalArgumentException("Comparator may not be null");
        }
        
        _comp = comp;
    }

    /**
     * Returns the comparator in use by this functor
     */
    public Comparator<T> getComparator() { return _comp; }

    // BinaryFunctor interface
    
    /**
     * Given arguments <b>x</b> and <b>y</b>, return the result of the
     * Comparator's <code>compare(x,y)</code> method, wrapped in an Integer.
     * Whether or not a NullPointerException is thrown if either x or y are
     * null is up to the Comparator
     * 
     * @return the result of the Comparator's <code>compare(x,y)</code> method
     */
    public Integer fn(T x, T y) {
        return new Integer(compare(x, y));
    }
    
    /**
     * Calls the Visitor's <code>visit(ComperatorFn)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ComparatorFn.Visitor)
            ((ComparatorFn.Visitor)v).visit(this); }

    // Comparator interface

    public int compare(T x, T y) {
        return _comp.compare(x, y);
    }

    // Object overrides

    public String toString() {
        return "ComperatorFn";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>ComparatorFn</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ComparatorFn host);
    }
}


                                      
