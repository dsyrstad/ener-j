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
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/TrackedValueFunctor.java,v 1.3 2006/02/24 03:00:43 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Returns the value set by setValue(). The argument to fn() is ignored.  
 * <p>
 * 
 * @version $Id: TrackedValueFunctor.java,v 1.3 2006/02/24 03:00:43 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TrackedValueFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = 7042782826462904426L;

    private Object mValue = null;
    

    /**
     * Construct a TrackedValueFunctor.
     */
    public TrackedValueFunctor() 
    {
    }


    /**
     * Gets the tracked value.
     *
     * @return the tracked value, or null if it is not set.
     */
    public Object getValue()
    {
        return mValue;
    }
    
    

    /**
     * Sets the tracked value.
     *
     * @param aValue the value to be set. May be null to clear it.
     */
    public void setValue(Object aValue)
    {
        mValue = aValue;
    }


    public Object fn(Object arg)
    {
        return mValue;
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(TrackedValueFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof TrackedValueFunctor.Visitor)
            ((TrackedValueFunctor.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "TrackedValueFunctor";
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>TrackedValueFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(TrackedValueFunctor host);
    }
}
