// ============================================================================
// $Id: ValueOf.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.arithmetic;

import java.text.MessageFormat;
import org.enerj.jga.fn.EvaluationException;
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

public class ValueOf<T extends Number,R extends Number> extends UnaryFunctor<T,R> {
    
    static final long serialVersionUID = -5844974650711636917L;

    private Arithmetic<R> _math;

    private Class<R> _class;
     
    /**
     * Builds ValueOf functor for the given class.  The class argument must
     * be the same as the generic class argument (when generics are in use)
     * or else a ClassCastException will be thrown when the functor is used.
     *
     * @throws IllegalArgumentException if the given class has no Arithmetic
     *      implementation registered with the ArithmeticFactory. 
     */
    public ValueOf(Class<R> c) {
        _class = c;
        _math = ArithmeticFactory.getArithmetic(c);
        if (_math == null) {
            String msg = "No implementation of Arithmetic registered for {0}";
            throw new IllegalArgumentException(MessageFormat.format(msg, new Object[]{c}));
        }
    }
    
    // Unary Functor interface
    
    /**
     * Given numeric argument <b>x</b>, converts x to the type passed at construction.
     * 
     * @return the converted version of its numeric argument. If the argument is null, null is returned.
     */
    public R fn(T x) {
        if (x == null) {
            return null;
        }

        return _math.valueOf(x);
    }
    
    /**
     * Calls the Visitor's <code>visit(ValueOf)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ValueOf.Visitor)
            ((ValueOf.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "ValueOf["+_class.getName()+"]";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>ValueOf</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ValueOf host);
    }
}
