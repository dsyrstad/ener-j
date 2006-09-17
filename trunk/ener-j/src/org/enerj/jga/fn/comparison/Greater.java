// ============================================================================
// $Id: Greater.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

/**
 * Binary Predicate that returns TRUE for arguments <b>x</b> and <b>y</b> when
 * x &gt; y.  The comparison is performed using a comparator supplied at
 * construction time, although a default comparator will be used if the nested
 * Comparable class' default constructor is used.
 * The behaviour of this class in the presence of null arguments is left to the
 * implementation of the specific Comparator, however it is generally safe to
 * assume that using null arguments will cause a NullPointerException to be
 * thrown.
 * <p>
 * To serialize a Greater predicate, the comparator passed at construction must be
 * Serializable.
 * <p> 
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Greater<T> extends BinaryPredicate<T,T> {

    static final long serialVersionUID = 8666386929519645339L;
    
    // The Comparator used to compare elements.
    private Comparator/*@*/<? super T>/*@*/ _comp;
    
    /**
     * Builds a Greater predicate using the given Comparator
     * @throws IllegalArgumentException if the argument is null
     */
    public Greater(Comparator<? super T> comp) {
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
     * Given Comparable arguments <b>x</b> and <b>y</b>, returns x &gt; y.
     *
     * @return x &gt; y. Returns false if either is null.
     */
    public Boolean fn(T x, T y) {
        if (x == null || y == null) {
            return Boolean.FALSE;
        }
        
        return Boolean.valueOf(getComparator().compare(x, y) > 0);
    }

    /**
     * Calls the Visitor's <code>visit(Greater)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Greater.Visitor)
            ((Greater.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Greater";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Greater</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Greater host);
    }
    
    /**
     * Greater predicate for use with Comparable arguments.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a Greater functor.
     */

    static public class Comparable<T extends java.lang.Comparable/*@*/<? super T>/*@*/>
            extends Greater<T>
    {
        static final long serialVersionUID = 5187118104447673700L;
        public Comparable() { super(new ComparableComparator<T>()); }
    }
}
