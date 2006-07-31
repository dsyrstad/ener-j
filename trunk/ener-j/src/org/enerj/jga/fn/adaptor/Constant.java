// ============================================================================
// $Id: Constant.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.fn.Generator;

/**
 * Functor that returns the constant value given at construction.  
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class Constant<V> extends Generator<V> {

    static final long serialVersionUID = 8451989044496281343L;

    private V _value;

    /**
     * Builds a Constant functor for the given value.  The value may be null:
     * in that case, evaluating the functor will return null.
     */
    public Constant (V val) {
        _value = val;
    }


    // Generator interface

    /**
     * Returns the constant value given at construction
     * @return the constant value given at construction
     */
    
    public V gen() {
        return _value;
    }

    /**
     * Calls the Visitor's <code>visit(Constant)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Constant.Visitor)
            ((Constant.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "const " +_value;
    }
    
     // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret a <b>Constant</b> generator.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Constant host);
    }
}
        
