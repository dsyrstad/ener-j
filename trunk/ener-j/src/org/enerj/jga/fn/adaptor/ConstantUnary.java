// ============================================================================
// $Id: ConstantUnary.java,v 1.4 2006/02/24 03:00:42 dsyrstad Exp $
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

import org.enerj.jga.fn.UnaryFunctor;
    
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

public class ConstantUnary<T,V> extends UnaryFunctor<T,V> {

    static final long serialVersionUID = 236974866552446215L;

    // the constant value
    private V _value;

    /**
     * Builds a Constant functor for the given value.  The value may be null:
     * in that case, evaluating the functor will return null.
     */
    public ConstantUnary (V val) {
        _value = val;
    }
    
    // UnaryFunctor interface

    /**
     * Given one argument, summarily ignores it and returns the constant value
     * given at construction.  The argument will not be evaluated in any way by
     * this functor.
     *
     * @return the constant value given at construction
     */
    
    public V fn(T unused) {
        return _value;
    }

    /**
     * Calls the Visitor's <code>visit(ConstantUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConstantUnary.Visitor)
            ((ConstantUnary.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "const " +_value;
   }
    
     // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret a <b>ConstantUnary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConstantUnary host);
    }
}
        
