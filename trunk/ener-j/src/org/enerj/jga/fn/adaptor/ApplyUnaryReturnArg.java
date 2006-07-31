// ============================================================================
// $Id: ApplyUnaryReturnArg.java,v 1.1 2006/02/19 01:20:33 dsyrstad Exp $
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

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.Arrays;

/**
 * Produces an array by passing an input argument to a given set of unary
 * functors and return the argument to fn().
 * <p>
 * Copyright &copy; 2004  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class ApplyUnaryReturnArg<T> extends UnaryFunctor<T, Object> {

    static final long serialVersionUID = -7934367561074978884L;

    private UnaryFunctor<T,?>[] _functors;
    
    public ApplyUnaryReturnArg (UnaryFunctor<T,?>... functors){
        _functors = functors;
    }

    public UnaryFunctor<T,?>[] getFunctors() {
        return _functors;
    }                                                 

    public Object fn(T arg) {
        Object[] result = new Object[_functors.length];
        for (int i = 0; i < _functors.length; ++i) {
            result[i] = _functors[i].fn(arg);
        }

        return arg;
    }

    /**
     * Calls the Visitor's <code>visit(ApplyUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ApplyUnaryReturnArg.Visitor)
            ((ApplyUnaryReturnArg.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ApplyUnaryReturnArg" + Arrays.toString(_functors);
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>ApplyUnary</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ApplyUnaryReturnArg host);
    }
}
