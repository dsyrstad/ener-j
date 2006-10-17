// ============================================================================
// $Id: ApplyUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

public class ApplyUnary<T> extends UnaryFunctor<T,Object[]> {

    static final long serialVersionUID = -7934367561074978884L;

    private UnaryFunctor<T,?>[] _functors;
    
    public ApplyUnary (UnaryFunctor<T,?>... functors){
        _functors = functors;
    }

    public UnaryFunctor<T,?>[] getFunctors() {
        return _functors;
    }                                                 

    public Object[] fn(T arg) {
        Object[] result = new Object[_functors.length];
        for (int i = 0; i < _functors.length; ++i) {
            result[i] = _functors[i].fn(arg);
        }

        return result;
    }

    /**
     * Calls the Visitor's <code>visit(ApplyUnary)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ApplyUnary.Visitor)
            ((ApplyUnary.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return Arrays.toString(_functors);
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>ApplyUnary</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ApplyUnary host);
    }
}
