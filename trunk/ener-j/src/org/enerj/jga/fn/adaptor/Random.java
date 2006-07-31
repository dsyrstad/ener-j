// ============================================================================
// $Id: Random.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
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
 * Functor that returns a pseudorandom value using Math.random().  Additional
 * implementations are left as an exercise...
 * <p>
 * Copyright &copy; 2004  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

// UNTESTED

public class Random extends Generator<Double> {
    
    static final long serialVersionUID = 8333040240099165144L;
    
    // Generator interface

    /**
     * Returns a pseudorandom value, using Math.random().
     * @return a pseudorandom value
     */
    
    public Double gen() {
        return new Double(Math.random());
    }

    /**
     * Calls the Visitor's <code>visit(Random)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Random.Visitor)
            ((Random.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Random()";
    }
    
     // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret a <b>Random</b> generator.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Random host);
    }
}
        
