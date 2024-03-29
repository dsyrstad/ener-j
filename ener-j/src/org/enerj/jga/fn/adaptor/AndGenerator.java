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
// ============================================================================
// $Id: AndGenerator.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.fn.Generator;

/**
 * Generator that performs a shortcircuit and operation using a given pair
 * of Boolean Generators.  When the first generator returns true, the second
 * is not evaluated.
 **/

public class AndGenerator extends Generator<Boolean> {

    static final long serialVersionUID = 6260506966868219143L;
    
    // The two generators that are tested
    private Generator<Boolean> _first;
    private Generator<Boolean> _second;
    
    /**
     * Builds a AndGenerator functor, given the two functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public AndGenerator(Generator<Boolean> first, Generator<Boolean> second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Two functors are required");
        }
        
        _first = first;
        _second = second;
    }


    public Generator<Boolean> getFirstFunctor() { return _first; }

    public Generator<Boolean> getSecondFunctor() { return _second; }
    
    
    public Boolean gen() {
        return _first.gen() && _second.gen();
    }

    /**
     * Calls the Visitor's <code>visit(AndGenerator)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof AndGenerator.Visitor)
            ((AndGenerator.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return _first +" && " +_second;
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>AndGenerator</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(AndGenerator host);
    }
}
