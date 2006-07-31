// ============================================================================
// $Id: Generate.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.fn.Generator;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Generator that wraps a UnaryFunctor around a nested Generator.  The values
 * produced by the nested generator are passed to the functor, and the results
 * returned to the caller.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Generate<T,R> extends Generator<R> {
    
    static final long serialVersionUID = -7866951985301046565L;

    // The Functor applied to each value generated
    private UnaryFunctor<T,R> _fn;

    // The Nested Generator
    private Generator<T> _gen;
    
    public Generate(UnaryFunctor<T,R> fn, Generator<T> gen) {
        _fn = fn;
        _gen = gen;
    }

    /**
     * Returns the UnaryFunctor that is applied to values returned by the
     * nested Generator.
     */
    public UnaryFunctor<T,R> getFunctor() { return _fn; }

    /**
     * Returns the nested Generator;
     */
    public Generator<T> getGenerator() { return _gen; }
    
    /**
     * Returns the results of the nested generator as modified by the functor.
     */
    public R gen() {
        return _fn.fn(_gen.gen());
    }
    
    /**
     * Calls the Visitor's <code>visit(Generate)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Generate.Visitor)
            ((Generate.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _fn+".generate("+_gen+")"; 
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Generate</b>
     * functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Generate host);
    }
}
