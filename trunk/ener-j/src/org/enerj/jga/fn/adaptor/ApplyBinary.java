// ============================================================================
// $Id: ApplyBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.util.Arrays;

/**
 * Produces an array by passing a pair of input arguments to a given set of
 * binary functors.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class ApplyBinary<T1,T2> extends BinaryFunctor<T1,T2,Object[]> {

    static final long serialVersionUID = 626713969730810589L;
    
    //
    private BinaryFunctor<T1,T2,?>[] _functors;
    
    public ApplyBinary (BinaryFunctor<T1,T2,?>[] functors){
        _functors = functors;
    }

    public BinaryFunctor<T1,T2,?>[] getFunctors() {
        return _functors;
    }                                                 

    public Object[] fn(T1 arg1, T2 arg2) {
        Object[] result = new Object[_functors.length];
        for (int i = 0; i < _functors.length; ++i) {
            result[i] = _functors[i].fn(arg1,arg2);
        }

        return result;
    }

    /**
     * Calls the Visitor's <code>visit(ApplyBinary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ApplyBinary.Visitor)
            ((ApplyBinary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return Arrays.toString(_functors);
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>ApplyBinary</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ApplyBinary host);
    }
}
