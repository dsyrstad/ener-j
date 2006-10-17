// ============================================================================
// $Id: BinaryFunctor.java,v 1.3 2005/08/12 02:56:47 dsyrstad Exp $
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

package org.enerj.jga.fn;

import java.io.Serializable;
import org.enerj.jga.fn.adaptor.Bind1st;
import org.enerj.jga.fn.adaptor.Bind2nd;
import org.enerj.jga.fn.adaptor.ChainBinary;
import org.enerj.jga.fn.adaptor.ComposeBinary;
import org.enerj.jga.fn.adaptor.ComposeUnary;
import org.enerj.jga.fn.adaptor.Distribute;
import org.enerj.jga.fn.adaptor.Generate1st;
import org.enerj.jga.fn.adaptor.Generate2nd;
import org.enerj.jga.fn.adaptor.Identity;

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

abstract public class BinaryFunctor<T1,T2,R> implements Serializable,Visitable {
    
    /**
     * Executes the function and returns the result.
     */
    abstract public R fn(T1 arg1, T2 arg2);

    /**
     * Factory method that creates a UnaryFunctor that binds a constant value to
     * this' first argument.  Given argument <b>x</b>, the new functor will
     * return <code>fn<sub>this</sub>(value, x)</code>
     */
    public UnaryFunctor<T2,R> bind1st (T1 value){
        return new Bind1st<T1,T2,R>(value, this);
    }

    /**
     * Factory method that creates a UnaryFunctor that binds a constant value to
     * this' second argument.  Given argument <b>x</b>, the new functor will
     * return <code>fn<sub>this</sub>(x, value)</code>
     */
    public UnaryFunctor<T1,R> bind2nd (T2 value){
        return new Bind2nd<T1,T2,R>(value, this);
    }

    /**
     * Factory method that creates a Generator that binds constant values to
     * this' arguments.  The new generator will return
     * <code>fn<sub>this</sub>(value1, value2)</code>
     */
    public Generator<R> bind (T1 value1, T2 value2) {
        return bind1st(value1).bind(value2);
    }

    /**
     * FactoryMethod that creates a UnaryFunctor that passes its argument to
     * each of the given functors, and uses the results as the arguments to this
     * function. Given argument <b>x</b>, the new functor will return
     * <code>fn<sub>this</sub>(g(x), h(x))</code>
     */
    public <F> UnaryFunctor<F,R> compose (UnaryFunctor<F,T1> g, UnaryFunctor<F,T2> h) {
        return new ComposeUnary<F,T1,T2,R>(g,h,this);
    }

    /**
     * FactoryMethod that creates a BinaryFunctor that passes its argument to
     * each of the given functors, and uses the results as the arguments to this
     * function. Given arguments <b>x</b> and <b>y</b>, the new functor will
     * return <code>fn<sub>this</sub>(g(x,y), h(x,y))</code>
     */
    public <F1,F2> BinaryFunctor<F1,F2,R>
    compose (BinaryFunctor<F1,F2,T1> g, BinaryFunctor<F1,F2,T2> h)
    {
        return new ComposeBinary<F1,F2,T1,T2,R>(g,h,this);
    }

    /**
     * FactoryMethod that creates a BinaryFunctor that passes each of its two
     * arguments to a pair of UnaryFunctors, then uses the results as arguments
     * to this function.  Given arguments <b>x</b> and <b>y</b>, the new functor
     * will return <code>fn<sub>this</sub>(g(x), h(y))</code>.  Note: this
     * method cannot be called compose, as it is only distinct from the unary
     * form of compose in its param types and its return type
     */
    public <F1,F2> BinaryFunctor<F1,F2,R> distribute (UnaryFunctor<F1,T1> g, UnaryFunctor<F2,T2> h)
    {
        if (g instanceof Identity && h instanceof Identity)
            return (BinaryFunctor<F1,F2,R>) this;
        
        return new Distribute<F1,F2,T1,T2,R>(g,h,this);
    }

    /**
     * FactoryMethod that converts this functor to a UnaryFunctor by using the
     * given generator to produce the first argument.  Given argument <b>y</b>,
     * the new functor will return <code>fn<sub>this</sub>(gen(), y)</code>.
     */
    
    public UnaryFunctor<T2,R> generate1st(Generator<T1> gen) {
        return new Generate1st<T1,T2,R>(this, gen);
    } 

    /**
     * FactoryMethod that converts this functor to a UnaryFunctor by using the
     * given generator to produce the first argument.  Given argument <b>x</b>,
     * the new functor will return <code>fn<sub>this</sub>(x, gen())</code>.
     */
    
    public UnaryFunctor<T1,R> generate2nd(Generator<T2> gen) {
        return new Generate2nd<T1,T2,R>(this, gen);
    } 

    /**
     * FactoryMethod that converts this functor to a Generator by using the two
     * given generators to produce the arguments.  The new functor will return
     * <code>fn<sub>this</sub>(gen1(), gen2())</code>.
     */
    
    public Generator<R> generate(Generator<T1> gen1, Generator<T2> gen2) {
        return generate1st(gen1).generate(gen2);
    } 

    /**
     * No-op implementation of Visitable interface.
     */
    public void accept(Visitor v) {}
} 
