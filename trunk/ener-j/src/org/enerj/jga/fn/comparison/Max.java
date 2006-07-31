// ============================================================================
// $Id: Max.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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
package org.enerj.jga.fn.comparison;

import java.util.Comparator;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.util.ComparableComparator;

/**
 * Binary Functor that returns the greater of two object arguments <b>x</b>
 * and <b>y</b>.  The comparison is performed using a comparator supplied at
 * construction time, although a default comparator will be used if the nested
 * Comparable class' default constructor is used.
 * The behaviour of this class in the presence of null arguments is left to the
 * implementation of the specific Comparator, however it is generally safe to
 * assume that using null arguments will cause a NullPointerException to be
 * thrown.
 * <p>
 * To serialize a Max functor, the comparator passed at construction must be
 * Serializable.
 * <p> 
 * Copyright &copy; 2003  David A. Hall
 *
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class Max<T> extends BinaryFunctor<T,T,T> {
    
    static final long serialVersionUID = -8414037855280091672L;
    
    // the comparator used to compare values
    private Comparator/*@*/<? super T>/*@*/ _comp;

    /**
     * Builds a Max functor using the given Comparator
     * @throws IllegalArgumentException if the argument is null
     */
    public Max(Comparator<? super T> comp) {
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

    // BinaryFunctor interface
    
    /**
     * Returns the greater of two arguments, or the first if they are equal.
     * @return the greater of two arguments, or the first if they are equal.
     */
    public T fn(T x, T y) {
        return _comp.compare(x,y) >= 0 ? x : y;
    }
    
    /**
     * Calls the Visitor's <code>visit(Max)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Max.Visitor)
            ((Max.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Max";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Max</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Max host);
    }
    

    /**
     * Max functor for use with Comparable arguments.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a Max functor.
     */

    static public class Comparable<T extends java.lang.Comparable/*@*/<? super T>/*@*/>
            extends Max<T>
    {
        static final long serialVersionUID = 2442624792379403532L;
        public Comparable() { super(new ComparableComparator<T>()); }
    }
}
