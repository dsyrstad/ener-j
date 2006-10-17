// ============================================================================
// $Id: ApplyGenerator.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

public class ApplyGenerator extends Generator<Object[]> {

    static final long serialVersionUID = -2160391060913269243L;
    
    //
    private Generator[] _generators;
    
    public ApplyGenerator (Generator[] generators){
        _generators = generators;
    }

    public Generator[] getGenerators() {
        return _generators;
    }                                                 

    public Object[] gen() {
        Object[] result = new Object[_generators.length];
        for (int i = 0; i < _generators.length; ++i) {
            result[i] = _generators[i].gen();
        }

        return result;
    }

    /**
     * Calls the Visitor's <code>visit(ApplyGenerator)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ApplyGenerator.Visitor)
            ((ApplyGenerator.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return Arrays.toString(_generators);
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>ApplyGenerator</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ApplyGenerator host);
    }
}
