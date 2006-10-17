// ============================================================================
// $Id: UnaryFunctor.java,v 1.4 2006/06/06 21:29:37 dsyrstad Exp $
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
import org.enerj.jga.fn.adaptor.Bind;
import org.enerj.jga.fn.adaptor.ChainBinary;
import org.enerj.jga.fn.adaptor.ChainUnary;
import org.enerj.jga.fn.adaptor.Generate;
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

abstract public class UnaryFunctor<T, R> implements Serializable, Visitable {

    /**
     * Executes the function and returns the result.
     */
    abstract public R fn(T arg);

    /**
     * FactoryMethod that creates a UnaryFunctor that passes its argument to
     * the given functor, and uses the result as the argument to this
     * function. Given argument <b>x</b>, the new functor will return
     * <code>fn<sub>this</sub>(f(x)))</code>
     */
    public <F> UnaryFunctor<F,R> compose (UnaryFunctor<F,T> f) {
        if (f instanceof Identity)
            return (UnaryFunctor<F,R>)this;
        
        return new ChainUnary<F,T,R>(this,f);
    }

    /**
     * FactoryMethod that creates a BinaryFunctor that passes its arguments to
     * the given functor, and uses the result as the argument to this
     * function. Given arguments <b>x</b> and <b>y</b>, the new functor will
     * return <code>fn<sub>this</sub>(f(x,y)))</code>
     */
    public <F1,F2> BinaryFunctor<F1,F2,R> compose (BinaryFunctor<F1,F2,T> f) {
        return new ChainBinary<F1,F2,T,R>(this, f);
    }

    /**
     * FactoryMethod that creates a Generator to create the argument to this
     * function.  The new functor will return
     * <code>fn<sub>this</sub>(gen())</code>.
     */

    public Generator<R> generate (Generator<T> gen) {
        return new Generate<T,R>(this, gen);
    }
    
    /**
     * FactoryMethod that binds the argument arguments to this function to a specific
     * value.  The new functor will return
     * <code>fn<sub>this</sub>(val)</code>.
     */

    public Generator<R> bind (T val) {
        return new Bind<T,R>(val, this);
    }
    
    /**
     * No-op implementation of Visitable interface.
     */
    public void accept(Visitor v) {}
} 

