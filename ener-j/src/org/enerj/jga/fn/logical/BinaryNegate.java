// ============================================================================
// $Id: BinaryNegate.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryPredicate;
import org.enerj.jga.fn.BinaryFunctor;

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

public class BinaryNegate<T1, T2> extends BinaryPredicate<T1, T2> {

    static final long serialVersionUID = 3005632919263334394L;
    
    private BinaryFunctor<? super T1, ? super T2, Boolean> _p;

    /**
     * Builds a BinaryNegate predicate wrapping the given Binary Predicate.
     * @throws IllegalArgumentException when no child predicate is given
     */
    public BinaryNegate(BinaryFunctor<? super T1, ? super T2, Boolean> p) {
        if (p == null) {
            throw new IllegalArgumentException("Child Predicate may not be null");
        }
        
        _p = p;
    }

    /**
     * Returns the child predicate.
     * @return the child predicate.
     */
    public BinaryFunctor<? super T1, ? super T2, Boolean> getPredicate() {
        return _p;
    }

    // BinaryPredicate interface
    
    /**
     * Given arguments <b>x</b> and <b>y</b>, returns true when child
     * predicate <b>p</b> returns false for x and y, otherwise returns true.
     * 
     * @return !(p.p(x,y))
     */
    public Boolean fn(T1 x, T2 y) {
        return ! _p.fn(x,y);
    }
    
    /**
     * Calls the Visitor's <code>visit(UnaryNegate)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof BinaryNegate.Visitor)
            ((BinaryNegate.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "BinaryNegate("+_p+")";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>BinaryNegate</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(BinaryNegate host);
    }
}
