// ============================================================================
// $Id: Any.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.UnaryPredicate;

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

public class Any<T> extends UnaryPredicate<T> {

    static final long serialVersionUID = -3665390675036353724L;
    
    private Collection<UnaryFunctor<T,Boolean>> _branches;

    /**
     * Builds the Any predicate with an empty default collection of branch
     * predicates.  The default collection is a list, and branches will be
     * evaluated in the order they are added.
     */
    public Any() {
        _branches = new ArrayList<UnaryFunctor<T,Boolean>>();
    }
     
    /**
     * Builds the Any predicate with the given collection of branch predicates.
     * More predicates may be added to the collection after construction.  The
     * order of evaluation of the branch predicates is determined by the
     * collection in use.
     */
    public Any(Collection<UnaryFunctor<T,Boolean>> branches) {
        _branches = (branches != null) ? branches : new ArrayList<UnaryFunctor<T,Boolean>>();
    }
     
    /**
     * Adds the predicate to the collection of branch predicates.  The
     * predicate is added at the default location for the chosen collection.
     * @deprecated
     */
    public void addBranch(UnaryFunctor<T,Boolean> pred) {
        _branches.add(pred);
    }
    
    /**
     * Returns an Iterator over the branch predicates.
     * @return an Iterator over the branch predicates
     */
    public Iterator<UnaryFunctor<T,Boolean>> branches() {
        return _branches.iterator();
    }

    // UnaryPredicate interface
    
    /**
     * Given arguments <b>x</b>, returns true if at least one branch predicate
     * returns true when given x.  Returns false when there are no branch
     * predicates.
     * 
     * @return true if one branch predicates return true, false otherwise
     */
    public Boolean fn(T x) {
        for (UnaryFunctor<T,Boolean> pred : _branches) {
            if (pred.fn(x))
                return true;
        }
        
        return false;
    }
    
    /**
     * Calls the Visitor's <code>visit(Any)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Any.Visitor)
            ((Any.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        StringBuffer buf = new StringBuffer("Any(");
        String sep = "";
        for (UnaryFunctor<T,Boolean> pred : _branches) {
            buf.append(sep).append(pred);
            sep=",";
        }
        buf.append(")");
        return buf.toString();
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret an <b>Any</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Any host);
    }
}
