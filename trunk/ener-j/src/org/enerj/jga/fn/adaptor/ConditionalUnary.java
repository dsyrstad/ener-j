// ============================================================================
// $Id: ConditionalUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

public class ConditionalUnary<T,R> extends UnaryFunctor<T,R> {

    static final long serialVersionUID = -8509676654602764401L;

    // The condition to be evaluated
    private UnaryFunctor<T,Boolean> _test;

    // The functor to execute when the condition passes
    private UnaryFunctor<T,R> _then;

    // The functor to execute when the condition fails
    private UnaryFunctor<T,R> _else;

    /**
     * Builds a ConditionalUnary functor, given the condition to test, and the two
     * functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public ConditionalUnary(UnaryFunctor<T,Boolean> test, UnaryFunctor<T,R> trueFn,
                            UnaryFunctor<T,R> falseFn)
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
    public UnaryFunctor<T,Boolean> getCondition() { return _test; }

    /**
     * Returns the functor that is executed when the condition is true
     * @return the functor that is executed when the condition is true
     */
    public UnaryFunctor<T,R> getTrueFunctor() { return _then; }

    /**
     * Returns the functor that is executed when the condition is false
     * @return the functor that is executed when the condition is false
     */
    public UnaryFunctor<T,R> getFalseFunctor() { return _else; }

    // UnaryFunctor interface
    
    /**
     * Given argument <b>x</b>, evaluates test(x); if true, returns trueFn(x),
     * otherwise, returns falseFn(x).
     * 
     * @return test(x) ? trueFn(x) : falseFn(x)
     */
    public R fn(T x) {
        return _test.fn(x) ? _then.fn(x) : _else.fn(x);
    }
    
    /**
     * Calls the Visitor's <code>visit(ConditionalUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConditionalUnary.Visitor)
            ((ConditionalUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ConditionalUnary["+_test+"?"+_then+":"+_else+"]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ConditionalUnary</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConditionalUnary host);
    }
}
