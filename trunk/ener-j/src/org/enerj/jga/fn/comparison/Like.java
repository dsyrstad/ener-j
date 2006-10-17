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
//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/jga/fn/comparison/Like.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.jga.fn.comparison;

import java.util.Comparator;

import org.enerj.jga.fn.BinaryPredicate;
import org.enerj.jga.util.ComparableComparator;
import org.enerj.util.LikeMatcher;

/**
 * Binary Predicate that returns TRUE for arguments <b>x</b> and <b>y</b> when
 * x like y, where y is an OQL LIKE pattern. 
 * <p>
 */
public class Like<T> extends BinaryPredicate<T, T>
{
    static final long serialVersionUID = 0;


    /**
     * Builds a Like predicate.
     */
    public Like()
    {
    }

    // BinaryPredicate interface

    /**
     * Given Comparable arguments <b>x</b> and <b>y</b>, returns x like y.
     *
     * @return x like y. If x or y are null, false is returned.
     */
    public Boolean fn(T x, T y)
    {
        if (x == null || y == null) {
            return Boolean.FALSE;
        }

        
        return new LikeMatcher(y.toString(), false, '\\').compare(x);
    }

    public void accept(org.enerj.jga.fn.Visitor v)
    {
        if (v instanceof Like.Visitor) {
            ((Like.Visitor)v).visit(this);
        }
    }

    public String toString()
    {
        return "Like";
    }


    // Acyclic Visitor

    /**
     * Interface for classes that may interpret a <b>Like</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor
    {
        public void visit(Like host);
    }
}
