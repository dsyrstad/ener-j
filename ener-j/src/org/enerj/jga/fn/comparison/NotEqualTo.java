// ============================================================================
// $Id: NotEqualTo.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryPredicate;
import java.util.Comparator;

/**
 * Binary Predicate that returns TRUE for object arguments <b>x</b> and
 * <b>y</b> when x != y using the built-in equals() method or an optional
 * Comparator given at construction.  By default, this object will not throw
 * NullPointerException: it will return true if either runtime arguments is null
 * but false if both are null.  If an optional comparator is used, then its
 * implementation will determine if a NullPointerException is thrown when passed
 * a null argument.
 * <p>
 * To serialize an NotEqual functor, the comparator passed at construction(if
 * any) must be Serializable.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/


public class NotEqualTo<T> extends BinaryPredicate<T,T> {
    
    static final long serialVersionUID = 1892296018875494847L;

    // An optional comparator used to compare values
    Comparator<? super T> _comp;

    /**
     * Builds an EqualTo that uses the built-in equals method.
     */
    public NotEqualTo() {}
    
    /**
     * Builds an EqualTo that uses the given comparator.
     */
    public NotEqualTo(Comparator<? super T> comp) {
        _comp = comp;
    }
    
    /**
     * Returns the comparator in use by this functor, if any.
     * @return the comparator in use by this functor, if any.
     */
    public Comparator<? super T> getComparator() { return _comp; }
    
    // BinaryPredicate interface

    /**
     * Given arguments <b>x</b> and <b>y</b>, returns !(x.equals(y)).  Will not
     * throw NullPointerException if either x or y are null: will return true
     * if either x or y (but not both) are null, will return false if both x
     * and y are null.
     * 
     * @return !(x.equals(y))
     */
    public Boolean fn(T x, T y) {
        if (_comp == null)
            return (x == null) ? y != null :
                   (y == null) ? true : ! (x.equals(y));
        else
            return _comp.compare(x, y) != 0;
    }

    /**
     * Calls the Visitor's <code>visit(NotEqualTo)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof NotEqualTo.Visitor)
            ((NotEqualTo.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "NotEqualTo";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>NotEqualTo</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(NotEqualTo host);
    }
}
