// ============================================================================
// $Id: ConstantBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryFunctor;
    
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

public class ConstantBinary<T1,T2,V> extends BinaryFunctor<T1,T2,V> {

    static final long serialVersionUID = 2834438191454731796L;

    // The constant value.
    private V _value;

    /**
     * Builds a Constant functor for the given value.  The value may be null:
     * in that case, evaluating the functor will return null.
     */
    public ConstantBinary (V val) {
        _value = val;
    }

    // BinaryFunctor interface
    
    /**
     * Given two arguments, ignores both and returns the constant value given
     * at construction.  Neither argument will be evaluated in any way by this
     * functor.
     *
     * @return the constant value given at construction
     */
    
    public V fn(T1 unused, T2 ignored) {
        return _value;
    }

    /**
     * Calls the Visitor's <code>visit(Constant)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConstantBinary.Visitor)
            ((ConstantBinary.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "const " +_value;
    }
    
     // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret a <b>ConstantBinary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConstantBinary host);
    }
}
        
