// ============================================================================
// $Id: LogicalOr.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
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

import org.enerj.jga.fn.BinaryPredicate;

/**
 * Binary Predicate that returns true when either of Boolean arguments <b>x</b>
 * and <b>y</b> are true.
 * <p>
 * Note that this functor does <i>not</i> short circuit the evaluation
 * of the second argument.  The reason for this is that, by itself,
 * this functor accepts only boolean arguments.  When it is used in
 * conjunction with the adaptor functors to implement compound
 * functors, the adaptor cannot know that the functor it is adapting
 * may not need to use both arguments, so the adaptor will fully
 * evaluate both arguments before passing them to the And functor.
 * This would give the illusion that the short circuit logic is
 * broken, when it is in fact unimplementable using the standard
 * functor mechanism.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class LogicalOr extends BinaryPredicate<Boolean, Boolean> {

    static final long serialVersionUID = -5329467559457020210L;
    
    // BinaryPredicate interface
    
    /**
     * Given Boolean arguments <b>x</b> and <b>y</b>, returns true when either
     * x and y are true, otherwise false.
     * 
     * @return x | y
     */
    public Boolean fn(Boolean x, Boolean y) {
        return x | y;
    }

    /**
     * Calls the Visitor's <code>visit(LogicalOr)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof LogicalOr.Visitor)
            ((LogicalOr.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "LogicalOr";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>LogicalOr</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(LogicalOr bp);
    }
}
