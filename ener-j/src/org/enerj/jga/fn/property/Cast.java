// ============================================================================
// $Id: Cast.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

package org.enerj.jga.fn.property;

import java.text.MessageFormat;
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

public class Cast<R> extends UnaryFunctor<Object,R> {

    static final long serialVersionUID = 2712605844695159349L;

    private Class<R> _class;

    /**
     * Builds a Cast fuctor that returns values cast to the given type.
     * @throws IllegalArgumentException if the method name is null or empty, or
     *     if the argument type array is null.
     */
    public Cast(Class<R> cl) {
        if (cl == null)
            throw new IllegalArgumentException("A class must be given");
        
        _class = cl;
    }


    public Class<R> getCastClass() { return _class; }
    
    // UnaryPredicate interface
    
    public R fn(Object arg) {
        // In Java 1.5, we could write this as
        //
        //         return _class.cast(arg);
        //
        // but that isn't backwards compatable with 1.4, so
        // we'll do it another way.
        
        if (_class.isInstance(arg))
            return (R) arg;

        String msg = "Cannot cast [{0}] to class {1}";
        Object[] args = new Object[]{arg, _class.getName()};
        ClassCastException x = new ClassCastException(MessageFormat.format(msg, args)); 
        throw x;
    }

    /**
     * Calls the Visitor's <code>visit(InstanceOf)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Cast.Visitor)
            ((Cast.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "Cast["+_class.getName()+"]";
    }
    
   // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>Cast</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Cast host);
    }
}
