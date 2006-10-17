// ============================================================================
// $Id: Identity.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.adaptor;

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

public class Identity<T> extends UnaryFunctor<T, T> {
    
    static final long serialVersionUID = -1170897535180697025L;

    // the last value passed to fn()
    private transient T _last;
    
    /**
     * Returns the last argument that was given, or null if called prior to the
     * first call to fn().  This value does not persist when the functor is
     * serialized.
     * 
     * @return the last argument that was given or null.
     */
    public T arg() {
        return _last;
    }
    
    // UnaryFunctor interface
    
    /**
     * Given one argument, returns it.
     *
     * @return x
     */
    public T fn(T x) {
        _last = x;
        return x;
    }

    /**
     * Calls the Visitor's <code>visit(Identity)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Identity.Visitor)
            ((Identity.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "Identity";
    }
    
     // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret an <b>Identity</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Identity host);
    }
}
        
