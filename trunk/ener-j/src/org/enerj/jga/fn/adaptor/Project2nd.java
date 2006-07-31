// ============================================================================
// $Id: Project2nd.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryFunctor;
    
/**
 * Binary Functor that returns the second of two runtime arguments.
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class Project2nd<T1,T2> extends BinaryFunctor<T1,T2,T2> {

    static final long serialVersionUID = -222194429833096707L;

    // BinaryFunctor interface
    
    /**
     * Given two arguments, returns the second.  The first argument is not
     * evaluated in any way by this predicate.
     *
     * @return x
     */
    public T2 fn(T1 unused, T2 y) {
        return y;
    }

    /**
     * Calls the Visitor's <code>visit(Project2nd)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Project2nd.Visitor)
            ((Project2nd.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "Project2nd";
    }
    
     // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret an <b>Project2nd</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Project2nd host);
    }
}
        
