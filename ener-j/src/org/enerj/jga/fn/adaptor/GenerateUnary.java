// ============================================================================
// $Id: GenerateUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
 * UnaryFunctor that returns the result of a nested Generator.  The argument
 * to the UnaryFunctor is ignored.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class GenerateUnary<T,R> extends UnaryFunctor<T,R> {
    
    static final long serialVersionUID = 2928255703029297803L;
    
    // The Nested Generator
    private Generator<R> _gen;
    
    public GenerateUnary(Generator<R> gen) {
        _gen = gen;
    }

    /**
     * Returns the generator whose results are returned.
     */

    public Generator<R> getGenerator() { return _gen; }
    
    /**
     * Returns the results of invoking the generator.  The argument is ignored.
     */
    public R fn(T arg) {
        return _gen.gen();
    }
    
    /**
     * Calls the Visitor's <code>visit(GenerateUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof GenerateUnary.Visitor)
            ((GenerateUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _gen.toString();
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>GenerateUnary</b>
     * functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(GenerateUnary host);
    }
}
