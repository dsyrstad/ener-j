// ============================================================================
// $Id: Generate2nd.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.Generator;
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

public class Generate2nd<T1,T2,R> extends UnaryFunctor<T1,R> {
    
    static final long serialVersionUID = 8944237143829635388L;

    // The Functor applied to each value generated
    private BinaryFunctor<T1,T2,R> _fn;

    // The Nested Generator
    private Generator<T2> _gen;
    
    public Generate2nd(BinaryFunctor<T1,T2,R> fn, Generator<T2> gen) {
        _fn = fn;
        _gen = gen;
    }

    /**
     * Returns the Functor that is invoked
     */

    public BinaryFunctor<T1,T2,R> getFunctor() { return _fn; }

    /**
     * Returns the generator that produces the 2nd argument.
     */

    public Generator<T2> getGenerator() { return _gen; }
    
    /**
     * Returns the results of the functor, using the nested generator to
     * produce the first argument.
     */
    public R fn(T1 arg) {
        return _fn.fn(arg, _gen.gen());
    }

    /**
     * Calls the Visitor's <code>visit(Generate2nd)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Generate2nd.Visitor)
            ((Generate2nd.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _fn+".generate2nd("+_gen+")"; 
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Generate2nd</b>
     * functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Generate2nd host);
    }
}
