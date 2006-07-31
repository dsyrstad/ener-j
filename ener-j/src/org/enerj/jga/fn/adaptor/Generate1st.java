// ============================================================================
// $Id: Generate1st.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

/**
 * UnaryFunctor that uses a Generator to produce the 1st argument to a given
 * BinaryFunctor.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Generate1st<T1,T2,R> extends UnaryFunctor<T2,R> {
    
    static final long serialVersionUID = -7075574361370967089L;
    
    // The Functor applied to each value generated
    private BinaryFunctor<T1,T2,R> _fn;

    // The Nested Generator
    private Generator<T1> _gen;
    
    public Generate1st(BinaryFunctor<T1,T2,R> fn, Generator<T1> gen) {
        _fn = fn;
        _gen = gen;
    }

    /**
     * Returns the Functor that is invoked
     */

    public BinaryFunctor<T1,T2,R> getFunctor() { return _fn; }

    /**
     * Returns the generator that produces the 1st argument.
     */

    public Generator<T1> getGenerator() { return _gen; }
    
    /**
     * Returns the results of the functor, using the nested generator to
     * produce the first argument.
     */
    public R fn(T2 arg) {
        return _fn.fn(_gen.gen(), arg);
    }
    
    /**
     * Calls the Visitor's <code>visit(Generate1st)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Generate1st.Visitor)
            ((Generate1st.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _fn+".generate1st("+_gen+")"; 
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Generate1st</b>
     * functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Generate1st host);
    }
}
