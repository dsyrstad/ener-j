// ============================================================================
// $Id: ConditionalBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

public class ConditionalBinary<T1,T2,R> extends BinaryFunctor<T1,T2,R> {

    static final long serialVersionUID = -2682605534388798188L;
    
    // The condition to be evaluated
    private BinaryFunctor<T1,T2,Boolean> _test;

    // The functor to execute when the condition passes
    private BinaryFunctor<T1,T2,R> _then;

    // The functor to execute when the condition fails
    private BinaryFunctor<T1,T2,R> _else;

    /**
     * Builds a ConditionalBinary functor, given the condition to test, and the two
     * functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public ConditionalBinary(BinaryFunctor<T1,T2,Boolean> test, BinaryFunctor<T1,T2,R> trueFn,
                       BinaryFunctor<T1,T2,R> falseFn)
    {
        if (test == null || trueFn == null || falseFn == null) {
            throw new IllegalArgumentException("Three functors are required");
        }

        _test = test;
        _then = trueFn;
        _else = falseFn;
    }

    /**
     * Returns the test functors
     * @return the test functors
     */
    public BinaryFunctor<T1,T2,Boolean> getCondition() { return _test; }

    /**
     * Returns the functor that is executed when the condition is true
     * @return the functor that is executed when the condition is true
     */
    public BinaryFunctor<T1,T2,R> getTrueFunctor() { return _then; }

    /**
     * Returns the functor that is executed when the condition is false
     * @return the functor that is executed when the condition is false
     */
    public BinaryFunctor<T1,T2,R> getFalseFunctor() { return _else; }

    // BinaryFunctor interface
    
    /**
     * Given arguments <b>x</b> and <b>x</b> evaluates test(x,y); if true,
     * returns trueFn(x,y), otherwise, returns falseFn(x,y).
     * 
     * @return test(x,y) ? trueFn(x,y) : falseFn(x,y)
     */
    public R fn(T1 x, T2 y) {
        return _test.fn(x,y) ? _then.fn(x,y) : _else.fn(x,y);
    }
    
    /**
     * Calls the Visitor's <code>visit(ConditionalBinary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConditionalBinary.Visitor)
            ((ConditionalBinary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ConditionalBinary["+_test+"?"+_then+":"+_else+"]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ConditionalBinary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConditionalBinary host);
    }
}
