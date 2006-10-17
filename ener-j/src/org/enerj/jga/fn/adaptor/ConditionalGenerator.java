// ============================================================================
// $Id: ConditionalGenerator.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.fn.Generator;

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

public class ConditionalGenerator<R> extends Generator<R> {

    static final long serialVersionUID = 5036672876367873391L;
    
    // The condition to be evaluated
    private Generator<Boolean> _test;

    // The functor to execute when the condition passes
    private Generator<R> _then;

    // The functor to execute when the condition fails
    private Generator<R> _else;

    /**
     * Builds a ConditionalGenerator functor, given the condition to test, and the two
     * functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public ConditionalGenerator(Generator<Boolean> test, Generator<R> trueFn,
                            Generator<R> falseFn)
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
    public Generator<Boolean> getCondition() { return _test; }

    /**
     * Returns the functor that is executed when the condition is true
     * @return the functor that is executed when the condition is true
     */
    public Generator<R> getTrueFunctor() { return _then; }

    /**
     * Returns the functor that is executed when the condition is false
     * @return the functor that is executed when the condition is false
     */
    public Generator<R> getFalseFunctor() { return _else; }

    // Generator interface
    
    /**
     * Given argument <b>x</b>, evaluates test(x); if true, returns trueFn(x),
     * otherwise, returns falseFn(x).
     * 
     * @return test() ? trueFn() : falseFn()
     */
    public R gen() {
        return _test.gen() ? _then.gen() : _else.gen();
    }
    
    /**
     * Calls the Visitor's <code>visit(ConditionalGenerator)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConditionalGenerator.Visitor)
            ((ConditionalGenerator.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ConditionalGenerator["+_test+"?"+_then+":"+_else+"]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ConditionalGenerator</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConditionalGenerator host);
    }
}
