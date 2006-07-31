// ============================================================================
// $Id: LogicalNot.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
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

package org.enerj.jga.fn.logical;

import org.enerj.jga.fn.UnaryPredicate;

/**
 * Unary Predicate that returns true when Boolean argument <b>x</b> is false.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class LogicalNot extends UnaryPredicate<Boolean> {

    static final long serialVersionUID = -3464871237361189509L;

    // UnaryPredicate interface
    
     /**
     * Given Boolean argument <b>x</b>, returns true when x is false, false
     * when x is true
     * 
     * @return !x
     */
   public Boolean fn(Boolean x) {
        return ! x;
    }

     /**
     * Calls the Visitor's <code>visit(LogicalNot)</code> method, if it
     * implements the nested Visitor interface.
     */
   public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof LogicalNot.Visitor)
            ((LogicalNot.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "LogicalNot";
    }
    
    // AcyclicVisitor
     
    /**
     * Interface for classes that may interpret a <b>LogicalNot</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(LogicalNot host);
    }
}
