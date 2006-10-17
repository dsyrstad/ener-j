// ============================================================================
// $Id: UnaryNegate.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
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

package org.enerj.jga.fn.logical;

import org.enerj.jga.fn.UnaryPredicate;
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

public class UnaryNegate<T> extends UnaryPredicate<T> {

    static final long serialVersionUID = -722445812960547108L;
    
    private UnaryFunctor<T, Boolean> _p;
    
    /**
     * Builds a UnaryNegate predicate wrapping the given Unary Predicate.
     * @throws IllegalArgumentException when no child predicate is given
     */
    public UnaryNegate(UnaryFunctor<T, Boolean> p) {
        if (p == null) {
            throw new IllegalArgumentException("Child Predicate may not be null");
        }
        
        _p = p;
    }

    /**
     * Returns the child predicate.
     * @return the child predicate.
     */
    public UnaryFunctor<T,Boolean> getPredicate() { return _p; }

    // UnaryPredicate interface
    
    /**
     * Given argument <b>x</b>, returns true when child
     * predicate <b>p</b> returns false for x, otherwise returns true.
     * 
     * @return !(p.p(x))
     */
    public Boolean fn(T x) {
        return ! _p.fn(x);
    }
    
    /**
     * Calls the Visitor's <code>visit(UnaryNegate)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof UnaryNegate.Visitor)
            ((UnaryNegate.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "UnaryNegate";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>UnaryNegate</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(UnaryNegate host);
    }
}
