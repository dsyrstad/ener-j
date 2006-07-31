// ============================================================================
// $Id: ArrayBinary.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
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

package org.enerj.jga.fn.property;

import org.enerj.jga.fn.BinaryFunctor;

/**
 * Binary Functor that returns its arguments in a two element array.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class ArrayBinary<T1,T2> extends BinaryFunctor<T1,T2,Object[]> {
    
    static final long serialVersionUID = -863270674599297166L;

    // BinaryFunctor interface
    
    /**
     * Returns its arguments in a two element array
     * <p>
     * @return the object built by the constructor
     */
    public Object[] fn(T1 arg1, T2 arg2) {
        return new Object[] { arg1, arg2 };
    }
    
    /**
     * Calls the Visitor's <code>visit(ArrayBinary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ArrayBinary.Visitor)
            ((ArrayBinary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ArrayBinary";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ArrayBinary</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ArrayBinary host);
    }
}
