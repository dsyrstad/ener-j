// ============================================================================
// $Id: InstanceOf.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

import org.enerj.jga.fn.UnaryPredicate;

/**
 * Functor that returns true if the argument is of the given subtype of T.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class InstanceOf<T> extends UnaryPredicate<T> {

    static final long serialVersionUID = -1792964506358538850L;    

    private Class<? extends T> _class;

    /**
     * Builds a InstanceOf predicate that tests against the given class.
     * @throws IllegalArgumentException if the class is null.
     */
    public InstanceOf(Class<? extends T> cl) {
        if (cl == null)
            throw new IllegalArgumentException("A class must be given");
        
        _class = cl;
    }


    public Class<? extends T> getTestClass() { return _class; }
    
    // UnaryPredicate interface
    
    public Boolean fn(T arg) {
        return _class.isInstance(arg);
    }

    /**
     * Calls the Visitor's <code>visit(InstanceOf)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof InstanceOf.Visitor)
            ((InstanceOf.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "InstanceOf["+_class.getName()+"]";
    }
    
   // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>InstanceOf</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(InstanceOf host);
    }
}
