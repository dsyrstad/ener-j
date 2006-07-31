// ============================================================================
// $Id: Between.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.UnaryPredicate;
import org.enerj.jga.util.ComparableComparator;

/**
 * Unary Predicate that returns TRUE when its argument is between two given
 * values.  By default, the range is inclusive: a constructor is provided that
 * allows client code to supply two predicates that can create exclusive ranges.
 * The comparison is performed using a comparator or a pair of functors supplied
 * at construction time: a default comparator will be used if the nested
 * Comparable class' default constructor is used.
 * The behaviour of this class in the presence of null arguments is left to the
 * implementation of the specific Comparator, however it is generally safe to
 * assume that using null arguments will cause a NullPointerException to be
 * thrown.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Between<T> extends UnaryPredicate<T> {
    
    static final long serialVersionUID = 7520704443234013748L;
    
    private UnaryFunctor<T,Boolean> _ge;
    private UnaryFunctor<T,Boolean> _le;

    /**
     * Builds a Between predicate that returns TRUE when its argument is between
     * its two arguments (inclusive).  The given comparator will be used to
     * compare values.
     * @throws IllegalArgumentException when either argument is null or when
     *                                  lo &gt; hi
     */
    public Between(T lo, T hi, Comparator<? super T> comp) {
        if (lo == null || hi == null) {
            String msg = "a pair of values is required";
            throw new IllegalArgumentException(msg);
        }

        if (comp.compare(lo,hi) > 0) {
            String msg = "lo value must be less than hi value";
            throw new IllegalArgumentException(msg);
        }

        _ge = new GreaterEqual<T>(comp).bind2nd(lo);
        _le = new LessEqual<T>(comp).bind2nd(hi);
    }

    /**
     * Builds a Between predicate that returns TRUE when both of the given
     * predicates return TRUE for the same argument.  This version of the
     * constructor is provided to allow finer control over the comparisons
     * performed.
     */
    public Between(UnaryFunctor<T,Boolean> lo, UnaryFunctor<T,Boolean> hi) {
        if (lo == null || hi == null) {
            String msg = "a pair of predicates is required";
            throw new IllegalArgumentException(msg);
        }

        _ge = lo;
        _le = hi;
    }
    
    // UnaryPredicate interface

    /**
     * Given argument <b>x</b>, returns TRUE if x is between lo and hi.
     * 
     * @return lo &lt; x &lt; hi
     */
    public Boolean fn(T x) {
        return Boolean.valueOf(_ge.fn(x).booleanValue() && _le.fn(x).booleanValue());
    }

    /**
     * Calls the Visitor's <code>visit(Between)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Between.Visitor)
            ((Between.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Between";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret an <b>Between</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Between host);
    }

    /**
     * Between functor for use with Comparable arguments.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a Between functor.
     */

    static public class Comparable<T extends java.lang.Comparable/*@*/<? super T>/*@*/>
            extends Between<T>
    {
        static final long serialVersionUID = 4385771596777515479L;
        public Comparable(T lo, T hi) { super(lo, hi, new ComparableComparator<T>()); }
    }
}
