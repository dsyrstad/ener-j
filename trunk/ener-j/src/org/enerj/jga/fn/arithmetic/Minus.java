// ============================================================================
// $Id: Minus.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.arithmetic;

import java.text.MessageFormat;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.EvaluationException;

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

public class Minus<T extends Number> extends BinaryFunctor<T,T,T> {
    
    static final long serialVersionUID = -9100995808748708449L;

    // Utility class that performs the subtraction as appropriate for the
    // class given at construction
    
    private Arithmetic<T> _math;
     
    /**
     * Builds Minus functor for the given class.  The class argument must
     * be the same as the generic class argument (when generics are in use)
     * or else a ClassCastException will be thrown when the functor is used.
     *
     * @throws IllegalArgumentException if the given class has no Arithmetic
     *      implementation registered with the ArithmeticFactory
     */
    public Minus(Class<T> c) {
         _math = ArithmeticFactory.getArithmetic(c);
         if (_math == null) {
             String msg = "No implementation of Arithmetic registered for {0}";
             throw new IllegalArgumentException(MessageFormat.format(msg, new Object[]{c}));
         }
    }
    // Binary Functor interface
    
    /**
     * Given numeric arguments <b>x</b> and <b>y</b>, returns x - y.
     * @return the difference of two numeric arguments. Returns null if either is null.
     */
    public T fn(T x, T y) {
        if (x == null || y == null) {
            return null;
        }

        try {
            return _math.minus(x, y);
        }
        catch (ClassCastException ex) {
            String msg = "ClassCastException: Cannot subtract {2}[{3}] from {0}[{1}]";
            String err = MessageFormat.format(msg, new Object[]{ x.getClass(), x, y.getClass(), y});
            throw new EvaluationException(err, ex);
        }
    }
    
    /**
     * Calls the Visitor's <code>visit(Minus)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Minus.Visitor)
            ((Minus.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "Minus";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>Minus</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Minus host);
    }
}
